package com.nicopoly.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Almacena metadatos de la última importación exitosa del Excel.
 *
 * Utiliza DataStore Preferences para persistir:
 * - Fecha/hora de la última actualización
 * - Cantidad de productos importados
 * - Cantidad de ubicaciones importadas
 *
 * Esta información es independiente de los productos y se utiliza
 * para mostrar al usuario cuándo fue la última vez que se actualizó.
 */

private val Context.importMetadataDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "import_metadata"
)

object ImportMetadataKeys {
    val LAST_IMPORT_TIMESTAMP = longPreferencesKey("last_import_timestamp")
    val LAST_IMPORT_PRODUCT_COUNT = intPreferencesKey("last_import_product_count")
    val LAST_IMPORT_LOCATION_COUNT = intPreferencesKey("last_import_location_count")
}

/**
 * Clase que gestiona el acceso a los metadatos de importación.
 */
class ImportMetadata(private val context: Context) {

    private val dataStore = context.importMetadataDataStore

    /**
     * Guarda los metadatos después de una importación exitosa.
     */
    suspend fun saveImportResult(
        productCount: Int,
        locationCount: Int
    ) {
        dataStore.edit { preferences ->
            preferences[ImportMetadataKeys.LAST_IMPORT_TIMESTAMP] = System.currentTimeMillis()
            preferences[ImportMetadataKeys.LAST_IMPORT_PRODUCT_COUNT] = productCount
            preferences[ImportMetadataKeys.LAST_IMPORT_LOCATION_COUNT] = locationCount
        }
    }

    /**
     * Obtiene un flujo con los metadatos de la última importación.
     */
    fun getImportInfo(): Flow<ImportInfo> {
        return dataStore.data.map { preferences ->
            val timestamp = preferences[ImportMetadataKeys.LAST_IMPORT_TIMESTAMP] ?: 0L
            val productCount = preferences[ImportMetadataKeys.LAST_IMPORT_PRODUCT_COUNT] ?: 0
            val locationCount = preferences[ImportMetadataKeys.LAST_IMPORT_LOCATION_COUNT] ?: 0

            ImportInfo(
                lastImportTimestamp = timestamp,
                productCount = productCount,
                locationCount = locationCount,
                hasImportedData = timestamp > 0
            )
        }
    }

    /**
     * Información de la última importación.
     */
    data class ImportInfo(
        val lastImportTimestamp: Long,
        val productCount: Int,
        val locationCount: Int,
        val hasImportedData: Boolean
    )
}
