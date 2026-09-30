package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.admin.AdminLoginScreen
import com.example.ui.components.FJFireHeader
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.AppDestination
import com.example.viewmodel.FJFireViewModel
import com.example.viewmodel.UserTab

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        FJFireApp()
      }
    }
  }
}

@Composable
fun FJFireApp(viewModel: FJFireViewModel = viewModel()) {
  val context = LocalContext.current

  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val currentUserTab by viewModel.currentUserTab.collectAsStateWithLifecycle()
  val currentAdminTab by viewModel.currentAdminTab.collectAsStateWithLifecycle()
  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
  val currentAdmin by viewModel.currentAdmin.collectAsStateWithLifecycle()
  val authError by viewModel.authError.collectAsStateWithLifecycle()
  val adminAuthError by viewModel.adminAuthError.collectAsStateWithLifecycle()
  val otpState by viewModel.otpState.collectAsStateWithLifecycle()
  val uiNotice by viewModel.uiNotice.collectAsStateWithLifecycle()
  val tournaments by viewModel.tournaments.collectAsStateWithLifecycle()
  val allApplications by viewModel.allApplications.collectAsStateWithLifecycle()
  val allRewards by viewModel.allRewards.collectAsStateWithLifecycle()
  val allWithdrawals by viewModel.allWithdrawals.collectAsStateWithLifecycle()
  val allNotifications by viewModel.allNotifications.collectAsStateWithLifecycle()
  val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
  val allResults by viewModel.allResults.collectAsStateWithLifecycle()
  val allAdminUsers by viewModel.allAdminUsers.collectAsStateWithLifecycle()
  val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
  val settings by viewModel.settingsMap.collectAsStateWithLifecycle()
  val lastWithdrawal by viewModel.lastWithdrawalRequest.collectAsStateWithLifecycle()
  val selectedTournamentId by viewModel.selectedTournamentId.collectAsStateWithLifecycle()

  // Display toast on notice
  LaunchedEffect(uiNotice) {
    uiNotice?.let { notice ->
      Toast.makeText(context, notice, Toast.LENGTH_SHORT).show()
      viewModel.clearNotice()
    }
  }

  val userApplications = remember(allApplications, currentUser) {
    if (currentUser == null) emptyList()
    else allApplications.filter { it.userId == currentUser?.id }
  }

  val appliedTournamentIds = remember(userApplications) {
    userApplications.map { it.tournamentId }.toSet()
  }

  val userRewards = remember(allRewards, currentUser) {
    if (currentUser == null) emptyList()
    else allRewards.filter { it.userId == currentUser?.id }
  }

  val userWithdrawals = remember(allWithdrawals, currentUser) {
    if (currentUser == null) emptyList()
    else allWithdrawals.filter { it.userId == currentUser?.id }
  }

  val userNotifications = remember(allNotifications, currentUser) {
    if (currentUser == null) allNotifications.filter { it.targetUserId == "ALL" }
    else allNotifications.filter { it.targetUserId == "ALL" || it.targetUserId == currentUser?.id }
  }

  val unreadNotifsCount = remember(userNotifications) {
    userNotifications.count { !it.isRead }
  }

  val userResults = remember(allResults, currentUser) {
    if (currentUser == null) emptyList()
    else allResults.filter { it.ffUid == currentUser?.ffUid }
  }

  val waNumber = settings["whatsapp_support_number"] ?: "+1234567890"
  val waName = settings["whatsapp_display_name"] ?: "FJ_Fire Official Support"
  val waMsg = settings["support_message"] ?: "Hello FJ_Fire Support, I need help regarding my request."

  when (currentScreen) {
    AppDestination.SPLASH -> {
      SplashScreen(
        isLoggedIn = currentUser != null,
        onSplashFinished = {
          if (currentUser != null) {
            viewModel.navigateTo(AppDestination.USER_MAIN)
          } else {
            viewModel.navigateTo(AppDestination.LOGIN_REGISTER)
          }
        }
      )
    }

    AppDestination.LOGIN_REGISTER -> {
      LoginRegisterScreen(
        onLogin = { mobile, pass -> viewModel.loginUser(mobile, pass) },
        onRequestOtp = { mobile -> viewModel.requestOtp(mobile) },
        onVerifyOtp = { mobile, otp -> viewModel.verifyOtpAndLogin(mobile, otp) },
        onRegister = { fn, un, mob, em, uid, ign, pass ->
          viewModel.registerUser(fn, un, mob, em, uid, ign, pass)
        },
        onAdminPortalClick = { viewModel.navigateTo(AppDestination.ADMIN_LOGIN) },
        errorMessage = authError,
        otpGenerated = otpState,
        onQuickDemoLogin = {
          viewModel.loginUser("9876543210", "pass123")
        }
      )
    }

    AppDestination.USER_MAIN -> {
      Scaffold(
        topBar = {
          FJFireHeader(
            onNotificationsClick = { viewModel.navigateTo(AppDestination.NOTIFICATIONS) },
            onProfileClick = { viewModel.setUserTab(UserTab.PROFILE) },
            unreadNotifications = unreadNotifsCount
          )
        },
        bottomBar = {
          // Bottom Navigation strictly has the 4 requested tabs:
          // 1. Home, 2. My Applied, 3. Rewards, 4. Profile
          NavigationBar(
            containerColor = DarkSurface,
            contentColor = TextPrimary,
            tonalElevation = 8.dp,
            windowInsets = WindowInsets.navigationBars,
            modifier = Modifier.testTag("user_bottom_navigation")
          ) {
            NavigationBarItem(
              selected = currentUserTab == UserTab.HOME,
              onClick = { viewModel.setUserTab(UserTab.HOME) },
              icon = {
                Icon(
                  imageVector = if (currentUserTab == UserTab.HOME) Icons.Filled.Home else Icons.Default.Home,
                  contentDescription = "Home"
                )
              },
              label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FireOrange,
                selectedTextColor = FireOrange,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color(0x33FF5722)
              ),
              modifier = Modifier.testTag("bottom_tab_home")
            )

            NavigationBarItem(
              selected = currentUserTab == UserTab.MY_APPLIED,
              onClick = { viewModel.setUserTab(UserTab.MY_APPLIED) },
              icon = {
                Icon(
                  imageVector = if (currentUserTab == UserTab.MY_APPLIED) Icons.Filled.Assignment else Icons.Default.Assignment,
                  contentDescription = "My Applied"
                )
              },
              label = { Text("My Applied", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FireOrange,
                selectedTextColor = FireOrange,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color(0x33FF5722)
              ),
              modifier = Modifier.testTag("bottom_tab_my_applied")
            )

            NavigationBarItem(
              selected = currentUserTab == UserTab.REWARDS,
              onClick = { viewModel.setUserTab(UserTab.REWARDS) },
              icon = {
                Icon(
                  imageVector = if (currentUserTab == UserTab.REWARDS) Icons.Filled.EmojiEvents else Icons.Default.EmojiEvents,
                  contentDescription = "Rewards"
                )
              },
              label = { Text("Rewards", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FireOrange,
                selectedTextColor = FireOrange,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color(0x33FF5722)
              ),
              modifier = Modifier.testTag("bottom_tab_rewards")
            )

            NavigationBarItem(
              selected = currentUserTab == UserTab.PROFILE,
              onClick = { viewModel.setUserTab(UserTab.PROFILE) },
              icon = {
                Icon(
                  imageVector = if (currentUserTab == UserTab.PROFILE) Icons.Filled.Person else Icons.Default.Person,
                  contentDescription = "Profile"
                )
              },
              label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FireOrange,
                selectedTextColor = FireOrange,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color(0x33FF5722)
              ),
              modifier = Modifier.testTag("bottom_tab_profile")
            )
          }
        }
      ) { innerPadding ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
          when (currentUserTab) {
            UserTab.HOME -> {
              HomeScreen(
                tournaments = tournaments,
                appliedTournamentIds = appliedTournamentIds,
                onTournamentClick = { id -> viewModel.viewTournamentDetails(id) }
              )
            }
            UserTab.MY_APPLIED -> {
              MyAppliedScreen(
                userApplications = userApplications,
                tournaments = tournaments,
                results = allResults,
                onViewTournament = { id -> viewModel.viewTournamentDetails(id) },
                onViewResults = { id -> viewModel.viewTournamentResults(id) }
              )
            }
            UserTab.REWARDS -> {
              RewardsScreen(
                rewards = userRewards,
                withdrawals = userWithdrawals,
                lastWithdrawalRequest = lastWithdrawal,
                whatsAppNumber = waNumber,
                onOpenWithdrawForm = { viewModel.navigateTo(AppDestination.WITHDRAW_FORM) },
                onDismissLastWithdrawal = { viewModel.clearLastWithdrawal() }
              )
            }
            UserTab.PROFILE -> {
              ProfileScreen(
                user = currentUser,
                userApplications = userApplications,
                userResults = userResults,
                onEditProfile = { fn, ign, uid -> viewModel.updateUserProfile(fn, ign, uid) },
                onOpenRewards = { viewModel.setUserTab(UserTab.REWARDS) },
                onOpenNotifications = { viewModel.navigateTo(AppDestination.NOTIFICATIONS) },
                onOpenSupport = { viewModel.navigateTo(AppDestination.HELP_SUPPORT) },
                onLogout = { viewModel.logoutUser() }
              )
            }
          }
        }
      }
    }

    AppDestination.TOURNAMENT_DETAILS -> {
      val t = tournaments.find { it.id == selectedTournamentId }
      val app = userApplications.find { it.tournamentId == selectedTournamentId }
      TournamentDetailsScreen(
        tournament = t,
        currentUser = currentUser,
        existingApplication = app,
        onApply = { id -> viewModel.applyForTournament(id) },
        onGoToMyApplied = { viewModel.setUserTab(UserTab.MY_APPLIED) },
        onViewResults = { id -> viewModel.viewTournamentResults(id) },
        onBack = { viewModel.navigateTo(AppDestination.USER_MAIN) }
      )
    }

    AppDestination.RESULTS_VIEW -> {
      val t = tournaments.find { it.id == selectedTournamentId }
      val res = allResults.filter { it.tournamentId == selectedTournamentId }
      ResultsScreen(
        tournament = t,
        results = res,
        onBack = { viewModel.navigateTo(AppDestination.USER_MAIN) }
      )
    }

    AppDestination.WITHDRAW_FORM -> {
      val available = userRewards.filter { it.status == "AVAILABLE" }
      WithdrawFormScreen(
        currentUser = currentUser,
        availableRewards = available,
        onSubmit = { title, value, method, account ->
          viewModel.submitWithdrawalRequest(title, value, method, account)
        },
        onBack = { viewModel.setUserTab(UserTab.REWARDS) }
      )
    }

    AppDestination.HELP_SUPPORT -> {
      HelpSupportScreen(
        whatsAppNumber = waNumber,
        whatsAppName = waName,
        supportMessage = waMsg,
        onSubmitTicket = { sub, msg -> viewModel.submitSupportTicket(sub, msg) },
        onBack = { viewModel.navigateTo(AppDestination.USER_MAIN) }
      )
    }

    AppDestination.NOTIFICATIONS -> {
      NotificationsScreen(
        notifications = userNotifications,
        onMarkRead = { id -> viewModel.markNotificationRead(id) },
        onBack = { viewModel.navigateTo(AppDestination.USER_MAIN) }
      )
    }

    AppDestination.ADMIN_LOGIN -> {
      AdminLoginScreen(
        onAdminLogin = { email, pass -> viewModel.loginAdmin(email, pass) },
        onBackToUserApp = { viewModel.navigateTo(AppDestination.USER_MAIN) },
        errorMessage = adminAuthError
      )
    }

    AppDestination.ADMIN_PANEL -> {
      AdminDashboardScreen(
        currentAdmin = currentAdmin,
        selectedTab = currentAdminTab,
        onSelectTab = { tab -> viewModel.setAdminTab(tab) },
        tournaments = tournaments,
        applications = allApplications,
        users = allUsers,
        results = allResults,
        rewards = allRewards,
        withdrawals = allWithdrawals,
        notifications = allNotifications,
        supportTickets = viewModel.allSupportTickets.collectAsStateWithLifecycle().value,
        staffUsers = allAdminUsers,
        auditLogs = auditLogs,
        settings = settings,
        onCreateTournament = { n, typ, m, d, tim, r, max, rew ->
          viewModel.adminCreateTournament(n, typ, m, d, tim, r, max, rew)
        },
        onUpdateRoomDetails = { id, rId, pass, rel ->
          viewModel.adminUpdateRoomDetails(id, rId, pass, rel)
        },
        onUpdateTournamentStatus = { tour, st ->
          viewModel.adminUpdateTournamentStatus(tour, st)
        },
        onDeleteTournament = { id ->
          viewModel.adminDeleteTournament(id)
        },
        onUpdateAppStatus = { app, st ->
          viewModel.adminUpdateApplicationStatus(app, st)
        },
        onAddResult = { tId, tName, pos, p, u, ign, k, pts, rSt ->
          viewModel.adminAddResult(tId, tName, pos, p, u, ign, k, pts, rSt)
        },
        onPublishResults = { tId, tName ->
          viewModel.adminPublishResults(tId, tName)
        },
        onUpdateWithdrawalStatus = { req, st, notes ->
          viewModel.adminUpdateWithdrawalStatus(req, st, notes)
        },
        onSendNotification = { title, msg, typ, target ->
          viewModel.adminSendNotification(title, msg, typ, target)
        },
        onUpdateSetting = { k, v ->
          viewModel.adminUpdateSetting(k, v)
        },
        onCreateStaff = { un, em, p, r, perms ->
          viewModel.adminCreateStaff(un, em, p, r, perms)
        },
        onToggleUserSuspension = { u ->
          viewModel.adminToggleUserSuspension(u)
        },
        onLogoutAdmin = {
          viewModel.logoutAdmin()
        }
      )
    }
  }
}
