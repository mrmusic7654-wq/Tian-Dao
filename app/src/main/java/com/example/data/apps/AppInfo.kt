package com.example.data.apps

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

data class AppInfo(
    val packageName: String,
    val className: String,
    val label: String,
    val iconBitmap: Bitmap? = null
) {
    val imageBitmap: ImageBitmap? by lazy {
        iconBitmap?.asImageBitmap()
    }
}
