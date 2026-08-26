package com.nicopoly.app.domain.repository

import com.nicopoly.app.domain.model.StockQueryResult
import com.nicopoly.app.domain.model.StockResultFull
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de consulta de stock para empleados.
 *
 * Define el contrato que debe cumplir la capa de datos para consultar
 * el inventario disponible en el Centro de Distribución por SKU.
 * Está diseñado para permitir reemplazar la implementación fake por
 * una real (SAP/API) sin tocar la lógica de presentación ni dominio.
 */
interface StockRepository {

    /**
     * Consulta el stock disponible para un SKU específico.
     *
     * @param sku Código SKU del producto a consultar.
     * @return Flow con el resultado de la consulta.
     */
    fun getStockBySku(sku: String): Flow<StockQueryResult>

    /**
     * Consulta el stock disponible y maneja errores mediante un Result wrapper.
     *
     * @param sku Código SKU del producto a consultar.
     * @return Flow<Result> donde Success contiene el resultado y Failure contiene el error.
     */
    fun getStockBySkuSafe(sku: String): Flow<Result<StockQueryResult>>

    /**
     * Consulta el stock completo para un SKU específico, incluyendo:
     * - Código padre
     * - Ubicación en bodega
     * - Categoría y precios
     * - Todas las variantes del mismo código padre con sus stocks individuales
     *
     * @param sku Código SKU del producto a consultar.
     * @return Flow con el resultado completo de la consulta.
     */
    fun getStockFullBySku(sku: String): Flow<StockResultFull>

    /**
     * Consulta el stock completo y maneja errores mediante un Result wrapper.
     *
     * @param sku Código SKU del producto a consultar.
     * @return Flow<Result> donde Success contiene el resultado completo y Failure contiene el error.
     */
    fun getStockFullBySkuSafe(sku: String): Flow<Result<StockResultFull>>
}
