package com.nicopoly.app.presentation.stockresult

import com.nicopoly.app.domain.model.StockQueryResult
import com.nicopoly.app.domain.model.StockResultFull

/**
 * Estado de UI para la pantalla de resultado de consulta de stock.
 *
 * @param isLoading Indica si se está cargando la información del producto.
 * @param stockResult Resultado simple de la consulta (legacy, mantenido por compatibilidad).
 * @param stockResultFull Resultado completo con código padre, ubicación, precios y tabla de variantes.
 * @param errorMessage Mensaje de error a mostrar, o null si no hay error.
 */
data class StockResultUiState(
    val isLoading: Boolean = false,
    val stockResult: StockQueryResult? = null,
    val stockResultFull: StockResultFull? = null,
    val errorMessage: String? = null
)
