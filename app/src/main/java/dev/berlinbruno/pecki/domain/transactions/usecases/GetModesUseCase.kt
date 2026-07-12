package dev.berlinbruno.pecki.domain.transactions.usecases

import dev.berlinbruno.pecki.domain.transactions.TransactionRepository
import dev.berlinbruno.pecki.domain.transactions.models.Mode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetModesUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Mode>> {
        return repository.getAllModes()
    }
}
