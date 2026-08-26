package com.nicopoly.app.data.repository

import android.util.Log
import com.nicopoly.app.data.local.dao.ProductoDao
import com.nicopoly.app.data.local.dao.UbicacionDao
import com.nicopoly.app.domain.model.StockQueryResult
import com.nicopoly.app.domain.model.StockResultFull
import com.nicopoly.app.domain.model.VarianteStock
import com.nicopoly.app.domain.repository.StockRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementación de StockRepository que consulta la base de datos Room.
 *
 * Lee los datos reales importados desde el Excel y los expone
 * a través del contrato StockRepository, permitiendo que las pantallas
 * no necesiten cambios si posteriormente se reemplaza por una API de SAP.
 *
 * @param productoDao DAO para consultar productos en Room.
 * @param ubicacionDao DAO para consultar ubicaciones en Room.
 */
@ViewModelScoped
class RoomStockRepository @Inject constructor(
    private val productoDao: ProductoDao,
    private val ubicacionDao: UbicacionDao
) : StockRepository {

    companion object {
        private const val TAG = "RoomStockRepository"
    }

    override fun getStockBySku(sku: String): Flow<StockQueryResult> = flow {
        if (sku.isBlank()) {
            throw IllegalArgumentException("El SKU no puede estar vacío")
        }

        val skuUpper = sku.uppercase()
        Log.w(TAG, "[DIAG] Buscando SKU='$skuUpper'")

        // Consultar el producto por código hijo (SKU)
        var producto = productoDao.getProductoByCodigoHijoOnce(skuUpper)

        if (producto == null) {
            // DIAGNÓSTICO: buscar con LIKE para ver si existe algo parecido
            val similares = productoDao.searchByPattern("%${skuUpper}%")
            Log.w(TAG, "[DIAG] SKU '$skuUpper' NO encontrado exactamente. Similares (${similares.size}): ${similares.map { it.codigoHijo }}")

            // Fallback: buscar por coincidencia parcial en codigoHijo
            producto = productoDao.getProductoBySkuPartial(skuUpper)
            if (producto != null) {
                Log.w(TAG, "[DIAG] SKU '$skuUpper' encontrado parcialmente → codigoHijo='${producto.codigoHijo}'")
            } else {
                throw NoSuchElementException("SKU no encontrado: $sku")
            }
        }
        Log.w(TAG, "[DIAG] SKU '$skuUpper' ENCONTRADO → stockBodega=${producto.stockBodega}")

        // Consultar la ubicación del código padre
        val ubicacion = ubicacionDao.getUbicacionByCodigoPadreOnce(producto.codigoPadre)

        emit(
            StockQueryResult(
                sku = producto.codigoHijo,
                description = buildDescription(producto),
                stockAvailable = producto.stockBodega,
                hasStock = producto.stockBodega > 0
            )
        )
    }

    override fun getStockBySkuSafe(sku: String): Flow<Result<StockQueryResult>> = flow {
        try {
            getStockBySku(sku).collect { result ->
                emit(Result.success(result))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Construye una descripción legible a partir de los datos del producto.
     */
    private fun buildDescription(producto: com.nicopoly.app.data.local.entity.ProductoEntity): String {
        val partes = mutableListOf<String>()

        if (producto.categoria.isNotBlank()) {
            partes.add(producto.categoria)
        }

        if (producto.color.isNotBlank() && producto.talla.isNotBlank()) {
            partes.add("Color: ${producto.color} - Talla: ${producto.talla}")
        } else if (producto.color.isNotBlank()) {
            partes.add("Color: ${producto.color}")
        } else if (producto.talla.isNotBlank()) {
            partes.add("Talla: ${producto.talla}")
        }

        return if (partes.isNotEmpty()) {
            partes.joinToString(" - ")
        } else {
            "Prenda Nicopoly - ${producto.codigoHijo}"
        }
    }

    fun getFullStockBySku(sku: String): Flow<StockResultFull> = flow {
        if (sku.isBlank()) {
            throw IllegalArgumentException("El SKU no puede estar vacío")
        }

        val skuUpper = sku.uppercase()
        Log.w(TAG, "[DIAG] Buscando SKU='$skuUpper' para FullStock")

        // Consultar el producto por código hijo (SKU)
        var producto = productoDao.getProductoByCodigoHijoOnce(skuUpper)

        if (producto == null) {
            Log.w(TAG, "[DIAG] SKU '$skuUpper' NO encontrado exactamente para FullStock")

            // Fallback: buscar por coincidencia parcial en codigoHijo
            producto = productoDao.getProductoBySkuPartial(skuUpper)
            if (producto != null) {
                Log.w(TAG, "[DIAG] SKU '$skuUpper' encontrado parcialmente → codigoHijo='${producto.codigoHijo}'")
            } else {
                throw NoSuchElementException("SKU no encontrado: $sku")
            }
        }

        // Consultar la ubicación del código padre
        val ubicacion = ubicacionDao.getUbicacionByCodigoPadreOnce(producto.codigoPadre)

        // Obtener todas las variantes de este código padre
        val variantes = productoDao.getProductosByCodigoPadre(producto.codigoPadre)
            .map {
                VarianteStock(
                    codigoHijo = it.codigoHijo,
                    color = it.color,
                    talla = it.talla,
                    stockBodega = it.stockBodega,
                    stockProvi1 = it.stockProvi1,
                    stockFilomena = it.stockFilomena,
                    stockProvi2 = it.stockProvi2,
                    t060 = it.t060
                )
            }

        emit(
            StockResultFull(
                codigoPadre = producto.codigoPadre,
                temporada = producto.temporada,
                ubicacion = ubicacion?.ubicacion,
                categoria = producto.categoria,
                precioTiendas = producto.precioTiendas,
                precioInicial = producto.precioInicial,
                precioMayor = producto.precioMayor,
                t060Total = variantes.sumOf { it.t060 },
                casaMatrizTotal = variantes.sumOf { it.stockBodega },
                t011Total = variantes.sumOf { it.stockFilomena },
                variantes = variantes
            )
        )
    }

    override fun getStockFullBySkuSafe(sku: String): Flow<Result<StockResultFull>> = flow {
        try {
            getStockFullBySku(sku).collect { result ->
                emit(Result.success(result))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getStockFullBySku(sku: String): Flow<StockResultFull> = flow {
        if (sku.isBlank()) {
            throw IllegalArgumentException("El SKU no puede estar vacío")
        }

        val skuUpper = sku.uppercase()
        Log.w(TAG, "[DIAG] Buscando SKU='$skuUpper' para FullStock")

        // Consultar el producto por código hijo (SKU)
        var producto = productoDao.getProductoByCodigoHijoOnce(skuUpper)

        if (producto == null) {
            Log.w(TAG, "[DIAG] SKU '$skuUpper' NO encontrado exactamente para FullStock")

            // Fallback: buscar por coincidencia parcial en codigoHijo
            producto = productoDao.getProductoBySkuPartial(skuUpper)
            if (producto != null) {
                Log.w(TAG, "[DIAG] SKU '$skuUpper' encontrado parcialmente → codigoHijo='${producto.codigoHijo}'")
            } else {
                throw NoSuchElementException("SKU no encontrado: $sku")
            }
        }

        // Consultar la ubicación del código padre
        val ubicacion = ubicacionDao.getUbicacionByCodigoPadreOnce(producto.codigoPadre)

        // Obtener todas las variantes de este código padre
        val variantes = productoDao.getProductosByCodigoPadre(producto.codigoPadre)
            .map {
                VarianteStock(
                    codigoHijo = it.codigoHijo,
                    color = it.color,
                    talla = it.talla,
                    stockBodega = it.stockBodega,
                    stockProvi1 = it.stockProvi1,
                    stockFilomena = it.stockFilomena,
                    stockProvi2 = it.stockProvi2,
                    t060 = it.t060
                )
            }

        emit(
            StockResultFull(
                codigoPadre = producto.codigoPadre,
                temporada = producto.temporada,
                ubicacion = ubicacion?.ubicacion,
                categoria = producto.categoria,
                precioTiendas = producto.precioTiendas,
                precioInicial = producto.precioInicial,
                precioMayor = producto.precioMayor,
                t060Total = variantes.sumOf { it.t060 },
                casaMatrizTotal = variantes.sumOf { it.stockBodega },
                t011Total = variantes.sumOf { it.stockFilomena },
                variantes = variantes
            )
        )
    }
}