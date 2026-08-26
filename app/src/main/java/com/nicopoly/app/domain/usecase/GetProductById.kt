package com.nicopoly.app.domain.usecase

import com.nicopoly.app.domain.model.Product
import com.nicopoly.app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case para obtener un producto específico por su ID.
 */
class GetProductById @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(id: String): Flow<Product?> = repository.getProductById(id)
}
