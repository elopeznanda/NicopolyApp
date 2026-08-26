package com.nicopoly.app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel de la pantalla Splash.
 *
 * Controla el tiempo de espera antes de navegar a la pantalla principal.
 * Ya no realiza importación automática del Excel; la importación es manual.
 */
@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _shouldNavigate = MutableStateFlow(false)
    val shouldNavigate: StateFlow<Boolean> = _shouldNavigate.asStateFlow()

    init {
        viewModelScope.launch {
            delay(SPLASH_DELAY_MS)
            _shouldNavigate.value = true
        }
    }

    companion object {
        private const val SPLASH_DELAY_MS = 2000L
    }
}
