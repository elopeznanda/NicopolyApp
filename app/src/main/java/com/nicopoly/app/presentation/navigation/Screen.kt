package com.nicopoly.app.presentation.navigation

/**
 * Define las rutas de navegación de la aplicación Nicopoly.
 *
 * Cada pantalla tiene una ruta única que se utiliza como identificador
 * en el grafo de navegación. Esto facilita la gestión de rutas y permite
 * agregar parámetros en el futuro sin romper la compatibilidad.
 *
 * @property route Ruta única que identifica la pantalla en el NavHost.
 * @property label Texto accesible para lectores de pantalla.
 */
sealed class Screen(open val route: String, val label: String) {

    /**
     * Pantalla Splash — pantalla de carga inicial.
     * Se muestra brevemente antes de navegar a la pantalla principal.
     */
    data object Splash : Screen(route = "splash", label = "Splash")

    /**
     * Contenedor principal con BottomNavigationBar.
     * Agrega un sufijo especial para distinguirlo de las pantallas hijas.
     */
    data object Main : Screen(route = "main_container", label = "Main Container")

    /**
     * Pantalla Inicio — vista principal con productos destacados,
     * recién llegados y acceso rápido a categorías.
     */
    data object Home : Screen(route = "home", label = "Inicio")

    /**
     * Pantalla Catálogo — exploración completa del catálogo con
     * búsqueda y filtros por categoría.
     */
    data object Catalog : Screen(route = "catalog", label = "Catálogo")

    /**
     * Pantalla Perfil — información y configuración del usuario.
     */
    data object Profile : Screen(route = "profile", label = "Perfil")

    /**
     * Pantalla de detalle de producto.
     * Acepta un parámetro `productId` para identificar el producto a mostrar.
     *
     * @param productId Identificador único del producto.
     */
    data class ProductDetail(val productId: String) : Screen(
        route = "product_detail/{productId}",
        label = "Detalle de producto"
    ) {
        override fun toString() = "product_detail/$productId"
    }

    // ========================================================================
    // EMPLOYEE SCREENS — v0.2 Flujo exclusivo para trabajadores
    // ========================================================================

    /**
     * Pantalla Login — autenticación del empleado.
     */
    data object Login : Screen(route = "login", label = "Login")

    /**
     * Pantalla principal de búsqueda por SKU.
     */
    data object SearchSku : Screen(route = "search_sku", label = "Buscar SKU")

    /**
     * Pantalla de resultado de consulta de stock.
     */
    data class StockResult(val sku: String) : Screen(
        route = "stock_result/{sku}",
        label = "Resultado de Stock"
    ) {
        override val route: String
            get() = "stock_result/$sku"
    }
}
