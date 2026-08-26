package com.nicopoly.app.domain.usecase

import com.nicopoly.app.domain.model.Product
import com.nicopoly.app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case para obtener todos los productos del catálogo.
 * Sigue el principio de responsabilidad única (SRP).
 */
class GetAllProducts @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<List<Product>> = repository.getAllProducts()
}
