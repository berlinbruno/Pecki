package dev.berlinbruno.pecki.domain.transactions.usecases

import dev.berlinbruno.pecki.domain.transactions.TransactionRepository
import javax.inject.Inject

class RestoreDefaultsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend fun restoreCategories() {
        repository.restoreDefaultCategories()
    }

    suspend fun restoreModes() {
        repository.restoreDefaultModes()
    }
}
