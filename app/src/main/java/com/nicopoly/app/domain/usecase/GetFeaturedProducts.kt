package com.nicopoly.app.domain.usecase

import com.nicopoly.app.domain.model.Product
import com.nicopoly.app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case para obtener los productos destacados.
 */
class GetFeaturedProducts @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<List<Product>> = repository.getFeaturedProducts()
}
