package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MistakeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MistakeDao {
    @Query("SELECT * FROM mistakes WHERE isResolved = 0 ORDER BY recordedAt DESC")
    fun getUnresolvedMistakes(): Flow<List<MistakeEntity>>

    @Query("SELECT * FROM mistakes ORDER BY recordedAt DESC")
    fun getAllMistakes(): Flow<List<MistakeEntity>>

    @Query("SELECT * FROM mistakes WHERE subject = :subject AND isResolved = 0 ORDER BY recordedAt DESC")
    fun getMistakesBySubject(subject: String): Flow<List<MistakeEntity>>

    @Query("SELECT COUNT(*) FROM mistakes WHERE isResolved = 0")
    fun getUnresolvedMistakeCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistake(mistake: MistakeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistakes(mistakes: List<MistakeEntity>): List<Long>

    @Update
    suspend fun updateMistake(mistake: MistakeEntity)

    @Delete
    suspend fun deleteMistake(mistake: MistakeEntity)

    @Query("UPDATE mistakes SET isResolved = 1 WHERE id = :id")
    suspend fun markResolved(id: Long)
}
