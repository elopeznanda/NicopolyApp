package com.nicopoly.app.data.api

import android.util.Log
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

// DTO for the API response structure
data class ApiResponse(
    val success: Boolean,
    val spreadsheet: String,
    val timestamp: String,
    val reposicion: List<ReposicionItem>,
    val ubicaciones: List<UbicacionItem>
)

// DTO for individual reposicion item
data class ReposicionItem(
    @SerializedName("Item")
    val item: String,
    
    @SerializedName("Descripcion")
    val descripcion: String,
    
    @SerializedName("Categoria")
    val categoria: String,
    
    @SerializedName("Temporada")
    val temporada: String?,
    
    @SerializedName("T003")
    val t003: Int,
    
    @SerializedName("T009")
    val t009: Int,
    
    @SerializedName("T012")
    val t012: Int,
    
    @SerializedName("T001")
    val t001: Int,
    
    @SerializedName("T060")
    val t060: Int,
    
    @SerializedName("T011")
    val t011: Int,
    
    @SerializedName("Casa Matriz")
    val casaMatriz: Int,
    
    @SerializedName("Precio Rec2")
    val precioRec2: Int,
    
    @SerializedName("Precio Base")
    val precioBase: Int,
    
    @SerializedName("Precio Mayor")
    val precioMayor: Int,
    
    @SerializedName("CódigoMV")
    val codigoMv: String?,
    
    @SerializedName("Ubicacion")
    val ubicacion: String?
)

// DTO for ubicaciones (empty list in example, but keeping structure)
data class UbicacionItem(
    // Empty as per example, but we define it for completeness
    val dummyField: String = ""
)

// Retrofit API service interface for Google Apps Script
interface NicopolyApiService {
    @GET("/macros/s/AKfycbyxKwq-sYlOQUFTEDUB1oXFj63dCNXwFYL21UHWlu4DIjF6GpEiOetWUrllN26BqJTM1A/exec")
    suspend fun getStockData(): ApiResponse
}

// Retrofit client builder
object ApiClient {
    private const val TAG = "ApiClient"
    private const val BASE_URL = "https://script.google.com"

    fun createService(): NicopolyApiService {
        return createRetrofit().create(NicopolyApiService::class.java)
    }

    private fun buildOkHttpClient(): OkHttpClient {
        // Logging interceptor to see the raw HTTP response body BEFORE Gson parsing
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, "[HTTP] $message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            // Follow redirects explicitly - Google Apps Script may redirect
            .followRedirects(true)
            .followSslRedirects(true)
            // Add browser-like headers so Google Apps Script treats us like a real client
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Accept", "application/json, text/plain, */*")
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
                    .header("Accept-Language", "es-ES,es;q=0.9,en-US;q=0.8,en;q=0.7")
                    .build()

                val response = chain.proceed(request)

                // LOG the raw response body to diagnose what Google Apps Script actually returns
                val contentType = response.body?.contentType()?.toString() ?: "unknown"
                val code = response.code
                Log.d(TAG, "[HTTP] Status: $code | Content-Type: $contentType")

                // Clone and read the raw body bytes for diagnostics
                val rawBytes = response.body?.bytes()
                if (rawBytes != null) {
                    try {
                        val rawContent = String(rawBytes, Charsets.UTF_8) // UTF-8 by default
                        val preview = rawContent.take(500)
                        Log.d(TAG, "[HTTP] Raw body (${rawContent.length} bytes): $preview")

                        // Check if response starts with JSON or HTML
                        val firstChar = rawContent.trim().firstOrNull() ?: '?'
                        when {
                            firstChar == '{' -> Log.d(TAG, "[HTTP] Body is valid JSON ✓")
                            firstChar == '[' -> Log.d(TAG, "[HTTP] Body is a JSON array ✓")
                            firstChar == '<' -> Log.e(TAG, "[HTTP] ERROR: Response starts with HTML! Body preview: ${rawContent.take(200)}")
                            else -> Log.w(TAG, "[HTTP] WARNING: Unexpected body start char: '$firstChar'")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "[HTTP] Error reading raw body", e)
                    }
                }

                // Rebuild response with original body so Gson can still parse it
                if (rawBytes != null && !response.body?.contentType()?.toString().isNullOrBlank()) {
                    val rebuiltBody = okhttp3.ResponseBody.create(
                        response.body!!.contentType(),
                        rawBytes
                    )
                    return@addInterceptor response.newBuilder()
                        .body(rebuiltBody)
                        .build()
                }

                response
            }
            .addNetworkInterceptor(loggingInterceptor)
            .build()
    }

    fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(buildOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}