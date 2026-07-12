package dev.berlinbruno.pecki.data.transactions.dao

import androidx.room.*
import dev.berlinbruno.pecki.data.transactions.entities.ModeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModeDao {
    @Query("SELECT * FROM modes")
    fun getAllModes(): Flow<List<ModeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMode(mode: ModeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModes(modes: List<ModeEntity>)

    @Query("DELETE FROM modes")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteMode(mode: ModeEntity)
}
