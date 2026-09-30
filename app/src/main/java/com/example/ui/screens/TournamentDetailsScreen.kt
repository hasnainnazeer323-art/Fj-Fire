package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ApplicationEntity
import com.example.data.entity.TournamentEntity
import com.example.data.entity.UserEntity
import com.example.ui.components.EsportsCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun TournamentDetailsScreen(
  tournament: TournamentEntity?,
  currentUser: UserEntity?,
  existingApplication: ApplicationEntity?,
  onApply: (String) -> Unit,
  onGoToMyApplied: () -> Unit,
  onViewResults: (String) -> Unit,
  onBack: () -> Unit
) {
  BackHandler(onBack = onBack)

  var showApplyConfirmationDialog by remember { mutableStateOf(false) }
  var showRulesDialog by remember { mutableStateOf(false) }

  if (tournament == null) {
    Box(
      modifier = Modifier.fillMaxSize().background(DarkBackground),
      contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator(color = FireOrange)
    }
    return
  }

  val isApplied = existingApplication != null
  val isFull = tournament.currentPlayers >= tournament.maxPlayers
  val isCompleted = tournament.status == "COMPLETED"

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
            modifier = Modifier.testTag("details_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Tournament Details",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White
          )
        }
      }
    },
    bottomBar = {
      Surface(
        color = DarkSurfaceElevated,
        tonalElevation = 8.dp,
        border = BorderStroke(1.dp, CardBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedButton(
            onClick = { showRulesDialog = true },
            border = BorderStroke(1.dp, FlameAmber),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f).height(48.dp).testTag("details_rules_button")
          ) {
            Icon(Icons.Default.MenuBook, contentDescription = null, tint = FlameAmber, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Rules", color = FlameAmber, fontWeight = FontWeight.Bold)
          }

          if (isCompleted) {
            Button(
              onClick = { onViewResults(tournament.id) },
              colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(2f).height(48.dp).testTag("details_view_results_button")
            ) {
              Icon(Icons.Default.Leaderboard, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("View Results & Standings", fontWeight = FontWeight.Bold)
            }
          } else if (isApplied) {
            Button(
              onClick = onGoToMyApplied,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F3924)),
              border = BorderStroke(1.dp, SuccessGreen),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(2f).height(48.dp).testTag("details_already_applied_button")
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Already Applied", color = SuccessGreen, fontWeight = FontWeight.Bold)
            }
          } else {
            Button(
              onClick = { showApplyConfirmationDialog = true },
              enabled = !isFull && tournament.status == "REGISTRATION_OPEN",
              colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(2f).height(48.dp).testTag("details_apply_now_button")
            ) {
              Icon(Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isFull) "SLOTS FULL" else "APPLY NOW (FREE)",
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(DarkBackground)
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Main Info Card
      EsportsCard {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Surface(
              color = DarkSurfaceElevated,
              shape = RoundedCornerShape(4.dp),
              border = BorderStroke(1.dp, CardBorder)
            ) {
              Text(
                text = tournament.type.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = FlameAmber,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }

            Surface(
              color = Color(0x2210B981),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "Map: ${tournament.mapName}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = SuccessGreen,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          StatusBadge(status = tournament.status)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = tournament.name,
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          color = Color.White
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Schedule Grid
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Surface(
            color = DarkSurfaceElevated,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("Match Date", fontSize = 11.sp, color = TextMuted)
              Text(tournament.dateStr, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
          }

          Surface(
            color = DarkSurfaceElevated,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("Start Time", fontSize = 11.sp, color = TextMuted)
              Text(tournament.timeStr, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FlameAmber)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Registration Progress
        val progress = (tournament.currentPlayers.toFloat() / tournament.maxPlayers.toFloat()).coerceIn(0f, 1f)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Registration Progress", fontSize = 12.sp, color = TextSecondary)
          Text("${tournament.currentPlayers} / ${tournament.maxPlayers} Players", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FlameAmber)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = FireOrange,
          trackColor = DarkSurfaceElevated
        )
      }

      // Application Status Banner (if user already applied)
      if (existingApplication != null) {
        Surface(
          color = Color(0xFF0F3924),
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, SuccessGreen),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Successfully Applied", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Application ID: ${existingApplication.id}", fontSize = 13.sp, color = Color(0xFFA7F3D0), fontWeight = FontWeight.SemiBold)
            Text("Registered UID: ${existingApplication.ffUid} (${existingApplication.ffIgn})", fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = if (tournament.isRoomReleased) "Room details are live! Check 'My Applied' tab." else "Room ID & password will be unlocked 15 minutes before match start.",
              fontSize = 11.sp,
              color = FlameAmber
            )
          }
        }
      }

      // Rewards & Recognition Section
      EsportsCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = ElectricYellow, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Reward & Prize Information", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = tournament.rewardInfo.ifEmpty { "Official Certificate of Participation & In-App Esports XP Points" },
          fontSize = 13.sp,
          color = TextSecondary,
          lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Note: 100% Free Entry tournament. Free promotional prizes are distributed strictly under applicable platform guidelines.",
          fontSize = 10.sp,
          color = TextMuted
        )
      }

      // Fair Play & Match Format
      EsportsCard {
        Text("Match Guidelines & Points System", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("Rank #1 (Booyah)", fontSize = 12.sp, color = FlameAmber, fontWeight = FontWeight.Bold)
            Text("Rank #2", fontSize = 12.sp, color = TextSecondary)
            Text("Rank #3", fontSize = 12.sp, color = TextSecondary)
            Text("Each Kill", fontSize = 12.sp, color = FireOrange, fontWeight = FontWeight.Bold)
          }
          Column(horizontalAlignment = Alignment.End) {
            Text("12 Points", fontSize = 12.sp, color = FlameAmber, fontWeight = FontWeight.Bold)
            Text("9 Points", fontSize = 12.sp, color = TextSecondary)
            Text("8 Points", fontSize = 12.sp, color = TextSecondary)
            Text("+1 Point", fontSize = 12.sp, color = FireOrange, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = CardBorder)
        Spacer(modifier = Modifier.height(10.dp))

        Text("Fair Play Regulations:", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = tournament.rules,
          fontSize = 12.sp,
          color = TextSecondary,
          lineHeight = 18.sp
        )
      }
    }
  }

  // Apply Confirmation Dialog
  if (showApplyConfirmationDialog) {
    AlertDialog(
      onDismissRequest = { showApplyConfirmationDialog = false },
      title = { Text("Confirm Tournament Application", color = Color.White, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text("Please verify your Free Fire credentials for entry:", color = TextSecondary, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(12.dp))
          Surface(
            color = DarkSurfaceElevated,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text("Free Fire UID: ${currentUser?.ffUid ?: "N/A"}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
              Text("In-Game Name: ${currentUser?.ffIgn ?: "N/A"}", fontWeight = FontWeight.Bold, color = FlameAmber, fontSize = 13.sp)
              Text("Mobile: ${currentUser?.mobile ?: "N/A"}", color = TextSecondary, fontSize = 12.sp)
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text("• Entry Fee: ₹0 (100% Free Entry)", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          Text("• You agree to join room at scheduled time without emulator.", color = TextMuted, fontSize = 11.sp)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showApplyConfirmationDialog = false
            onApply(tournament.id)
          },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
          modifier = Modifier.testTag("confirm_apply_button")
        ) {
          Text("Confirm Application")
        }
      },
      dismissButton = {
        TextButton(onClick = { showApplyConfirmationDialog = false }) {
          Text("Cancel", color = TextSecondary)
        }
      },
      containerColor = DarkSurfaceCard
    )
  }

  // Rules Dialog
  if (showRulesDialog) {
    AlertDialog(
      onDismissRequest = { showRulesDialog = false },
      title = { Text("Official Tournament Rules", color = Color.White, fontWeight = FontWeight.Bold) },
      text = {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
          Text(tournament.rules, color = TextSecondary, fontSize = 13.sp, lineHeight = 20.sp)
          Spacer(modifier = Modifier.height(12.dp))
          Text("Disqualification Clause:", color = ErrorRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          Text("Any abusive language, config scripts, glitch abuse or teaming will result in immediate disqualification and account ban.", color = TextMuted, fontSize = 11.sp)
        }
      },
      confirmButton = {
        Button(
          onClick = { showRulesDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange)
        ) {
          Text("Understood")
        }
      },
      containerColor = DarkSurfaceCard
    )
  }
}
