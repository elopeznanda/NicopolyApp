package com.nicopoly.app.data.repository

import com.nicopoly.app.domain.model.LoginResult
import com.nicopoly.app.domain.repository.AuthRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementación fake del repositorio de autenticación.
 *
 * Simula la autenticación sin conectar con ningún backend real.
 * En futuras versiones se reemplazará por una implementación que
 * consulte la API de SAP u otro servicio externo.
 *
 * **Credenciales de prueba:**
 * - Email: cualquier valor no vacío
 * - Password: cualquier valor no vacío
 *
 * Si ambos campos están vacíos, el login fallará.
 */
@ViewModelScoped
class FakeAuthRepository @Inject constructor() : AuthRepository {

    private var isAuthenticated = false

    override fun login(email: String, password: String): Flow<LoginResult> = flow {
        // Simular delay de red
        kotlinx.coroutines.delay(1000)

        if (email.isBlank() || password.isBlank()) {
            emit(LoginResult(success = false, message = "Complete todos los campos"))
        } else {
            isAuthenticated = true
            emit(LoginResult(success = true, message = "Autenticación exitosa"))
        }
    }

    override fun isLoggedIn(): Flow<Boolean> = flow {
        emit(isAuthenticated)
    }

    override fun logout() {
        isAuthenticated = false
    }
}
