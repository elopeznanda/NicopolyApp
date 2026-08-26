package com.nicopoly.app.domain.model

/**
 * Resultado de la consulta de stock por SKU.
 *
 * @property sku Código SKU del producto consultado.
 * @property description Descripción del producto.
 * @property stockAvailable Cantidad disponible en el Centro de Distribución.
 * @property hasStock Indica si hay unidades disponibles.
 */
data class StockQueryResult(
    val sku: String,
    val description: String,
    val stockAvailable: Int,
    val hasStock: Boolean = stockAvailable > 0
)
