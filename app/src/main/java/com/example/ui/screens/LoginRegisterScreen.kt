package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun LoginRegisterScreen(
  onLogin: (String, String) -> Unit,
  onRequestOtp: (String) -> Unit,
  onVerifyOtp: (String, String) -> Unit,
  onRegister: (String, String, String, String, String, String, String) -> Unit,
  onAdminPortalClick: () -> Unit,
  errorMessage: String?,
  otpGenerated: String?,
  onQuickDemoLogin: () -> Unit
) {
  var selectedTab by remember { mutableStateOf(0) } // 0: Login, 1: Register, 2: OTP

  // Login inputs
  var loginMobile by remember { mutableStateOf("9876543210") }
  var loginPassword by remember { mutableStateOf("pass123") }
  var showLoginPassword by remember { mutableStateOf(false) }

  // Register inputs
  var regFullName by remember { mutableStateOf("") }
  var regUsername by remember { mutableStateOf("") }
  var regMobile by remember { mutableStateOf("") }
  var regEmail by remember { mutableStateOf("") }
  var regFfUid by remember { mutableStateOf("") }
  var regFfIgn by remember { mutableStateOf("") }
  var regPassword by remember { mutableStateOf("") }
  var showRegPassword by remember { mutableStateOf(false) }

  // OTP inputs
  var otpMobile by remember { mutableStateOf("9876543210") }
  var enteredOtp by remember { mutableStateOf("") }

  // Forgot password dialog
  var showForgotPasswordDialog by remember { mutableStateOf(false) }
  var forgotMobile by remember { mutableStateOf("") }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkBackground)
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("login_register_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(12.dp))
      FJFireLogo(size = 46)

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Free Fire Tournaments Hub",
        color = TextSecondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Tabs: Login, Register, OTP
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = DarkSurfaceElevated,
        contentColor = FireOrange,
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Login", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
          modifier = Modifier.testTag("tab_login")
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Register", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
          modifier = Modifier.testTag("tab_register")
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("OTP Login", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
          modifier = Modifier.testTag("tab_otp")
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Error banner
      AnimatedVisibility(visible = errorMessage != null) {
        Surface(
          color = Color(0x33EF4444),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, Color(0x88EF4444)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = "Error",
              tint = ErrorRed,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = errorMessage ?: "",
              color = Color(0xFFFFCDD2),
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      // TAB 0: LOGIN
      if (selectedTab == 0) {
        OutlinedTextField(
          value = loginMobile,
          onValueChange = { loginMobile = it },
          label = { Text("Mobile Number") },
          leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FlameAmber) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FireOrange,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = DarkSurfaceCard,
            unfocusedContainerColor = DarkSurfaceCard
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("login_mobile_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = loginPassword,
          onValueChange = { loginPassword = it },
          label = { Text("Password") },
          leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = FlameAmber) },
          trailingIcon = {
            IconButton(onClick = { showLoginPassword = !showLoginPassword }) {
              Icon(
                imageVector = if (showLoginPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = "Toggle password visibility",
                tint = TextSecondary
              )
            }
          },
          visualTransformation = if (showLoginPassword) VisualTransformation.None else PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FireOrange,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = DarkSurfaceCard,
            unfocusedContainerColor = DarkSurfaceCard
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("login_password_input")
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = { showForgotPasswordDialog = true }) {
            Text("Forgot Password?", color = FlameAmber, fontSize = 12.sp)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
          onClick = { onLogin(loginMobile, loginPassword) },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("login_submit_button")
        ) {
          Text("LOGIN TO FJ_FIRE", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
          onClick = onQuickDemoLogin,
          border = BorderStroke(1.dp, FlameAmber),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("quick_demo_login_button")
        ) {
          Icon(Icons.Default.FlashOn, contentDescription = null, tint = FlameAmber, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Quick Demo Player Login", color = FlameAmber, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
      }

      // TAB 1: REGISTER
      if (selectedTab == 1) {
        OutlinedTextField(
          value = regFullName,
          onValueChange = { regFullName = it },
          label = { Text("Full Name") },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = FlameAmber) },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FireOrange,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = DarkSurfaceCard,
            unfocusedContainerColor = DarkSurfaceCard
          ),
          modifier = Modifier.fillMaxWidth().testTag("reg_fullname_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = regUsername,
          onValueChange = { regUsername = it },
          label = { Text("Username") },
          leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = FlameAmber) },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FireOrange,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = DarkSurfaceCard,
            unfocusedContainerColor = DarkSurfaceCard
          ),
          modifier = Modifier.fillMaxWidth().testTag("reg_username_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = regMobile,
          onValueChange = { regMobile = it },
          label = { Text("Mobile Number") },
          leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FlameAmber) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FireOrange,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = DarkSurfaceCard,
            unfocusedContainerColor = DarkSurfaceCard
          ),
          modifier = Modifier.fillMaxWidth().testTag("reg_mobile_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = regEmail,
          onValueChange = { regEmail = it },
          label = { Text("Email (Optional)") },
          leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextMuted) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FireOrange,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = DarkSurfaceCard,
            unfocusedContainerColor = DarkSurfaceCard
          ),
          modifier = Modifier.fillMaxWidth().testTag("reg_email_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Free Fire Specific Fields
        OutlinedTextField(
          value = regFfUid,
          onValueChange = { regFfUid = it },
          label = { Text("Free Fire UID (Required)") },
          placeholder = { Text("e.g. 8492049102") },
          leadingIcon = { Icon(Icons.Default.VideogameAsset, contentDescription = null, tint = FireOrange) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FireOrange,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = DarkSurfaceCard,
            unfocusedContainerColor = DarkSurfaceCard
          ),
          modifier = Modifier.fillMaxWidth().testTag("reg_ffuid_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = regFfIgn,
          onValueChange = { regFfIgn = it },
          label = { Text("Free Fire In-Game Name (IGN)") },
          placeholder = { Text("e.g. FJ_PhoenixX") },
          leadingIcon = { Icon(Icons.Default.SportsEsports, contentDescription = null, tint = FireOrange) },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FireOrange,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = DarkSurfaceCard,
            unfocusedContainerColor = DarkSurfaceCard
          ),
          modifier = Modifier.fillMaxWidth().testTag("reg_ffign_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = regPassword,
          onValueChange = { regPassword = it },
          label = { Text("Password") },
          leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = FlameAmber) },
          trailingIcon = {
            IconButton(onClick = { showRegPassword = !showRegPassword }) {
              Icon(
                imageVector = if (showRegPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = "Toggle password visibility",
                tint = TextSecondary
              )
            }
          },
          visualTransformation = if (showRegPassword) VisualTransformation.None else PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FireOrange,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = DarkSurfaceCard,
            unfocusedContainerColor = DarkSurfaceCard
          ),
          modifier = Modifier.fillMaxWidth().testTag("reg_password_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = {
            onRegister(regFullName, regUsername, regMobile, regEmail, regFfUid, regFfIgn, regPassword)
          },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("reg_submit_button")
        ) {
          Text("CREATE FREE FIRE PLAYER ACCOUNT", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }

      // TAB 2: OTP LOGIN
      if (selectedTab == 2) {
        OutlinedTextField(
          value = otpMobile,
          onValueChange = { otpMobile = it },
          label = { Text("Mobile Number") },
          leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FlameAmber) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FireOrange,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = DarkSurfaceCard,
            unfocusedContainerColor = DarkSurfaceCard
          ),
          modifier = Modifier.fillMaxWidth().testTag("otp_mobile_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
          onClick = { onRequestOtp(otpMobile) },
          border = BorderStroke(1.dp, FlameAmber),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth().testTag("otp_request_button")
        ) {
          Text("Request 6-Digit OTP Code", color = FlameAmber, fontWeight = FontWeight.Bold)
        }

        if (otpGenerated != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            color = Color(0x2210B981),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0x6610B981)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "Demo Verification Code: $otpGenerated",
              color = SuccessGreen,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(10.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = enteredOtp,
          onValueChange = { enteredOtp = it },
          label = { Text("Enter 6-Digit OTP") },
          leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = FlameAmber) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = FireOrange,
            unfocusedBorderColor = CardBorder,
            focusedContainerColor = DarkSurfaceCard,
            unfocusedContainerColor = DarkSurfaceCard
          ),
          modifier = Modifier.fillMaxWidth().testTag("otp_code_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = { onVerifyOtp(otpMobile, enteredOtp) },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("otp_verify_button")
        ) {
          Text("VERIFY & SIGN IN", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Legal & Free Entry guarantee note
      Text(
        text = "🛡️ 100% Free Entry • No Betting/Gambling • Fair Play Enforcement",
        fontSize = 11.sp,
        color = TextMuted,
        fontWeight = FontWeight.Medium
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Separate Administration Access Link (Product 2)
      Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CardBorder),
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
            Icon(
              imageVector = Icons.Default.AdminPanelSettings,
              contentDescription = "Admin",
              tint = TextSecondary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text("FJ_Fire Administration", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              Text("Staff & tournament managers portal", color = TextMuted, fontSize = 11.sp)
            }
          }

          TextButton(
            onClick = onAdminPortalClick,
            modifier = Modifier.testTag("admin_portal_access_button")
          ) {
            Text("Admin Portal", color = FlameAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }
  }

  // Forgot Password Dialog
  if (showForgotPasswordDialog) {
    AlertDialog(
      onDismissRequest = { showForgotPasswordDialog = false },
      title = { Text("Password Recovery", color = Color.White, fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text(
            text = "Enter your registered mobile number. A reset verification code will be sent.",
            color = TextSecondary,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(12.dp))
          OutlinedTextField(
            value = forgotMobile,
            onValueChange = { forgotMobile = it },
            label = { Text("Mobile Number") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = FireOrange,
              unfocusedBorderColor = CardBorder,
              focusedContainerColor = DarkSurfaceCard,
              unfocusedContainerColor = DarkSurfaceCard
            ),
            singleLine = true
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showForgotPasswordDialog = false
            onRequestOtp(forgotMobile)
            selectedTab = 2
          },
          colors = ButtonDefaults.buttonColors(containerColor = FireOrange)
        ) {
          Text("Send Recovery OTP")
        }
      },
      dismissButton = {
        TextButton(onClick = { showForgotPasswordDialog = false }) {
          Text("Cancel", color = TextSecondary)
        }
      },
      containerColor = DarkSurfaceCard
    )
  }
}
