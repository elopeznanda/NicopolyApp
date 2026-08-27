package com.nicopoly.app.presentation.searchsku

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nicopoly.app.data.importer.ExcelImporter
import com.nicopoly.app.data.local.ImportMetadata
import com.nicopoly.app.data.local.dao.ProductoDao
import com.nicopoly.app.domain.model.StockQueryResult
import com.nicopoly.app.domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * ViewModel para la pantalla de búsqueda por SKU.
 *
 * Gestiona:
 * - Consulta de stock por SKU (código hijo o padre)
 * - Importación manual de Excel seleccionada por el usuario
 * - Estado de la base de datos (vacía o con datos)
 *
 * @property stockRepository Repositorio de stock inyectado vía Hilt.
 * @param excelImporter Importador de Excel para importación manual.
 * @param productoDao DAO para verificar si Room tiene datos.
 * @param importMetadata Almacenamiento de metadatos de última importación.
 */
@HiltViewModel
class SearchSkuViewModel @Inject constructor(
    private val stockRepository: StockRepository,
    private val excelImporter: ExcelImporter,
    private val productoDao: ProductoDao,
    private val importMetadata: ImportMetadata,
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        private const val TAG = "SearchSkuViewModel"
    }

    private val _uiState = MutableStateFlow(SearchSkuUiState())
    val uiState: StateFlow<SearchSkuUiState> = _uiState

    init {
        // Verificar si Room está vacía al iniciar
        checkDatabaseStatus()
        // Cargar información de última actualización
        loadLastUpdateInfo()
    }

    /**
     * Consulta el stock disponible para un SKU específico.
     */
    fun searchStock(sku: String) {
        Log.w(TAG, "[DIAG] searchStock llamado con sku='$sku'")
        viewModelScope.launch {
            // Si la base de datos está vacía, mostrar mensaje apropiado
            val count = productoDao.countProductos()
            if (count == 0) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "No hay información cargada. Actualiza la información con un archivo Excel."
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, stockResult = null)

            stockRepository.getStockFullBySku(sku)
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al consultar stock"
                    )
                }
                .collectLatest { fullResult ->
                    val firstVariante = fullResult.variantes.firstOrNull()
                    val stockResult = StockQueryResult(
                        sku = firstVariante?.codigoHijo ?: fullResult.codigoPadre,
                        description = fullResult.categoria.ifBlank { "Prenda Nicopoly" },
                        stockAvailable = firstVariante?.stockBodega ?: 0,
                        hasStock = (firstVariante?.stockBodega ?: 0) > 0
                    )
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        stockResult = stockResult,
                        searchCounter = _uiState.value.searchCounter + 1
                    )
                }
        }
    }

    /**
     * Importa un archivo Excel seleccionado por el usuario.
     *
     * El archivo se lee desde un Uri del Storage Access Framework.
     * La importación se ejecuta en Dispatchers.IO para no bloquear la UI.
     * Los datos anteriores se reemplazan solo si la importación es exitosa.
     */
    fun importExcelFromUri(uri: android.net.Uri) {
        viewModelScope.launch {
            // Actualizar estado a "importando"
            _uiState.value = _uiState.value.copy(
                isImporting = true,
                importProgress = 0,
                importStage = "Validando archivo...",
                importResult = null,
                importError = null,
                isLoading = true
            )

            try {
                // Abrir el archivo seleccionado
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        isLoading = false,
                        importError = "No se pudo abrir el archivo seleccionado."
                    )
                    return@launch
                }

                // Validar extensión del nombre
                val fileName = getFileNameFromUri(uri)
                if (!fileName.isNullOrEmpty() && !fileName.endsWith(".xlsx", ignoreCase = true)) {
                    inputStream.close()
                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        isLoading = false,
                        importError = "El archivo seleccionado no es un Excel .xlsx válido."
                    )
                    return@launch
                }

                // Ejecutar la importación en IO con callback de progreso
                val stats = kotlinx.coroutines.withContext(Dispatchers.IO) {
                    excelImporter.importFromStream(inputStream) { progress, stage ->
                        _uiState.value = _uiState.value.copy(
                            isImporting = true,
                            importProgress = progress,
                            importStage = stage
                        )
                    }
                }

                if (stats.summaryMessage.startsWith("ERROR")) {
                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        isLoading = false,
                        importProgress = 0,
                        importStage = "",
                        importError = stats.summaryMessage.replace("ERROR: ", "")
                    )
                } else {
                    // Guardar metadatos de la importación exitosa
                    importMetadata.saveImportResult(
                        productCount = stats.productosInserted,
                        locationCount = stats.ubicacionesInserted
                    )

                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        isLoading = false,
                        importProgress = 100,
                        importStage = "",
                        importResult = "Información actualizada correctamente"
                    )

                    // Actualizar estado de la base de datos
                    checkDatabaseStatus()
                    // Cargar información de última actualización
                    loadLastUpdateInfo()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error importando Excel", e)
                _uiState.value = _uiState.value.copy(
                    isImporting = false,
                    isLoading = false,
                    importProgress = 0,
                    importStage = "",
                    importError = "Error al importar: ${e.message}"
                )
            }
        }
    }

    /**
     * Actualiza los datos desde Google Sheets mediante la API de Google Apps Script.
     *
     * Conecta a Internet, descarga los datos de reposición actuales,
     * los convierte al formato interno y reemplaza los datos en Room.
     *
     * Utiliza el mismo patrón de estado UI que importExcelFromUri():
     * - isImporting / importProgress / importStage para progreso
     * - importResult / importError para resultado final
     */
    fun importFromApi() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isImporting = true,
                importProgress = 0,
                importStage = "Actualizando",
                importResult = null,
                importError = null,
                isLoading = true
            )

            try {
                // Ejecutar la importación desde API en IO con callback de progreso
                val stats = kotlinx.coroutines.withContext(Dispatchers.IO) {
                    excelImporter.importFromGoogleSheets(onProgress = { progress, stage ->
                        _uiState.value = _uiState.value.copy(
                            isImporting = true,
                            importProgress = progress,
                            importStage = stage
                        )
                    })
                }

                if (stats.summaryMessage.startsWith("ERROR")) {
                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        isLoading = false,
                        importProgress = 0,
                        importStage = "",
                        importError = stats.summaryMessage.replace("ERROR: ", "")
                    )
                } else {
                    // Guardar metadatos de la importación exitosa
                    importMetadata.saveImportResult(
                        productCount = stats.productosInserted,
                        locationCount = stats.ubicacionesInserted
                    )

                    _uiState.value = _uiState.value.copy(
                        isImporting = false,
                        isLoading = false,
                        importProgress = 100,
                        importStage = "",
                        importResult = "Información actualizada"
                    )

                    // Actualizar estado de la base de datos
                    checkDatabaseStatus()
                    loadLastUpdateInfo()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error importando desde API", e)
                _uiState.value = _uiState.value.copy(
                    isImporting = false,
                    isLoading = false,
                    importProgress = 0,
                    importStage = "",
                    importError = "Error de conexión: ${e.message ?: "Desconocido"}"
                )
            }
        }
    }

    /**
     * Limpia solo el resultado de stock para evitar re-navegaciones
     * al restaurar SearchSkuScreen desde el back stack.
     */
    fun clearStockResult() {
        _uiState.value = _uiState.value.copy(stockResult = null)
    }

    /**
     * Reinicia el estado de la consulta.
     */
    fun resetState() {
        _uiState.value = _uiState.value.copy(
            stockResult = null,
            errorMessage = null,
            importResult = null,
            importError = null,
            isLoading = false,
            isImporting = false,
            importProgress = 0,
            importStage = "",
            searchCounter = _uiState.value.searchCounter + 1
        )
    }

    /**
     * Limpia el mensaje de resultado/error de importación.
     */
    fun clearImportResult() {
        _uiState.value = _uiState.value.copy(
            importResult = null,
            importError = null
        )
    }

    /**
     * Verifica si la base de datos tiene productos.
     */
    private fun checkDatabaseStatus() {
        viewModelScope.launch {
            val count = productoDao.countProductos()
            _uiState.value = _uiState.value.copy(
                databaseIsEmpty = count == 0
            )
        }
    }

    private fun loadLastUpdateInfo() {
        viewModelScope.launch {
            importMetadata.getImportInfo().collectLatest { info ->
                if (info.hasImportedData) {
                    val date = SimpleDateFormat("dd-M-yy", Locale.getDefault()).format(Date(info.lastImportTimestamp))
                    _uiState.value = _uiState.value.copy(
                        lastUpdateInfo = "Última actualización ($date)"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(lastUpdateInfo = null)
                }
            }
        }
    }

    /**
     * Obtiene el nombre del archivo desde un Uri.
     */
    private fun getFileNameFromUri(uri: android.net.Uri): String? {
        var fileName: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (index >= 0) {
                        fileName = it.getString(index)
                    }
                }
            }
        }
        if (fileName == null) {
            fileName = uri.lastPathSegment
        }
        return fileName
    }
}