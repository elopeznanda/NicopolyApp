package com.nicopoly.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nicopoly.app.domain.model.Category
import com.nicopoly.app.domain.repository.CategoryInfo

/**
 * Chip de categoría filtrable.
 */
@Composable
fun CategoryFilterChip(
    categoryInfo: CategoryInfo,
    isSelected: Boolean,
    onClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = { onClick(categoryInfo.category) },
        label = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                // Category emoji icon
                Text(
                    text = categoryInfo.category.iconEmoji,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                // Category name
                Text(
                    text = categoryInfo.category.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
                // Product count
                Text(
                    text = "${categoryInfo.productCount} productos",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        modifier = modifier.widthIn(min = 90.dp, max = 120.dp)
    )
}

/**
 * Chip "Todos" para mostrar todo el catálogo sin filtrar.
 */
@Composable
fun ShowAllChip(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🛍️",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = "Todos",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        },
        modifier = modifier.widthIn(min = 90.dp, max = 120.dp)
    )
}
