package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.TestAttemptEntity
import com.example.data.model.TestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDao {
    @Query("SELECT * FROM tests ORDER BY createdAt DESC")
    fun getAllTests(): Flow<List<TestEntity>>

    @Query("SELECT * FROM tests WHERE id = :id LIMIT 1")
    suspend fun getTestById(id: Long): TestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: TestEntity): Long

    @Delete
    suspend fun deleteTest(test: TestEntity)

    // Test Attempts
    @Query("SELECT * FROM test_attempts ORDER BY completedAt DESC")
    fun getAllAttempts(): Flow<List<TestAttemptEntity>>

    @Query("SELECT * FROM test_attempts WHERE id = :id LIMIT 1")
    suspend fun getAttemptById(id: Long): TestAttemptEntity?

    @Query("SELECT * FROM test_attempts WHERE testId = :testId ORDER BY completedAt DESC")
    fun getAttemptsForTest(testId: Long): Flow<List<TestAttemptEntity>>

    @Query("SELECT COUNT(*) FROM test_attempts")
    fun getCompletedTestsCount(): Flow<Int>

    @Query("SELECT AVG(accuracyPercent) FROM test_attempts")
    fun getAverageAccuracy(): Flow<Float?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: TestAttemptEntity): Long
}
