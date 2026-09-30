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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EsportsCard
import com.example.ui.components.openWhatsAppChat
import com.example.ui.theme.*

@Composable
fun HelpSupportScreen(
  whatsAppNumber: String,
  whatsAppName: String,
  supportMessage: String,
  onSubmitTicket: (String, String) -> Unit,
  onBack: () -> Unit
) {
  BackHandler(onBack = onBack)
  val context = LocalContext.current

  var ticketSubject by remember { mutableStateOf("") }
  var ticketMessage by remember { mutableStateOf("") }

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
            modifier = Modifier.testTag("support_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Help & Support",
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
      // Official WhatsApp Support Card
      EsportsCard {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .background(Color(0xFF25D366), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White)
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(whatsAppName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
            Text("Fastest support for match & redemption issues", fontSize = 11.sp, color = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "Support Hotline: $whatsAppNumber",
          fontSize = 12.sp,
          color = FlameAmber,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
          onClick = {
            openWhatsAppChat(context, whatsAppNumber, supportMessage)
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("open_whatsapp_support_button")
        ) {
          Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Connect on WhatsApp", fontWeight = FontWeight.Bold, color = Color.White)
        }
      }

      // Frequently Asked Questions
      EsportsCard {
        Text("Frequently Asked Questions (FAQ)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
        Spacer(modifier = Modifier.height(12.dp))

        FaqItem(
          question = "Are FJ_Fire tournaments really free?",
          answer = "Yes! Every single tournament on FJ_Fire is 100% free entry. There are no registration fees, bets, or wagers whatsoever."
        )
        FaqItem(
          question = "Where do I get the Room ID and Password?",
          answer = "For registered players, Room ID and Password are unlocked under the 'My Applied' tab approximately 15 minutes before match start."
        )
        FaqItem(
          question = "Can I play on PC or Emulator?",
          answer = "No. Emulators and PC controls are strictly forbidden to ensure equal fair play. Mobile devices only."
        )
        FaqItem(
          question = "How are results calculated?",
          answer = "Admins record official match results based on placement points (Booyah = 12 pts) plus 1 point per kill."
        )
      }

      // Submit Ticket
      EsportsCard {
        Text("Report an Issue / Submit Ticket", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = ticketSubject,
          onValueChange = { ticketSubject = it },
          label = { Text("Subject") },
          placeholder = { Text("e.g. Room password issue / UID update") },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FireOrange,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = DarkSurfaceElevated,
            unfocusedContainerColor = DarkSurfaceElevated
          ),
          modifier = Modifier.fillMaxWidth().testTag("ticket_subject_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = ticketMessage,
          onValueChange = { ticketMessage = it },
          label = { Text("Detailed Message") },
          minLines = 3,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FireOrange,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = DarkSurfaceElevated,
            unfocusedContainerColor = DarkSurfaceElevated
          ),
          modifier = Modifier.fillMaxWidth().testTag("ticket_message_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = {
            onSubmitTicket(ticketSubject, ticketMessage)
            ticketSubject = ""
            ticketMessage = ""
          },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth().testTag("submit_ticket_button")
        ) {
          Text("SUBMIT TICKET", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun FaqItem(question: String, answer: String) {
  Column(modifier = Modifier.padding(vertical = 6.dp)) {
    Text(text = question, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = FlameAmber)
    Spacer(modifier = Modifier.height(2.dp))
    Text(text = answer, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
  }
}
