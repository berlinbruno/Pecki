package dev.berlinbruno.pecki.domain.transactions.usecases

import dev.berlinbruno.pecki.domain.transactions.TransactionRepository
import dev.berlinbruno.pecki.domain.transactions.models.Category
import dev.berlinbruno.pecki.domain.transactions.models.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(type: TransactionType? = null): Flow<List<Category>> {
        return repository.getAllCategories().map { categories ->
            if (type != null) {
                categories.filter { it.type == type }
            } else {
                categories
            }
        }
    }
}
