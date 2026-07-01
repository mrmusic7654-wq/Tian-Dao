package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.LauncherAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [GestureConfigEntity::class, ActivityLogEntity::class], version = 1, exportSchema = false)
abstract class LauncherDatabase : RoomDatabase() {
    abstract fun launcherDao(): LauncherDao

    companion object {
        @Volatile
        private var INSTANCE: LauncherDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): LauncherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LauncherDatabase::class.java,
                    "tian_dao_launcher_db"
                )
                .addCallback(LauncherDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class LauncherDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val dao = database.launcherDao()
                    // Pre-populate default gestures
                    dao.insertGestureConfig(GestureConfigEntity("swipe_up", LauncherAction.OPEN_DRAWER.name))
                    dao.insertGestureConfig(GestureConfigEntity("swipe_down", LauncherAction.OPEN_AI_CORE.name))
                    dao.insertGestureConfig(GestureConfigEntity("swipe_left", LauncherAction.NONE.name))
                    dao.insertGestureConfig(GestureConfigEntity("swipe_right", LauncherAction.NONE.name))
                    dao.insertGestureConfig(GestureConfigEntity("double_tap", LauncherAction.TOGGLE_THEME.name))
                    dao.insertGestureConfig(GestureConfigEntity("long_press", LauncherAction.NONE.name))
                    
                    // Add an initial log
                    dao.insertActivityLog(
                        ActivityLogEntity(
                            query = "System Initialization",
                            response = "Tian Dao Conscious Core activated. Yin-Yang energies balanced.",
                            actionExecuted = "INITIALIZE"
                        )
                    )
                }
            }
        }
    }
}
