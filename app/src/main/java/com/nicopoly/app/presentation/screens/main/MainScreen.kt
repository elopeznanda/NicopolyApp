package com.nicopoly.app.presentation.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.nicopoly.app.presentation.components.NicopolyBottomNavigationBar

/**
 * Pantalla contenedor principal de Nicopoly.
 * Proporciona el Scaffold con la barra de navegación inferior persistente.
 *
 * El contenido de cada tab (Home, Catalog, Profile) se renderiza dentro
 * del área de contenido del Scaffold, aplicando los innerPadding para que
 * el contenido no quede oculto detrás de la BottomNavigationBar.
 *
 * @param navController Controlador de navegación interno para las pantallas hijas.
 * @param content Composable que contiene el NavHost con las pantallas hijas.
 */
@Composable
fun MainScreen(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = {
            NicopolyBottomNavigationBar(
                navController = navController,
                modifier = Modifier
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}
