package dev.berlinbruno.pecki.domain.transactions.models

data class Transaction(
    val id: String,
    val title: String,
    val type: TransactionType,
    val amount: Double,
    val currency: String,
    val dateTime: Long,
    val merchant: String,
    val categoryId: String?,
    val accountId: String?,
    val note: String?,
    val referenceId: String?,
    val modeId: String?,
    val status: TransactionStatus,
    val source: TransactionSource,
    val hashValue: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
