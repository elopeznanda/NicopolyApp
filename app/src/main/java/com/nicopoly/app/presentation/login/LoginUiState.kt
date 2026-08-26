package com.nicopoly.app.presentation.login

/**
 * Estado de UI para la pantalla Login.
 *
 * @param isLoading Indica si se está procesando la autenticación.
 * @param errorMessage Mensaje de error a mostrar, o null si no hay error.
 * @param loginSuccess Indica si el login fue exitoso y se debe navegar.
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false
)
