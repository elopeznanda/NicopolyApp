package com.nicopoly.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nicopoly.app.data.local.entity.UbicacionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la tabla de ubicaciones.
 *
 * Define las operaciones sobre la base de datos local de ubicaciones
 * importadas desde la hoja UBICACIONES del Excel.
 */
@Dao
interface UbicacionDao {

    /**
     * Obtiene la ubicación para un código padre específico.
     *
     * @param codigoPadre Código padre a buscar.
     * @return Flow con la ubicación encontrada, o null si no existe.
     */
    @Query("SELECT * FROM ubicaciones WHERE codigoPadre = :codigoPadre LIMIT 1")
    fun getUbicacionByCodigoPadre(codigoPadre: String): Flow<UbicacionEntity?>

    /**
     * Obtiene la ubicación para un código padre específico de forma suspendida.
     *
     * @param codigoPadre Código padre a buscar.
     * @return La ubicación encontrada, o null si no existe.
     */
    @Query("SELECT * FROM ubicaciones WHERE codigoPadre = :codigoPadre LIMIT 1")
    suspend fun getUbicacionByCodigoPadreOnce(codigoPadre: String): UbicacionEntity?

    /**
     * Inserta o reemplaza una lista de ubicaciones.
     * Útil para la importación masiva desde el Excel.
     *
     * @param ubicaciones Lista de ubicaciones a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUbicaciones(ubicaciones: List<UbicacionEntity>)

    /**
     * Inserta o reemplaza una ubicación individual.
     *
     * @param ubicacion Ubicación a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUbicacion(ubicacion: UbicacionEntity)

    /**
     * Elimina todas las ubicaciones de la base de datos.
     * Útil para reiniciar la importación.
     */
    @Query("DELETE FROM ubicaciones")
    suspend fun deleteAllUbicaciones()

    /**
     * Obtiene el total de ubicaciones en la base de datos.
     *
     * @return Cantidad total de registros.
     */
    @Query("SELECT COUNT(*) FROM ubicaciones")
    suspend fun countUbicaciones(): Int
}
