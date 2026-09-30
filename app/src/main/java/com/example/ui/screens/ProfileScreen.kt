package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ApplicationEntity
import com.example.data.entity.MatchResultEntity
import com.example.data.entity.UserEntity
import com.example.ui.components.EsportsCard
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
  user: UserEntity?,
  userApplications: List<ApplicationEntity>,
  userResults: List<MatchResultEntity>,
  onEditProfile: (String, String, String) -> Unit,
  onOpenRewards: () -> Unit,
  onOpenNotifications: () -> Unit,
  onOpenSupport: () -> Unit,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showEditDialog by remember { mutableStateOf(false) }
  var showLegalDialog by remember { mutableStateOf(false) }
  var showAccountDeleteDialog by remember { mutableStateOf(false) }

  var editFullName by remember { mutableStateOf(user?.fullName ?: "") }
  var editIgn by remember { mutableStateOf(user?.ffIgn ?: "") }
  var editUid by remember { mutableStateOf(user?.ffUid ?: "") }

  val totalTournaments = userApplications.size
  val totalWins = userResults.count { it.position == 1 }
  val totalKills = userResults.sumOf { it.kills }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .verticalScroll(rememberScrollState())
      .padding(bottom = 90.dp)
      .testTag("profile_screen")
  ) {
    // Header Avatar Card
    Surface(
      color = DarkSurface,
      tonalElevation = 4.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(DarkSurfaceElevated)
            .border(2.dp, FireGradient, CircleShape)
        ) {
          Icon(
            imageVector = Icons.Default.SportsEsports,
            contentDescription = "Avatar",
            tint = FireOrange,
            modifier = Modifier.size(44.dp)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = user?.fullName ?: "Free Fire Competitor",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = Color.White
        )

        Text(
          text = "@${user?.username ?: "player"}",
          fontSize = 12.sp,
          color = FlameAmber,
          fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            color = Color(0x2210B981),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, Color(0x6610B981))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Verified Player", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }

          Surface(
            color = DarkSurfaceElevated,
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "UID: ${user?.ffUid ?: "N/A"}",
              color = TextSecondary,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Statistics Cards
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Text("TOURNAMENT STATISTICS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FlameAmber, letterSpacing = 1.sp)
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        StatBox(label = "Tournaments", value = totalTournaments.toString(), color = Color.White, modifier = Modifier.weight(1f))
        StatBox(label = "Booyahs (Wins)", value = totalWins.toString(), color = ElectricYellow, modifier = Modifier.weight(1f))
        StatBox(label = "Total Kills", value = totalKills.toString(), color = FireOrange, modifier = Modifier.weight(1f))
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Free Fire Identity Card
      EsportsCard {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Free Fire In-Game Identity", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
          TextButton(
            onClick = {
              editFullName = user?.fullName ?: ""
              editIgn = user?.ffIgn ?: ""
              editUid = user?.ffUid ?: ""
              showEditDialog = true
            },
            modifier = Modifier.testTag("edit_profile_button")
          ) {
            Text("Edit", color = FlameAmber, fontSize = 12.sp)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        ProfileInfoRow(label = "Free Fire IGN", value = user?.ffIgn ?: "Not configured")
        Spacer(modifier = Modifier.height(6.dp))
        ProfileInfoRow(label = "Free Fire UID", value = user?.ffUid ?: "Not configured")
        Spacer(modifier = Modifier.height(6.dp))
        ProfileInfoRow(label = "Mobile Number", value = user?.mobile ?: "Not configured")
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Navigation Options List
      Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          ProfileOptionItem(
            icon = Icons.Default.EmojiEvents,
            title = "Rewards & Redemptions",
            subtitle = "View tournament earnings and vouchers",
            onClick = onOpenRewards,
            tag = "option_rewards"
          )
          HorizontalDivider(color = CardBorder)
          ProfileOptionItem(
            icon = Icons.Default.Notifications,
            title = "Notifications",
            subtitle = "Room alerts, results and announcements",
            onClick = onOpenNotifications,
            tag = "option_notifications"
          )
          HorizontalDivider(color = CardBorder)
          ProfileOptionItem(
            icon = Icons.Default.SupportAgent,
            title = "Help & WhatsApp Support",
            subtitle = "Get assistance regarding matches and redemptions",
            onClick = onOpenSupport,
            tag = "option_support"
          )
          HorizontalDivider(color = CardBorder)
          ProfileOptionItem(
            icon = Icons.Default.Gavel,
            title = "Terms, Privacy & Responsible Play",
            subtitle = "Official legal notice & fair play policy",
            onClick = { showLegalDialog = true },
            tag = "option_legal"
          )
          HorizontalDivider(color = CardBorder)
          ProfileOptionItem(
            icon = Icons.Default.DeleteForever,
            title = "Delete Account",
            subtitle = "Permanent account and data removal request",
            onClick = { showAccountDeleteDialog = true },
            tint = ErrorRed,
            tag = "option_delete_account"
          )
          HorizontalDivider(color = CardBorder)
          ProfileOptionItem(
            icon = Icons.Default.Logout,
            title = "Logout",
            subtitle = "Sign out from this device",
            onClick = onLogout,
            tint = FlameAmber,
            tag = "option_logout"
          )
        }
      }
    }
  }

  // Edit Profile Dialog
  if (showEditDialog) {
    AlertDialog(
      onDismissRequest = { showEditDialog = false },
      title = { Text("Edit Player Profile", color = Color.White, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          OutlinedTextField(
            value = editFullName,
            onValueChange = { editFullName = it },
            label = { Text("Full Name") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = FireOrange,
              unfocusedBorderColor = CardBorder,
              focusedContainerColor = DarkSurfaceElevated,
              unfocusedContainerColor = DarkSurfaceElevated
            )
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = editIgn,
            onValueChange = { editIgn = it },
            label = { Text("Free Fire IGN") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = FireOrange,
              unfocusedBorderColor = CardBorder,
              focusedContainerColor = DarkSurfaceElevated,
              unfocusedContainerColor = DarkSurfaceElevated
            )
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = editUid,
            onValueChange = { editUid = it },
            label = { Text("Free Fire UID") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = FireOrange,
              unfocusedBorderColor = CardBorder,
              focusedContainerColor = DarkSurfaceElevated,
              unfocusedContainerColor = DarkSurfaceElevated
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showEditDialog = false
            onEditProfile(editFullName, editIgn, editUid)
          },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange)
        ) {
          Text("Save Changes")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditDialog = false }) {
          Text("Cancel", color = TextSecondary)
        }
      },
      containerColor = DarkSurfaceCard
    )
  }

  // Legal Information Dialog
  if (showLegalDialog) {
    AlertDialog(
      onDismissRequest = { showLegalDialog = false },
      title = { Text("Terms & Responsible Play", color = Color.White, fontWeight = FontWeight.Bold) },
      text = {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
          Text(
            text = "1. FJ_Fire is exclusively an esports organizer for Free Fire.\n\n" +
              "2. 100% Free Entry: No entry fees, wagering, betting, or gambling mechanics exist in FJ_Fire.\n\n" +
              "3. Rewards: Any promotional rewards, vouchers, or points are provided free of cost to tournament winners and subject to applicable local laws, age requirements, and identity verification.\n\n" +
              "4. Anti-Cheat: Emulators, modified APKs, scripts, or teaming are strictly prohibited and result in permanent ban.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 18.sp
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { showLegalDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange)
        ) {
          Text("I Agree")
        }
      },
      containerColor = DarkSurfaceCard
    )
  }

  // Account Deletion Dialog
  if (showAccountDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showAccountDeleteDialog = false },
      title = { Text("Delete Account?", color = ErrorRed, fontWeight = FontWeight.Bold) },
      text = {
        Text(
          text = "Are you sure you want to permanently delete your FJ_Fire player account and all associated statistics and rewards? This action is irreversible.",
          color = TextSecondary,
          fontSize = 13.sp
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showAccountDeleteDialog = false
            onLogout()
          },
          colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
        ) {
          Text("Delete Account")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAccountDeleteDialog = false }) {
          Text("Cancel", color = TextSecondary)
        }
      },
      containerColor = DarkSurfaceCard
    )
  }
}

@Composable
fun StatBox(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    color = DarkSurfaceCard,
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, CardBorder),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = color)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = label, fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Medium)
    }
  }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(text = label, fontSize = 12.sp, color = TextMuted)
    Text(text = value, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
  }
}

@Composable
fun ProfileOptionItem(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit,
  tint: Color = TextPrimary,
  tag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 14.dp)
      .testTag(tag),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
      Spacer(modifier = Modifier.width(14.dp))
      Column {
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = tint)
        Text(text = subtitle, fontSize = 11.sp, color = TextMuted)
      }
    }
    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
  }
}
