package com.nicopoly.app.presentation.catalog

import com.nicopoly.app.domain.model.Category
import com.nicopoly.app.domain.model.Product
import com.nicopoly.app.domain.repository.CategoryInfo

/**
 * Estado de la pantalla Catálogo.
 */
data class CatalogUiState(
    val products: List<Product> = emptyList(),
    val categories: List<CategoryInfo> = emptyList(),
    val selectedCategory: Category? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isEmpty: Boolean = false
)
