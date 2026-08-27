package com.nicopoly.app.presentation.screens.stockresult

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nicopoly.app.presentation.stockresult.StockResultViewModel

/**
 * Pantalla de resultado de consulta de stock.
 *
 * Muestra toda la información del producto consultado:
 * - Código padre y ubicación en bodega
 * - Categoría y precios
 * - Tabla completa de variantes con stocks individuales
 *
 * @param sku Código SKU recibido desde la navegación.
 * @param onNavigateBack Callback para volver a la pantalla de búsqueda.
 * @param viewModel ViewModel inyectado vía Hilt.
 */
@Composable
fun StockResultScreen(
    sku: String,
    onNavigateBack: () -> Unit,
    viewModel: StockResultViewModel = hiltViewModel()
) {

    // Cargar datos al abrir la pantalla
    LaunchedEffect(sku) {
        viewModel.loadStock(sku)
    }

    val uiState by viewModel.uiState.collectAsState()

    StockResultContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack
    )
}

/**
 * Contenido principal de la pantalla de resultado.
 */
@Composable
private fun StockResultContent(
    uiState: com.nicopoly.app.presentation.stockresult.StockResultUiState,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                when {
                    uiState.isLoading -> LoadingState()
                    uiState.errorMessage != null -> ErrorState(uiState.errorMessage!!)
                    uiState.stockResultFull != null -> FullResultState(
                        result = uiState.stockResultFull!!,
                        onNavigateBack = onNavigateBack
                    )
                }
            }
        }
    )
}

/**
 * Estado de carga.
 */
@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Estado de error.
 */
@Composable
private fun ErrorState(errorMessage: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = errorMessage)
    }
}

// ---------------------------------------------------------------------------
// Sistema visual compartido por todas las tarjetas de la pantalla.
// ---------------------------------------------------------------------------

/** Radio de esquinas común a todas las tarjetas. */
private val CardShape = RoundedCornerShape(20.dp)

/** Bordes redondeados para los contenedores internos (chips, iconos). */
private val ChipShape = RoundedCornerShape(12.dp)

/**
 * Contenedor base de tarjeta: mismo fondo, borde y elevación en toda la pantalla.
 */
@Composable
private fun ResultCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        content()
    }
}

/**
 * Encabezado de tarjeta: icono dentro de un chip con tinte primario + etiqueta en mayúsculas.
 */
@Composable
private fun CardHeader(
    icon: ImageVector,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(ChipShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Valor principal de una tarjeta: tipografía grande y peso extra-bold.
 */
@Composable
private fun CardValue(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = color,
        fontWeight = FontWeight.ExtraBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

/**
 * Texto secundario de una tarjeta.
 */
@Composable
private fun CardSecondary(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = color,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

/**
 * Tarjeta de ubicación.
 */
@Composable
private fun LocationCard(ubicacion: String?) {
    ResultCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            CardHeader(icon = Icons.Default.LocationOn, label = "UBICACIÓN")
            Spacer(modifier = Modifier.height(10.dp))
            CardValue(text = ubicacion ?: "No registrada", color = MaterialTheme.colorScheme.primary)
        }
    }
}

/**
 * Tarjeta de stock online (T060).
 */
@Composable
private fun OnlineCard(t060Total: Int, casaMatrizTotal: Int) {
    val isOnline = t060Total > 0
    ResultCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                CardHeader(icon = Icons.Default.Inventory2, label = "ONLINE")
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isOnline) "Sí ✓" else "No ✕",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (isOnline) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    CardSecondary(text = "Total = ${if (isOnline) t060Total else casaMatrizTotal}")
                }
            }
        }
    }
}

/**
 * Tarjeta de categoría.
 */
@Composable
private fun CategoryCard(categoria: String) {
    ResultCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            CardHeader(icon = Icons.Default.Category, label = "CATEGORÍA")
            Spacer(modifier = Modifier.height(10.dp))
            CardValue(text = categoria, color = MaterialTheme.colorScheme.primary)
        }
    }
}

/**
 * Tarjeta de precios: TIENDAS e INICIAL en una sola fila.
 */
@Composable
private fun PricesCard(precioTiendas: Double, precioInicial: Double) {
    ResultCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            CardHeader(icon = Icons.Default.Inventory2, label = "PRECIOS")
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriceCell(label = "$ TIENDAS", value = formatPrecio(precioTiendas), weight = 1f)
                Box(
                    modifier = Modifier
                        .width(0.5.dp)
                        .height(44.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                )
                PriceCell(label = "$ INICIAL", value = formatPrecio(precioInicial), weight = 1f)
            }
        }
    }
}

/**
 * Celda individual de precio dentro de la tarjeta de precios.
 */
@Composable
private fun RowScope.PriceCell(
    label: String,
    value: String,
    weight: Float
) {
    Column(
        modifier = Modifier.weight(weight),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CardSecondary(text = label)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Tarjeta de precio mayorista. Solo se muestra cuando existe precio mayor.
 */
@Composable
private fun WholesaleCard(precioMayor: Double) {
    if (precioMayor > 0) {
        ResultCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CardHeader(icon = Icons.Default.Inventory2, label = "$ MAYORISTA")
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = formatPrecio(precioMayor),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Remove StockTable function, it will be inlined in FullResultState

/**
 * Encabezado de columna de stock.
 */
@Composable
private fun RowScope.StockColumnHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Center
    )
}

/**
 * Celda de valor de stock.
 */
@Composable
private fun RowScope.StockCell(value: Int) {
    Text(
        text = value.toString(),
        style = MaterialTheme.typography.bodySmall,
        fontWeight = if (value > 0) FontWeight.SemiBold else FontWeight.Normal,
        color = if (value > 0) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        },
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Center
    )
}

/**
 * Formatea un precio para mostrarlo con separador de miles y sin decimales.
 */
private fun formatPrecio(precio: Double): String {
    return if (precio == 0.0) {
        "$0"
    } else {
        val formatter = java.text.NumberFormat.getInstance(java.util.Locale("es", "CL"))
        formatter.isGroupingUsed = true
        formatter.maximumFractionDigits = 0
        "${formatter.format(precio.toLong())}"
    }
}

/**
 * Convierte el código de temporada a formato legible.
 */
private fun convertTemporada(temporada: String): String {
    return when (temporada.uppercase().trim()) {
        "FW25" -> "Invierno 2025"
        "SS25" -> "Verano 2025"
        "FW26" -> "Invierno 2026"
        "SS26" -> "Verano 2026"
        "FW27" -> "Invierno 2027"
        "SS27" -> "Verano 2027"
        else -> "Temporada no registrada"
    }
}

/**
 * Pantalla completa de resultado.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FullResultState(
    result: com.nicopoly.app.domain.model.StockResultFull,
    onNavigateBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Encabezado: código padre + chip de temporada
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = result.codigoPadre,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold
                )
                result.temporada?.let { temporada ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = ChipShape,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Text(
                            text = convertTemporada(temporada),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            LocationCard(ubicacion = result.ubicacion)
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            OnlineCard(t060Total = result.t060Total, casaMatrizTotal = result.casaMatrizTotal)
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            CategoryCard(categoria = result.categoria)
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            PricesCard(precioTiendas = result.precioTiendas, precioInicial = result.precioInicial)
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            WholesaleCard(precioMayor = result.precioMayor)
            Spacer(modifier = Modifier.height(10.dp))
        }

        stickyHeader {
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 8.dp)) {
                    CardHeader(icon = Icons.Default.Inventory2, label = "STOCKS")
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "SKU",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(2f),
                            textAlign = TextAlign.Start
                        )
                        StockColumnHeader("BODEGA")
                        StockColumnHeader("P1")
                        StockColumnHeader("F")
                        StockColumnHeader("P2")
                    }
                }
            }
        }

        itemsIndexed(result.variantes) { index, variante ->
            val isLast = index == result.variantes.lastIndex
            val shape = if (isLast) {
                RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
            } else {
                androidx.compose.ui.graphics.RectangleShape
            }

            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = shape,
                elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = variante.codigoHijo,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(2f),
                            maxLines = 1,
                            textDecoration = null,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        StockCell(variante.stockBodega)
                        StockCell(variante.stockProvi1)
                        StockCell(variante.stockFilomena)
                        StockCell(variante.stockProvi2)
                    }
                    if (!isLast) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.5.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        )
                    } else {
                        Spacer(modifier = Modifier.height(14.dp)) // padding bottom for the last card part
                    }
                }
            }
        }
    }
}
