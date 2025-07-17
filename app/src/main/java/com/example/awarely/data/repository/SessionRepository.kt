package com.example.awarely.data.repository

import com.example.awarely.data.databases.SessionDao
import com.example.awarely.model.SessionEntity
import kotlinx.coroutines.flow.Flow

class SessionRepository(private val sessionDao: SessionDao) {

    suspend fun insertSession(session: SessionEntity) {
        sessionDao.insertSession(session)
    }

    fun getAllSessions(): Flow<List<SessionEntity>> {
        return sessionDao.getAllSessions()
    }

    suspend fun clearAllSessions() {
        sessionDao.clearAllSessions()
    }
}
