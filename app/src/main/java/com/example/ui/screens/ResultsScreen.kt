package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
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
import com.example.data.entity.MatchResultEntity
import com.example.data.entity.TournamentEntity
import com.example.ui.components.EsportsCard
import com.example.ui.theme.*

@Composable
fun ResultsScreen(
  tournament: TournamentEntity?,
  results: List<MatchResultEntity>,
  onBack: () -> Unit
) {
  BackHandler(onBack = onBack)

  val sortedResults = results.sortedBy { it.position }

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
            modifier = Modifier.testTag("results_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "Official Match Results",
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = Color.White
            )
            Text(
              text = tournament?.name ?: "Free Fire Scrims",
              fontSize = 12.sp,
              color = FlameAmber
            )
          }
        }
      }
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(DarkBackground)
        .padding(paddingValues)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      item {
        EsportsCard {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("OFFICIAL LEADERBOARD", fontWeight = FontWeight.Black, fontSize = 14.sp, color = ElectricYellow, letterSpacing = 1.sp)
              Text("Results verified and published by FJ_Fire Admins", fontSize = 11.sp, color = TextMuted)
            }
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = ElectricYellow, modifier = Modifier.size(28.dp))
          }
        }
      }

      if (sortedResults.isEmpty()) {
        item {
          Box(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Results are currently being compiled by administrators.",
              color = TextSecondary,
              fontSize = 13.sp
            )
          }
        }
      } else {
        items(sortedResults, key = { it.id }) { result ->
          val rankColor = when (result.position) {
            1 -> Color(0xFFFFD700) // Gold
            2 -> Color(0xFFC0C0C0) // Silver
            3 -> Color(0xFFCD7F32) // Bronze
            else -> TextSecondary
          }

          Card(
            colors = CardDefaults.cardColors(
              containerColor = if (result.position <= 3) DarkSurfaceCard else DarkSurfaceElevated
            ),
            shape = RoundedCornerShape(12.dp),
            border = if (result.position <= 3) BorderStroke(1.dp, rankColor.copy(alpha = 0.5f)) else null,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(rankColor.copy(alpha = 0.15f))
                ) {
                  Text(
                    text = "#${result.position}",
                    color = rankColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(
                    text = result.ffIgn.ifEmpty { result.playerName },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                  )
                  Text(
                    text = "UID: ${result.ffUid}",
                    fontSize = 11.sp,
                    color = TextMuted
                  )
                }
              }

              Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = FireOrange, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(2.dp))
                  Text(
                    text = "${result.kills} Kills",
                    fontSize = 12.sp,
                    color = FireOrange,
                    fontWeight = FontWeight.Bold
                  )
                }
                Text(
                  text = "${result.points} Pts",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  color = FlameAmber
                )
                if (result.rewardStatus.isNotEmpty()) {
                  Text(
                    text = result.rewardStatus,
                    fontSize = 10.sp,
                    color = SuccessGreen,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
