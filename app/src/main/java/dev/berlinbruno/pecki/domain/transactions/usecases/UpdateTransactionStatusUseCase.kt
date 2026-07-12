package dev.berlinbruno.pecki.domain.transactions.usecases

import dev.berlinbruno.pecki.domain.transactions.TransactionRepository
import dev.berlinbruno.pecki.domain.transactions.models.TransactionStatus
import javax.inject.Inject

class UpdateTransactionStatusUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transactionId: String, status: TransactionStatus) {
        val transaction = repository.getTransactionById(transactionId) ?: return
        repository.updateTransaction(transaction.copy(status = status, updatedAt = System.currentTimeMillis()))
    }

    suspend fun invokeBulk(transactionIds: List<String>, status: TransactionStatus) {
        transactionIds.forEach { id ->
            invoke(id, status)
        }
    }
}
