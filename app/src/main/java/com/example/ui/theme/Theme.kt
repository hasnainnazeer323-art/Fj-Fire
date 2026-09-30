package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FJFireDarkColorScheme = darkColorScheme(
  primary = FireOrange,
  onPrimary = Color.White,
  primaryContainer = Color(0xFF3B1A0E),
  onPrimaryContainer = Color(0xFFFFDBCF),
  secondary = FlameAmber,
  onSecondary = Color.Black,
  secondaryContainer = Color(0xFF332005),
  onSecondaryContainer = Color(0xFFFFE082),
  tertiary = ElectricYellow,
  onTertiary = Color.Black,
  background = DarkBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceElevated,
  onSurfaceVariant = TextSecondary,
  outline = CardBorder,
  error = FireRed,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // For FJ_Fire we want the authentic esports dark styling consistently
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = FJFireDarkColorScheme,
    typography = Typography,
    content = content
  )
}
