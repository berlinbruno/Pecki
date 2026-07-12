package dev.berlinbruno.pecki.domain.transactions.usecases

import dev.berlinbruno.pecki.domain.transactions.TransactionRepository
import javax.inject.Inject

class DeleteTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transactionIds: List<String>) {
        transactionIds.forEach { id ->
            val transaction = repository.getTransactionById(id)
            transaction?.let { repository.deleteTransaction(it) }
        }
    }
}
