package dev.berlinbruno.pecki.data.transactions.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.berlinbruno.pecki.domain.transactions.models.*

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
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
    val hashValue: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    title = title,
    type = type,
    amount = amount,
    currency = currency,
    dateTime = dateTime,
    merchant = merchant,
    categoryId = categoryId,
    accountId = accountId,
    note = note,
    referenceId = referenceId,
    modeId = modeId,
    status = status,
    source = source,
    hashValue = hashValue,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    title = title,
    type = type,
    amount = amount,
    currency = currency,
    dateTime = dateTime,
    merchant = merchant,
    categoryId = categoryId,
    accountId = accountId,
    note = note,
    referenceId = referenceId,
    modeId = modeId,
    status = status,
    source = source,
    hashValue = hashValue,
    createdAt = createdAt,
    updatedAt = updatedAt
)
