package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ExcelGreenDarkPrimary,
    onPrimary = Color.Black,
    primaryContainer = ExcelGreenSecondary,
    onPrimaryContainer = Color.White,
    secondary = Amber600,
    onSecondary = Color.White,
    background = ExcelDarkBackground,
    surface = ExcelDarkSurface,
    surfaceVariant = ExcelDarkCard,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9),
    outline = Color(0xFF2A4232)
)

private val LightColorScheme = lightColorScheme(
    primary = ExcelGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = ExcelGreenLight,
    onPrimaryContainer = ExcelGreenDark,
    secondary = Amber600,
    onSecondary = Color.White,
    secondaryContainer = Amber100,
    onSecondaryContainer = Color(0xFF78350F),
    background = Color(0xFFF8FAF9),
    surface = Color.White,
    surfaceVariant = Color(0xFFF1F6F2),
    onBackground = Slate900,
    onSurface = Slate900,
    outline = Color(0xFFD4DFD7)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep distinctive Excel brand theme
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
