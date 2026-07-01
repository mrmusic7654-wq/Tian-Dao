package com.example.data.database

import kotlinx.coroutines.flow.Flow

class LauncherRepository(private val launcherDao: LauncherDao) {
    val allGestureConfigs: Flow<List<GestureConfigEntity>> = launcherDao.getAllGestureConfigs()
    val allActivityLogs: Flow<List<ActivityLogEntity>> = launcherDao.getActivityLogs()

    suspend fun getGestureConfig(gestureId: String): GestureConfigEntity? {
        return launcherDao.getGestureConfig(gestureId)
    }

    suspend fun updateGestureConfig(config: GestureConfigEntity) {
        launcherDao.insertGestureConfig(config)
    }

    suspend fun insertActivityLog(log: ActivityLogEntity) {
        launcherDao.insertActivityLog(log)
    }

    suspend fun clearActivityLogs() {
        launcherDao.clearActivityLogs()
    }
}
