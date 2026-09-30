package com.example.data.repository

import com.example.data.AppDatabase
import com.example.data.entity.AdminUserEntity
import com.example.data.entity.AppSettingEntity
import com.example.data.entity.ApplicationEntity
import com.example.data.entity.AuditLogEntity
import com.example.data.entity.MatchResultEntity
import com.example.data.entity.NotificationEntity
import com.example.data.entity.RewardEntity
import com.example.data.entity.SupportTicketEntity
import com.example.data.entity.TournamentEntity
import com.example.data.entity.UserEntity
import com.example.data.entity.WithdrawalRequestEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class FJFireRepository(private val database: AppDatabase) {
  private val userDao = database.userDao()
  private val tournamentDao = database.tournamentDao()
  private val applicationDao = database.applicationDao()
  private val matchResultDao = database.matchResultDao()
  private val rewardDao = database.rewardDao()
  private val withdrawalDao = database.withdrawalRequestDao()
  private val notificationDao = database.notificationDao()
  private val supportDao = database.supportTicketDao()
  private val adminDao = database.adminUserDao()
  private val auditDao = database.auditLogDao()
  private val settingDao = database.appSettingDao()

  // User Flows
  fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()
  fun getUserById(id: String): Flow<UserEntity?> = userDao.getUserById(id)
  suspend fun getUserByMobile(mobile: String): UserEntity? = userDao.getUserByMobile(mobile)
  suspend fun getUserByUsername(username: String): UserEntity? = userDao.getUserByUsername(username)
  suspend fun registerUser(user: UserEntity): Boolean {
    userDao.insertUser(user)
    return true
  }
  suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)
  suspend fun deleteUser(id: String) = userDao.deleteUser(id)

  // Tournament Flows
  fun getAllTournaments(): Flow<List<TournamentEntity>> = tournamentDao.getAllTournaments()
  fun getTournamentById(id: String): Flow<TournamentEntity?> = tournamentDao.getTournamentById(id)

  suspend fun createTournament(tournament: TournamentEntity, adminId: String, adminName: String) {
    tournamentDao.insertTournament(tournament)
    logAudit(adminId, adminName, "Tournament Created", tournament.name, "", "Status: ${tournament.status}")
  }

  suspend fun updateTournament(tournament: TournamentEntity, adminId: String, adminName: String) {
    tournamentDao.updateTournament(tournament)
    logAudit(adminId, adminName, "Tournament Updated", tournament.name, "", "Type: ${tournament.type}, Max: ${tournament.maxPlayers}")
  }

  suspend fun updateRoomDetails(tournamentId: String, roomId: String, roomPass: String, isReleased: Boolean, adminId: String, adminName: String) {
    val existing = tournamentDao.getTournamentByIdDirect(tournamentId) ?: return
    val updated = existing.copy(roomId = roomId, roomPassword = roomPass, isRoomReleased = isReleased)
    tournamentDao.updateTournament(updated)
    val action = if (isReleased) "Room Credentials Released" else "Room Credentials Updated"
    logAudit(adminId, adminName, action, existing.name, "Released: ${existing.isRoomReleased}", "Released: $isReleased")

    if (isReleased) {
      notificationDao.insertNotification(
        NotificationEntity(
          id = "NOTIF-ROOM-" + UUID.randomUUID().toString().take(6),
          targetUserId = "ALL",
          title = "Room Details Released: ${existing.name}",
          message = "Room ID and Password are now available in your 'My Applied' tab. Join promptly!",
          type = "ROOM_RELEASED"
        )
      )
    }
  }

  suspend fun deleteTournament(id: String, adminId: String, adminName: String) {
    val existing = tournamentDao.getTournamentByIdDirect(id)
    tournamentDao.deleteTournament(id)
    logAudit(adminId, adminName, "Tournament Deleted", existing?.name ?: id, "", "Deleted")
  }

  // Application Flows
  fun getAllApplications(): Flow<List<ApplicationEntity>> = applicationDao.getAllApplications()
  fun getUserApplications(userId: String): Flow<List<ApplicationEntity>> = applicationDao.getApplicationsByUser(userId)
  fun getTournamentApplications(tournamentId: String): Flow<List<ApplicationEntity>> = applicationDao.getApplicationsByTournament(tournamentId)

  suspend fun applyForTournament(tournamentId: String, user: UserEntity): Result<ApplicationEntity> {
    val tournament = tournamentDao.getTournamentByIdDirect(tournamentId)
      ?: return Result.failure(Exception("Tournament not found"))

    if (tournament.currentPlayers >= tournament.maxPlayers) {
      return Result.failure(Exception("Tournament slots are completely full!"))
    }

    val existingApp = applicationDao.getUserApplicationForTournament(user.id, tournamentId)
    if (existingApp != null) {
      return Result.failure(Exception("You have already applied for this tournament! Application ID: ${existingApp.id}"))
    }

    val appId = "FJ-APP-" + (1000..9999).random()
    val app = ApplicationEntity(
      id = appId,
      tournamentId = tournament.id,
      tournamentName = tournament.name,
      tournamentType = tournament.type,
      userId = user.id,
      playerName = user.fullName,
      ffUid = user.ffUid,
      ffIgn = user.ffIgn,
      mobile = user.mobile,
      status = "CONFIRMED"
    )

    applicationDao.insertApplication(app)
    tournamentDao.updateTournament(tournament.copy(currentPlayers = tournament.currentPlayers + 1))

    notificationDao.insertNotification(
      NotificationEntity(
        id = "NOTIF-APP-" + UUID.randomUUID().toString().take(6),
        targetUserId = user.id,
        title = "Application Confirmed",
        message = "Successfully applied for ${tournament.name}. Application ID: $appId.",
        type = "TOURNAMENT"
      )
    )

    return Result.success(app)
  }

  suspend fun updateApplicationStatus(app: ApplicationEntity, newStatus: String, adminId: String, adminName: String) {
    applicationDao.updateApplication(app.copy(status = newStatus))
    logAudit(adminId, adminName, "Application Status Updated", "${app.id} (${app.playerName})", app.status, newStatus)
  }

  // Match Results
  fun getAllResults(): Flow<List<MatchResultEntity>> = matchResultDao.getAllResults()
  fun getTournamentResults(tournamentId: String): Flow<List<MatchResultEntity>> = matchResultDao.getResultsByTournament(tournamentId)

  suspend fun addResult(result: MatchResultEntity, adminId: String, adminName: String) {
    matchResultDao.insertResult(result)
    logAudit(adminId, adminName, "Result Saved", "${result.tournamentName} - Pos ${result.position}", "", "${result.playerName} (${result.kills} kills, ${result.points} pts)")
  }

  suspend fun publishResults(tournamentId: String, tournamentName: String, adminId: String, adminName: String) {
    val tour = tournamentDao.getTournamentByIdDirect(tournamentId)
    if (tour != null) {
      tournamentDao.updateTournament(tour.copy(status = "COMPLETED"))
    }
    logAudit(adminId, adminName, "Results Confirmed & Published", tournamentName, "Unpublished", "Published")

    notificationDao.insertNotification(
      NotificationEntity(
        id = "NOTIF-RES-" + UUID.randomUUID().toString().take(6),
        targetUserId = "ALL",
        title = "Official Results Published: $tournamentName",
        message = "Verified results and leaderboards have been published by the administration. Check the Results tab!",
        type = "RESULT_PUBLISHED"
      )
    )
  }

  // Rewards
  fun getAllRewards(): Flow<List<RewardEntity>> = rewardDao.getAllRewards()
  fun getUserRewards(userId: String): Flow<List<RewardEntity>> = rewardDao.getRewardsByUser(userId)

  suspend fun createReward(reward: RewardEntity, adminId: String, adminName: String) {
    rewardDao.insertReward(reward)
    logAudit(adminId, adminName, "Reward Created", "${reward.id} - ${reward.title}", "", reward.value)
    notificationDao.insertNotification(
      NotificationEntity(
        id = "NOTIF-RWD-" + UUID.randomUUID().toString().take(6),
        targetUserId = reward.userId,
        title = "New Reward Earned!",
        message = "You received ${reward.value} for '${reward.tournamentName}'. Check your Rewards tab.",
        type = "REWARD_UPDATE"
      )
    )
  }

  suspend fun updateRewardStatus(reward: RewardEntity, status: String, adminId: String, adminName: String) {
    rewardDao.updateReward(reward.copy(status = status))
    logAudit(adminId, adminName, "Reward Status Changed", reward.id, reward.status, status)
  }

  // Withdrawals
  fun getAllWithdrawals(): Flow<List<WithdrawalRequestEntity>> = withdrawalDao.getAllRequests()
  fun getUserWithdrawals(userId: String): Flow<List<WithdrawalRequestEntity>> = withdrawalDao.getRequestsByUser(userId)

  suspend fun createWithdrawalRequest(req: WithdrawalRequestEntity) {
    withdrawalDao.insertRequest(req)
  }

  suspend fun updateWithdrawalStatus(req: WithdrawalRequestEntity, newStatus: String, notes: String, adminId: String, adminName: String) {
    withdrawalDao.updateRequest(req.copy(status = newStatus, adminNotes = notes))
    logAudit(adminId, adminName, "Withdrawal Request $newStatus", req.id, req.status, newStatus)

    notificationDao.insertNotification(
      NotificationEntity(
        id = "NOTIF-WD-" + UUID.randomUUID().toString().take(6),
        targetUserId = req.userId,
        title = "Withdrawal Request Status: $newStatus",
        message = "Your redemption request #${req.id} is now $newStatus. ${if (notes.isNotEmpty()) "Note: $notes" else ""}",
        type = "REWARD_UPDATE"
      )
    )
  }

  // Notifications
  fun getAllNotifications(): Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()
  fun getUserNotifications(userId: String): Flow<List<NotificationEntity>> = notificationDao.getNotificationsForUser(userId)

  suspend fun sendNotification(notif: NotificationEntity, adminId: String, adminName: String) {
    notificationDao.insertNotification(notif)
    logAudit(adminId, adminName, "Notification Dispatched", notif.title, "", "Target: ${notif.targetUserId}")
  }

  suspend fun markNotificationRead(id: String) {
    notificationDao.markAsRead(id)
  }

  // Support
  fun getAllSupportTickets(): Flow<List<SupportTicketEntity>> = supportDao.getAllTickets()
  fun getUserSupportTickets(userId: String): Flow<List<SupportTicketEntity>> = supportDao.getTicketsByUser(userId)
  suspend fun createSupportTicket(ticket: SupportTicketEntity) = supportDao.insertTicket(ticket)
  suspend fun updateSupportTicket(ticket: SupportTicketEntity, adminId: String, adminName: String) {
    supportDao.updateTicket(ticket)
    logAudit(adminId, adminName, "Support Ticket Updated", ticket.id, "", ticket.status)
  }

  // Admin Users & Staff
  fun getAllAdminUsers(): Flow<List<AdminUserEntity>> = adminDao.getAllAdminUsers()
  suspend fun getAdminByEmail(email: String): AdminUserEntity? = adminDao.getAdminByEmail(email)
  suspend fun insertAdminUser(admin: AdminUserEntity, creatorId: String, creatorName: String) {
    adminDao.insertAdminUser(admin)
    logAudit(creatorId, creatorName, "Admin User Added", admin.email, "", "Role: ${admin.role}")
  }
  suspend fun updateAdminUser(admin: AdminUserEntity, modifierId: String, modifierName: String) {
    adminDao.updateAdminUser(admin)
    logAudit(modifierId, modifierName, "Admin User Modified", admin.email, "", "Role: ${admin.role}")
  }
  suspend fun deleteAdminUser(id: String, modifierId: String, modifierName: String) {
    adminDao.deleteAdminUser(id)
    logAudit(modifierId, modifierName, "Admin User Removed", id, "", "Deleted")
  }

  // Audit Logs
  fun getAllAuditLogs(): Flow<List<AuditLogEntity>> = auditDao.getAllLogs()

  suspend fun logAudit(adminId: String, adminName: String, action: String, target: String, oldValue: String, newValue: String) {
    auditDao.insertLog(
      AuditLogEntity(
        id = "LOG-" + UUID.randomUUID().toString().take(8),
        adminId = adminId,
        adminName = adminName,
        action = action,
        target = target,
        oldValue = oldValue,
        newValue = newValue
      )
    )
  }

  // Settings
  fun getAllSettings(): Flow<List<AppSettingEntity>> = settingDao.getAllSettings()
  suspend fun getSettingValue(key: String): String? = settingDao.getSettingValue(key)
  suspend fun updateSetting(key: String, value: String, adminId: String, adminName: String) {
    val old = settingDao.getSettingValue(key) ?: ""
    settingDao.insertSetting(AppSettingEntity(key, value))
    logAudit(adminId, adminName, "Setting Updated: $key", key, old, value)
  }
}
