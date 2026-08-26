package com.nicopoly.app.presentation.screens.searchsku

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nicopoly.app.presentation.searchsku.SearchSkuViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Pantalla principal de búsqueda por SKU para empleados.
 *
 * Permite consultar el stock disponible en el Centro de Distribución
 * ingresando el código SKU de una prenda, e importar/actualizar datos
 * desde un archivo Excel seleccionado manualmente.
 */
@Composable
fun SearchSkuScreen(
    onNavigateToResult: (String) -> Unit,
    viewModel: SearchSkuViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navegar al resultado cuando se obtenga información de stock.
    // Se usa un key único (searchCounter) para que el efecto se ejecute
    // solo una vez por búsqueda. Después de navegar, stockResult se
    // limpia para evitar re-navegaciones al restaurar desde back stack.
    LaunchedEffect(uiState.searchCounter) {
        uiState.stockResult?.let { result ->
            onNavigateToResult(result.sku)
            viewModel.clearStockResult()
        }
    }

    SearchSkuContent(
        uiState = uiState,
        onSearch = viewModel::searchStock,
        onReset = viewModel::resetState,
        onImportExcel = viewModel::importExcelFromUri,
        onImportFromApi = viewModel::importFromApi,
        onClearImportResult = viewModel::clearImportResult
    )
}

/**
 * Contenido principal de la pantalla de búsqueda por SKU.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchSkuContent(
    uiState: com.nicopoly.app.presentation.searchsku.SearchSkuUiState,
    onSearch: (String) -> Unit,
    onReset: () -> Unit,
    onImportExcel: (Uri) -> Unit,
    onImportFromApi: () -> Unit,
    onClearImportResult: () -> Unit
) {
    var sku by rememberSaveable { mutableStateOf("") }

    // Launcher para seleccionar archivo Excel
    val excelPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onImportExcel(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Empty title to remove Nicopoly and Consulta de Stock
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Título ──
                // Eliminado texto "Buscar por SKU"

                // ── Campo SKU ──
                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it.uppercase() },
                    label = { Text("Ingrese Código") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (!uiState.isLoading && sku.isNotBlank()) {
                                onSearch(sku.trim())
                            }
                        }
                    ),
                    enabled = !(uiState.isLoading || uiState.isImporting)
                )

                // ── Mensaje de error ──
                if (uiState.errorMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = uiState.errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // ── Botón Buscar ──
                // El botón "Buscar" se oculta durante la importación
                if (!uiState.isImporting) {
                    val buttonEnabled = !uiState.isLoading && sku.isNotBlank()

                    Button(
                        onClick = { onSearch(sku.trim()) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = buttonEnabled
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Buscar")
                        }
                    }

                    // ── Botón Limpiar ──
                    if (sku.isNotBlank()) {
                        TextButton(
                            onClick = {
                                sku = ""
                                onReset()
                            },
                            enabled = !(uiState.isLoading || uiState.isImporting)
                        ) {
                            Text("Limpiar")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── Separador ──
                androidx.compose.material3.HorizontalDivider()

                // ── Información de última actualización ──
                if (uiState.lastUpdateInfo != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = uiState.lastUpdateInfo,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // ── Sección de importación ──
                ImportSection(
                    uiState = uiState,
                    onImportClick = { excelPickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") },
                    onImportFromApi = onImportFromApi,
                    onClearResult = onClearImportResult
                )
            }
        }
    }
}

/**
 * Sección de importación con botón de actualizar, progreso y resultados.
 */
@Composable
private fun ImportSection(
    uiState: com.nicopoly.app.presentation.searchsku.SearchSkuUiState,
    onImportClick: () -> Unit,
    onImportFromApi: () -> Unit,
    onClearResult: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Botón Actualizar información ──
        Button(
            onClick = onImportFromApi,
            modifier = Modifier.fillMaxWidth(),
            enabled = !(uiState.isLoading || uiState.isImporting),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                // Azul oscuro fijo + contenido blanco: contraste garantizado
                // tanto en modo claro como en modo oscuro.
                containerColor = Color(0xFF1565C0),
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Actualizar información")
        }

        // ── Progreso de importación ──
        if (uiState.isImporting || uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Mostrar animación de carga
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                }
                Text(
                    text = uiState.importStage.ifEmpty { "Importando..." },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ── Resultado exitoso ──
        if (uiState.importResult != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = uiState.importResult,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onClearResult) {
                        Text("Aceptar")
                    }
                }
            }
        }

        // ── Error de importación ──
        if (uiState.importError != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = uiState.importError,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onClearResult) {
                        Text("Aceptar")
                    }
                }
            }
        }

        // ── Estado de la base de datos ──
        if (uiState.databaseIsEmpty && uiState.importResult == null && uiState.importError == null && !uiState.isImporting) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "No hay información cargada. Actualice con un archivo Excel.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
