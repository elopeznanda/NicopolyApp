package com.nicopoly.app.data.di

import com.nicopoly.app.data.repository.FakeAuthRepository
import com.nicopoly.app.data.repository.RoomStockRepository
import com.nicopoly.app.data.repository.ProductRepositoryImpl
import com.nicopoly.app.domain.repository.AuthRepository
import com.nicopoly.app.domain.repository.ProductRepository
import com.nicopoly.app.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Módulo Hilt que vincula las interfaces de repositorio con sus implementaciones.
 * Instalado en ViewModelComponent para que los repositorios vivan mientras viva el ViewModel.
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    abstract fun bindAuthRepository(
        impl: FakeAuthRepository
    ): AuthRepository

    @Binds
    abstract fun bindStockRepository(
        impl: RoomStockRepository
    ): StockRepository
}
