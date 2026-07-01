package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gesture_configs")
data class GestureConfigEntity(
    @PrimaryKey val gestureId: String, // "swipe_up", "swipe_down", "double_tap", "long_press"
    val actionName: String, // Name of LauncherAction enum
    val appPackageName: String? = null,
    val appLabel: String? = null
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val query: String,
    val response: String,
    val actionExecuted: String? = null,
    val isSuccess: Boolean = true
)
