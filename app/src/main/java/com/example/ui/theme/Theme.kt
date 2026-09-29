package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WarmSandColorScheme = lightColorScheme(
    primary = BloodRed,
    onPrimary = Color.White,
    primaryContainer = SoftRoseLight,
    onPrimaryContainer = DeepBlood,
    secondary = SoftRose,
    onSecondary = Color.White,
    secondaryContainer = SoftRoseLight,
    onSecondaryContainer = SoftRoseDark,
    tertiary = RareBloodAccent,
    onTertiary = Color.White,
    background = WarmSand,
    onBackground = TextPrimary,
    surface = WarmSand,
    onSurface = TextPrimary,
    surfaceVariant = WarmSandDark,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = CreamContainer,
    surfaceContainerHigh = Color.White,
    outline = OutlineColor
)

private val HighContrastLabDarkColorScheme = darkColorScheme(
    primary = LabDarkPrimary,
    onPrimary = Color.White,
    primaryContainer = BloodRed,
    onPrimaryContainer = Color.White,
    secondary = LabDarkSecondary,
    onSecondary = DeepBlood,
    secondaryContainer = Color(0xFF4A1820),
    onSecondaryContainer = Color(0xFFFFD9DC),
    tertiary = Color(0xFFCE93D8),
    onTertiary = Color(0xFF38004D),
    background = LabDarkBackground,
    onBackground = LabDarkTextPrimary,
    surface = LabDarkSurface,
    onSurface = LabDarkTextPrimary,
    surfaceVariant = LabDarkCard,
    onSurfaceVariant = LabDarkTextSecondary,
    surfaceContainer = LabDarkCard,
    surfaceContainerHigh = LabDarkCardHigh,
    outline = LabDarkOutline
)

@Composable
fun CrossMatchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) HighContrastLabDarkColorScheme else WarmSandColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
