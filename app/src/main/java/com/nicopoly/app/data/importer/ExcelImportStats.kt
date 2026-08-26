package com.nicopoly.app.data.importer

/**
 * Estadísticas del proceso de importación desde Excel.
 *
 * Proporciona un resumen completo de lo que ocurrió durante la importación,
 * incluyendo conteos de éxito, errores y casos que requieren revisión manual.
 */
data class ExcelImportStats(
    /** Total de filas leídas de la hoja DATOS (excluyendo header). */
    val totalDataRowsRead: Int = 0,

    /** Total de filas leídas de la hoja UBICACIONES (excluyendo header). */
    val totalUbicacionRowsRead: Int = 0,

    /** Productos exitosamente insertados en Room. */
    val productosInserted: Int = 0,

    /** Ubicaciones exitosamente insertadas en Room. */
    val ubicacionesInserted: Int = 0,

    /** Códigos padre que encontraron su ubicación en UBICACIONES. */
    val padresWithUbicacion: Int = 0,

    /** Códigos padre que NO encontraron ubicación en UBICACIONES. */
    val padresWithoutUbicacion: Int = 0,

    /** Códigos hijo cuyo color/talla no pudo determinarse de forma segura. */
    val codesWithUncertainParsing: Int = 0,

    /** Duplicados encontrados en la hoja DATOS. */
    val duplicatesFound: Int = 0,

    /** Filas saltadas por estar vacías o tener datos inválidos. */
    val skippedRows: Int = 0,

    /** Lista de códigos con parsing incierto (para revisión). */
    val uncertainCodes: List<String> = emptyList(),

    /** Mensaje resumen del proceso. */
    val summaryMessage: String = ""
) {
    override fun toString(): String {
        return """
            ════════════════════════════════════════
            RESULTADO IMPORTACIÓN EXCEL
            ════════════════════════════════════════
            Filas DATOS leídas:       $totalDataRowsRead
            Filas UBICACIONES leídas: $totalUbicacionRowsRead
            ────────────────────────────────────────
            Productos insertados:     $productosInserted
            Ubicaciones insertadas:   $ubicacionesInserted
            ────────────────────────────────────────
            Padres con ubicación:     $padresWithUbicacion
            Padres sin ubicación:     $padresWithoutUbicacion
            Parsing incierto:         $codesWithUncertainParsing
            Duplicados encontrados:   $duplicatesFound
            Filas saltadas:           $skippedRows
            ════════════════════════════════════════
        """.trimIndent()
    }
}
