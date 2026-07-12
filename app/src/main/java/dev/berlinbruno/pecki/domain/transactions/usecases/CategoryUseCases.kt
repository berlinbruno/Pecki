package dev.berlinbruno.pecki.domain.transactions.usecases

import dev.berlinbruno.pecki.domain.transactions.TransactionRepository
import dev.berlinbruno.pecki.domain.transactions.models.Category
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(category: Category) {
        repository.insertCategory(category)
    }
}

class DeleteCategoryUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(category: Category) {
        repository.deleteCategory(category)
    }
}
