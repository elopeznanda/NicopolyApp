package com.nicopoly.app.presentation.searchsku

import com.nicopoly.app.domain.model.StockQueryResult

/**
 * Estado de UI para la pantalla de búsqueda por SKU.
 *
 * @param isLoading Indica si se está procesando la consulta.
 * @param errorMessage Mensaje de error a mostrar, o null si no hay error.
 * @param stockResult Resultado de la consulta de stock, o null si aún no se ha consultado.
 * @param isImporting Indica si se está importando un Excel.
 * @param importProgress Progreso de la importación (porcentaje 0-100).
 * @param importStage Mensaje descriptivo del estado actual de la importación.
 * @param importResult Mensaje de resultado de la importación, o null si no hay resultado.
 * @param importError Mensaje de error de la importación, o null si no hay error.
 * @param databaseIsEmpty Indica si la base de datos está vacía (sin productos).
 */
data class SearchSkuUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val stockResult: StockQueryResult? = null,
    /**
     * Contador que se incrementa con cada búsqueda exitosa.
     * Se usa como clave en LaunchedEffect para evitar
     * re-navegaciones al restaurar SearchSkuScreen desde el back stack.
     */
    val searchCounter: Int = 0,
    val isImporting: Boolean = false,
    val importProgress: Int = 0,
    val importStage: String = "",
    val importResult: String? = null,
    val importError: String? = null,
    val databaseIsEmpty: Boolean = false,
    val lastUpdateInfo: String? = null
)
