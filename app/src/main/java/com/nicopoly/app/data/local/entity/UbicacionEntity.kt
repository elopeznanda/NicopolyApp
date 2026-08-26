package com.nicopoly.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa la ubicación física de un código padre en bodega.
 *
 * Cada fila corresponde a un registro de la hoja UBICACIONES con:
 * - CODIGO PADRE (primaryKey)
 * - UBICACIÓN
 *
 * La ubicación pertenece al código padre y no a una variante individual.
 * Todas las variantes (códigos hijo) que comparten el mismo código padre
 * heredan esta misma ubicación.
 */
@Entity(tableName = "ubicaciones")
data class UbicacionEntity(
    @PrimaryKey
    val codigoPadre: String,

    /**
     * Ubicación física en el Centro de Distribución.
     * Ejemplo: "4 A - BODEGA CHICA", "1 A - ONLINE", "49 F - COLECCIÓN"
     */
    val ubicacion: String
)
