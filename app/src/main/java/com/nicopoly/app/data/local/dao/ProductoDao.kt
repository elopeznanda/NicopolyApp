package com.nicopoly.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nicopoly.app.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la tabla de productos.
 *
 * Define las operaciones de lectura y escritura sobre la base de datos local
 * de productos importados desde el Excel.
 */
@Dao
interface ProductoDao {

    /**
     * Obtiene un producto por su código hijo (SKU).
     *
     * @param codigoHijo Código SKU exacto a buscar.
     * @return Flow con el producto encontrado, o null si no existe.
     */
    @Query("SELECT * FROM productos WHERE TRIM(codigoHijo) = TRIM(:codigoHijo) LIMIT 1")
    fun getProductoByCodigoHijo(codigoHijo: String): Flow<ProductoEntity?>

    /**
     * Obtiene un producto por su código hijo (SKU) de forma suspendida.
     *
     * @param codigoHijo Código SKU exacto a buscar.
     * @return El producto encontrado, o null si no existe.
     */
    @Query("SELECT * FROM productos WHERE TRIM(codigoHijo) = TRIM(:codigoHijo) LIMIT 1")
    suspend fun getProductoByCodigoHijoOnce(codigoHijo: String): ProductoEntity?

    /**
     * Inserta o reemplaza una lista de productos.
     * Útil para la importación masiva desde el Excel.
     *
     * @param productos Lista de productos a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductos(productos: List<ProductoEntity>)

    /**
     * Inserta o reemplaza un producto individual.
     *
     * @param producto Producto a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: ProductoEntity)

    /**
     * Elimina todos los productos de la base de datos.
     * Útil para reiniciar la importación.
     */
    @Query("DELETE FROM productos")
    suspend fun deleteAllProductos()

    /**
     * Obtiene el total de productos en la base de datos.
     *
     * @return Cantidad total de registros.
     */
    @Query("SELECT COUNT(*) FROM productos")
    suspend fun countProductos(): Int

    /**
     * Obtiene todos los productos (variantes) que pertenecen al mismo código padre.
     * Útil para mostrar la tabla completa de stock de una prenda con todas sus variantes.
     *
     * @param codigoPadre Código padre para filtrar las variantes.
     * @return Lista de productos con el mismo código padre.
     */
    /**
     * Obtiene el primer producto que coincide con un código padre.
     * Útil para buscar directamente por código padre cuando se desconoce el hijo.
     *
     * @param codigoPadre Código padre a buscar.
     * @return El primer producto encontrado, o null si no existe.
     */
    @Query("SELECT * FROM productos WHERE codigoPadre = :codigoPadre LIMIT 1")
    suspend fun getProductoByCodigoPadreOnce(codigoPadre: String): ProductoEntity?

    /**
     * Obtiene todos los productos (variantes) que pertenecen al mismo código padre.
     * Útil para mostrar la tabla completa de stock de una prenda con todas sus variantes.
     *
     * @param codigoPadre Código padre para filtrar las variantes.
     * @return Lista de productos con el mismo código padre.
     */
    @Query("SELECT * FROM productos WHERE codigoPadre = :codigoPadre")
    suspend fun getProductosByCodigoPadre(codigoPadre: String): List<ProductoEntity>

    /**
     * DIAGNÓSTICO TEMPORAL: Retorna los primeros 10 registros para inspeccionar codigoHijo.
     */
    @Query("SELECT * FROM productos LIMIT 10")
    suspend fun getFirst10Productos(): List<ProductoEntity>

    /**
     * DIAGNÓSTICO TEMPORAL: Busca por patrón LIKE.
     */
    @Query("SELECT * FROM productos WHERE codigoHijo LIKE :pattern LIMIT 5")
    suspend fun searchByPattern(pattern: String): List<ProductoEntity>

    /**
     * Busca un producto cuyo codigoHijo contenga el fragmento dado.
     * Se usa como fallback cuando la búsqueda exacta por SKU no encuentra coincidencia,
     * permitiendo que consultas parciales como "06182" encuentren "N06182AM".
     *
     * @param sku Fragmento del SKU a buscar dentro de codigoHijo.
     * @return El primer producto cuyo codigoHijo contiene el fragmento, o null si no existe.
     */
    @Query("SELECT * FROM productos WHERE codigoHijo LIKE '%' || :sku || '%' LIMIT 1")
    suspend fun getProductoBySkuPartial(sku: String): ProductoEntity?
}
