package com.nicopoly.app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicopoly.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla Login.
 *
 * Gestiona el estado de autenticación y maneja la lógica de inicio de sesión.
 * Utiliza el repositorio de autenticación para realizar la operación, permitiendo
 * reemplazar fácilmente la implementación fake por una real en el futuro.
 *
 * @property authRepository Repositorio de autenticación inyectado vía Hilt.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    /**
     * Inicia el proceso de autenticación.
     *
     * @param email Correo electrónico del empleado.
     * @param password Contraseña del empleado.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            authRepository.login(email, password)
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error de autenticación"
                    )
                }
                .collectLatest { result ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginSuccess = result.success,
                        errorMessage = if (result.success) null else result.message
                    )
                }
        }
    }

    /**
     * Reinicia el estado del login. Útil para limpiar el estado después de navegar.
     */
    fun resetState() {
        _uiState.value = LoginUiState()
    }
}
