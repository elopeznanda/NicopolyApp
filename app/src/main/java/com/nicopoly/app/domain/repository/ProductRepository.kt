package com.nicopoly.app.domain.repository

import com.nicopoly.app.domain.model.Category
import com.nicopoly.app.domain.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de productos.
 * Definido en la capa domain para mantener el desacoplamiento.
 */
interface ProductRepository {

    /**
     * Retorna el flujo de todos los productos disponibles.
     */
    fun getAllProducts(): Flow<List<Product>>

    /**
     * Retorna el flujo de productos filtrados por categoría.
     */
    fun getProductsByCategory(category: Category): Flow<List<Product>>

    /**
     * Retorna el flujo de productos destacados.
     */
    fun getFeaturedProducts(): Flow<List<Product>>

    /**
     * Retorna el flujo de productos recién llegados.
     */
    fun getNewArrivals(): Flow<List<Product>>

    /**
     * Busca productos por término de búsqueda.
     */
    fun searchProducts(query: String): Flow<List<Product>>

    /**
     * Retorna un producto específico por su ID.
     */
    fun getProductById(id: String): Flow<Product?>

    /**
     * Retorna las categorías disponibles con cantidad de productos.
     */
    fun getCategories(): Flow<List<CategoryInfo>>

    /**
     * Retorna un producto específico por su código SKU.
     */
    fun getProductBySku(sku: String): Flow<Product>
}

/**
 * Información resumida de una categoría para la UI.
 */
data class CategoryInfo(
    val category: Category,
    val productCount: Int
)
