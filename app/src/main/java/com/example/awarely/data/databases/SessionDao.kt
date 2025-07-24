package com.example.awarely.data.databases

import androidx.room.*
import com.example.awarely.model.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE startTime >= :startTime AND endTime <= :endTime ORDER BY startTime DESC")
    suspend fun getSessionsInTimeRange(startTime: Long, endTime: Long): List<SessionEntity>

    @Query("DELETE FROM sessions")
    suspend fun clearAllSessions()
}