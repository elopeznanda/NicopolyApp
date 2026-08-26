package com.nicopoly.app.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt que proporciona la base de datos local de Nicopoly.
 *
 * Instancia única (Singleton) de [NicopolyDatabase] compartida en toda la aplicación.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNicopolyDatabase(
        @ApplicationContext context: Context
    ): com.nicopoly.app.data.local.NicopolyDatabase {
        return Room.databaseBuilder(
            context,
            com.nicopoly.app.data.local.NicopolyDatabase::class.java,
            "nicopoly_database"
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideProductoDao(
        database: com.nicopoly.app.data.local.NicopolyDatabase
    ): com.nicopoly.app.data.local.dao.ProductoDao {
        return database.productoDao()
    }

    @Provides
    @Singleton
    fun provideUbicacionDao(
        database: com.nicopoly.app.data.local.NicopolyDatabase
    ): com.nicopoly.app.data.local.dao.UbicacionDao {
        return database.ubicacionDao()
    }

    @Provides
    @Singleton
    fun provideImportMetadata(
        @ApplicationContext context: Context
    ): com.nicopoly.app.data.local.ImportMetadata {
        return com.nicopoly.app.data.local.ImportMetadata(context)
    }
}
