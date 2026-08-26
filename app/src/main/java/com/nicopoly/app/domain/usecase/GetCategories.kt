package com.nicopoly.app.domain.usecase

import com.nicopoly.app.domain.repository.CategoryInfo
import com.nicopoly.app.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case para obtener las categorías disponibles con su cantidad de productos.
 */
class GetCategories @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<List<CategoryInfo>> = repository.getCategories()
}
