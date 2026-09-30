package com.example.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FJFireLogo
import com.example.ui.theme.*

@Composable
fun AdminLoginScreen(
  onAdminLogin: (String, String) -> Unit,
  onBackToUserApp: () -> Unit,
  errorMessage: String?
) {
  var adminEmail by remember { mutableStateOf("superadmin@fjfire.com") }
  var adminPassword by remember { mutableStateOf("admin123") }
  var showPassword by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkBackground)
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("admin_login_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
      ) {
        IconButton(
          onClick = onBackToUserApp,
          modifier = Modifier.testTag("admin_back_to_user_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Return to User App", tint = Color.White)
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      FJFireLogo(size = 52)

      Spacer(modifier = Modifier.height(16.dp))

      Surface(
        color = Color(0x33FF5722),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, CardBorderHighlight)
      ) {
        Text(
          text = "ADMINISTRATION CONTROL CONSOLE",
          fontSize = 11.sp,
          fontWeight = FontWeight.Black,
          color = FireOrange,
          letterSpacing = 1.sp,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Authorized Staff & Esports Directors Only",
        fontSize = 13.sp,
        color = TextSecondary
      )

      Spacer(modifier = Modifier.height(28.dp))

      // Error message
      AnimatedVisibility(visible = errorMessage != null) {
        Surface(
          color = Color(0x33EF4444),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, Color(0x88EF4444)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(errorMessage ?: "", color = Color(0xFFFFCDD2), fontSize = 12.sp)
          }
        }
      }

      Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "Admin Portal Credentials",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color.White
          )
          Spacer(modifier = Modifier.height(14.dp))

          OutlinedTextField(
            value = adminEmail,
            onValueChange = { adminEmail = it },
            label = { Text("Admin Email / Operator ID") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = FlameAmber) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = FireOrange,
              unfocusedBorderColor = CardBorder,
              focusedContainerColor = DarkSurfaceElevated,
              unfocusedContainerColor = DarkSurfaceElevated
            ),
            modifier = Modifier.fillMaxWidth().testTag("admin_email_input")
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = adminPassword,
            onValueChange = { adminPassword = it },
            label = { Text("Security Key / Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = FlameAmber) },
            trailingIcon = {
              IconButton(onClick = { showPassword = !showPassword }) {
                Icon(
                  imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                  contentDescription = null,
                  tint = TextSecondary
                )
              }
            },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = FireOrange,
              unfocusedBorderColor = CardBorder,
              focusedContainerColor = DarkSurfaceElevated,
              unfocusedContainerColor = DarkSurfaceElevated
            ),
            modifier = Modifier.fillMaxWidth().testTag("admin_password_input")
          )

          Spacer(modifier = Modifier.height(20.dp))

          Button(
            onClick = { onAdminLogin(adminEmail, adminPassword) },
            colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("admin_login_submit_button")
          ) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("ENTER ADMIN DASHBOARD", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Quick Demo Admin Selector buttons
          Text(
            text = "Demo Credentials (Pre-seeded):",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = {
                adminEmail = "superadmin@fjfire.com"
                adminPassword = "admin123"
              },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f).height(38.dp)
            ) {
              Text("Super Admin", fontSize = 11.sp, color = FlameAmber, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
              onClick = {
                adminEmail = "staff@fjfire.com"
                adminPassword = "staff123"
              },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f).height(38.dp)
            ) {
              Text("Staff / Dist.", fontSize = 11.sp, color = TextSecondary)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(30.dp))

      TextButton(
        onClick = onBackToUserApp,
        modifier = Modifier.testTag("back_to_user_app_button")
      ) {
        Text("← Back to FJ_Fire User App", color = TextSecondary, fontSize = 13.sp)
      }
    }
  }
}
