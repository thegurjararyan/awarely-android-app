package com.example.awarely.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.awarely.data.databases.AppDatabase
import com.example.awarely.model.UsageData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class UsageViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val sessionDao = database.sessionDao()

    suspend fun getUsageDataForDays(days: Int): List<UsageData> {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis

        // Calculate start time based on days
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val startTime = calendar.timeInMillis

        // Get sessions within the time range
        val sessions = sessionDao.getSessionsInTimeRange(startTime, endTime)

        // Group by package name and calculate total usage
        val usageMap = sessions.groupBy { it.packageName }
            .mapValues { (_, sessions) ->
                sessions.sumOf { it.duration }
            }

        // Convert to UsageData list and sort by usage time
        return usageMap.map { (packageName, totalDuration) ->
            val appName = sessions.find { it.packageName == packageName }?.appName ?: packageName
            UsageData(
                appName = appName,
                packageName = packageName,
                usageTime = totalDuration,
                usageTimeFormatted = formatDuration(totalDuration)
            )
        }.sortedByDescending { it.usageTime }
    }

    private fun formatDuration(milliseconds: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60

        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }
}