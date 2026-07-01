package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val YinColorScheme = darkColorScheme(
    primary = YinPrimary,
    onPrimary = YinOnPrimary,
    secondary = YinSecondary,
    tertiary = YinTertiary,
    background = YinBackground,
    surface = YinSurface,
    onBackground = YinOnBackground,
    onSurface = YinOnBackground
)

private val YangColorScheme = lightColorScheme(
    primary = YangPrimary,
    onPrimary = YangOnPrimary,
    secondary = YangSecondary,
    tertiary = YangTertiary,
    background = YangBackground,
    surface = YangSurface,
    onBackground = YangOnBackground,
    onSurface = YangOnBackground
)

@Composable
fun TianDaoTheme(
    isYinMode: Boolean = true, // Yin Mode = Dark Mode, Yang Mode = Light Mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (isYinMode) YinColorScheme else YangColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

