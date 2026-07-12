package dev.berlinbruno.pecki.domain.transactions

import dev.berlinbruno.pecki.domain.transactions.models.Category
import dev.berlinbruno.pecki.domain.transactions.models.Mode
import dev.berlinbruno.pecki.domain.transactions.models.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    suspend fun getTransactionById(id: String): Transaction?
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)

    fun getAllCategories(): Flow<List<Category>>
    suspend fun insertCategory(category: Category)
    suspend fun deleteCategory(category: Category)

    fun getAllModes(): Flow<List<Mode>>
    suspend fun insertMode(mode: Mode)
    suspend fun deleteMode(mode: Mode)
    suspend fun restoreDefaultCategories()
    suspend fun restoreDefaultModes()
    suspend fun clearAllData()
}
