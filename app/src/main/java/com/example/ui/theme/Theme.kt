package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = StarballCyanPrimary,
    onPrimary = StarballNavyDark,
    primaryContainer = StarballNavySurfaceVariant,
    onPrimaryContainer = StarballCyanPrimary,
    secondary = StarballGold,
    onSecondary = StarballNavyDark,
    tertiary = StarballLiveRed,
    background = StarballNavyDark,
    onBackground = StarballTextPrimary,
    surface = StarballNavySurface,
    onSurface = StarballTextPrimary,
    surfaceVariant = StarballNavySurfaceVariant,
    onSurfaceVariant = StarballTextSecondary,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = StarballCyanSecondary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFFB45309),
    tertiary = StarballLiveRed,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to sleek sports dark mode
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
