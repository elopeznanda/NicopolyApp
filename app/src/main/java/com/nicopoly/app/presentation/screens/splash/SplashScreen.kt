package com.nicopoly.app.presentation.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.nicopoly.app.R
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Pantalla Splash de Nicopoly.
 *
 * Muestra el logo y un indicador de carga mientras la aplicación
 * inicializa los componentes necesarios. Después de un delay controlado
 * por [SplashViewModel], navega automáticamente a la pantalla principal.
 *
 * @param onNavigateToMain Callback invocado cuando el splash ha terminado
 *                         y se debe navegar al contenedor principal.
 * @param shouldNavigate Estado que indica si la navegación puede proceder.
 */
@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit,
    shouldNavigate: Boolean
) {
    // Navegar automáticamente cuando el ViewModel lo indique
    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate) {
            onNavigateToMain()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo / Nombre de la marca
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(800)) +
                    scaleIn(initialScale = 0.8f, animationSpec = tween(800))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
            ) {
                // Nueva imagen de logo
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_logo),
                    contentDescription = "Logo Nicopoly",
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .aspectRatio(1f)
                )

            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Indicador de carga
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(400, delayMillis = 400))
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
        }
    }
}
