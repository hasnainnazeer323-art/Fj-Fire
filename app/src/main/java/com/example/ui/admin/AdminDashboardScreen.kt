package com.example.ui.admin

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.*
import com.example.ui.components.EsportsCard
import com.example.ui.components.FJFireLogo
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.AdminTab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
  currentAdmin: AdminUserEntity?,
  selectedTab: AdminTab,
  onSelectTab: (AdminTab) -> Unit,
  tournaments: List<TournamentEntity>,
  applications: List<ApplicationEntity>,
  users: List<UserEntity>,
  results: List<MatchResultEntity>,
  rewards: List<RewardEntity>,
  withdrawals: List<WithdrawalRequestEntity>,
  notifications: List<NotificationEntity>,
  supportTickets: List<SupportTicketEntity>,
  staffUsers: List<AdminUserEntity>,
  auditLogs: List<AuditLogEntity>,
  settings: Map<String, String>,
  onCreateTournament: (String, String, String, String, String, String, Int, String) -> Unit,
  onUpdateRoomDetails: (String, String, String, Boolean) -> Unit,
  onUpdateTournamentStatus: (TournamentEntity, String) -> Unit,
  onDeleteTournament: (String) -> Unit,
  onUpdateAppStatus: (ApplicationEntity, String) -> Unit,
  onAddResult: (String, String, Int, String, String, String, Int, Int, String) -> Unit,
  onPublishResults: (String, String) -> Unit,
  onUpdateWithdrawalStatus: (WithdrawalRequestEntity, String, String) -> Unit,
  onSendNotification: (String, String, String, String) -> Unit,
  onUpdateSetting: (String, String) -> Unit,
  onCreateStaff: (String, String, String, String, String) -> Unit,
  onToggleUserSuspension: (UserEntity) -> Unit,
  onLogoutAdmin: () -> Unit
) {
  BackHandler(onBack = onLogoutAdmin)

  val permissions = currentAdmin?.permissions?.split(",")?.map { it.trim() }?.toSet() ?: emptySet()
  val isSuperAdmin = currentAdmin?.role == "SUPER_ADMIN"

  fun hasPermission(p: String): Boolean = isSuperAdmin || permissions.contains(p)

  Scaffold(
    topBar = {
      Surface(
        color = DarkSurface,
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              FJFireLogo(size = 32)
              Spacer(modifier = Modifier.width(10.dp))
              Surface(
                color = Color(0x33FF5722),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, CardBorderHighlight)
              ) {
                Text(
                  text = "ADMIN CONSOLE",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  color = FireOrange,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Surface(
                color = DarkSurfaceElevated,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CardBorder)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(SuccessGreen)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = currentAdmin?.role?.replace("_", " ") ?: "ADMIN",
                    fontSize = 10.sp,
                    color = FlameAmber,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              IconButton(
                onClick = onLogoutAdmin,
                modifier = Modifier.testTag("admin_logout_button")
              ) {
                Icon(Icons.Default.Logout, contentDescription = "Exit to User App", tint = ErrorRed)
              }
            }
          }

          // Admin Horizontal Navigation Bar
          ScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = DarkSurfaceElevated,
            contentColor = FireOrange,
            edgePadding = 12.dp
          ) {
            AdminTab.values().forEach { tab ->
              Tab(
                selected = selectedTab == tab,
                onClick = { onSelectTab(tab) },
                text = {
                  Text(
                    text = tab.name.replace("_", " "),
                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.sp
                  )
                },
                modifier = Modifier.testTag("admin_tab_${tab.name.lowercase()}")
              )
            }
          }
        }
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(DarkBackground)
        .padding(paddingValues)
    ) {
      when (selectedTab) {
        AdminTab.OVERVIEW -> AdminOverviewSection(
          users = users,
          tournaments = tournaments,
          applications = applications,
          withdrawals = withdrawals,
          results = results,
          onNavigateTab = onSelectTab
        )
        AdminTab.TOURNAMENTS -> AdminTournamentsSection(
          tournaments = tournaments,
          onCreateTournament = onCreateTournament,
          onUpdateStatus = onUpdateTournamentStatus,
          onDeleteTournament = onDeleteTournament
        )
        AdminTab.MATCHES -> AdminMatchesSection(
          tournaments = tournaments,
          onUpdateRoom = onUpdateRoomDetails
        )
        AdminTab.APPLICATIONS -> AdminApplicationsSection(
          applications = applications,
          tournaments = tournaments,
          onUpdateAppStatus = onUpdateAppStatus
        )
        AdminTab.RESULTS -> AdminResultsSection(
          tournaments = tournaments,
          results = results,
          onAddResult = onAddResult,
          onPublishResults = onPublishResults
        )
        AdminTab.REQUESTS -> AdminWithdrawalsSection(
          withdrawals = withdrawals,
          onUpdateWithdrawalStatus = onUpdateWithdrawalStatus
        )
        AdminTab.USERS -> AdminUsersSection(
          users = users,
          onToggleSuspension = onToggleUserSuspension
        )
        AdminTab.REWARDS -> AdminRewardsSection(
          rewards = rewards
        )
        AdminTab.NOTIFICATIONS -> AdminNotificationsSection(
          notifications = notifications,
          tournaments = tournaments,
          onSendNotification = onSendNotification
        )
        AdminTab.SUPPORT -> AdminSupportSection(
          supportTickets = supportTickets
        )
        AdminTab.STAFF -> AdminStaffSection(
          staffUsers = staffUsers,
          onCreateStaff = onCreateStaff,
          isSuperAdmin = isSuperAdmin
        )
        AdminTab.AUDIT_LOGS -> AdminAuditLogsSection(
          auditLogs = auditLogs
        )
        AdminTab.SETTINGS -> AdminSettingsSection(
          settings = settings,
          onUpdateSetting = onUpdateSetting
        )
      }
    }
  }
}

// -------------------------------------------------------------
// 1. OVERVIEW SECTION
// -------------------------------------------------------------
@Composable
fun AdminOverviewSection(
  users: List<UserEntity>,
  tournaments: List<TournamentEntity>,
  applications: List<ApplicationEntity>,
  withdrawals: List<WithdrawalRequestEntity>,
  results: List<MatchResultEntity>,
  onNavigateTab: (AdminTab) -> Unit
) {
  val totalUsers = users.size
  val activeTournaments = tournaments.count { it.status == "REGISTRATION_OPEN" || it.status == "LIVE" }
  val completedTournaments = tournaments.count { it.status == "COMPLETED" }
  val pendingRequests = withdrawals.count { it.status == "PENDING" }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Text(
        text = "Esports Control Dashboard",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
      Text(
        text = "Real-time Free Fire tournament metrics and pending actions",
        fontSize = 12.sp,
        color = TextSecondary
      )
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        KpiCard(title = "Total Users", count = totalUsers.toString(), icon = Icons.Default.People, color = Color.White, modifier = Modifier.weight(1f))
        KpiCard(title = "Live/Open Scrims", count = activeTournaments.toString(), icon = Icons.Default.SportsEsports, color = FireOrange, modifier = Modifier.weight(1f))
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        KpiCard(title = "Total Applications", count = applications.size.toString(), icon = Icons.Default.Assignment, color = FlameAmber, modifier = Modifier.weight(1f))
        KpiCard(title = "Pending Requests", count = pendingRequests.toString(), icon = Icons.Default.PendingActions, color = WarningYellow, modifier = Modifier.weight(1f))
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        KpiCard(title = "Completed Matches", count = completedTournaments.toString(), icon = Icons.Default.CheckCircle, color = SuccessGreen, modifier = Modifier.weight(1f))
        KpiCard(title = "Results Recorded", count = results.size.toString(), icon = Icons.Default.Leaderboard, color = ElectricYellow, modifier = Modifier.weight(1f))
      }
    }

    item {
      EsportsCard {
        Text("Quick Administration Actions", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = { onNavigateTab(AdminTab.TOURNAMENTS) },
            colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("Create Tourney", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
          OutlinedButton(
            onClick = { onNavigateTab(AdminTab.MATCHES) },
            border = BorderStroke(1.dp, FlameAmber),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("Release Rooms", fontSize = 11.sp, color = FlameAmber, fontWeight = FontWeight.Bold)
          }
          OutlinedButton(
            onClick = { onNavigateTab(AdminTab.REQUESTS) },
            border = BorderStroke(1.dp, WarningYellow),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("Review Req ($pendingRequests)", fontSize = 11.sp, color = WarningYellow, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun KpiCard(
  title: String,
  count: String,
  icon: ImageVector,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, CardBorder),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = title, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(text = count, fontSize = 22.sp, fontWeight = FontWeight.Black, color = color)
    }
  }
}

// -------------------------------------------------------------
// 2. TOURNAMENTS SECTION
// -------------------------------------------------------------
@Composable
fun AdminTournamentsSection(
  tournaments: List<TournamentEntity>,
  onCreateTournament: (String, String, String, String, String, String, Int, String) -> Unit,
  onUpdateStatus: (TournamentEntity, String) -> Unit,
  onDeleteTournament: (String) -> Unit
) {
  var showCreateDialog by remember { mutableStateOf(false) }

  var tName by remember { mutableStateOf("") }
  var tType by remember { mutableStateOf("Solo") }
  var tMap by remember { mutableStateOf("Bermuda") }
  var tDate by remember { mutableStateOf("Today") }
  var tTime by remember { mutableStateOf("8:00 PM") }
  var tRules by remember { mutableStateOf("1. Mobile only, no emulators.\n2. 100% Free Entry.\n3. Join room 5 mins prior.") }
  var tMaxPlayers by remember { mutableStateOf("100") }
  var tReward by remember { mutableStateOf("Winner Trophy & 500 Reward Points (Free promotional contest)") }

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Tournaments (${tournaments.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
      Button(
        onClick = { showCreateDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("admin_add_tournament_button")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Create Tournament", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(tournaments, key = { it.id }) { t ->
        EsportsCard {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(t.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White, modifier = Modifier.weight(1f))
            StatusBadge(status = t.status)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text("${t.type} • Map: ${t.mapName} • ${t.dateStr} at ${t.timeStr}", fontSize = 12.sp, color = FlameAmber)
          Text("Players: ${t.currentPlayers}/${t.maxPlayers}", fontSize = 11.sp, color = TextSecondary)

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            if (t.status == "REGISTRATION_OPEN") {
              OutlinedButton(
                onClick = { onUpdateStatus(t, "REGISTRATION_CLOSED") },
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f).height(34.dp)
              ) {
                Text("Close Reg", fontSize = 10.sp, color = WarningYellow)
              }
            } else if (t.status == "REGISTRATION_CLOSED") {
              OutlinedButton(
                onClick = { onUpdateStatus(t, "LIVE") },
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f).height(34.dp)
              ) {
                Text("Mark Live", fontSize = 10.sp, color = FireOrange)
              }
            } else if (t.status == "LIVE") {
              OutlinedButton(
                onClick = { onUpdateStatus(t, "COMPLETED") },
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f).height(34.dp)
              ) {
                Text("Mark Done", fontSize = 10.sp, color = SuccessGreen)
              }
            }

            IconButton(
              onClick = { onDeleteTournament(t.id) },
              modifier = Modifier.size(34.dp)
            ) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed, modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    }
  }

  // Create Tournament Dialog
  if (showCreateDialog) {
    AlertDialog(
      onDismissRequest = { showCreateDialog = false },
      title = { Text("Create Free Fire Tournament", color = Color.White, fontWeight = FontWeight.Bold) },
      text = {
        Column(
          modifier = Modifier.verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(value = tName, onValueChange = { tName = it }, label = { Text("Tournament Name") }, singleLine = true)
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = tType, onValueChange = { tType = it }, label = { Text("Type (Solo/Squad/Custom)") }, modifier = Modifier.weight(1f), singleLine = true)
            OutlinedTextField(value = tMap, onValueChange = { tMap = it }, label = { Text("Map Name") }, modifier = Modifier.weight(1f), singleLine = true)
          }
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = tDate, onValueChange = { tDate = it }, label = { Text("Date") }, modifier = Modifier.weight(1f), singleLine = true)
            OutlinedTextField(value = tTime, onValueChange = { tTime = it }, label = { Text("Time") }, modifier = Modifier.weight(1f), singleLine = true)
          }
          OutlinedTextField(value = tMaxPlayers, onValueChange = { tMaxPlayers = it }, label = { Text("Max Players") }, singleLine = true)
          OutlinedTextField(value = tReward, onValueChange = { tReward = it }, label = { Text("Reward Information") }, minLines = 2)
          OutlinedTextField(value = tRules, onValueChange = { tRules = it }, label = { Text("Rules") }, minLines = 2)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (tName.isNotBlank()) {
              onCreateTournament(
                tName,
                tType,
                tMap,
                tDate,
                tTime,
                tRules,
                tMaxPlayers.toIntOrNull() ?: 100,
                tReward
              )
              showCreateDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
          modifier = Modifier.testTag("admin_submit_create_tournament")
        ) {
          Text("Publish")
        }
      },
      dismissButton = {
        TextButton(onClick = { showCreateDialog = false }) { Text("Cancel", color = TextSecondary) }
      },
      containerColor = DarkSurfaceCard
    )
  }
}

// -------------------------------------------------------------
// 3. MATCHES & ROOM RELEASE SECTION
// -------------------------------------------------------------
@Composable
fun AdminMatchesSection(
  tournaments: List<TournamentEntity>,
  onUpdateRoom: (String, String, String, Boolean) -> Unit
) {
  var selectedTournament by remember { mutableStateOf<TournamentEntity?>(null) }
  var roomIdInput by remember { mutableStateOf("") }
  var roomPassInput by remember { mutableStateOf("") }

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Text("Match Room Credentials Management", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
    Text("Admins enter Room ID and Password, and release them to eligible players", fontSize = 12.sp, color = TextSecondary)

    Spacer(modifier = Modifier.height(14.dp))

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(tournaments, key = { it.id }) { t ->
        EsportsCard {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(t.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
              Text("${t.type} • ${t.dateStr} at ${t.timeStr}", fontSize = 11.sp, color = FlameAmber)
            }
            if (t.isRoomReleased) {
              Surface(
                color = Color(0xFF0F3924),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text("ROOM RELEASED", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
              }
            } else {
              Surface(
                color = DarkSurfaceElevated,
                shape = RoundedCornerShape(4.dp)
              ) {
                Text("CREDENTIALS HIDDEN", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text("Current Room ID: ${t.roomId.ifEmpty { "Not Set" }} | Password: ${t.roomPassword.ifEmpty { "Not Set" }}", fontSize = 12.sp, color = TextSecondary)

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                selectedTournament = t
                roomIdInput = t.roomId
                roomPassInput = t.roomPassword
              },
              colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f).height(36.dp).testTag("edit_room_${t.id}")
            ) {
              Text("Edit & Release Credentials", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }

  // Room details dialog
  if (selectedTournament != null) {
    val tourney = selectedTournament!!
    AlertDialog(
      onDismissRequest = { selectedTournament = null },
      title = { Text("Update Room Credentials", color = Color.White, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Target: ${tourney.name}", fontSize = 12.sp, color = FlameAmber)
          OutlinedTextField(
            value = roomIdInput,
            onValueChange = { roomIdInput = it },
            label = { Text("Free Fire Custom Room ID") },
            singleLine = true
          )
          OutlinedTextField(
            value = roomPassInput,
            onValueChange = { roomPassInput = it },
            label = { Text("Room Password") },
            singleLine = true
          )
          Text("Releasing will broadcast credentials to all registered users.", fontSize = 11.sp, color = TextMuted)
        }
      },
      confirmButton = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = {
              onUpdateRoom(tourney.id, roomIdInput, roomPassInput, true)
              selectedTournament = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
            modifier = Modifier.testTag("admin_release_room_button")
          ) {
            Text("Save & Release Live")
          }
        }
      },
      dismissButton = {
        TextButton(onClick = {
          onUpdateRoom(tourney.id, roomIdInput, roomPassInput, false)
          selectedTournament = null
        }) {
          Text("Save Without Release", color = TextSecondary)
        }
      },
      containerColor = DarkSurfaceCard
    )
  }
}

// -------------------------------------------------------------
// 4. APPLICATIONS SECTION
// -------------------------------------------------------------
@Composable
fun AdminApplicationsSection(
  applications: List<ApplicationEntity>,
  tournaments: List<TournamentEntity>,
  onUpdateAppStatus: (ApplicationEntity, String) -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }

  val filteredApps = remember(applications, searchQuery) {
    if (searchQuery.isBlank()) applications
    else applications.filter {
      it.playerName.contains(searchQuery, ignoreCase = true) ||
        it.ffUid.contains(searchQuery) ||
        it.id.contains(searchQuery, ignoreCase = true) ||
        it.tournamentName.contains(searchQuery, ignoreCase = true)
    }
  }

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Text("Player Applications (${filteredApps.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      label = { Text("Search by Player Name, Free Fire UID or App ID") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
      singleLine = true,
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = FireOrange,
        unfocusedBorderColor = CardBorder,
        focusedContainerColor = DarkSurfaceElevated,
        unfocusedContainerColor = DarkSurfaceElevated
      ),
      modifier = Modifier.fillMaxWidth().testTag("admin_search_app_input")
    )

    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(filteredApps, key = { it.id }) { app ->
        EsportsCard {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(app.id, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FlameAmber)
            StatusBadge(status = app.status)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text("${app.playerName} (IGN: ${app.ffIgn})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
          Text("Free Fire UID: ${app.ffUid} • Phone: ${app.mobile}", fontSize = 12.sp, color = TextSecondary)
          Text("Tournament: ${app.tournamentName}", fontSize = 11.sp, color = TextMuted)

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (app.status != "CONFIRMED") {
              OutlinedButton(
                onClick = { onUpdateAppStatus(app, "CONFIRMED") },
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f).height(32.dp)
              ) {
                Text("Confirm", fontSize = 11.sp, color = SuccessGreen)
              }
            }
            if (app.status != "REJECTED") {
              OutlinedButton(
                onClick = { onUpdateAppStatus(app, "REJECTED") },
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f).height(32.dp)
              ) {
                Text("Reject", fontSize = 11.sp, color = ErrorRed)
              }
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// 5. RESULTS MANAGEMENT SECTION
// -------------------------------------------------------------
@Composable
fun AdminResultsSection(
  tournaments: List<TournamentEntity>,
  results: List<MatchResultEntity>,
  onAddResult: (String, String, Int, String, String, String, Int, Int, String) -> Unit,
  onPublishResults: (String, String) -> Unit
) {
  var selectedTourney by remember { mutableStateOf(tournaments.firstOrNull()) }
  var showAddResultDialog by remember { mutableStateOf(false) }

  var posInput by remember { mutableStateOf("1") }
  var playerInput by remember { mutableStateOf("") }
  var uidInput by remember { mutableStateOf("") }
  var ignInput by remember { mutableStateOf("") }
  var killsInput by remember { mutableStateOf("5") }
  var pointsInput by remember { mutableStateOf("17") }

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Text("Results & Leaderboard Management", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
    Text("Record scores and officially publish standings", fontSize = 12.sp, color = TextSecondary)

    Spacer(modifier = Modifier.height(10.dp))

    // Tournament Selector chips
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(tournaments) { t ->
        FilterChip(
          selected = selectedTourney?.id == t.id,
          onClick = { selectedTourney = t },
          label = { Text(t.name, maxLines = 1, fontSize = 11.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = FireOrange,
            selectedLabelColor = Color.White
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    val currentResults = results.filter { it.tournamentId == selectedTourney?.id }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("${currentResults.size} Positions Recorded", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FlameAmber)
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Button(
          onClick = { showAddResultDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.height(34.dp)
        ) {
          Text("+ Add Position", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        if (selectedTourney != null) {
          Button(
            onClick = { onPublishResults(selectedTourney!!.id, selectedTourney!!.name) },
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Text("Publish Results", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(currentResults, key = { it.id }) { res ->
        EsportsCard {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("#${res.position}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = FlameAmber)
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(res.ffIgn.ifEmpty { res.playerName }, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                Text("UID: ${res.ffUid}", fontSize = 11.sp, color = TextMuted)
              }
            }
            Column(horizontalAlignment = Alignment.End) {
              Text("${res.kills} Kills | ${res.points} Pts", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricYellow)
              Text("Status: ${res.rewardStatus}", fontSize = 10.sp, color = SuccessGreen)
            }
          }
        }
      }
    }
  }

  // Add Result Dialog
  if (showAddResultDialog && selectedTourney != null) {
    AlertDialog(
      onDismissRequest = { showAddResultDialog = false },
      title = { Text("Add Standings Position", color = Color.White, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = posInput, onValueChange = { posInput = it }, label = { Text("Position (Rank)") }, singleLine = true)
          OutlinedTextField(value = ignInput, onValueChange = { ignInput = it }, label = { Text("Free Fire IGN") }, singleLine = true)
          OutlinedTextField(value = uidInput, onValueChange = { uidInput = it }, label = { Text("Free Fire UID") }, singleLine = true)
          OutlinedTextField(value = killsInput, onValueChange = { killsInput = it }, label = { Text("Total Kills") }, singleLine = true)
          OutlinedTextField(value = pointsInput, onValueChange = { pointsInput = it }, label = { Text("Total Points") }, singleLine = true)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onAddResult(
              selectedTourney!!.id,
              selectedTourney!!.name,
              posInput.toIntOrNull() ?: 1,
              ignInput,
              uidInput,
              ignInput,
              killsInput.toIntOrNull() ?: 0,
              pointsInput.toIntOrNull() ?: 0,
              "Awarded"
            )
            showAddResultDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange)
        ) {
          Text("Save Position")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddResultDialog = false }) { Text("Cancel", color = TextSecondary) }
      },
      containerColor = DarkSurfaceCard
    )
  }
}

// -------------------------------------------------------------
// 6. WITHDRAWALS / REDEMPTIONS SECTION
// -------------------------------------------------------------
@Composable
fun AdminWithdrawalsSection(
  withdrawals: List<WithdrawalRequestEntity>,
  onUpdateWithdrawalStatus: (WithdrawalRequestEntity, String, String) -> Unit
) {
  var selectedReq by remember { mutableStateOf<WithdrawalRequestEntity?>(null) }
  var adminNotes by remember { mutableStateOf("") }

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Text("Reward Redemption / Withdrawal Requests (${withdrawals.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
    Text("Review player reward redemption claims and update status", fontSize = 12.sp, color = TextSecondary)

    Spacer(modifier = Modifier.height(14.dp))

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(withdrawals, key = { it.id }) { req ->
        EsportsCard {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(req.id, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FlameAmber)
            StatusBadge(status = req.status)
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text("Player: ${req.userName} • ${req.userMobile}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
          Text("Requested Value: ${req.requestedValue} (${req.rewardTitle})", fontSize = 12.sp, color = ElectricYellow, fontWeight = FontWeight.SemiBold)
          Text("Method: ${req.paymentMethod}", fontSize = 11.sp, color = TextSecondary)
          Text("Target Account: ${req.accountDetails}", fontSize = 11.sp, color = Color.White)
          Text("Requested At: ${req.dateStr}", fontSize = 10.sp, color = TextMuted)

          if (req.adminNotes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Admin Note: ${req.adminNotes}", fontSize = 11.sp, color = FlameAmber)
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            OutlinedButton(
              onClick = { onUpdateWithdrawalStatus(req, "PROCESSING", "Under verification") },
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.weight(1f).height(32.dp)
            ) {
              Text("Processing", fontSize = 10.sp, color = Color(0xFF60A5FA))
            }
            Button(
              onClick = { onUpdateWithdrawalStatus(req, "COMPLETED", "Redemption delivered successfully") },
              colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.weight(1f).height(32.dp)
            ) {
              Text("Complete", fontSize = 10.sp)
            }
            OutlinedButton(
              onClick = {
                selectedReq = req
                adminNotes = ""
              },
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.weight(1f).height(32.dp)
            ) {
              Text("Reject/Notes", fontSize = 10.sp, color = ErrorRed)
            }
          }
        }
      }
    }
  }

  // Reject / Note dialog
  if (selectedReq != null) {
    val req = selectedReq!!
    AlertDialog(
      onDismissRequest = { selectedReq = null },
      title = { Text("Update Request Status / Reason", color = Color.White, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text("Request: ${req.id} (${req.userName})", color = FlameAmber, fontSize = 12.sp)
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = adminNotes,
            onValueChange = { adminNotes = it },
            label = { Text("Admin Notes / Reason for rejection") },
            minLines = 2
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateWithdrawalStatus(req, "REJECTED", adminNotes)
            selectedReq = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
        ) {
          Text("Reject Request")
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedReq = null }) { Text("Cancel", color = TextSecondary) }
      },
      containerColor = DarkSurfaceCard
    )
  }
}

// -------------------------------------------------------------
// 7. USERS SECTION
// -------------------------------------------------------------
@Composable
fun AdminUsersSection(
  users: List<UserEntity>,
  onToggleSuspension: (UserEntity) -> Unit
) {
  var userSearch by remember { mutableStateOf("") }
  val filteredUsers = remember(users, userSearch) {
    if (userSearch.isBlank()) users
    else users.filter {
      it.fullName.contains(userSearch, ignoreCase = true) ||
        it.username.contains(userSearch, ignoreCase = true) ||
        it.ffUid.contains(userSearch) ||
        it.mobile.contains(userSearch)
    }
  }

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Text("Player Accounts (${filteredUsers.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
      value = userSearch,
      onValueChange = { userSearch = it },
      label = { Text("Search by Name, Username, UID or Mobile") },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
      singleLine = true,
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = FireOrange,
        unfocusedBorderColor = CardBorder,
        focusedContainerColor = DarkSurfaceElevated,
        unfocusedContainerColor = DarkSurfaceElevated
      ),
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(filteredUsers, key = { it.id }) { user ->
        EsportsCard {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
              Text("@${user.username} • Phone: ${user.mobile}", fontSize = 12.sp, color = TextSecondary)
              Text("Free Fire UID: ${user.ffUid} (IGN: ${user.ffIgn})", fontSize = 11.sp, color = FlameAmber)
            }
            if (user.isSuspended) {
              Surface(color = Color(0x33EF4444), shape = RoundedCornerShape(4.dp)) {
                Text("SUSPENDED", color = ErrorRed, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
              }
            } else {
              Surface(color = Color(0x2210B981), shape = RoundedCornerShape(4.dp)) {
                Text("ACTIVE", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            OutlinedButton(
              onClick = { onToggleSuspension(user) },
              border = BorderStroke(1.dp, if (user.isSuspended) SuccessGreen else ErrorRed),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.height(32.dp)
            ) {
              Text(if (user.isSuspended) "Unsuspend Account" else "Suspend Player", fontSize = 11.sp, color = if (user.isSuspended) SuccessGreen else ErrorRed)
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// 8. REWARDS SECTION
// -------------------------------------------------------------
@Composable
fun AdminRewardsSection(rewards: List<RewardEntity>) {
  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Text("Rewards Records (${rewards.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
    Spacer(modifier = Modifier.height(10.dp))
    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(rewards, key = { it.id }) { r ->
        EsportsCard {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(r.id, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FlameAmber)
            StatusBadge(status = r.status)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(r.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
          Text("Tournament: ${r.tournamentName}", fontSize = 12.sp, color = TextSecondary)
          Text("Reward Value: ${r.value} (${r.points} Pts)", fontSize = 12.sp, color = ElectricYellow, fontWeight = FontWeight.Bold)
          Text("Awarded Date: ${r.dateStr}", fontSize = 10.sp, color = TextMuted)
        }
      }
    }
  }
}

// -------------------------------------------------------------
// 9. NOTIFICATIONS SECTION
// -------------------------------------------------------------
@Composable
fun AdminNotificationsSection(
  notifications: List<NotificationEntity>,
  tournaments: List<TournamentEntity>,
  onSendNotification: (String, String, String, String) -> Unit
) {
  var showSendDialog by remember { mutableStateOf(false) }
  var notifTitle by remember { mutableStateOf("") }
  var notifMsg by remember { mutableStateOf("") }
  var notifType by remember { mutableStateOf("SYSTEM") }
  var notifTarget by remember { mutableStateOf("ALL") }

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Notification Dispatcher", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
      Button(
        onClick = { showSendDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Broadcast Notice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(notifications, key = { it.id }) { n ->
        EsportsCard {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(n.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
            Text(n.type, fontSize = 10.sp, color = FlameAmber, fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(n.message, fontSize = 12.sp, color = TextSecondary)
          Spacer(modifier = Modifier.height(4.dp))
          Text("Target: ${n.targetUserId}", fontSize = 10.sp, color = TextMuted)
        }
      }
    }
  }

  if (showSendDialog) {
    AlertDialog(
      onDismissRequest = { showSendDialog = false },
      title = { Text("Broadcast Notification", color = Color.White, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = notifTitle, onValueChange = { notifTitle = it }, label = { Text("Title") }, singleLine = true)
          OutlinedTextField(value = notifMsg, onValueChange = { notifMsg = it }, label = { Text("Message Body") }, minLines = 2)
          OutlinedTextField(value = notifType, onValueChange = { notifType = it }, label = { Text("Type (TOURNAMENT/SYSTEM/MATCH)") }, singleLine = true)
          OutlinedTextField(value = notifTarget, onValueChange = { notifTarget = it }, label = { Text("Target ('ALL' or User ID)") }, singleLine = true)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (notifTitle.isNotBlank() && notifMsg.isNotBlank()) {
              onSendNotification(notifTitle, notifMsg, notifType, notifTarget)
              showSendDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange)
        ) {
          Text("Send Now")
        }
      },
      dismissButton = {
        TextButton(onClick = { showSendDialog = false }) { Text("Cancel", color = TextSecondary) }
      },
      containerColor = DarkSurfaceCard
    )
  }
}

// -------------------------------------------------------------
// 10. SUPPORT TICKETS SECTION
// -------------------------------------------------------------
@Composable
fun AdminSupportSection(supportTickets: List<SupportTicketEntity>) {
  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Text("User Support Tickets (${supportTickets.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
    Spacer(modifier = Modifier.height(10.dp))
    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(supportTickets, key = { it.id }) { t ->
        EsportsCard {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(t.id, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FlameAmber)
            StatusBadge(status = t.status)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(t.subject, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
          Text("From: ${t.userName} (${t.userId})", fontSize = 11.sp, color = TextMuted)
          Spacer(modifier = Modifier.height(6.dp))
          Text(t.message, fontSize = 12.sp, color = TextSecondary)
        }
      }
    }
  }
}

// -------------------------------------------------------------
// 11. STAFF & ROLES SECTION
// -------------------------------------------------------------
@Composable
fun AdminStaffSection(
  staffUsers: List<AdminUserEntity>,
  onCreateStaff: (String, String, String, String, String) -> Unit,
  isSuperAdmin: Boolean
) {
  var showAddStaffDialog by remember { mutableStateOf(false) }
  var staffName by remember { mutableStateOf("") }
  var staffEmail by remember { mutableStateOf("") }
  var staffPass by remember { mutableStateOf("") }
  var staffRole by remember { mutableStateOf("STAFF_DISTRIBUTOR") }

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Staff & Role-Based Access (${staffUsers.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
      if (isSuperAdmin) {
        Button(
          onClick = { showAddStaffDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("+ Add Staff", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(staffUsers, key = { it.id }) { s ->
        EsportsCard {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(s.username, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
            Surface(
              color = Color(0x33FF5722),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(s.role, color = FlameAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text("Email: ${s.email}", fontSize = 12.sp, color = TextSecondary)
          Text("Permissions: ${s.permissions}", fontSize = 10.sp, color = TextMuted)
        }
      }
    }
  }

  if (showAddStaffDialog) {
    AlertDialog(
      onDismissRequest = { showAddStaffDialog = false },
      title = { Text("Add Admin / Staff Member", color = Color.White, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = staffName, onValueChange = { staffName = it }, label = { Text("Staff Username") }, singleLine = true)
          OutlinedTextField(value = staffEmail, onValueChange = { staffEmail = it }, label = { Text("Email Address") }, singleLine = true)
          OutlinedTextField(value = staffPass, onValueChange = { staffPass = it }, label = { Text("Access Password") }, singleLine = true)
          OutlinedTextField(value = staffRole, onValueChange = { staffRole = it }, label = { Text("Role (ADMIN / STAFF_DISTRIBUTOR)") }, singleLine = true)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (staffEmail.isNotBlank()) {
              onCreateStaff(
                staffName,
                staffEmail,
                staffPass,
                staffRole,
                "VIEW_USERS,VIEW_APPLICATIONS,MANAGE_TOURNAMENTS,VIEW_RESULTS"
              )
              showAddStaffDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange)
        ) {
          Text("Add Member")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddStaffDialog = false }) { Text("Cancel", color = TextSecondary) }
      },
      containerColor = DarkSurfaceCard
    )
  }
}

// -------------------------------------------------------------
// 12. AUDIT LOGS SECTION
// -------------------------------------------------------------
@Composable
fun AdminAuditLogsSection(auditLogs: List<AuditLogEntity>) {
  val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Text("System Audit Trail (${auditLogs.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
    Text("Immutable record of administrator actions and settings changes", fontSize = 12.sp, color = TextSecondary)

    Spacer(modifier = Modifier.height(12.dp))

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(auditLogs, key = { it.id }) { log ->
        EsportsCard {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(log.action, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FlameAmber)
            Text(dateFormat.format(Date(log.timestamp)), fontSize = 10.sp, color = TextMuted)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text("Target: ${log.target}", fontSize = 12.sp, color = Color.White)
          Text("Operator: ${log.adminName} (${log.adminId})", fontSize = 11.sp, color = TextSecondary)
          if (log.newValue.isNotEmpty()) {
            Text("Update: ${log.newValue}", fontSize = 11.sp, color = SuccessGreen)
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// 13. SETTINGS SECTION (WHATSAPP NUMBER & SUPPORT SETTINGS)
// -------------------------------------------------------------
@Composable
fun AdminSettingsSection(
  settings: Map<String, String>,
  onUpdateSetting: (String, String) -> Unit
) {
  var waNumber by remember(settings) {
    mutableStateOf(settings["whatsapp_support_number"] ?: "+1234567890")
  }
  var waName by remember(settings) {
    mutableStateOf(settings["whatsapp_display_name"] ?: "FJ_Fire Official Support")
  }
  var waMsg by remember(settings) {
    mutableStateOf(settings["support_message"] ?: "Hello FJ_Fire Support, I need help regarding my request.")
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text("Global Application & Support Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
    Text("Changes apply dynamically to all users in real-time without app updates.", fontSize = 12.sp, color = TextSecondary)

    EsportsCard {
      Text("WhatsApp Support Configuration", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
      Spacer(modifier = Modifier.height(6.dp))
      Text("This dynamic phone number is used across all user-side WhatsApp buttons.", fontSize = 11.sp, color = TextMuted)

      Spacer(modifier = Modifier.height(14.dp))

      OutlinedTextField(
        value = waNumber,
        onValueChange = { waNumber = it },
        label = { Text("WhatsApp Support Number") },
        placeholder = { Text("+1234567890") },
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = SuccessGreen) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = FireOrange,
          unfocusedBorderColor = CardBorder,
          focusedContainerColor = DarkSurfaceElevated,
          unfocusedContainerColor = DarkSurfaceElevated
        ),
        modifier = Modifier.fillMaxWidth().testTag("setting_whatsapp_number_input")
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = waName,
        onValueChange = { waName = it },
        label = { Text("WhatsApp Display Name") },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = FireOrange,
          unfocusedBorderColor = CardBorder,
          focusedContainerColor = DarkSurfaceElevated,
          unfocusedContainerColor = DarkSurfaceElevated
        ),
        modifier = Modifier.fillMaxWidth().testTag("setting_whatsapp_name_input")
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = waMsg,
        onValueChange = { waMsg = it },
        label = { Text("Default Support Message") },
        minLines = 2,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = FireOrange,
          unfocusedBorderColor = CardBorder,
          focusedContainerColor = DarkSurfaceElevated,
          unfocusedContainerColor = DarkSurfaceElevated
        ),
        modifier = Modifier.fillMaxWidth().testTag("setting_whatsapp_msg_input")
      )

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          onUpdateSetting("whatsapp_support_number", waNumber)
          onUpdateSetting("whatsapp_display_name", waName)
          onUpdateSetting("support_message", waMsg)
        },
        colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_settings_button")
      ) {
        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("SAVE SETTINGS & LOG AUDIT", fontWeight = FontWeight.Bold, fontSize = 13.sp)
      }
    }
  }
}
