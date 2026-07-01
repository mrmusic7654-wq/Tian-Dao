package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LauncherDao {
    @Query("SELECT * FROM gesture_configs")
    fun getAllGestureConfigs(): Flow<List<GestureConfigEntity>>

    @Query("SELECT * FROM gesture_configs WHERE gestureId = :gestureId")
    suspend fun getGestureConfig(gestureId: String): GestureConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGestureConfig(config: GestureConfigEntity)

    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 50")
    fun getActivityLogs(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: ActivityLogEntity)

    @Query("DELETE FROM activity_logs")
    suspend fun clearActivityLogs()
}
