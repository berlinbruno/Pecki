package dev.berlinbruno.pecki.data.transactions.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.berlinbruno.pecki.domain.transactions.models.Mode

@Entity(tableName = "modes")
data class ModeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val icon: String?,
    val isSystem: Boolean
)

fun ModeEntity.toDomain() = Mode(
    id = id,
    name = name,
    icon = icon,
    isSystem = isSystem
)

fun Mode.toEntity() = ModeEntity(
    id = id,
    name = name,
    icon = icon,
    isSystem = isSystem
)
