package com.nicopoly.app.domain.model

/**
 * Resultado de la autenticación del empleado.
 *
 * @property success Indica si la autenticación fue exitosa.
 * @property message Mensaje descriptivo del resultado.
 */
data class LoginResult(
    val success: Boolean,
    val message: String
)
