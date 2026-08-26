package com.nicopoly.app.data.repository

import com.nicopoly.app.domain.model.Category
import com.nicopoly.app.domain.model.Product

/**
 * Conjunto de productos de ejemplo para la versión 0.1.
 * En futuras versiones, estos datos serán reemplazados por una API REST real.
 */
object SampleProducts {

    val catalog: List<Product> = listOf(
        // === VESTIDOS ===
        Product(
            id = "1",
            name = "Vestido Floral Primavera",
            price = 49990.0,
            originalPrice = 69990.0,
            imageUrl = "",
            description = "Hermoso vestido floral con estampado de primavera. Perfecto para ocasiones casuales y semi-formales. Tela ligera y cómoda con forro interior.",
            category = Category.VESTIDOS,
            sizes = listS("XS", "S", "M", "L"),
            colors = listOf("Rosa Floral", "Azul Cielo", "Verde Menta"),
            rating = 4.8f,
            reviewCount = 124,
            isNew = true,
            isFeatured = true,
            isOnSale = true
        ),
        Product(
            id = "2",
            name = "Vestido Midi Elegante Negro",
            price = 79990.0,
            imageUrl = "",
            description = "Vestido midi negro de corte elegante. Ideal para eventos formales, cenas y ocasiones especiales. Con detalles de encaje en el escote.",
            category = Category.VESTIDOS,
            sizes = listS("S", "M", "L", "XL"),
            colors = listOf("Negro"),
            rating = 4.9f,
            reviewCount = 89,
            isFeatured = true
        ),
        Product(
            id = "3",
            name = "Vestido Casual Lino Beige",
            price = 39990.0,
            imageUrl = "",
            description = "Vestido casual de lino natural en tono beige. Perfecto para el día a día con un toque sofisticado. Incluye cinturón a juego.",
            category = Category.VESTIDOS,
            sizes = listS("XS", "S", "M", "L", "XL"),
            colors = listOf("Beige", "Blanco Hueso", "Arena"),
            rating = 4.5f,
            reviewCount = 67,
            isNew = true
        ),
        // === BLUSAS ===
        Product(
            id = "4",
            name = "Blusa Seda Premium Rosa",
            price = 34990.0,
            originalPrice = 44990.0,
            imageUrl = "",
            description = "Blusa de seda premium en delicado tono rosa. Corte fluido y elegante con botones de nácar. Versátil para oficina o salida.",
            category = Category.BLUSAS,
            sizes = listS("XS", "S", "M", "L"),
            colors = listOf("Rosa Pale", "Blanco Perla", "Celeste"),
            rating = 4.7f,
            reviewCount = 156,
            isFeatured = true,
            isOnSale = true
        ),
        Product(
            id = "5",
            name = "Blusa Crop Top Blanca",
            price = 24990.0,
            imageUrl = "",
            description = "Crop top blanco de algodón orgánico. Diseño moderno y cómodo, perfecto para combinar con pantalones de tiro alto.",
            category = Category.BLUSAS,
            sizes = listS("XS", "S", "M", "L"),
            colors = listOf("Blanco", "Negro", "Crema"),
            rating = 4.3f,
            reviewCount = 203,
            isNew = true
        ),
        Product(
            id = "6",
            name = "Blusa Satinada Dorada",
            price = 29990.0,
            imageUrl = "",
            description = "Blusa con acabado satinado en tono dorado suave. Elegante y versátil, ideal para combinar con faldas o pantalones de vestir.",
            category = Category.BLUSAS,
            sizes = listS("S", "M", "L"),
            colors = listOf("Dorado", "Plata", "Champagne"),
            rating = 4.6f,
            reviewCount = 78
        ),
        // === FALDAS ===
        Product(
            id = "7",
            name = "Falda Plisada Midi Rosa",
            price = 34990.0,
            imageUrl = "",
            description = "Falda midi plisada en tono rosa pastel. Movimiento elegante al caminar con cintura elástica cómoda.",
            category = Category.FALDAS,
            sizes = listS("XS", "S", "M", "L"),
            colors = listOf("Rosa", "Verde Salvia", "Lavanda"),
            rating = 4.4f,
            reviewCount = 92,
            isNew = true
        ),
        Product(
            id = "8",
            name = "Falda Denim High Waist",
            price = 29990.0,
            originalPrice = 39990.0,
            imageUrl = "",
            description = "Falda de jean de tiro alto con lavado vintage. Estilo casual chic que combina con todo.",
            category = Category.FALDAS,
            sizes = listS("XS", "S", "M", "L", "XL"),
            colors = listOf("Azul Claro", "Azul Oscuro"),
            rating = 4.6f,
            reviewCount = 145,
            isOnSale = true
        ),
        // === PANTALONES ===
        Product(
            id = "9",
            name = "Pantalón Wide Leg Negro",
            price = 44990.0,
            imageUrl = "",
            description = "Pantalón de pierna ancha en negro elegante. Corte recto y fluido con cintura alta. Perfecto para oficina o salida nocturna.",
            category = Category.PANTALONES,
            sizes = listS("XS", "S", "M", "L", "XL"),
            colors = listOf("Negro", "Gris Carbón", "Camel"),
            rating = 4.7f,
            reviewCount = 178,
            isFeatured = true
        ),
        Product(
            id = "10",
            name = "Jeans Slim Fit Azul",
            price = 39990.0,
            imageUrl = "",
            description = "Jean slim fit con stretch cómodo. Lavado medio que realza la silueta. Bolsillos funcionales y costuras reforzadas.",
            category = Category.PANTALONES,
            sizes = listS("24", "26", "28", "30", "32"),
            colors = listOf("Azul Medio", "Azul Oscuro", "Negro"),
            rating = 4.5f,
            reviewCount = 234
        ),
        // === CHALECOS Y JACKETS ===
        Product(
            id = "11",
            name = "Blazer Oversize Crema",
            price = 69990.0,
            originalPrice = 89990.0,
            imageUrl = "",
            description = "Blazer oversize en tono crema. Corte moderno y relajado con hombreras sutiles. Perfecto para looks sofisticados.",
            category = Category.CHALECOS_Y_JACKETS,
            sizes = listS("S", "M", "L"),
            colors = listOf("Crema", "Negro", "Gris Perla"),
            rating = 4.8f,
            reviewCount = 97,
            isFeatured = true,
            isOnSale = true
        ),
        Product(
            id = "12",
            name = "Jacket Denim Vintage",
            price = 54990.0,
            imageUrl = "",
            description = "Chaqueta de jean con lavado vintage desgastado. Forro interior suave y botones metálicos. Un clásico atemporal.",
            category = Category.CHALECOS_Y_JACKETS,
            sizes = listS("XS", "S", "M", "L"),
            colors = listOf("Azul Vintage", "Negro Desgastado"),
            rating = 4.6f,
            reviewCount = 156,
            isNew = true
        )
    )

    /** Retorna todos los productos. */
    fun getAll(): List<Product> = catalog

    /** Retorna productos destacados. */
    fun getFeatured(): List<Product> = catalog.filter { it.isFeatured }

    /** Retorna productos recién llegados. */
    fun getNewArrivals(): List<Product> = catalog.filter { it.isNew }

    /** Filtra productos por categoría. */
    fun getByCategory(category: Category): List<Product> =
        catalog.filter { it.category == category }

    /** Busca productos por término en nombre o descripción. */
    fun search(query: String): List<Product> {
        val lowerQuery = query.lowercase().trim()
        if (lowerQuery.isEmpty()) return catalog
        return catalog.filter {
            it.name.lowercase().contains(lowerQuery) ||
                it.description.lowercase().contains(lowerQuery) ||
                it.category.displayName.lowercase().contains(lowerQuery)
        }
    }

    /** Retorna un producto por su ID. */
    fun getById(id: String): Product? = catalog.find { it.id == id }

    private fun listS(vararg sizes: String) = sizes.toList()
}
