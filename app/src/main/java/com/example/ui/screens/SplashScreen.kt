package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  isLoggedIn: Boolean,
  onSplashFinished: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scale"
  )

  LaunchedEffect(Unit) {
    delay(2400)
    onSplashFinished()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkBackground)
      .testTag("splash_screen"),
    contentAlignment = Alignment.Center
  ) {
    // Fire Glow Ambient
    Box(
      modifier = Modifier
        .size(320.dp)
        .scale(pulseScale)
        .background(
          brush = Brush.radialGradient(
            colors = listOf(Color(0x33FF5722), Color(0x11FF9800), Color.Transparent)
          ),
          shape = CircleShape
        )
    )

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(24.dp)
    ) {
      // Big Fiery Emblem
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(110.dp)
          .scale(pulseScale)
          .clip(RoundedCornerShape(28.dp))
          .background(
            brush = Brush.radialGradient(
              colors = listOf(Color(0xFF3C1408), Color(0xFF140D0B))
            )
          )
          .border(
            width = 2.5.dp,
            brush = FireGradient,
            shape = RoundedCornerShape(28.dp)
          )
      ) {
        Icon(
          imageVector = Icons.Filled.LocalFireDepartment,
          contentDescription = "FJ_Fire",
          tint = FireOrange,
          modifier = Modifier.size(68.dp)
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "FJ_",
          fontWeight = FontWeight.Black,
          fontSize = 38.sp,
          color = Color.White,
          letterSpacing = 1.5.sp
        )
        Text(
          text = "FIRE",
          fontWeight = FontWeight.Black,
          fontSize = 38.sp,
          color = FireOrange,
          letterSpacing = 1.5.sp
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "PLAY • COMPETE • WIN",
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = ElectricYellow,
        letterSpacing = 2.5.sp
      )

      Spacer(modifier = Modifier.height(16.dp))

      Surface(
        color = Color(0x22FF5722),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FF5722))
      ) {
        Text(
          text = "ONLY FREE FIRE TOURNAMENTS",
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp,
          color = FlameAmber,
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
      }

      Spacer(modifier = Modifier.height(48.dp))

      CircularProgressIndicator(
        color = FireOrange,
        strokeWidth = 2.5.dp,
        modifier = Modifier.size(28.dp)
      )
    }

    // Bottom Tagline
    Text(
      text = "100% Free Entry Esports Platform",
      color = TextMuted,
      fontSize = 12.sp,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .navigationBarsPadding()
        .padding(bottom = 24.dp)
    )
  }
}
