package com.example.awarely.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val startTime: Long,
    val endTime: Long,
    val duration: Long = endTime - startTime, // Add duration property
    val openCount: Int = 1,
    val durationMinutes: Long // Add durationMinutes property
)