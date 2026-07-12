package dev.berlinbruno.pecki.data.transactions.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.berlinbruno.pecki.domain.transactions.models.Category
import dev.berlinbruno.pecki.domain.transactions.models.TransactionType

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: TransactionType?,
    val icon: String?,
    val color: Int?,
    val keywords: List<String>,
    val isSystem: Boolean
)

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    type = type,
    icon = icon,
    color = color,
    keywords = keywords,
    isSystem = isSystem
)

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    type = type,
    icon = icon,
    color = color,
    keywords = keywords,
    isSystem = isSystem
)
