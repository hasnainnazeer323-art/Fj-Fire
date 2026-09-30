package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// FJ_Fire Signature Esports Palette
val FireRed = Color(0xFFFF2A14)
val FireOrange = Color(0xFFFF5E00)
val FlameAmber = Color(0xFFFF9800)
val ElectricYellow = Color(0xFFFFD600)

val DarkBackground = Color(0xFF090C12)
val DarkSurface = Color(0xFF111622)
val DarkSurfaceElevated = Color(0xFF182030)
val DarkSurfaceCard = Color(0xFF1E273A)
val CardBorder = Color(0xFF2C384E)
val CardBorderHighlight = Color(0x66FF5E00)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val SuccessGreen = Color(0xFF10B981)
val WarningYellow = Color(0xFFF59E0B)
val ErrorRed = Color(0xFFEF4444)

// Gaming Gradients
val FireGradient = Brush.horizontalGradient(
  colors = listOf(FireRed, FireOrange, FlameAmber)
)

val FireVerticalGradient = Brush.verticalGradient(
  colors = listOf(FireOrange, FireRed)
)

val CardGlassGradient = Brush.verticalGradient(
  colors = listOf(Color(0xFF1A2334), Color(0xFF0F1522))
)

val GoldTrophyGradient = Brush.linearGradient(
  colors = listOf(Color(0xFFFFD700), Color(0xFFFF9800))
)
