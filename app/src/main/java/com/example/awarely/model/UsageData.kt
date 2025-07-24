package com.example.awarely.model

data class UsageData(
    val appName: String,
    val packageName: String,
    val usageTime: Long, // in milliseconds
    val usageTimeFormatted: String
)