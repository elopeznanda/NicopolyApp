package com.nicopoly.app.data.repository

import com.nicopoly.app.domain.model.StockQueryResult
import com.nicopoly.app.domain.model.StockResultFull
import com.nicopoly.app.domain.model.VarianteStock
import com.nicopoly.app.domain.repository.StockRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementación fake del repositorio de consulta de stock.
 *
 * Simula la consulta de inventario sin conectar con SAP ni ningún backend real.
 * En futuras versiones se reemplazará por una implementación que consulte
 * la API de SAP u otro servicio externo.
 *
 * Genera datos simulados basados en el SKU ingresado:
 * - Si el SKU es válido (no vacío), retorna un resultado simulado.
 * - Si el SKU es inválido, retorna un error.
 */
@ViewModelScoped
class FakeStockRepository @Inject constructor() : StockRepository {

    override fun getStockBySku(sku: String): Flow<StockQueryResult> = flow {
        // Simular delay de red
        kotlinx.coroutines.delay(800)

        if (sku.isBlank()) {
            throw IllegalArgumentException("El SKU no puede estar vacío")
        }

        // Simular resultado basado en el SKU
        val stock = when {
            sku.uppercase().contains("0") -> 0  // SKU con "0" → sin stock
            else -> (1..100).random()           // Otros SKU → stock aleatorio
        }

        emit(
            StockQueryResult(
                sku = sku.uppercase(),
                description = generateDescription(sku),
                stockAvailable = stock,
                hasStock = stock > 0
            )
        )
    }

    override fun getStockBySkuSafe(sku: String): Flow<Result<StockQueryResult>> = flow {
        try {
            getStockBySku(sku).collect { result ->
                emit(Result.success(result))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getStockFullBySku(sku: String): Flow<StockResultFull> = flow {
        // Simular delay de red
        kotlinx.coroutines.delay(800)

        if (sku.isBlank()) {
            throw IllegalArgumentException("El SKU no puede estar vacío")
        }

        // Generar datos simulados
        val variantes = mutableListOf<VarianteStock>()
        for (i in 1..3) {
            variantes.add(
                VarianteStock(
                    codigoHijo = "$sku-$i",
                    color = "Color $i",
                    talla = "Talla $i",
                    stockBodega = (0..50).random(),
                    stockProvi1 = (0..50).random(),
                    stockFilomena = (0..50).random(),
                    stockProvi2 = (0..50).random()
                )
            )
        }

        emit(
            StockResultFull(
                codigoPadre = sku,
                temporada = "Temporada 2023",
                ubicacion = "Ubicación $sku",
                categoria = "Categoria Simulada",
                precioTiendas = (100..500).random().toDouble(),
                precioInicial = (100..500).random().toDouble(),
                precioMayor = (100..500).random().toDouble(),
                t060Total = variantes.sumOf { it.stockProvi2 },
                casaMatrizTotal = variantes.sumOf { it.stockBodega },
                t011Total = variantes.sumOf { it.stockFilomena },
                variantes = variantes
            )
        )
    }

    override fun getStockFullBySkuSafe(sku: String): Flow<Result<StockResultFull>> = flow {
        try {
            getStockFullBySku(sku).collect { result ->
                emit(Result.success(result))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun generateDescription(sku: String): String {
        return "Prenda simulada - $sku"
    }
}