package com.example.awarely.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.awarely.data.databases.AppDatabase
import com.example.awarely.model.UsageData
import kotlinx.coroutines.flow.Flow
import java.util.*
import java.util.concurrent.TimeUnit

class UsageViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val sessionDao = database.sessionDao()

    suspend fun getUsageDataForDays(days: Int): List<UsageData> {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val startTime = calendar.timeInMillis

        Log.d("Awarely", "Fetching usage sessions from $startTime to $endTime")

        val sessions = sessionDao.getSessionsInTimeRange(startTime, endTime)

        if (sessions.isEmpty()) {
            Log.d("Awarely", "No sessions found for the selected range.")
            return emptyList()
        }

        // Group by package name and calculate total duration
        val usageMap = sessions.groupBy { it.packageName }
            .mapValues { (_, sessions) ->
                sessions.sumOf { it.duration }
            }

        return usageMap.map { (packageName, totalDuration) ->
            // Get first session to extract app name safely
            val appName = sessions.firstOrNull { it.packageName == packageName }?.appName ?: packageName
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
