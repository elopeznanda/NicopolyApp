package com.nicopoly.app.domain.model

/**
 * Entidad de dominio que representa un producto de ropa femenina.
 *
 * @property id Identificador único del producto.
 * @property name Nombre comercial del producto.
 * @property price Precio en CLP (Pesos Chilenos).
 * @property originalPrice Precio original antes de descuento (null si no hay descuento).
 * @property imageUrl URL de la imagen principal del producto.
 * @property description Descripción detallada del producto.
 * @property category Categoría a la que pertenece el producto.
 * @property sizes Tallas disponibles.
 * @property colors Colores disponibles.
 * @property rating Calificación promedio (1.0 - 5.0).
 * @property reviewCount Cantidad de reseñas.
 * @property isNew true si es un producto recién llegado.
 * @property isFeatured true si es un producto destacado.
 * @property isOnSale true si el producto tiene descuento activo.
 * @property precioTiendas Precio actual de venta en tiendas (debe aparecer en pantalla de resultados).
 * @property precioInicial Precio inicial de la prenda (debe aparecer en pantalla de resultados).
 */
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val originalPrice: Double? = null,
    val imageUrl: String,
    val description: String,
    val category: Category,
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val isNew: Boolean = false,
    val isFeatured: Boolean = false,
    val isOnSale: Boolean = false,
    val precioTiendas: Double? = null,
    val precioInicial: Double? = null
) {
    /** Calcula el porcentaje de descuento si aplica. */
    val discountPercentage: Int?
        get() = originalPrice?.let { original ->
            ((original - price) / original * 100).toInt()
        }
}

/**
 * Categorías de productos disponibles en Nicopoly.
 */
enum class Category(val displayName: String, val iconEmoji: String) {
    VESTIDOS("Vestidos", "👗"),
    BLUSAS("Blusas", "👚"),
    FALDAS("Faldas", "🩱"),
    PANTALONES("Pantalones", "👖"),
    CHALECOS_Y_JACKETS("Chalecos y Jackets", "🧥"),
    ACCESORIOS("Accesorios", "👜"),
    CALZADO("Calzado", "👠"),
    INTIMIDAD("Intimidad", "🩲")
}