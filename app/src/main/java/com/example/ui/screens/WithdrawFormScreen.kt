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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.RewardEntity
import com.example.data.entity.UserEntity
import com.example.ui.components.EsportsCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawFormScreen(
  currentUser: UserEntity?,
  availableRewards: List<RewardEntity>,
  onSubmit: (String, String, String, String) -> Unit,
  onBack: () -> Unit
) {
  BackHandler(onBack = onBack)

  var selectedRewardTitle by remember {
    mutableStateOf(availableRewards.firstOrNull()?.title ?: "Tournament Reward Points")
  }
  var requestedValue by remember {
    mutableStateOf(availableRewards.firstOrNull()?.value ?: "200 Points")
  }
  var paymentMethod by remember { mutableStateOf("Free Fire Diamonds Direct UID Transfer") }
  var accountDetails by remember { mutableStateOf(currentUser?.ffUid ?: "") }

  val paymentMethods = listOf(
    "Free Fire Diamonds Direct UID Transfer",
    "Google Play Gift Card Voucher",
    "Bank Account / UPI (Where legally permissible)"
  )

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
            modifier = Modifier.testTag("withdraw_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Reward Redemption Request",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = Color.White
          )
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
      // Compliance info box
      Surface(
        color = Color(0x22F59E0B),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0x66F59E0B)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(Icons.Default.Info, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Notice: Reward redemptions are subject to identity verification, age eligibility, and local legal guidelines. 100% Free Entry tournament rewards only.",
            fontSize = 11.sp,
            color = Color(0xFFFFE082),
            lineHeight = 16.sp
          )
        }
      }

      EsportsCard {
        Text("Redemption Details", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = selectedRewardTitle,
          onValueChange = { selectedRewardTitle = it },
          label = { Text("Reward Title / Event") },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FireOrange,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = DarkSurfaceElevated,
            unfocusedContainerColor = DarkSurfaceElevated
          ),
          modifier = Modifier.fillMaxWidth().testTag("withdraw_title_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = requestedValue,
          onValueChange = { requestedValue = it },
          label = { Text("Points / Value to Redeem") },
          placeholder = { Text("e.g. 200 Points / 100 Diamonds") },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FireOrange,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = DarkSurfaceElevated,
            unfocusedContainerColor = DarkSurfaceElevated
          ),
          modifier = Modifier.fillMaxWidth().testTag("withdraw_value_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text("Select Redemption Method", fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))

        paymentMethods.forEach { method ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
          ) {
            RadioButton(
              selected = paymentMethod == method,
              onClick = {
                paymentMethod = method
                if (method.contains("UID") && currentUser != null) {
                  accountDetails = currentUser.ffUid
                }
              },
              colors = RadioButtonDefaults.colors(selectedColor = FireOrange)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = method,
              fontSize = 12.sp,
              color = if (paymentMethod == method) Color.White else TextSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = accountDetails,
          onValueChange = { accountDetails = it },
          label = { Text("Delivery Account / Details (FF UID, Voucher Email or UPI ID)") },
          placeholder = { Text("e.g. Free Fire UID: 8492049102") },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FireOrange,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = DarkSurfaceElevated,
            unfocusedContainerColor = DarkSurfaceElevated
          ),
          modifier = Modifier.fillMaxWidth().testTag("withdraw_account_input")
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = {
            onSubmit(selectedRewardTitle, requestedValue, paymentMethod, accountDetails)
            onBack()
          },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("submit_withdraw_button")
        ) {
          Text("SUBMIT REDEMPTION REQUEST", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}
