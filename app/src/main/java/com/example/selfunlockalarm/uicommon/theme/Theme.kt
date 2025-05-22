package com.example.selfunlockalarm.uicommon.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AppThemeColor.MdBluePrimary,
    secondary = AppThemeColor.MdPurpleSecondary,
    tertiary = AppThemeColor.MdPurpleSecondary,
    background = Color(0xFF121212),
    surface = Color(0xFF121212)
)

private val LightColorScheme = lightColorScheme(
    primary = AppThemeColor.MdBluePrimary,
    secondary = AppThemeColor.MdPurpleSecondary,
    tertiary = AppThemeColor.MdPurpleSecondary, // Or another accent
    background = AppThemeColor.MdBackground,
    surface = AppThemeColor.MdSurface,
    onPrimary = AppThemeColor.TextWhite,
    onSecondary = AppThemeColor.TextWhite,
    onTertiary = AppThemeColor.TextWhite,
    onBackground = Color(0xFF1C1B1F), // Default dark text on light background
    onSurface = Color(0xFF1C1B1F),   // Default dark text on light surface
    primaryContainer = AppThemeColor.MdBluePrimary, // For Switch checked color
    surfaceVariant = AppThemeColor.CardBg, // For Card background if needed directly
    outline = AppThemeColor.BorderBlueLight // For OutlinedButton border
)

@Composable
fun SelfUnlockAlarmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}