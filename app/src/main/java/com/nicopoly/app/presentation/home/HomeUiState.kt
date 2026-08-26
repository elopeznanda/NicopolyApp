package com.nicopoly.app.presentation.home

import com.nicopoly.app.domain.model.Product
import com.nicopoly.app.domain.repository.CategoryInfo

/**
 * Estado de la pantalla Home.
 */
data class HomeUiState(
    val featuredProducts: List<Product> = emptyList(),
    val newArrivals: List<Product> = emptyList(),
    val categories: List<CategoryInfo> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
