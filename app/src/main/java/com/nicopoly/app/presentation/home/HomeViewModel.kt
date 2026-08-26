package com.nicopoly.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicopoly.app.domain.usecase.GetCategories
import com.nicopoly.app.domain.usecase.GetFeaturedProducts
import com.nicopoly.app.domain.usecase.GetNewArrivals
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel de la pantalla Home.
 * Carga productos destacados, nuevos ingresos y categorías.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFeaturedProducts: GetFeaturedProducts,
    private val getNewArrivals: GetNewArrivals,
    private val getCategories: GetCategories
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val featured = getFeaturedProducts().first()
                val newItems = getNewArrivals().first()
                val categories = getCategories().first()

                _uiState.update {
                    it.copy(
                        featuredProducts = featured,
                        newArrivals = newItems,
                        categories = categories,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No se pudieron cargar los productos. Intenta de nuevo."
                    )
                }
            }
        }
    }

    /** Recarga los datos (pull-to-refresh). */
    fun refresh() {
        loadHomeData()
    }
}
