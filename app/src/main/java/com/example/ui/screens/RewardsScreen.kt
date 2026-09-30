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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.RewardEntity
import com.example.data.entity.WithdrawalRequestEntity
import com.example.ui.components.EsportsCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.openWhatsAppChat
import com.example.ui.theme.*

@Composable
fun RewardsScreen(
  rewards: List<RewardEntity>,
  withdrawals: List<WithdrawalRequestEntity>,
  lastWithdrawalRequest: WithdrawalRequestEntity?,
  whatsAppNumber: String,
  onOpenWithdrawForm: () -> Unit,
  onDismissLastWithdrawal: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableStateOf(0) } // 0: Available, 1: Pending, 2: Completed, 3: History

  val availableRewards = remember(rewards) { rewards.filter { it.status == "AVAILABLE" } }
  val pendingRewards = remember(rewards) { rewards.filter { it.status == "PENDING" } }
  val completedRewards = remember(rewards) { rewards.filter { it.status == "COMPLETED" } }

  val totalAvailablePoints = remember(availableRewards) {
    availableRewards.sumOf { it.points }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .testTag("rewards_screen")
  ) {
    // Header Balance Card
    Surface(
      color = DarkSurface,
      tonalElevation = 4.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Text(
          text = "FJ_Fire Rewards & Wallet",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = Color.White
        )
        Text(
          text = "Earned through Free Fire tournament performance",
          fontSize = 12.sp,
          color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Balance Overview Box
        EsportsCard {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("AVAILABLE REWARD BALANCE", fontSize = 11.sp, color = FlameAmber, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = ElectricYellow, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "$totalAvailablePoints Points",
                  fontSize = 24.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.White
                )
              }
              Text("Redeemable for Free Fire Diamond Codes & Vouchers", fontSize = 11.sp, color = TextMuted)
            }

            Button(
              onClick = onOpenWithdrawForm,
              enabled = availableRewards.isNotEmpty(),
              colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("withdraw_redeem_button")
            ) {
              Text("Redeem", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 4 Sections: Available Rewards, Pending Rewards, Completed Rewards, Reward History
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = DarkSurfaceElevated,
      contentColor = FireOrange
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Available (${availableRewards.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
        modifier = Modifier.testTag("tab_rewards_available")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Pending (${pendingRewards.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
        modifier = Modifier.testTag("tab_rewards_pending")
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = { Text("Completed (${completedRewards.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
        modifier = Modifier.testTag("tab_rewards_completed")
      )
      Tab(
        selected = selectedTab == 3,
        onClick = { selectedTab = 3 },
        text = { Text("Redemptions (${withdrawals.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
        modifier = Modifier.testTag("tab_rewards_history")
      )
    }

    if (selectedTab == 3) {
      // Withdrawal / Redemption Requests History
      if (withdrawals.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
          Text("No redemption requests submitted yet.", color = TextSecondary, fontSize = 13.sp)
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
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
              Text(req.rewardTitle, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
              Text("Value: ${req.requestedValue}", fontSize = 12.sp, color = ElectricYellow, fontWeight = FontWeight.Bold)
              Text("Method: ${req.paymentMethod}", fontSize = 11.sp, color = TextSecondary)
              Text("Account: ${req.accountDetails}", fontSize = 11.sp, color = TextMuted)
              Text("Requested: ${req.dateStr}", fontSize = 10.sp, color = TextMuted)
              if (req.adminNotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Admin Note: ${req.adminNotes}", fontSize = 11.sp, color = FlameAmber)
              }
            }
          }
        }
      }
    } else {
      val listToShow = when (selectedTab) {
        0 -> availableRewards
        1 -> pendingRewards
        else -> completedRewards
      }

      if (listToShow.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("No rewards in this category.", color = TextSecondary, fontSize = 13.sp)
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(listToShow, key = { it.id }) { reward ->
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
                    text = reward.id,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FlameAmber,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }

                StatusBadge(status = reward.status)
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(reward.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
              Spacer(modifier = Modifier.height(2.dp))
              Text("Tournament: ${reward.tournamentName}", fontSize = 12.sp, color = TextSecondary)
              Spacer(modifier = Modifier.height(6.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(reward.value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = ElectricYellow)
                Text(reward.dateStr, fontSize = 11.sp, color = TextMuted)
              }
            }
          }
        }
      }
    }
  }

  // POST-WITHDRAWAL WHATSAPP SUPPORT POPUP MODAL
  if (lastWithdrawalRequest != null) {
    val req = lastWithdrawalRequest
    val defaultMessage = "Hello FJ_Fire Support. I have a withdrawal/reward request. Request ID: #${req.id}."

    AlertDialog(
      onDismissRequest = onDismissLastWithdrawal,
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Request Submitted!", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column {
          Text(
            text = "Your reward redemption request has been registered in the system.",
            color = TextSecondary,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            color = DarkSurfaceElevated,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text("Request ID: #${req.id}", fontWeight = FontWeight.Bold, color = FlameAmber, fontSize = 13.sp)
              Text("Amount: ${req.requestedValue}", color = Color.White, fontSize = 12.sp)
              Text("Method: ${req.paymentMethod}", color = TextSecondary, fontSize = 11.sp)
              Text("Status: PENDING", color = WarningYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "To expedite verification, tap below to contact FJ_Fire Support on WhatsApp with your Request ID.",
            color = TextSecondary,
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "WhatsApp Support: $whatsAppNumber",
            color = SuccessGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            openWhatsAppChat(context, whatsAppNumber, defaultMessage)
            onDismissLastWithdrawal()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
          modifier = Modifier.testTag("contact_whatsapp_button")
        ) {
          Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Contact on WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = onDismissLastWithdrawal) {
          Text("Done", color = TextSecondary)
        }
      },
      containerColor = DarkSurfaceCard
    )
  }
}
