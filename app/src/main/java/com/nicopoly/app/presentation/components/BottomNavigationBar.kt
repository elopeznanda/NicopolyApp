package com.nicopoly.app.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nicopoly.app.R
import com.nicopoly.app.presentation.navigation.Screen

/**
 * Barra de navegación inferior de Nicopoly.
 * Define las pestañas principales: Inicio, Catálogo y Perfil.
 */
@Composable
fun NicopolyBottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Catalog,
        BottomNavItem.Profile
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = modifier
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    val iconRes = if (item.screen.route == currentRoute) item.iconSelected else item.icon
                    Icon(
                        imageVector = ImageVector.vectorResource(iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

/**
 * Definición de cada ítem de la barra de navegación.
 */
private data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: Int,
    val iconSelected: Int
) {
    companion object {
        val Home = BottomNavItem(
            screen = Screen.Home,
            label = "Inicio",
            icon = R.drawable.ic_home_outlined,
            iconSelected = R.drawable.ic_home_filled
        )

        val Catalog = BottomNavItem(
            screen = Screen.Catalog,
            label = "Catálogo",
            icon = R.drawable.ic_grid_outlined,
            iconSelected = R.drawable.ic_grid_filled
        )

        val Profile = BottomNavItem(
            screen = Screen.Profile,
            label = "Perfil",
            icon = R.drawable.ic_person_outlined,
            iconSelected = R.drawable.ic_person_filled
        )
    }
}
