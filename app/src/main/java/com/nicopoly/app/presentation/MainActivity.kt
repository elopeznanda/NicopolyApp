package com.nicopoly.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import com.nicopoly.app.presentation.navigation.NicopolyNavigationGraph
import com.nicopoly.app.presentation.theme.NicopolyTheme

/**
 * Activity principal de la aplicación.
 * Punto de entrada único que inicializa el grafo de navegación Compose.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NicopolyTheme {
                NicopolyNavigationGraph()
            }
        }
    }
}
