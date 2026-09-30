package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.NotificationEntity
import com.example.ui.theme.*

@Composable
fun NotificationsScreen(
  notifications: List<NotificationEntity>,
  onMarkRead: (String) -> Unit,
  onBack: () -> Unit
) {
  BackHandler(onBack = onBack)

  Scaffold(
    topBar = {
      Surface(
        color = DarkSurface,
        tonalElevation = 4.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("notifications_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Notifications",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Color.White
          )
        }
      }
    }
  ) { paddingValues ->
    if (notifications.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(DarkBackground)
          .padding(paddingValues),
        contentAlignment = Alignment.Center
      ) {
        Text("No notifications yet.", color = TextSecondary, fontSize = 14.sp)
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .background(DarkBackground)
          .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(notifications, key = { it.id }) { notif ->
          val icon = when (notif.type) {
            "ROOM_RELEASED" -> Icons.Default.VpnKey
            "RESULT_PUBLISHED" -> Icons.Default.Leaderboard
            "REWARD_UPDATE" -> Icons.Default.EmojiEvents
            "TOURNAMENT" -> Icons.Default.SportsEsports
            else -> Icons.Default.Notifications
          }
          val iconTint = when (notif.type) {
            "ROOM_RELEASED" -> SuccessGreen
            "RESULT_PUBLISHED" -> ElectricYellow
            "REWARD_UPDATE" -> FlameAmber
            else -> FireOrange
          }

          Card(
            colors = CardDefaults.cardColors(
              containerColor = if (notif.isRead) DarkSurfaceElevated else DarkSurfaceCard
            ),
            shape = RoundedCornerShape(12.dp),
            border = if (!notif.isRead) BorderStroke(1.dp, CardBorderHighlight) else BorderStroke(1.dp, CardBorder),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onMarkRead(notif.id) }
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.Top
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = notif.title,
                    fontWeight = if (notif.isRead) FontWeight.SemiBold else FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                  )
                  if (!notif.isRead) {
                    Box(
                      modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(FireOrange)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = notif.message,
                  fontSize = 12.sp,
                  color = TextSecondary,
                  lineHeight = 17.sp
                )
              }
            }
          }
        }
      }
    }
  }
}
