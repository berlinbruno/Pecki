package dev.berlinbruno.pecki.domain.transactions.models

data class Category(
    val id: String,
    val name: String,
    val type: TransactionType?,
    val icon: String?,
    val color: Int?,
    val keywords: List<String> = emptyList(),
    val isSystem: Boolean = false
)
