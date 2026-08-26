package com.nicopoly.app.presentation.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicopoly.app.domain.model.Category
import com.nicopoly.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel de la pantalla Catálogo.
 * Maneja la carga de productos, filtrado por categoría y búsqueda.
 */
@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getAllProducts: GetAllProducts,
    private val getProductsByCategory: GetProductsByCategory,
    private val searchProducts: SearchProducts,
    private val getCategories: GetCategories
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogUiState())
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        loadCatalog()
    }

    private fun loadCatalog() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val selectedCategory = _uiState.value.selectedCategory
                val query = _uiState.value.searchQuery

                val products = when {
                    _uiState.value.isSearchActive && query.isNotBlank() -> {
                        searchProducts(query).first()
                    }
                    selectedCategory != null -> {
                        getProductsByCategory(selectedCategory).first()
                    }
                    else -> {
                        getAllProducts().first()
                    }
                }

                val categories = getCategories().first()

                _uiState.update {
                    it.copy(
                        products = products,
                        categories = categories,
                        isLoading = false,
                        isEmpty = products.isEmpty()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No se pudieron cargar los productos del catálogo."
                    )
                }
            }
        }
    }

    /** Filtra por categoría. null para mostrar todos. */
    fun selectCategory(category: Category?) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                isSearchActive = false,
                searchQuery = ""
            )
        }
        loadCatalog()
    }

    /** Activa o desactiva el modo búsqueda. */
    fun setSearchActive(isActive: Boolean) {
        _uiState.update { it.copy(isSearchActive = isActive) }
        if (!isActive) {
            _uiState.update { it.copy(searchQuery = "") }
            loadCatalog()
        }
    }

    /** Actualiza el query de búsqueda y filtra productos. */
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isNotBlank()) {
            _uiState.update { it.copy(isSearchActive = true) }
            loadCatalog()
        } else if (!_uiState.value.isSearchActive) {
            loadCatalog()
        }
    }

    /** Recarga los datos. */
    fun refresh() {
        loadCatalog()
    }
}
