package com.nicopoly.app.data.importer

import android.content.Context
import android.util.Log
import androidx.room.withTransaction
import com.nicopoly.app.data.local.NicopolyDatabase
import com.nicopoly.app.data.local.entity.ProductoEntity
import com.nicopoly.app.data.local.entity.UbicacionEntity
import com.nicopoly.app.data.api.GoogleSheetsService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Importador de datos desde Excel y Google Sheets a la base de datos Room.
 *
 * @param database Base de datos Room inyectada vía Hilt.
 * @param googleSheetsService Servicio de Google Sheets para conexión directa.
 * @param context Contexto de la aplicación para acceder a assets.
 */
@Singleton
class ExcelImporter @Inject constructor(
    private val database: NicopolyDatabase,
    private val googleSheetsService: GoogleSheetsService,
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG = "ExcelImporter"
        private const val EXCEL_FILE_PATH = "DATOS.xlsx"
        private const val BATCH_SIZE = 500 // Insertar en lotes de 500
    }

    /**
     * Ejecuta la importación completa del Excel a Room desde un InputStream.
     *
     * Este método permite importar desde cualquier fuente (archivo seleccionado
     * por el usuario, red, etc.) sin depender de assets.
     *
     * La importación es segura:
     * - Primero valida la estructura del Excel
     * - Si es inválido, NO modifica Room
     * - Utiliza transacción para garantizar atomicidad
     * - Si falla durante la transacción, Room hace rollback automáticamente
     *
     * @param inputStream InputStream del archivo Excel seleccionado.
     * @return Estadísticas detalladas del proceso de importación.
     */
    suspend fun importFromStream(inputStream: java.io.InputStream, onProgress: ((Int, String) -> Unit)? = null): ExcelImportStats {
        Log.i(TAG, "════ Iniciando importación desde stream ════")

        return try {
            val workbook = org.apache.poi.xssf.usermodel.XSSFWorkbook(inputStream)

            // ── VALIDACIÓN ESTRUCTURAL ──
            // Verificar que existan las hojas requeridas ANTES de modificar Room
            val datosSheet = workbook.getSheet("DATOS")
            if (datosSheet == null) {
                Log.e(TAG, "Hoja DATOS no encontrada en el Excel seleccionado")
                workbook.close()
                return ExcelImportStats(
                    summaryMessage = "ERROR: El archivo no contiene la hoja 'DATOS'"
                )
            }

            val ubicacionesSheet = workbook.getSheet("UBICACIONES")
            if (ubicacionesSheet == null) {
                Log.e(TAG, "Hoja UBICACIONES no encontrada en el Excel seleccionado")
                workbook.close()
                return ExcelImportStats(
                    summaryMessage = "ERROR: El archivo no contiene la hoja 'UBICACIONES'"
                )
            }

            // Validar encabezados de DATOS (fila 0)
            val datosHeader = datosSheet.getRow(0)
            if (datosHeader == null) {
                workbook.close()
                return ExcelImportStats(
                    summaryMessage = "ERROR: La hoja 'DATOS' está vacía o no tiene encabezados"
                )
            }

            // Validar que existan registros (más allá del header)
            val datosLastRow = datosSheet.lastRowNum
            if (datosLastRow < 1) {
                workbook.close()
                return ExcelImportStats(
                    summaryMessage = "ERROR: La hoja 'DATOS' no contiene registros"
                )
            }

            // ── PROCESAMIENTO ──
            var stats = ExcelImportStats()

            // Paso 1: Leer ubicaciones
            val ubicacionesMap = readUbicacionesSheet(ubicacionesSheet)
            stats = stats.copy(totalUbicacionRowsRead = ubicacionesMap.size)
            Log.i(TAG, "Ubicaciones leídas: ${ubicacionesMap.size}")
            onProgress?.invoke(10, "Validando estructura")

            // Paso 2: Leer productos con progreso real
            val totalProductos = datosLastRow - 1
            var processedProductos = 0
            
            val sheetResult = readProductosSheet(
                datosSheet = datosSheet,
                ubicacionesSet = ubicacionesMap.keys.toSet(),
                maxRows = null
            )
            val productos = sheetResult.productos
            val globalStats = sheetResult.stats

            if (productos.isEmpty()) {
                workbook.close()
                return ExcelImportStats(
                    summaryMessage = "ERROR: No se encontraron productos válidos en el archivo"
                )
            }

            // Paso 3: Reemplazar datos en Room dentro de transacción atómica
            database.withTransaction {
                // Borrar datos anteriores
                database.productoDao().deleteAllProductos()
                database.ubicacionDao().deleteAllUbicaciones()

                // Insertar nuevos datos - con progreso
                // Ejecutar la inserción de ubicaciones
                insertUbicacionesBatch(ubicacionesMap)
                onProgress?.invoke(25, "Preparando base de datos")
                
                // Insertar productos por lotes con progreso real
                val batches = productos.chunked(BATCH_SIZE)
                for ((index, batch) in batches.withIndex()) {
                    insertProductosBatch(batch)
                    processedProductos += batch.size
                    val progress = 25 + (65 * processedProductos / totalProductos).coerceAtMost(65)
                    onProgress?.invoke(progress, "Importando productos")
                }
            }

            // Estadísticas finales
            stats = ExcelImportStats(
                totalDataRowsRead = productos.size,
                totalUbicacionRowsRead = ubicacionesMap.size,
                productosInserted = productos.size,
                ubicacionesInserted = ubicacionesMap.size,
                padresWithUbicacion = globalStats.padresWithUbicacion,
                padresWithoutUbicacion = globalStats.padresWithoutUbicacion,
                codesWithUncertainParsing = globalStats.codesWithUncertainParsing,
                duplicatesFound = globalStats.duplicatesFound,
                skippedRows = globalStats.skippedRows,
                uncertainCodes = globalStats.uncertainCodes.toList().take(50),
                summaryMessage = "Importación completada exitosamente"
            )

            Log.i(TAG, stats.toString())
            workbook.close()
            inputStream.close()
            stats

        } catch (e: Exception) {
            Log.e(TAG, "ERROR durante importación desde Excel", e)
            ExcelImportStats(
                summaryMessage = "ERROR: ${e.message ?: e.javaClass.simpleName}"
            )
        }
    }

    // ── FUNCIONES PRIVADAS ──

    private fun readUbicacionesSheet(sheet: org.apache.poi.ss.usermodel.Sheet): Map<String, String> {
        val ubicaciones = mutableMapOf<String, String>()

        // Recorrer filas (empezando desde 1 para evitar header)
        for (row in sheet.rowIterator()) {
            if (row.rowNum == 0) continue // Saltar header

            val codigoPadre = row.getCell(0)?.getStringCellValue()?.trim()
            val ubicacion = row.getCell(1)?.getStringCellValue()?.trim()

            if (!codigoPadre.isNullOrEmpty() && !ubicacion.isNullOrEmpty()) {
                ubicaciones[codigoPadre] = ubicacion
            }
        }

        return ubicaciones
    }

    private data class SheetResult(val productos: List<ProductoEntity>, val stats: GlobalImportStats)

    private fun readProductosSheet(
        datosSheet: org.apache.poi.ss.usermodel.Sheet,
        ubicacionesSet: Set<String>,
        maxRows: Int?
    ): SheetResult {
        val productos = mutableListOf<ProductoEntity>()
        val globalStats = GlobalImportStats()

        // Recorrer filas (empezando desde 1 para evitar header)
        val limit = maxRows ?: datosSheet.lastRowNum
        for (row in datosSheet.rowIterator()) {
            if (row.rowNum == 0) continue // Saltar header
            if (row.rowNum > limit) break

            val rawCodigoHijo = row.getCell(0)?.getStringCellValue()?.trim()
            val rawColor = row.getCell(1)?.getStringCellValue()?.trim()
            val rawTalla = row.getCell(2)?.getStringCellValue()?.trim()
            val rawCategoria = row.getCell(3)?.getStringCellValue()?.trim()
            val rawTemporada = row.getCell(4)?.getStringCellValue()?.trim()
            val rawStockBodega = row.getCell(5)?.getNumericCellValue().takeIf { it != 0.0 }
            val rawStockProvi1 = row.getCell(6)?.getNumericCellValue().takeIf { it != 0.0 }
            val rawStockFilomena = row.getCell(7)?.getNumericCellValue().takeIf { it != 0.0 }
            val rawStockProvi2 = row.getCell(8)?.getNumericCellValue().takeIf { it != 0.0 }
            val rawPrecioTiendas = row.getCell(9)?.getNumericCellValue().takeIf { it != 0.0 }
            val rawPrecioInicial = row.getCell(10)?.getNumericCellValue().takeIf { it != 0.0 }
            val rawPrecioMayor = row.getCell(11)?.getNumericCellValue().takeIf { it != 0.0 }

            // Parsear código hijo y extraer padre, color
            val (codigoPadre, color) = parseCodigoHijo(rawCodigoHijo)

            // Validar que el padre exista en ubicaciones si es necesario
            if (!ubicacionesSet.contains(codigoPadre)) {
                globalStats.padresWithoutUbicacion++
                Log.w(TAG, "[DIAG] Código padre '$codigoPadre' sin ubicación registrada")
            }
            
            // Validar que el código hijo sea válido
            if (rawCodigoHijo.isNullOrEmpty()) {
                globalStats.skippedRows++
                continue
            }

            // Validar que exista al menos un stock
            val hasStock = rawStockBodega != null || rawStockProvi1 != null || rawStockFilomena != null || rawStockProvi2 != null
            if (!hasStock) {
                globalStats.skippedRows++
                continue
            }

            // Validar que exista al menos un precio
            val hasPrecio = rawPrecioTiendas != null || rawPrecioInicial != null || rawPrecioMayor != null
            if (!hasPrecio) {
                globalStats.skippedRows++
                continue
            }

            // Validar que el código hijo no tenga un formato incierto (para diagnóstico)
            if (rawCodigoHijo.contains("?")) {
                globalStats.codesWithUncertainParsing++
                Log.w(TAG, "[DIAG] Código hijo con símbolo '?' encontrado: $rawCodigoHijo")
            }

            // Crear el ProductoEntity
            val producto = ProductoEntity(
                codigoHijo = rawCodigoHijo,
                codigoPadre = codigoPadre,
                color = color,
                talla = rawTalla ?: "",
                categoria = rawCategoria.orEmpty(),
                temporada = rawTemporada.orEmpty(),
                stockBodega = rawStockBodega?.toInt() ?: 0,
                stockProvi1 = rawStockProvi1?.toInt() ?: 0,
                stockFilomena = rawStockFilomena?.toInt() ?: 0,
                stockProvi2 = rawStockProvi2?.toInt() ?: 0,
                precioTiendas = rawPrecioTiendas?.toFloat()?.toDouble() ?: 0.0,
                precioInicial = rawPrecioInicial?.toFloat()?.toDouble() ?: 0.0,
                precioMayor = rawPrecioMayor?.toFloat()?.toDouble() ?: 0.0
            )

            // Validar que el código hijo no sea duplicado (esto es un control extra)
            if (productos.any { it.codigoHijo == producto.codigoHijo }) {
                globalStats.duplicatesFound++
                Log.w(TAG, "[DIAG] Código hijo duplicado encontrado: ${producto.codigoHijo}")
            }

            productos.add(producto)
        }

        return SheetResult(productos, globalStats)
    }

    private suspend fun insertUbicacionesBatch(ubicaciones: Map<String, String>) {
        val ubicacionesList = ubicaciones.map { (codigoPadre, ubicacion) ->
            UbicacionEntity(
                codigoPadre = codigoPadre,
                ubicacion = ubicacion
            )
        }

        database.ubicacionDao().insertUbicaciones(ubicacionesList)
    }

    private suspend fun insertProductosBatch(productos: List<ProductoEntity>) {
        database.productoDao().insertProductos(productos)
    }

    private data class GlobalImportStats(
        var padresWithUbicacion: Int = 0,
        var padresWithoutUbicacion: Int = 0,
        var codesWithUncertainParsing: Int = 0,
        var duplicatesFound: Int = 0,
        var skippedRows: Int = 0,
        var uncertainCodes: MutableList<String> = mutableListOf()
    )

    /**
     * Método para obtener datos directamente desde la API V4 de Google Sheets.
     *
     * Utiliza GoogleSheetsService para conectarse a la hoja privada usando
     * la cuenta de servicio, descarga los datos, los mapea y los guarda en Room.
     *
     * @param onProgress Callback opcional para reportar progreso.
     * @return Estadísticas detalladas del proceso de importación.
     */
    suspend fun importFromGoogleSheets(onProgress: ((Int, String) -> Unit)? = null): ExcelImportStats {
        Log.i(TAG, "════ Iniciando importación directa desde Google Sheets ════")

        return try {
            onProgress?.invoke(5, "Conectando a Google Sheets...")

            // Llamar al nuevo servicio (descarga ambas pestañas)
            val sheetData = googleSheetsService.fetchSheetData()
            val reposicionValues = sheetData.reposicionRows
            val ubicacionesValues = sheetData.ubicacionesRows

            if (reposicionValues.size <= 1) {
                Log.e(TAG, "No se recibieron datos de productos en la hoja 'Reposicion'")
                return ExcelImportStats(
                    summaryMessage = "ERROR: No se encontraron datos en la hoja 'Reposicion'"
                )
            }

            Log.i(TAG, "Datos recibidos: ${reposicionValues.size - 1} productos, ${ubicacionesValues.size - 1} ubicaciones.")
            onProgress?.invoke(20, "Procesando ubicaciones...")

            // 1. Procesar pestaña 'Ubicaciones'
            val ubicacionesMap = mutableMapOf<String, String>()
            for (i in 1 until ubicacionesValues.size) {
                val row = ubicacionesValues[i]
                val itemCode = row.getOrNull(0)?.toString()?.trim().orEmpty()
                val padreColor = row.getOrNull(1)?.toString()?.trim().orEmpty()
                val ubi = row.getOrNull(2)?.toString()?.trim().orEmpty()

                if (ubi.isNotEmpty()) {
                    if (padreColor.isNotEmpty()) {
                        ubicacionesMap[padreColor] = ubi
                    }
                    if (itemCode.isNotEmpty()) {
                        val (codigoPadre, _) = parseCodigoHijoFromAPI(itemCode)
                        if (codigoPadre.isNotEmpty() && !ubicacionesMap.containsKey(codigoPadre)) {
                            ubicacionesMap[codigoPadre] = ubi
                        }
                    }
                }
            }

            onProgress?.invoke(35, "Procesando ${reposicionValues.size - 1} productos...")

            // 2. Procesar pestaña 'Reposicion'
            val productos = mutableListOf<ProductoEntity>()

            for (i in 1 until reposicionValues.size) {
                val row = reposicionValues[i]
                
                val itemCodeRaw = row.getOrNull(0)?.toString()?.trim()
                if (itemCodeRaw.isNullOrEmpty()) continue

                val (codigoPadre, color) = parseCodigoHijoFromAPI(itemCodeRaw)

                fun getStr(idx: Int): String = row.getOrNull(idx)?.toString()?.trim().orEmpty()
                fun getNum(idx: Int): Int = getStr(idx).toDoubleOrNull()?.toInt() ?: 0
                fun getDouble(idx: Int): Double = getStr(idx).toDoubleOrNull() ?: 0.0

                val producto = ProductoEntity(
                    codigoHijo = itemCodeRaw,
                    codigoPadre = codigoPadre,
                    color = color,
                    talla = "",
                    categoria = getStr(2),
                    temporada = getStr(3),
                    stockProvi1 = getNum(4),
                    stockFilomena = getNum(5),
                    stockProvi2 = getNum(6),
                    t060 = getNum(8),
                    stockBodega = getNum(10),
                    precioTiendas = getDouble(11),
                    precioInicial = getDouble(12),
                    precioMayor = getDouble(13)
                )

                productos.add(producto)

                // Si la columna 15 de Reposición tiene ubicación y no estaba en la pestaña Ubicaciones, complementarla
                val ubiRep = getStr(15)
                if (ubiRep.isNotEmpty() && !ubicacionesMap.containsKey(codigoPadre)) {
                    ubicacionesMap[codigoPadre] = ubiRep
                }
            }

            onProgress?.invoke(50, "Preparando base de datos...")

            // 3. Reemplazar datos en Room dentro de transacción atómica
            database.withTransaction {
                database.productoDao().deleteAllProductos()
                database.ubicacionDao().deleteAllUbicaciones()

                onProgress?.invoke(60, "Guardando productos...")
                val batches = productos.chunked(BATCH_SIZE)
                for ((index, batch) in batches.withIndex()) {
                    insertProductosBatch(batch)
                    val progress = 60 + (30 * (index + 1) / batches.size)
                    onProgress?.invoke(progress, "Guardando productos...")
                }

                if (ubicacionesMap.isNotEmpty()) {
                    onProgress?.invoke(95, "Guardando ubicaciones (${ubicacionesMap.size})...")
                    insertUbicacionesBatch(ubicacionesMap)
                }
            }

            val stats = ExcelImportStats(
                totalDataRowsRead = reposicionValues.size - 1,
                totalUbicacionRowsRead = ubicacionesMap.size,
                productosInserted = productos.size,
                ubicacionesInserted = ubicacionesMap.size,
                summaryMessage = "Google Sheets: ${productos.size} productos y ${ubicacionesMap.size} ubicaciones actualizadas"
            )

            Log.i(TAG, stats.toString())
            onProgress?.invoke(100, "Completado")
            stats

        } catch (e: Exception) {
            Log.e(TAG, "ERROR general durante importación desde Google Sheets", e)
            ExcelImportStats(
                summaryMessage = "ERROR: ${e.message ?: e.javaClass.simpleName}"
            )
        }
    }

    /**
     * Método para obtener datos desde assets.
     *
     * Este método es una alternativa a importFromStream, que permite leer desde
     * el directorio de assets sin necesidad de un InputStream.
     */
    suspend fun importFromAssets(onProgress: ((Int, String) -> Unit)? = null): ExcelImportStats {
        Log.i(TAG, "════ Iniciando importación desde assets ════")

        return try {
            val inputStream = context.assets.open(EXCEL_FILE_PATH)
            importFromStream(inputStream, onProgress)
        } catch (e: Exception) {
            Log.e(TAG, "ERROR durante importación desde assets", e)
            ExcelImportStats(
                summaryMessage = "ERROR: ${e.message ?: e.javaClass.simpleName}"
            )
        }
    }

    /**
     * Deriva codigoPadre y color a partir de un código hijo del formato API.
     *
     * La API entrega códigos consolidados sin guiones, por ejemplo: "N06182AM".
     * El codigoPadre se obtiene tomando los primeros 6 caracteres (base numérica + prefijo).
     * El color corresponde a las letras finales restantes después del padre.
     *
     * Si el código ya tiene guiones, se delega a parseCodigoHijo para mantener compatibilidad.
     */
    private fun parseCodigoHijoFromAPI(codigoHijo: String?): Pair<String, String> {
        if (codigoHijo.isNullOrEmpty()) {
            return Pair("", "")
        }

        // Si el código contiene guiones, usar la lógica existente del Excel
        if (codigoHijo.contains('-')) {
            return parseCodigoHijo(codigoHijo)
        }

        // Formato API sin guiones: ej. "N06182AM" → padre="N06182", color="AM"
        // El codigoPadre son los primeros 6 caracteres (letra + número de 5 dígitos)
        return if (codigoHijo.length >= 6) {
            Pair(codigoHijo.substring(0, 6), codigoHijo.substring(6))
        } else {
            // Código demasiado corto: usarlo como padre y color vacío
            Log.w(TAG, "[DIAG] Código API muy corto para parsear: $codigoHijo")
            Pair(codigoHijo, "")
        }
    }

    private fun parseCodigoHijo(codigoHijo: String?): Pair<String, String> {
        // Validar que el código hijo no sea nulo o vacío
        if (codigoHijo.isNullOrEmpty()) {
            return Pair("", "")
        }

        // Supongamos que el formato es "PADRE-COLOR-TALLA"
        val partes = codigoHijo.split("-")
        if (partes.size >= 3) {
            val padre = partes[0]
            val color = partes[1]
            val talla = partes[2]
            return Pair(padre, color)
        } else {
            // Si no se puede parsear correctamente, devolver valores por defecto
            Log.w(TAG, "[DIAG] Código hijo no tiene formato esperado: $codigoHijo")
            return Pair("", "")
        }
    }

    data class ExcelImportStats(
        val totalDataRowsRead: Int = 0,
        val totalUbicacionRowsRead: Int = 0,
        val productosInserted: Int = 0,
        val ubicacionesInserted: Int = 0,
        val padresWithUbicacion: Int = 0,
        val padresWithoutUbicacion: Int = 0,
        val codesWithUncertainParsing: Int = 0,
        val duplicatesFound: Int = 0,
        val skippedRows: Int = 0,
        val uncertainCodes: List<String> = emptyList(),
        val summaryMessage: String = ""
    )
}