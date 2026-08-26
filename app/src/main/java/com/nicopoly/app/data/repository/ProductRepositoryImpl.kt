package com.nicopoly.app.data.repository

import com.nicopoly.app.data.local.dao.ProductoDao
import com.nicopoly.app.domain.model.Category
import com.nicopoly.app.domain.model.Product
import com.nicopoly.app.domain.repository.CategoryInfo
import com.nicopoly.app.domain.repository.ProductRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Implementación del repositorio de productos.
 */
@ViewModelScoped
class ProductRepositoryImpl @Inject constructor(
    private val productoDao: ProductoDao
) : ProductRepository {

    override fun getAllProducts(): Flow<List<Product>> = flowOf(SampleProducts.getAll())

    override fun getProductsByCategory(category: Category): Flow<List<Product>> =
        flowOf(SampleProducts.getByCategory(category))

    override fun getFeaturedProducts(): Flow<List<Product>> =
        flowOf(SampleProducts.getFeatured())

    override fun getNewArrivals(): Flow<List<Product>> =
        flowOf(SampleProducts.getNewArrivals())

    override fun searchProducts(query: String): Flow<List<Product>> =
        flowOf(SampleProducts.search(query))

    override fun getProductById(id: String): Flow<Product?> = flowOf(SampleProducts.getById(id))

    override fun getCategories(): Flow<List<CategoryInfo>> {
        val allProducts = SampleProducts.getAll()
        val categories = Category.entries.map { category ->
            CategoryInfo(
                category = category,
                productCount = allProducts.count { it.category == category }
            )
        }.filter { it.productCount > 0 }
        return flowOf(categories)
    }

    override fun getProductBySku(sku: String): Flow<Product> = flow {
        val productoEntity = productoDao.getProductoByCodigoHijoOnce(sku)
        if (productoEntity == null) {
            throw NoSuchElementException("SKU no encontrado: $sku")
        }

        // Convertir ProductoEntity a Product para que sea compatible con la UI
        emit(
            Product(
                id = productoEntity.codigoPadre,
                name = "",
                price = productoEntity.precioTiendas,
                originalPrice = productoEntity.precioInicial,
                imageUrl = "",
                description = "",
                category = Category.VESTIDOS,
                sizes = emptyList(),
                colors = emptyList(),
                rating = 0f,
                reviewCount = 0,
                isNew = false,
                isFeatured = false,
                isOnSale = false,
                precioTiendas = productoEntity.precioTiendas,
                precioInicial = productoEntity.precioInicial
            )
        )
    }
}