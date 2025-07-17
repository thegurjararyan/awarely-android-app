package com.example.awarely.data.repository


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.awarely.model.SessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.example.awarely.data.databases.AppDatabase
class SessionViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = SessionRepository(db.sessionDao())

    val allSessions: Flow<List<SessionEntity>> = repository.getAllSessions()


    fun addSession(session: SessionEntity) {
        viewModelScope.launch {
            repository.insertSession(session)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearAllSessions()
        }
    }
}
