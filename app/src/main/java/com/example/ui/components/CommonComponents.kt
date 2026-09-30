package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.net.URLEncoder

@Composable
fun FJFireLogo(
  modifier: Modifier = Modifier,
  size: Int = 40,
  showText: Boolean = true
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(size.dp)
        .clip(RoundedCornerShape((size / 4).dp))
        .background(
          brush = Brush.radialGradient(
            colors = listOf(Color(0xFF351206), Color(0xFF0F0B08))
          )
        )
        .border(
          width = 1.5.dp,
          brush = FireGradient,
          shape = RoundedCornerShape((size / 4).dp)
        )
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Filled.LocalFireDepartment,
          contentDescription = "FJ_Fire Flame",
          tint = FireOrange,
          modifier = Modifier.size((size * 0.75).dp)
        )
      }
    }

    if (showText) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "FJ_",
            fontWeight = FontWeight.Black,
            fontSize = (size * 0.45).sp,
            color = Color.White,
            letterSpacing = 1.sp
          )
          Text(
            text = "FIRE",
            fontWeight = FontWeight.Black,
            fontSize = (size * 0.45).sp,
            color = FireOrange,
            letterSpacing = 1.sp
          )
        }
        Text(
          text = "PLAY • COMPETE • WIN",
          fontWeight = FontWeight.Bold,
          fontSize = 8.sp,
          color = FlameAmber,
          letterSpacing = 1.5.sp
        )
      }
    }
  }
}

@Composable
fun FJFireHeader(
  onNotificationsClick: () -> Unit,
  onProfileClick: () -> Unit,
  unreadNotifications: Int = 0,
  modifier: Modifier = Modifier
) {
  Surface(
    color = DarkSurface,
    tonalElevation = 4.dp,
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      FJFireLogo(size = 38)

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        IconButton(
          onClick = onNotificationsClick,
          modifier = Modifier
            .minimumInteractiveComponentSize()
            .testTag("notification_button")
        ) {
          BadgedBox(
            badge = {
              if (unreadNotifications > 0) {
                Badge(
                  containerColor = FireRed,
                  contentColor = Color.White
                ) {
                  Text(
                    text = unreadNotifications.toString(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          ) {
            Icon(
              imageVector = Icons.Default.Notifications,
              contentDescription = "Notifications",
              tint = TextPrimary
            )
          }
        }

        IconButton(
          onClick = onProfileClick,
          modifier = Modifier
            .minimumInteractiveComponentSize()
            .testTag("header_profile_button")
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(DarkSurfaceElevated)
              .border(1.dp, FlameAmber, CircleShape)
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = "My Profile",
              tint = FlameAmber,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun EsportsCard(
  modifier: Modifier = Modifier,
  borderBrush: Brush = Brush.linearGradient(listOf(CardBorderHighlight, CardBorder)),
  content: @Composable ColumnScope.() -> Unit
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, borderBrush),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      content = content
    )
  }
}

@Composable
fun StatusBadge(
  status: String,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor, text) = when (status) {
    "REGISTRATION_OPEN" -> Triple(Color(0xFF0F3924), SuccessGreen, "REGISTRATION OPEN")
    "REGISTRATION_CLOSED" -> Triple(Color(0xFF33230A), WarningYellow, "REGISTRATION CLOSED")
    "LIVE" -> Triple(Color(0xFF450D0D), FireRed, "● LIVE NOW")
    "COMPLETED" -> Triple(Color(0xFF1E293B), Color(0xFF94A3B8), "COMPLETED")
    "CONFIRMED" -> Triple(Color(0xFF0F3924), SuccessGreen, "CONFIRMED")
    "PENDING" -> Triple(Color(0xFF33230A), WarningYellow, "PENDING")
    "PROCESSING" -> Triple(Color(0xFF1E3A8A), Color(0xFF60A5FA), "PROCESSING")
    "AVAILABLE" -> Triple(Color(0xFF0F3924), SuccessGreen, "AVAILABLE")
    "REJECTED", "CANCELLED" -> Triple(Color(0xFF3F1313), ErrorRed, status)
    else -> Triple(DarkSurfaceElevated, TextSecondary, status)
  }

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Text(
      text = text,
      color = textColor,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.5.sp
    )
  }
}

@Composable
fun CopyableDetailRow(
  label: String,
  value: String,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(DarkBackground)
      .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    Column {
      Text(
        text = label,
        fontSize = 11.sp,
        color = TextMuted,
        fontWeight = FontWeight.Medium
      )
      Text(
        text = value,
        fontSize = 15.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
      )
    }

    IconButton(
      onClick = {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, value)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
      },
      modifier = Modifier
        .minimumInteractiveComponentSize()
        .testTag("copy_${label.lowercase().replace(" ", "_")}")
    ) {
      Icon(
        imageVector = Icons.Default.ContentCopy,
        contentDescription = "Copy $label",
        tint = FlameAmber,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

fun openWhatsAppChat(context: Context, phoneNumber: String, message: String) {
  try {
    val cleanNumber = phoneNumber.replace("+", "").replace(" ", "").replace("-", "")
    val encodedMsg = URLEncoder.encode(message, "UTF-8")
    val url = "https://api.whatsapp.com/send?phone=$cleanNumber&text=$encodedMsg"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
  } catch (e: Exception) {
    Toast.makeText(context, "WhatsApp not installed. Contact number: $phoneNumber", Toast.LENGTH_LONG).show()
  }
}
