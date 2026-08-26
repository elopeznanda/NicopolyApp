package com.nicopoly.app.domain.usecase

import com.nicopoly.app.domain.model.Category
import com.nicopoly.app.domain.model.Product
import com.nicopoly.app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case para obtener productos filtrados por categoría.
 */
class GetProductsByCategory @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(category: Category): Flow<List<Product>> =
        repository.getProductsByCategory(category)
}
