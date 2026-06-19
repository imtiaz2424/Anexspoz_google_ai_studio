package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF81C784),      // High visibility light green for dark mode
    secondary = Color(0xFFA5D6A7),
    tertiary = Color(0xFFC8E6C9),
    background = Color(0xFF121212),     // True deep slate theme backgrounds
    surface = Color(0xFF1E1E1E),
    onPrimary = Color(0xFF1B5E20),
    onSecondary = Color(0xFF1B5E20),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFEEEEEE),
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color(0xFFE8F5E9)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Color(0xFF2E7D32),       // Signature ANEXSOPZ Green
    secondary = Color(0xFF4CAF50),
    tertiary = Color(0xFF81C784),
    background = Color(0xFFFAFAFA),    // Clean paper-white backgrounds
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121),
    primaryContainer = Color(0xFFE8F5E9),
    onPrimaryContainer = Color(0xFF2E7D32)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
