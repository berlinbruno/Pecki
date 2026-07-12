package dev.berlinbruno.pecki.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.berlinbruno.pecki.data.transactions.dao.CategoryDao
import dev.berlinbruno.pecki.data.transactions.dao.ModeDao
import dev.berlinbruno.pecki.data.transactions.dao.TransactionDao
import dev.berlinbruno.pecki.data.transactions.entities.CategoryEntity
import dev.berlinbruno.pecki.data.transactions.entities.ModeEntity
import dev.berlinbruno.pecki.data.transactions.entities.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        ModeEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PeckiDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun modeDao(): ModeDao

    companion object {
        const val DATABASE_NAME = "pecki_db"
    }
}
