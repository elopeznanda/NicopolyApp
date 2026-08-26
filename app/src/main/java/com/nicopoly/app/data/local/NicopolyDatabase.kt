package com.nicopoly.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nicopoly.app.data.local.dao.ProductoDao
import com.nicopoly.app.data.local.dao.UbicacionDao
import com.nicopoly.app.data.local.entity.ProductoEntity
import com.nicopoly.app.data.local.entity.UbicacionEntity

/**
 * Base de datos local de Nicopoly.
 *
 * Almacena los datos importados desde el Excel:
 * - Productos (hoja DATOS): 32,776 registros de variantes con stock y precios
 * - Ubicaciones (hoja UBICACIONES): 1,619 registros de ubicación por código padre
 *
 * Versión 1: Esquema inicial con productos y ubicaciones.
 */
@Database(
    entities = [
        ProductoEntity::class,
        UbicacionEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class NicopolyDatabase : RoomDatabase() {

    /**
     * DAO para acceder a la tabla de productos.
     */
    abstract fun productoDao(): ProductoDao

    /**
     * DAO para acceder a la tabla de ubicaciones.
     */
    abstract fun ubicacionDao(): UbicacionDao
}
