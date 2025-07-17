package com.example.awarely.service

//noinspection SuspiciousImport
import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.util.Log
import com.example.awarely.overlay.FloatingTimerView
import com.example.awarely.model.SessionEntity
import com.example.awarely.data.databases.AppDatabase


import kotlinx.coroutines.*
import java.util.*

class TrackerService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var floatingTimerView: FloatingTimerView? = null
    private var sessionStartTime: Long = 0L
    private var currentApp: String? = null
    private var isTracking = false

    private val monitoredApps = setOf(
        "com.instagram.android",
        "com.facebook.katana",
        "com.snapchat.android",
        "com.whatsapp",
        "com.tiktok.android",
        "com.youtube.android"
    )

    override fun onCreate() {
        super.onCreate()
        floatingTimerView = FloatingTimerView(applicationContext)
        startForegroundService()
        startTrackingLoop()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        floatingTimerView?.hide()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundService() {
        val channelId = "awarely_channel"
        val channelName = "Awarely Tracker"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Awarely is monitoring")
            .setContentText("Tracking app usage")
            .setSmallIcon(R.drawable.ic_menu_view)
            .build()

        startForeground(1, notification)
    }

    private fun startTrackingLoop() {
        val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val repository = AppDatabase.getDatabase(applicationContext).sessionDao()

        scope.launch {
            while (true) {
                val endTime = System.currentTimeMillis()
                val startTime = endTime - 3000

                val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
                val event = UsageEvents.Event()

                var latestForegroundApp: String? = null

                while (usageEvents.hasNextEvent()) {
                    usageEvents.getNextEvent(event)
                    if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        latestForegroundApp = event.packageName
                    }
                }

                if (latestForegroundApp != null) {
                    if (monitoredApps.contains(latestForegroundApp)) {
                        if (!isTracking || currentApp != latestForegroundApp) {
                            sessionStartTime = System.currentTimeMillis()
                            currentApp = latestForegroundApp
                            isTracking = true
                            floatingTimerView?.show()
                            Log.d("Awarely", "Started tracking $currentApp")
                        }
                    } else if (isTracking && latestForegroundApp != currentApp) {
                        val sessionEndTime = System.currentTimeMillis()
                        val duration = (sessionEndTime - sessionStartTime) / 60000L

                        if (duration >= 1) {
                            val session = SessionEntity(
                                appName = currentApp ?: "Unknown",
                                packageName = currentApp ?: "Unknown",
                                startTime = sessionStartTime,
                                endTime = sessionEndTime,
                                durationMinutes = duration
                            )
                            repository.insertSession(session)
                            Log.d("Awarely", "Session saved for $currentApp: $duration minutes")
                        }

                        floatingTimerView?.hide()
                        currentApp = null
                        isTracking = false
                    }
                }

                delay(1500)
            }
        }
    }
}
