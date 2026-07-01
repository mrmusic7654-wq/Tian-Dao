package com.example.data.apps

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppManager(private val context: Context) {
    private val packageManager: PackageManager = context.packageManager

    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = try {
            packageManager.queryIntentActivities(mainIntent, 0)
        } catch (e: Exception) {
            Log.e("AppManager", "Error querying launcher apps", e)
            emptyList()
        }

        val ownPackageName = context.packageName

        resolveInfos.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            // Skip our own launcher app in the drawer
            if (packageName == ownPackageName) return@mapNotNull null

            val className = resolveInfo.activityInfo.name
            val label = resolveInfo.loadLabel(packageManager).toString()
            
            // Load and convert icon to Bitmap
            val iconDrawable = try {
                resolveInfo.loadIcon(packageManager)
            } catch (e: Exception) {
                packageManager.defaultActivityIcon
            }
            val bitmap = drawableToBitmap(iconDrawable)

            AppInfo(
                packageName = packageName,
                className = className,
                label = label,
                iconBitmap = bitmap
            )
        }.sortedBy { it.label.lowercase() }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return drawable.bitmap
        }

        // Handle case where drawable doesn't have intrinsic width/height (e.g., adaptive vector drawable)
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun launchApp(packageName: String) {
        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                context.startActivity(intent)
            } else {
                Log.e("AppManager", "Launch intent not found for package: $packageName")
            }
        } catch (e: Exception) {
            Log.e("AppManager", "Failed to launch app $packageName", e)
        }
    }
}
