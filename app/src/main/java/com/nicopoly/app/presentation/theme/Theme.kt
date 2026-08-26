package com.nicopoly.app.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Tema Material Design 3 de Nicopoly.
 * Define colores, tipografía y shapes para toda la aplicación.
 */

private val LightColorScheme = lightColorScheme(
    primary = NicopolyColors.PrimaryLight,
    onPrimary = NicopolyColors.OnPrimaryLight,
    primaryContainer = NicopolyColors.PrimaryContainerLight,
    onPrimaryContainer = NicopolyColors.OnPrimaryContainerLight,
    secondary = NicopolyColors.SecondaryLight,
    onSecondary = NicopolyColors.OnSecondaryLight,
    secondaryContainer = NicopolyColors.SecondaryContainerLight,
    onSecondaryContainer = NicopolyColors.OnSecondaryContainerLight,
    tertiary = NicopolyColors.TertiaryLight,
    onTertiary = NicopolyColors.OnTertiaryLight,
    tertiaryContainer = NicopolyColors.TertiaryContainerLight,
    onTertiaryContainer = NicopolyColors.OnTertiaryContainerLight,
    error = NicopolyColors.ErrorLight,
    onError = NicopolyColors.OnErrorLight,
    background = NicopolyColors.BackgroundLight,
    onBackground = NicopolyColors.OnBackgroundLight,
    surface = NicopolyColors.SurfaceLight,
    onSurface = NicopolyColors.OnSurfaceLight,
    surfaceVariant = NicopolyColors.SurfaceVariantLight,
    onSurfaceVariant = NicopolyColors.OnSurfaceVariantLight,
    surfaceContainer = NicopolyColors.SurfaceContainerLight,
    outline = NicopolyColors.OutlineLight,
    outlineVariant = NicopolyColors.OutlineVariantLight
)

private val DarkColorScheme = darkColorScheme(
    primary = NicopolyColors.PrimaryDark,
    onPrimary = NicopolyColors.OnPrimaryDark,
    primaryContainer = NicopolyColors.PrimaryContainerDark,
    onPrimaryContainer = NicopolyColors.OnPrimaryContainerDark,
    secondary = NicopolyColors.SecondaryDark,
    onSecondary = NicopolyColors.OnSecondaryDark,
    secondaryContainer = NicopolyColors.SecondaryContainerDark,
    onSecondaryContainer = NicopolyColors.OnSecondaryContainerDark,
    tertiary = NicopolyColors.TertiaryDark,
    onTertiary = NicopolyColors.OnTertiaryDark,
    tertiaryContainer = NicopolyColors.TertiaryContainerDark,
    onTertiaryContainer = NicopolyColors.OnTertiaryContainerDark,
    error = NicopolyColors.ErrorLight,
    onError = NicopolyColors.OnErrorLight,
    background = NicopolyColors.BackgroundDark,
    onBackground = NicopolyColors.OnBackgroundDark,
    surface = NicopolyColors.SurfaceDark,
    onSurface = NicopolyColors.OnSurfaceDark,
    surfaceVariant = NicopolyColors.SurfaceVariantDark,
    onSurfaceVariant = NicopolyColors.OnSurfaceVariantDark,
    surfaceContainer = NicopolyColors.SurfaceContainerDark,
    outline = NicopolyColors.OutlineLight,
    outlineVariant = NicopolyColors.OutlineVariantLight
)

@Composable
fun NicopolyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable() () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = Color(0x000000).toArgb()
            window.navigationBarColor = Color(0x000000).toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        shapes = NicopolyShapes,
        content = content
    )
}