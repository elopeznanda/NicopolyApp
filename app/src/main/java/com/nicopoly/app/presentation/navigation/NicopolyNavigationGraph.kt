package com.nicopoly.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.nicopoly.app.presentation.screens.catalog.CatalogScreen
import com.nicopoly.app.presentation.screens.home.HomeScreen
import com.nicopoly.app.presentation.screens.login.LoginScreen
import com.nicopoly.app.presentation.screens.profile.ProfileScreen
import com.nicopoly.app.presentation.screens.searchsku.SearchSkuScreen
import com.nicopoly.app.presentation.screens.splash.SplashScreen
import com.nicopoly.app.presentation.screens.stockresult.StockResultScreen

/**
 * Grafo de navegación principal de Nicopoly.
 *
 * Estructura:
 * ```
 * Splash → MainContainer (Scaffold + BottomNav)
 *                    ├─ Home
 *                    ├─ Catalog
 *                    └─ Profile
 * ProductDetail (navegable desde Home y Catalog, dentro del mismo gráfico)
 * ```
 *
 * La navegación Splash → Main es _one-shot_: una vez que el usuario
 * llega al contenedor principal, el splash nunca se vuelve a mostrar.
 *
 * @param modifier Modificador para personalizar el NavHost.
 * @param navController Controlador de navegación inyectado o recordado.
 */
@Composable
fun NicopolyNavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {

        // ──────────────────────────────────────────────
        // SPLASH — Pantalla de carga inicial
        // ──────────────────────────────────────────────
        composable(route = Screen.Splash.route) { backStackEntry ->
            val splashViewModel = hiltViewModel<com.nicopoly.app.presentation.splash.SplashViewModel>()
            val shouldNavigate by splashViewModel.shouldNavigate.collectAsStateWithLifecycle()

            SplashScreen(
                onNavigateToMain = {
                    // v0.3: Splash → SearchSku (Login temporalmente oculto)
                    navController.navigate(Screen.SearchSku.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                shouldNavigate = shouldNavigate
            )
        }

        // ──────────────────────────────────────────────
        // MAIN — Contenedor principal con Scaffold + BottomNav
        // Usa un NavGraph anidado para que el Scaffold persista entre tabs
        // ──────────────────────────────────────────────
        composable(route = Screen.Main.route) {
            val innerNavController = rememberNavController()

            androidx.compose.material3.Scaffold(
                bottomBar = {
                    com.nicopoly.app.presentation.components.NicopolyBottomNavigationBar(
                        navController = innerNavController
                    )
                }
            ) { innerPadding ->
                NavHost(
                    navController = innerNavController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    // HOME — Pantalla de inicio
                    composable(route = Screen.Home.route) {
                        HomeScreen(
                            onProductClick = { productId ->
                                innerNavController.navigate(Screen.ProductDetail(productId).route)
                            }
                        )
                    }

                    // CATALOG — Catálogo completo con búsqueda y filtros
                    composable(route = Screen.Catalog.route) {
                        CatalogScreen(
                            onProductClick = { productId ->
                                innerNavController.navigate(Screen.ProductDetail(productId).route)
                            }
                        )
                    }

                    // PROFILE — Perfil del usuario (placeholder en v0.1)
                    composable(route = Screen.Profile.route) {
                        ProfileScreen()
                    }

                    // PRODUCT DETAIL — Detalle de un producto específico
                    composable(
                        route = Screen.ProductDetail("").route,
                        arguments = listOf(
                            navArgument("productId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->
                        val productId = backStackEntry.arguments?.getString("productId") ?: ""
                        // En v0.1, la pantalla de detalle aún no existe;
                        // se reutiliza el catálogo como placeholder temporal.
                        CatalogScreen(
                            onProductClick = { _ -> }
                        )
                    }
                }
            }
        }

        // ========================================================================
        // EMPLOYEE FLOW — v0.2 Flujo exclusivo para trabajadores
        // ========================================================================

        // LOGIN — Autenticación del empleado
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.SearchSku.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // SEARCH SKU — Búsqueda de stock por SKU
        composable(route = Screen.SearchSku.route) {
            SearchSkuScreen(
                onNavigateToResult = { sku ->
                    navController.navigate(Screen.StockResult(sku).route)
                }
            )
        }

        // STOCK RESULT — Resultado de consulta
        composable(
            route = "stock_result/{sku}",
            arguments = listOf(
                navArgument("sku") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val sku = backStackEntry.arguments?.getString("sku") ?: ""
            StockResultScreen(
                sku = sku,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
