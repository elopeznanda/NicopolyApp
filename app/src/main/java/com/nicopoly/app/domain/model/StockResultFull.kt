package com.nicopoly.app.domain.model

/**
 * Resultado completo de la consulta de stock.
 *
 * Contiene toda la información necesaria para la pantalla de resultado:
 * - Código padre y ubicación
 * - Categoría y precios
 * - Lista completa de variantes con sus stocks individuales
 *
 * @property codigoPadre Código padre que identifica el modelo/prenda.
 * @property ubicacion Ubicación física en el Centro de Distribución, o null si no está registrada.
 * @property categoria Tipo de prenda: CHAQUETA, BUFANDA, VESTIDO, etc.
 * @property precioTiendas Precio actual de venta en tiendas.
 * @property precioInicial Precio inicial de la prenda.
 * @property variantes Lista de todas las variantes (códigos hijo) con su stock individual.
 */
data class StockResultFull(
    val codigoPadre: String,
    val temporada: String?,
    val ubicacion: String?,
    val categoria: String,
    val precioTiendas: Double,
    val precioInicial: Double,
    val precioMayor: Double,
    val t060Total: Int,
    val casaMatrizTotal: Int,
    val t011Total: Int,
    val variantes: List<VarianteStock>
)

/**
 * Variante individual de un producto con su información de stock.
 *
 * @property codigoHijo Código SKU completo de la variante.
 * @property color Color de la variante.
 * @property talla Talla de la variante.
 * @property stockBodega Stock disponible en la bodega principal (Casa Matriz).
 * @property stockProvi1 Stock disponible en Provi 1 (T003).
 * @property stockFilomena Stock disponible en Filomena (T009).
 * @property stockProvi2 Stock disponible en Provi 2 (T012).
 * @property t060 Stock ONLINE (T060).
 */
data class VarianteStock(
    val codigoHijo: String,
    val color: String,
    val talla: String,
    val stockBodega: Int,
    val stockProvi1: Int,
    val stockFilomena: Int,
    val stockProvi2: Int,
    val t060: Int = 0
)
