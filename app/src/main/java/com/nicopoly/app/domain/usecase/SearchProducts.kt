package com.nicopoly.app.domain.usecase

import com.nicopoly.app.domain.model.Product
import com.nicopoly.app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case para buscar productos por término de búsqueda.
 */
class SearchProducts @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(query: String): Flow<List<Product>> = repository.searchProducts(query)
}
