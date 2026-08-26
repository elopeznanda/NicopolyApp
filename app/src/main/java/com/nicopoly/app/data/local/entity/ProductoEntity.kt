package com.nicopoly.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa una variante de producto importada desde el Excel.
 *
 * Cada fila corresponde a un registro de la hoja DATOS con:
 * - CODIGO HIJO (primaryKey)
 * - CATEGORIA
 * - BODEGA, P1, F, P2 (stocks)
 * - PRECIO TIENDAS, PRECIO INICIAL
 *
 * El campo [codigoPadre] se deriva del codigoHijo durante el proceso de importación
 * y permite relacionar la variante con su ubicación física en bodega.
 */
@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey
    val codigoHijo: String,

    /**
     * Código padre derivado del código hijo.
     * Para códigos de 8 caracteres: primeros 6 caracteres.
     * Para otros patrones: se determina durante la importación.
     */
    val codigoPadre: String,

    /**
     * Color de la variante (derivado).
     * Puede ser una letra, dos letras o dos números según el patrón del código.
     */
    val color: String,

    /**
     * Talla de la variante (derivado).
     * Puede ser S, M, L, XL, un número, etc.
     */
    val talla: String,

    /**
     * Tipo de prenda: CHAQUETA, BUFANDA, VESTIDO, etc.
     */
    val categoria: String,

    /**
     * Stock disponible en la bodega principal del Centro de Distribución.
     */
    val stockBodega: Int = 0,

    /**
     * Stock disponible en la tienda Provi 1.
     */
    val stockProvi1: Int = 0,

    /**
     * Stock disponible en la tienda Filomena.
     */
    val stockFilomena: Int = 0,

    /**
     * Stock disponible en la tienda Provi 2.
     */
    val stockProvi2: Int = 0,

    /**
     * Precio actual de venta en tiendas.
     */
    val precioTiendas: Double = 0.0,

    /**
     * Precio inicial de la prenda.
     */
    val precioInicial: Double = 0.0,

    // Nuevas columnas del Excel
    val temporada: String = "",

    val t060: Int = 0,

    val t011: Int = 0,

    val precioMayor: Double = 0.0
)
