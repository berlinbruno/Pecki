package dev.berlinbruno.pecki.domain.transactions.usecases

import dev.berlinbruno.pecki.domain.transactions.TransactionRepository
import dev.berlinbruno.pecki.domain.transactions.models.Mode
import javax.inject.Inject

class AddModeUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(mode: Mode) {
        repository.insertMode(mode)
    }
}

class DeleteModeUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(mode: Mode) {
        repository.deleteMode(mode)
    }
}
