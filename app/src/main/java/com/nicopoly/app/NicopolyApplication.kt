package com.nicopoly.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Entry point de la aplicación Nicopoly.
 * Anotado con @HiltAndroidApp para inicializar el graph de inyección de dependencias.
 */
@HiltAndroidApp
class NicopolyApplication : Application()
