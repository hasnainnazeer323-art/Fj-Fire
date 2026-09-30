package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ApplicationEntity
import com.example.data.entity.MatchResultEntity
import com.example.data.entity.TournamentEntity
import com.example.ui.components.CopyableDetailRow
import com.example.ui.components.EsportsCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun MyAppliedScreen(
  userApplications: List<ApplicationEntity>,
  tournaments: List<TournamentEntity>,
  results: List<MatchResultEntity>,
  onViewTournament: (String) -> Unit,
  onViewResults: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(0) } // 0: Upcoming, 1: Completed, 2: Cancelled

  val tournamentMap = remember(tournaments) { tournaments.associateBy { it.id } }

  val upcomingApps = remember(userApplications, tournamentMap) {
    userApplications.filter { app ->
      val t = tournamentMap[app.tournamentId]
      app.status == "CONFIRMED" && (t?.status == "REGISTRATION_OPEN" || t?.status == "REGISTRATION_CLOSED" || t?.status == "LIVE")
    }
  }

  val completedApps = remember(userApplications, tournamentMap) {
    userApplications.filter { app ->
      val t = tournamentMap[app.tournamentId]
      t?.status == "COMPLETED"
    }
  }

  val cancelledApps = remember(userApplications, tournamentMap) {
    userApplications.filter { app ->
      val t = tournamentMap[app.tournamentId]
      app.status == "CANCELLED" || app.status == "REJECTED" || t?.status == "CANCELLED"
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .testTag("my_applied_screen")
  ) {
    // Header
    Surface(
      color = DarkSurface,
      tonalElevation = 4.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Text(
          text = "My Registered Tournaments",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = Color.White
        )
        Text(
          text = "Access your application IDs and match room credentials",
          fontSize = 12.sp,
          color = TextSecondary
        )
      }
    }

    // 3 Tabs: UPCOMING, COMPLETED, CANCELLED
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = DarkSurfaceElevated,
      contentColor = FireOrange
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Text(
            text = "UPCOMING (${upcomingApps.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        },
        modifier = Modifier.testTag("tab_applied_upcoming")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Text(
            text = "COMPLETED (${completedApps.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        },
        modifier = Modifier.testTag("tab_applied_completed")
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = {
          Text(
            text = "CANCELLED (${cancelledApps.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        },
        modifier = Modifier.testTag("tab_applied_cancelled")
      )
    }

    val currentList = when (selectedTab) {
      0 -> upcomingApps
      1 -> completedApps
      else -> cancelledApps
    }

    if (currentList.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.SportsEsports,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(56.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = when (selectedTab) {
              0 -> "No upcoming tournament registrations"
              1 -> "No completed tournament history"
              else -> "No cancelled tournaments"
            },
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Browse Free Fire tournaments on the Home tab and apply for free.",
            color = TextMuted,
            fontSize = 12.sp
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(currentList, key = { it.id }) { app ->
          val tournament = tournamentMap[app.tournamentId]
          val userResult = results.find { it.tournamentId == app.tournamentId && it.ffUid == app.ffUid }

          AppliedTournamentCard(
            application = app,
            tournament = tournament,
            userResult = userResult,
            onViewTournament = { onViewTournament(app.tournamentId) },
            onViewResults = { onViewResults(app.tournamentId) }
          )
        }
      }
    }
  }
}

@Composable
fun AppliedTournamentCard(
  application: ApplicationEntity,
  tournament: TournamentEntity?,
  userResult: MatchResultEntity?,
  onViewTournament: () -> Unit,
  onViewResults: () -> Unit
) {
  EsportsCard {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        color = DarkSurfaceElevated,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, CardBorder)
      ) {
        Text(
          text = application.id,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = FlameAmber,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }

      StatusBadge(status = tournament?.status ?: application.status)
    }

    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = application.tournamentName,
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )

    Spacer(modifier = Modifier.height(6.dp))

    // Player Details
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "UID: ${application.ffUid}",
        fontSize = 12.sp,
        color = TextSecondary
      )
      Text(
        text = "IGN: ${application.ffIgn}",
        fontSize = 12.sp,
        color = Color.White,
        fontWeight = FontWeight.Medium
      )
    }

    if (tournament != null) {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(tournament.dateStr, fontSize = 12.sp, color = TextSecondary)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Schedule, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(tournament.timeStr, fontSize = 12.sp, color = FlameAmber, fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // MATCH / ROOM INFORMATION SECTION
    if (tournament?.status != "COMPLETED" && tournament?.status != "CANCELLED") {
      HorizontalDivider(color = CardBorder)
      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "MATCH / ROOM CREDENTIALS",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = ElectricYellow,
        letterSpacing = 0.5.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      if (tournament != null && tournament.isRoomReleased && tournament.roomId.isNotEmpty()) {
        Surface(
          color = Color(0xFF0F3924),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, SuccessGreen),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.VpnKey, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Room Released • Join Now", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            CopyableDetailRow(label = "Room ID", value = tournament.roomId)
            Spacer(modifier = Modifier.height(6.dp))
            CopyableDetailRow(label = "Room Password", value = tournament.roomPassword)

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "🔒 Do not share room credentials publicly. Room is monitored.",
              color = Color(0xFFA7F3D0),
              fontSize = 10.sp
            )
          }
        }
      } else {
        Surface(
          color = DarkSurfaceElevated,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = FlameAmber, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Room details will be available before the match.",
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = "Admin will release credentials ~15 minutes prior to start time.",
                fontSize = 11.sp,
                color = TextMuted
              )
            }
          }
        }
      }
    }

    // Result badge if completed
    if (userResult != null) {
      Spacer(modifier = Modifier.height(10.dp))
      Surface(
        color = Color(0xFF332005),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, FlameAmber),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = ElectricYellow, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text("Tournament Result: Rank #${userResult.position}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
              Text("${userResult.kills} Kills • ${userResult.points} Total Points", fontSize = 11.sp, color = FlameAmber)
            }
          }

          TextButton(onClick = onViewResults) {
            Text("Full Standings", color = ElectricYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.End
    ) {
      TextButton(onClick = onViewTournament) {
        Text("Tournament Details", color = FlameAmber, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}
