package com.nicopoly.app.domain.repository

import com.nicopoly.app.domain.model.LoginResult
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de autenticación para empleados.
 *
 * Define el contrato que debe cumplir la capa de datos para manejar
 * el login de trabajadores. Está diseñado para permitir reemplazar
 * la implementación fake por una real (SAP/API) sin tocar la lógica
 * de presentación ni dominio.
 */
interface AuthRepository {

    /**
     * Autentica un empleado con correo y contraseña.
     *
     * @param email Correo electrónico del empleado.
     * @param password Contraseña.
     * @return Flow con el resultado de la autenticación.
     */
    fun login(email: String, password: String): Flow<LoginResult>

    /**
     * Verifica si existe una sesión activa.
     *
     * @return Flow<Boolean> indicando si el empleado está autenticado.
     */
    fun isLoggedIn(): Flow<Boolean>

    /**
     * Cierra la sesión del empleado actual.
     */
    fun logout()
}
