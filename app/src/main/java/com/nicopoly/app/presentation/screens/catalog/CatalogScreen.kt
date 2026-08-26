package com.nicopoly.app.presentation.screens.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nicopoly.app.presentation.components.CategoryFilterChip
import com.nicopoly.app.presentation.components.ProductCard
import com.nicopoly.app.presentation.components.ShowAllChip
import com.nicopoly.app.presentation.catalog.CatalogUiState
import com.nicopoly.app.presentation.catalog.CatalogViewModel

/**
 * Pantalla Catálogo de Nicopoly.
 *
 * Funcionalidades:
 * - Barra de búsqueda con toggle on/off.
 * - Filtros por categoría (chips horizontales).
 * - Grid de productos (2 columnas).
 * - Estados: carga, vacío, error.
 *
 * @param onProductClick Callback al tocar un producto.
 * @param viewModel ViewModel inyectado vía Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onProductClick: (String) -> Unit,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CatalogTopAppBar(
                isSearchActive = uiState.isSearchActive,
                searchQuery = uiState.searchQuery,
                onSearchToggle = { viewModel.setSearchActive(!uiState.isSearchActive) },
                onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                onSearchClear = { viewModel.onSearchQueryChanged("") }
            )
        }
    ) { innerPadding ->
        val error = uiState.error
        when {
            uiState.isLoading && uiState.products.isEmpty() -> LoadingState(
                modifier = Modifier.padding(innerPadding)
            )
            error != null && uiState.products.isEmpty() -> ErrorState(
                message = error,
                onRetry = { /* Recarga automática al cambiar filtro */ },
                modifier = Modifier.padding(innerPadding)
            )
            uiState.products.isEmpty() -> EmptyState(
                modifier = Modifier.padding(innerPadding)
            )
            else -> ContentState(
                state = uiState,
                onProductClick = onProductClick,
                onCategorySelect = { viewModel.selectCategory(it) },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

/**
 * Barra superior del catálogo con título o barra de búsqueda.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogTopAppBar(
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchToggle: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSearchClear: () -> Unit
) {
    if (isSearchActive) {
        // Modo búsqueda: barra de texto + botón cancelar
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        placeholder = { Text("Buscar productos…") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small,
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                    IconButton(onClick = onSearchClear) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                    }
                }
            },
            actions = {
                IconButton(onClick = onSearchToggle) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver al catálogo"
                    )
                }
            }
        )
    } else {
        // Modo normal: título + botón búsqueda
        TopAppBar(
            title = {
                Text(
                    text = "Catálogo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                IconButton(onClick = onSearchToggle) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                }
            }
        )
    }
}

/**
 * Contenido principal: filtros de categoría + grid de productos.
 */
@Composable
private fun ContentState(
    state: CatalogUiState,
    onProductClick: (String) -> Unit,
    onCategorySelect: (com.nicopoly.app.domain.model.Category?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Fila de categorías
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Chip "Todos"
            item {
                ShowAllChip(
                    isSelected = state.selectedCategory == null,
                    onClick = { onCategorySelect(null) }
                )
            }
            // Chips de categorías
            items(state.categories) { categoryInfo ->
                CategoryFilterChip(
                    categoryInfo = categoryInfo,
                    isSelected = state.selectedCategory == categoryInfo.category,
                    onClick = { onCategorySelect(categoryInfo.category) }
                )
            }
        }

        // Grid de productos
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.products) { product ->
                ProductCard(
                    product = product,
                    onClick = onProductClick
                )
            }
        }
    }
}

/**
 * Estado de carga: spinner centrado.
 */
@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            androidx.compose.material3.CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
            Text(
                text = "Cargando catálogo…",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Estado vacío: sin resultados.
 */
@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "🛍️",
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = "No se encontraron productos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Intenta con otra búsqueda o categoría",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Estado de error.
 */
@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            androidx.compose.material3.Button(onClick = onRetry) {
                Text("Intentar de nuevo")
            }
        }
    }
}
