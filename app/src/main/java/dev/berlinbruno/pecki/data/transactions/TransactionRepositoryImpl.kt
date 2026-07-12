package dev.berlinbruno.pecki.data.transactions

import dev.berlinbruno.pecki.data.transactions.dao.CategoryDao
import dev.berlinbruno.pecki.data.transactions.dao.ModeDao
import dev.berlinbruno.pecki.data.transactions.dao.TransactionDao
import dev.berlinbruno.pecki.data.transactions.defaults.DefaultData
import dev.berlinbruno.pecki.data.transactions.entities.toDomain
import dev.berlinbruno.pecki.data.transactions.entities.toEntity
import dev.berlinbruno.pecki.domain.transactions.TransactionRepository
import dev.berlinbruno.pecki.domain.transactions.models.Category
import dev.berlinbruno.pecki.domain.transactions.models.Mode
import dev.berlinbruno.pecki.domain.transactions.models.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val modeDao: ModeDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTransactionById(id: String): Transaction? {
        return transactionDao.getTransactionById(id)?.toDomain()
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category.toEntity())
    }

    override fun getAllModes(): Flow<List<Mode>> {
        return modeDao.getAllModes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertMode(mode: Mode) {
        modeDao.insertMode(mode.toEntity())
    }

    override suspend fun deleteMode(mode: Mode) {
        modeDao.deleteMode(mode.toEntity())
    }

    override suspend fun restoreDefaultCategories() {
        categoryDao.deleteAll()
        categoryDao.insertCategories(DefaultData.categories.map { it.toEntity() })
    }

    override suspend fun restoreDefaultModes() {
        modeDao.deleteAll()
        modeDao.insertModes(DefaultData.modes.map { it.toEntity() })
    }

    override suspend fun clearAllData() {
        transactionDao.deleteAll()
    }
}
