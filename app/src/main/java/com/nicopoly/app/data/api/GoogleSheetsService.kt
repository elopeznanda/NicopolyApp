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

@Singleton
class GoogleSheetsService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "GoogleSheetsService"
        private const val APPLICATION_NAME = "NicopolyApp"
        private const val SPREADSHEET_ID = "1WKP6-EMvt2uhlXLXzlvR7u8dvKaXu-3ukr_GxbHxZno"
        private const val RANGE = "Reposición General!A:P"
    }

    /**
     * Obtiene los datos de la hoja de cálculo usando la cuenta de servicio.
     * Retorna una lista de listas de objetos, donde cada lista interna representa una fila.
     */
    suspend fun fetchSheetData(): List<List<Any>>? = withContext(Dispatchers.IO) {
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
                
            Log.d(TAG, "Conectando a Google Sheets API...")
            
            // 3. Consultar la hoja de cálculo
            val response = service.spreadsheets().values()
                .get(SPREADSHEET_ID, RANGE)
                .execute()
                
            val values = response.getValues()
            
            if (values == null || values.isEmpty()) {
                Log.w(TAG, "No se encontraron datos en la hoja de cálculo.")
                return@withContext null
            }
            
            Log.d(TAG, "Se descargaron ${values.size} filas exitosamente.")
            return@withContext values
            
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener datos de Google Sheets", e)
            throw e
        }
    }
}
