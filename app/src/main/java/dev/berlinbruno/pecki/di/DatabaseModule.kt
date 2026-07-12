package dev.berlinbruno.pecki.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.berlinbruno.pecki.data.PeckiDatabase
import dev.berlinbruno.pecki.data.DatabaseCallback
import dev.berlinbruno.pecki.data.transactions.dao.CategoryDao
import dev.berlinbruno.pecki.data.transactions.dao.ModeDao
import dev.berlinbruno.pecki.data.transactions.dao.TransactionDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob())
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        callback: DatabaseCallback
    ): PeckiDatabase {
        return Room.databaseBuilder(
            context,
            PeckiDatabase::class.java,
            PeckiDatabase.DATABASE_NAME
        )
        .addCallback(callback)
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: PeckiDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: PeckiDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideModeDao(database: PeckiDatabase): ModeDao {
        return database.modeDao()
    }
}
