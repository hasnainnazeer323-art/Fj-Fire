package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.entity.AdminUserEntity
import com.example.data.entity.ApplicationEntity
import com.example.data.entity.AuditLogEntity
import com.example.data.entity.MatchResultEntity
import com.example.data.entity.NotificationEntity
import com.example.data.entity.RewardEntity
import com.example.data.entity.SupportTicketEntity
import com.example.data.entity.TournamentEntity
import com.example.data.entity.UserEntity
import com.example.data.entity.WithdrawalRequestEntity
import com.example.data.repository.FJFireRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class AppDestination {
  SPLASH,
  LOGIN_REGISTER,
  USER_MAIN,
  TOURNAMENT_DETAILS,
  RESULTS_VIEW,
  WITHDRAW_FORM,
  HELP_SUPPORT,
  NOTIFICATIONS,
  ADMIN_LOGIN,
  ADMIN_PANEL
}

enum class UserTab {
  HOME,
  MY_APPLIED,
  REWARDS,
  PROFILE
}

enum class AdminTab {
  OVERVIEW,
  USERS,
  TOURNAMENTS,
  APPLICATIONS,
  MATCHES,
  RESULTS,
  REWARDS,
  REQUESTS,
  NOTIFICATIONS,
  SUPPORT,
  STAFF,
  AUDIT_LOGS,
  SETTINGS
}

class FJFireViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application, viewModelScope)
  val repository = FJFireRepository(database)

  // Current Destination & Navigation
  private val _currentScreen = MutableStateFlow(AppDestination.SPLASH)
  val currentScreen: StateFlow<AppDestination> = _currentScreen.asStateFlow()

  private val _currentUserTab = MutableStateFlow(UserTab.HOME)
  val currentUserTab: StateFlow<UserTab> = _currentUserTab.asStateFlow()

  private val _currentAdminTab = MutableStateFlow(AdminTab.OVERVIEW)
  val currentAdminTab: StateFlow<AdminTab> = _currentAdminTab.asStateFlow()

  // Selected tournament for details or results
  private val _selectedTournamentId = MutableStateFlow<String?>(null)
  val selectedTournamentId: StateFlow<String?> = _selectedTournamentId.asStateFlow()

  // Authentication States
  private val _currentUser = MutableStateFlow<UserEntity?>(null)
  val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

  private val _currentAdmin = MutableStateFlow<AdminUserEntity?>(null)
  val currentAdmin: StateFlow<AdminUserEntity?> = _currentAdmin.asStateFlow()

  private val _authError = MutableStateFlow<String?>(null)
  val authError: StateFlow<String?> = _authError.asStateFlow()

  private val _adminAuthError = MutableStateFlow<String?>(null)
  val adminAuthError: StateFlow<String?> = _adminAuthError.asStateFlow()

  private val _otpState = MutableStateFlow<String?>(null)
  val otpState: StateFlow<String?> = _otpState.asStateFlow()

  // Global UI feedback (Toast / Snack / Dialog)
  private val _uiNotice = MutableStateFlow<String?>(null)
  val uiNotice: StateFlow<String?> = _uiNotice.asStateFlow()

  // Last completed withdrawal request for WhatsApp modal
  private val _lastWithdrawalRequest = MutableStateFlow<WithdrawalRequestEntity?>(null)
  val lastWithdrawalRequest: StateFlow<WithdrawalRequestEntity?> = _lastWithdrawalRequest.asStateFlow()

  // Reactive Data
  val tournaments: StateFlow<List<TournamentEntity>> = repository.getAllTournaments()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allApplications: StateFlow<List<ApplicationEntity>> = repository.getAllApplications()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allResults: StateFlow<List<MatchResultEntity>> = repository.getAllResults()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allRewards: StateFlow<List<RewardEntity>> = repository.getAllRewards()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allWithdrawals: StateFlow<List<WithdrawalRequestEntity>> = repository.getAllWithdrawals()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allNotifications: StateFlow<List<NotificationEntity>> = repository.getAllNotifications()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allSupportTickets: StateFlow<List<SupportTicketEntity>> = repository.getAllSupportTickets()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allAdminUsers: StateFlow<List<AdminUserEntity>> = repository.getAllAdminUsers()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val auditLogs: StateFlow<List<AuditLogEntity>> = repository.getAllAuditLogs()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val settingsMap: StateFlow<Map<String, String>> = repository.getAllSettings()
    .map { list -> list.associate { it.key to it.value } }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

  init {
    // Attempt auto-login with default user for quick preview
    viewModelScope.launch(Dispatchers.IO) {
      val user = repository.getUserByMobile("9876543210")
      if (user != null) {
        _currentUser.value = user
      }
    }
  }

  fun navigateTo(destination: AppDestination) {
    _currentScreen.value = destination
    _authError.value = null
  }

  fun setUserTab(tab: UserTab) {
    _currentUserTab.value = tab
    _currentScreen.value = AppDestination.USER_MAIN
  }

  fun setAdminTab(tab: AdminTab) {
    _currentAdminTab.value = tab
  }

  fun viewTournamentDetails(tournamentId: String) {
    _selectedTournamentId.value = tournamentId
    _currentScreen.value = AppDestination.TOURNAMENT_DETAILS
  }

  fun viewTournamentResults(tournamentId: String) {
    _selectedTournamentId.value = tournamentId
    _currentScreen.value = AppDestination.RESULTS_VIEW
  }

  fun clearNotice() {
    _uiNotice.value = null
  }

  fun clearLastWithdrawal() {
    _lastWithdrawalRequest.value = null
  }

  // --- USER AUTHENTICATION ---

  fun loginUser(mobile: String, pass: String) {
    viewModelScope.launch(Dispatchers.IO) {
      val user = repository.getUserByMobile(mobile.trim())
      if (user == null) {
        _authError.value = "Account not found for mobile number. Please register."
        return@launch
      }
      if (user.isSuspended) {
        _authError.value = "This account is suspended due to fair play violation. Contact support."
        return@launch
      }
      if (user.password != pass.trim()) {
        _authError.value = "Incorrect password. Please try again."
        return@launch
      }
      _currentUser.value = user
      _authError.value = null
      _currentScreen.value = AppDestination.USER_MAIN
    }
  }

  fun requestOtp(mobile: String) {
    if (mobile.length < 10) {
      _authError.value = "Please enter a valid 10-digit mobile number."
      return
    }
    val code = (100000..999999).random().toString()
    _otpState.value = code
    _uiNotice.value = "Demo Verification OTP sent: $code"
  }

  fun verifyOtpAndLogin(mobile: String, enteredOtp: String) {
    if (_otpState.value != enteredOtp.trim()) {
      _authError.value = "Invalid OTP code. Please check and retry."
      return
    }
    viewModelScope.launch(Dispatchers.IO) {
      val user = repository.getUserByMobile(mobile.trim())
      if (user != null) {
        _currentUser.value = user
        _otpState.value = null
        _authError.value = null
        _currentScreen.value = AppDestination.USER_MAIN
      } else {
        _authError.value = "Mobile verified. Please complete registration details below."
      }
    }
  }

  fun registerUser(
    fullName: String,
    username: String,
    mobile: String,
    email: String,
    ffUid: String,
    ffIgn: String,
    pass: String
  ) {
    if (fullName.isBlank() || username.isBlank() || mobile.isBlank() || ffUid.isBlank() || ffIgn.isBlank() || pass.isBlank()) {
      _authError.value = "Please fill in all mandatory fields."
      return
    }
    if (ffUid.length < 7) {
      _authError.value = "Free Fire UID must be at least 7-10 digits."
      return
    }
    viewModelScope.launch(Dispatchers.IO) {
      val existingMobile = repository.getUserByMobile(mobile.trim())
      if (existingMobile != null) {
        _authError.value = "Mobile number is already registered. Please login."
        return@launch
      }
      val existingUser = repository.getUserByUsername(username.trim())
      if (existingUser != null) {
        _authError.value = "Username already taken. Please choose another."
        return@launch
      }

      val newUser = UserEntity(
        id = "USER-" + (1000..9999).random(),
        fullName = fullName.trim(),
        username = username.trim(),
        mobile = mobile.trim(),
        email = email.trim(),
        ffUid = ffUid.trim(),
        ffIgn = ffIgn.trim(),
        password = pass.trim(),
        isVerified = true
      )
      repository.registerUser(newUser)
      _currentUser.value = newUser
      _authError.value = null
      _currentScreen.value = AppDestination.USER_MAIN
      _uiNotice.value = "Account created successfully! Welcome to FJ_Fire."
    }
  }

  fun logoutUser() {
    _currentUser.value = null
    _currentScreen.value = AppDestination.LOGIN_REGISTER
  }

  fun updateUserProfile(fullName: String, ffIgn: String, ffUid: String) {
    val user = _currentUser.value ?: return
    viewModelScope.launch(Dispatchers.IO) {
      val updated = user.copy(fullName = fullName, ffIgn = ffIgn, ffUid = ffUid)
      repository.updateUser(updated)
      _currentUser.value = updated
      _uiNotice.value = "Profile updated successfully."
    }
  }

  // --- ADMIN AUTHENTICATION ---

  fun loginAdmin(email: String, pass: String) {
    viewModelScope.launch(Dispatchers.IO) {
      val admin = repository.getAdminByEmail(email.trim().lowercase())
      if (admin == null || admin.password != pass.trim()) {
        _adminAuthError.value = "Invalid administrator credentials. Access restricted."
        return@launch
      }
      _currentAdmin.value = admin
      _adminAuthError.value = null
      _currentAdminTab.value = AdminTab.OVERVIEW
      _currentScreen.value = AppDestination.ADMIN_PANEL
      repository.logAudit(admin.id, admin.username, "Admin Logged In", "Admin Portal", "", "Role: ${admin.role}")
    }
  }

  fun logoutAdmin() {
    val admin = _currentAdmin.value
    if (admin != null) {
      viewModelScope.launch(Dispatchers.IO) {
        repository.logAudit(admin.id, admin.username, "Admin Logged Out", "Admin Portal", "", "Session Ended")
      }
    }
    _currentAdmin.value = null
    _currentScreen.value = AppDestination.USER_MAIN
  }

  // --- TOURNAMENT USER ACTIONS ---

  fun applyForTournament(tournamentId: String) {
    val user = _currentUser.value
    if (user == null) {
      _currentScreen.value = AppDestination.LOGIN_REGISTER
      return
    }
    viewModelScope.launch(Dispatchers.IO) {
      val result = repository.applyForTournament(tournamentId, user)
      if (result.isSuccess) {
        val app = result.getOrThrow()
        _uiNotice.value = "Successfully registered! Application ID: ${app.id}"
      } else {
        _uiNotice.value = result.exceptionOrNull()?.message ?: "Application failed."
      }
    }
  }

  // --- REWARD WITHDRAWAL / SUPPORT ---

  fun submitWithdrawalRequest(
    rewardTitle: String,
    requestedValue: String,
    method: String,
    accountDetails: String
  ) {
    val user = _currentUser.value ?: return
    if (requestedValue.isBlank() || accountDetails.isBlank()) {
      _uiNotice.value = "Please complete all required redemption details."
      return
    }
    viewModelScope.launch(Dispatchers.IO) {
      val reqId = "WD-" + (1000..9999).random()
      val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
      val req = WithdrawalRequestEntity(
        id = reqId,
        userId = user.id,
        userName = user.fullName,
        userMobile = user.mobile,
        rewardTitle = rewardTitle,
        requestedValue = requestedValue,
        paymentMethod = method,
        accountDetails = accountDetails,
        dateStr = dateStr,
        status = "PENDING"
      )
      repository.createWithdrawalRequest(req)
      _lastWithdrawalRequest.value = req
      _uiNotice.value = "Redemption request submitted! Request ID: $reqId"
    }
  }

  fun submitSupportTicket(subject: String, message: String) {
    val user = _currentUser.value ?: return
    if (subject.isBlank() || message.isBlank()) {
      _uiNotice.value = "Please enter subject and message."
      return
    }
    viewModelScope.launch(Dispatchers.IO) {
      val ticket = SupportTicketEntity(
        id = "TICK-" + (1000..9999).random(),
        userId = user.id,
        userName = user.fullName,
        subject = subject,
        message = message,
        status = "OPEN"
      )
      repository.createSupportTicket(ticket)
      _uiNotice.value = "Support ticket submitted. Support team will respond shortly."
    }
  }

  fun markNotificationRead(id: String) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.markNotificationRead(id)
    }
  }

  // --- ADMIN ACTIONS ---

  fun adminCreateTournament(
    name: String,
    type: String,
    mapName: String,
    dateStr: String,
    timeStr: String,
    rules: String,
    maxPlayers: Int,
    rewardInfo: String
  ) {
    val admin = _currentAdmin.value ?: return
    viewModelScope.launch(Dispatchers.IO) {
      val tournament = TournamentEntity(
        id = "FF-TOURN-" + (100..999).random(),
        name = name,
        type = type,
        mapName = mapName,
        dateStr = dateStr,
        timeStr = timeStr,
        rules = rules,
        maxPlayers = maxPlayers,
        currentPlayers = 0,
        status = "REGISTRATION_OPEN",
        roomId = "",
        roomPassword = "",
        isRoomReleased = false,
        rewardInfo = rewardInfo
      )
      repository.createTournament(tournament, admin.id, admin.username)
      _uiNotice.value = "Tournament created and published!"
    }
  }

  fun adminUpdateRoomDetails(tournamentId: String, roomId: String, roomPass: String, isReleased: Boolean) {
    val admin = _currentAdmin.value ?: return
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateRoomDetails(tournamentId, roomId, roomPass, isReleased, admin.id, admin.username)
      _uiNotice.value = if (isReleased) "Room details released to all registered players!" else "Room details updated."
    }
  }

  fun adminUpdateTournamentStatus(tournament: TournamentEntity, status: String) {
    val admin = _currentAdmin.value ?: return
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateTournament(tournament.copy(status = status), admin.id, admin.username)
      _uiNotice.value = "Tournament status updated to $status."
    }
  }

  fun adminDeleteTournament(id: String) {
    val admin = _currentAdmin.value ?: return
    viewModelScope.launch(Dispatchers.IO) {
      repository.deleteTournament(id, admin.id, admin.username)
      _uiNotice.value = "Tournament deleted."
    }
  }

  fun adminUpdateApplicationStatus(app: ApplicationEntity, status: String) {
    val admin = _currentAdmin.value ?: return
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateApplicationStatus(app, status, admin.id, admin.username)
      _uiNotice.value = "Application status updated to $status."
    }
  }

  fun adminAddResult(
    tournamentId: String,
    tournamentName: String,
    pos: Int,
    playerName: String,
    ffUid: String,
    ffIgn: String,
    kills: Int,
    points: Int,
    rewardStatus: String
  ) {
    val admin = _currentAdmin.value ?: return
    viewModelScope.launch(Dispatchers.IO) {
      val res = MatchResultEntity(
        id = "RES-" + (1000..9999).random(),
        tournamentId = tournamentId,
        tournamentName = tournamentName,
        position = pos,
        playerName = playerName,
        ffUid = ffUid,
        ffIgn = ffIgn,
        kills = kills,
        points = points,
        rewardStatus = rewardStatus,
        isPublished = true
      )
      repository.addResult(res, admin.id, admin.username)
      _uiNotice.value = "Leaderboard result saved."
    }
  }

  fun adminPublishResults(tournamentId: String, tournamentName: String) {
    val admin = _currentAdmin.value ?: return
    viewModelScope.launch(Dispatchers.IO) {
      repository.publishResults(tournamentId, tournamentName, admin.id, admin.username)
      _uiNotice.value = "Results officially confirmed and published to players!"
    }
  }

  fun adminUpdateWithdrawalStatus(req: WithdrawalRequestEntity, status: String, notes: String) {
    val admin = _currentAdmin.value ?: return
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateWithdrawalStatus(req, status, notes, admin.id, admin.username)
      _uiNotice.value = "Request #${req.id} marked as $status."
    }
  }

  fun adminSendNotification(title: String, message: String, type: String, target: String) {
    val admin = _currentAdmin.value ?: return
    viewModelScope.launch(Dispatchers.IO) {
      val notif = NotificationEntity(
        id = "NOTIF-" + (1000..9999).random(),
        targetUserId = target,
        title = title,
        message = message,
        type = type
      )
      repository.sendNotification(notif, admin.id, admin.username)
      _uiNotice.value = "Notification dispatched successfully."
    }
  }

  fun adminUpdateSetting(key: String, value: String) {
    val admin = _currentAdmin.value ?: return
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateSetting(key, value, admin.id, admin.username)
      _uiNotice.value = "Setting updated: $key"
    }
  }

  fun adminCreateStaff(username: String, email: String, pass: String, role: String, permissions: String) {
    val admin = _currentAdmin.value ?: return
    viewModelScope.launch(Dispatchers.IO) {
      val staff = AdminUserEntity(
        id = "ADMIN-" + (10..99).random(),
        username = username,
        email = email.lowercase(),
        password = pass,
        role = role,
        permissions = permissions
      )
      repository.insertAdminUser(staff, admin.id, admin.username)
      _uiNotice.value = "Staff member $username created with role $role."
    }
  }

  fun adminToggleUserSuspension(user: UserEntity) {
    val admin = _currentAdmin.value ?: return
    viewModelScope.launch(Dispatchers.IO) {
      val updated = user.copy(isSuspended = !user.isSuspended)
      repository.updateUser(updated)
      val action = if (updated.isSuspended) "User Suspended" else "User Unbanned"
      repository.logAudit(admin.id, admin.username, action, user.username, "Suspended: ${user.isSuspended}", "Suspended: ${updated.isSuspended}")
      _uiNotice.value = "User ${user.username} is now ${if (updated.isSuspended) "Suspended" else "Active"}."
    }
  }
}
