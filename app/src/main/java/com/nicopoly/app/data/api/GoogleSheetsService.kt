package com.nicopoly.app.data.api

import android.content.Context
import android.util.Log
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.nicopoly.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class GoogleSheetsData(
    val reposicionRows: List<List<Any>>,
    val ubicacionesRows: List<List<Any>>
)

@Singleton
class GoogleSheetsService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "GoogleSheetsService"
        private const val APPLICATION_NAME = "NicopolyApp"
        private const val SPREADSHEET_ID = "1WKP6-EMvt2uhlXLXzlvR7u8dvKaXu-3ukr_GxbHxZno"
        private const val RANGE_REPOSICION = "Reposicion!A:P"
        private const val RANGE_UBICACIONES = "Ubicaciones!A:C"
    }

    /**
     * Obtiene los datos de ambas hojas (Reposición y Ubicaciones) usando la cuenta de servicio.
     */
    suspend fun fetchSheetData(): GoogleSheetsData = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Iniciando autenticación con cuenta de servicio...")
            
            // 1. Cargar credenciales desde res/raw/credentials.json
            val credentialsStream = context.resources.openRawResource(R.raw.credentials)
            val credentials = GoogleCredentials.fromStream(credentialsStream)
                .createScoped(listOf(SheetsScopes.SPREADSHEETS_READONLY))
            
            // 2. Crear instancia del servicio Sheets API
            val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
            val jsonFactory = GsonFactory.getDefaultInstance()
            
            val requestInitializer = HttpCredentialsAdapter(credentials)
            
            val service = Sheets.Builder(httpTransport, jsonFactory, requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build()
                
            Log.d(TAG, "Conectando a Google Sheets API (batchGet para Reposicion y Ubicaciones)...")
            
            // 3. Consultar ambas hojas en una sola llamada de red
            val batchResponse = service.spreadsheets().values()
                .batchGet(SPREADSHEET_ID)
                .setRanges(listOf(RANGE_REPOSICION, RANGE_UBICACIONES))
                .execute()
                
            val valueRanges = batchResponse.valueRanges
            val reposicionValues = valueRanges?.getOrNull(0)?.getValues() ?: emptyList()
            val ubicacionesValues = valueRanges?.getOrNull(1)?.getValues() ?: emptyList()
            
            Log.d(TAG, "Se descargaron ${reposicionValues.size} filas de Reposición y ${ubicacionesValues.size} filas de Ubicaciones.")
            return@withContext GoogleSheetsData(
                reposicionRows = reposicionValues,
                ubicacionesRows = ubicacionesValues
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener datos de Google Sheets", e)
            throw e
        }
    }
}
