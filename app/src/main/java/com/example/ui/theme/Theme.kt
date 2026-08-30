package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AcademiaBlueLight,
    onPrimary = AcademiaBlack,
    primaryContainer = AcademiaBluePrimary,
    onPrimaryContainer = AcademiaWhite,
    secondary = AcademiaSkyBlue,
    onSecondary = AcademiaBlack,
    secondaryContainer = Color(0xFF0369A1),
    onSecondaryContainer = AcademiaWhite,
    tertiary = AcademiaCyanAccent,
    onTertiary = AcademiaBlack,
    background = AcademiaBlack,
    onBackground = Color(0xFFF1F5F9),
    surface = AcademiaDarkSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = AcademiaDarkCard,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = AcademiaDarkBorder,
    error = AcademiaError,
    onError = AcademiaWhite
)

private val LightColorScheme = lightColorScheme(
    primary = AcademiaBluePrimary,
    onPrimary = AcademiaWhite,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = AcademiaBlueDark,
    secondary = AcademiaElectricBlue,
    onSecondary = AcademiaWhite,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = AcademiaCyanAccent,
    onTertiary = AcademiaWhite,
    background = AcademiaLightBackground,
    onBackground = Color(0xFF0F172A),
    surface = AcademiaLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = AcademiaLightCard,
    onSurfaceVariant = Color(0xFF475569),
    outline = AcademiaLightBorder,
    error = AcademiaError,
    onError = AcademiaWhite
)

@Composable
fun AcademiaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
