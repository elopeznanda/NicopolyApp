package com.nicopoly.app.presentation.stockresult

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicopoly.app.domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de resultado de consulta de stock.
 *
 * Recibe un SKU como parámetro y consulta el repositorio para obtener
 * la información completa: código padre, ubicación, categoría, precios
 * y tabla de todas las variantes con sus stocks individuales.
 *
 * @property stockRepository Repositorio de stock inyectado vía Hilt.
 */
@HiltViewModel
class StockResultViewModel @Inject constructor(
    private val stockRepository: StockRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StockResultUiState())
    val uiState: StateFlow<StockResultUiState> = _uiState

    /**
     * Consulta el stock completo para un SKU específico.
     *
     * @param sku Código SKU del producto a consultar.
     */
    companion object {
        private const val TAG = "StockResultViewModel"
    }

    fun loadStock(sku: String) {
        Log.w(TAG, "[DIAG] loadStock llamado con sku='$sku'")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                stockResult = null,
                stockResultFull = null,
                errorMessage = null
            )

            stockRepository.getStockFullBySku(sku)
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al consultar stock"
                    )
                }
                .collectLatest { result ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        stockResultFull = result
                    )
                }
        }
    }
}
