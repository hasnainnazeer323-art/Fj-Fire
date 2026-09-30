package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface UserDao {
  @Query("SELECT * FROM users ORDER BY createdAt DESC")
  fun getAllUsers(): Flow<List<UserEntity>>

  @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
  fun getUserById(id: String): Flow<UserEntity?>

  @Query("SELECT * FROM users WHERE mobile = :mobile LIMIT 1")
  suspend fun getUserByMobile(mobile: String): UserEntity?

  @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
  suspend fun getUserByUsername(username: String): UserEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity)

  @Update
  suspend fun updateUser(user: UserEntity)

  @Query("DELETE FROM users WHERE id = :id")
  suspend fun deleteUser(id: String)
}

@Dao
interface TournamentDao {
  @Query("SELECT * FROM tournaments ORDER BY createdAt DESC")
  fun getAllTournaments(): Flow<List<TournamentEntity>>

  @Query("SELECT * FROM tournaments WHERE id = :id LIMIT 1")
  fun getTournamentById(id: String): Flow<TournamentEntity?>

  @Query("SELECT * FROM tournaments WHERE id = :id LIMIT 1")
  suspend fun getTournamentByIdDirect(id: String): TournamentEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTournament(tournament: TournamentEntity)

  @Update
  suspend fun updateTournament(tournament: TournamentEntity)

  @Query("DELETE FROM tournaments WHERE id = :id")
  suspend fun deleteTournament(id: String)
}

@Dao
interface ApplicationDao {
  @Query("SELECT * FROM applications ORDER BY appliedAt DESC")
  fun getAllApplications(): Flow<List<ApplicationEntity>>

  @Query("SELECT * FROM applications WHERE userId = :userId ORDER BY appliedAt DESC")
  fun getApplicationsByUser(userId: String): Flow<List<ApplicationEntity>>

  @Query("SELECT * FROM applications WHERE tournamentId = :tournamentId ORDER BY appliedAt DESC")
  fun getApplicationsByTournament(tournamentId: String): Flow<List<ApplicationEntity>>

  @Query("SELECT * FROM applications WHERE userId = :userId AND tournamentId = :tournamentId LIMIT 1")
  suspend fun getUserApplicationForTournament(userId: String, tournamentId: String): ApplicationEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertApplication(application: ApplicationEntity)

  @Update
  suspend fun updateApplication(application: ApplicationEntity)

  @Query("DELETE FROM applications WHERE id = :id")
  suspend fun deleteApplication(id: String)
}

@Dao
interface MatchResultDao {
  @Query("SELECT * FROM match_results ORDER BY position ASC")
  fun getAllResults(): Flow<List<MatchResultEntity>>

  @Query("SELECT * FROM match_results WHERE tournamentId = :tournamentId ORDER BY position ASC")
  fun getResultsByTournament(tournamentId: String): Flow<List<MatchResultEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertResult(result: MatchResultEntity)

  @Query("DELETE FROM match_results WHERE id = :id")
  suspend fun deleteResult(id: String)
}

@Dao
interface RewardDao {
  @Query("SELECT * FROM rewards ORDER BY dateStr DESC")
  fun getAllRewards(): Flow<List<RewardEntity>>

  @Query("SELECT * FROM rewards WHERE userId = :userId ORDER BY dateStr DESC")
  fun getRewardsByUser(userId: String): Flow<List<RewardEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReward(reward: RewardEntity)

  @Update
  suspend fun updateReward(reward: RewardEntity)
}

@Dao
interface WithdrawalRequestDao {
  @Query("SELECT * FROM withdrawal_requests ORDER BY timestamp DESC")
  fun getAllRequests(): Flow<List<WithdrawalRequestEntity>>

  @Query("SELECT * FROM withdrawal_requests WHERE userId = :userId ORDER BY timestamp DESC")
  fun getRequestsByUser(userId: String): Flow<List<WithdrawalRequestEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRequest(request: WithdrawalRequestEntity)

  @Update
  suspend fun updateRequest(request: WithdrawalRequestEntity)
}

@Dao
interface NotificationDao {
  @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
  fun getAllNotifications(): Flow<List<NotificationEntity>>

  @Query("SELECT * FROM notifications WHERE targetUserId = 'ALL' OR targetUserId = :userId ORDER BY createdAt DESC")
  fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotification(notification: NotificationEntity)

  @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
  suspend fun markAsRead(id: String)
}

@Dao
interface SupportTicketDao {
  @Query("SELECT * FROM support_tickets ORDER BY createdAt DESC")
  fun getAllTickets(): Flow<List<SupportTicketEntity>>

  @Query("SELECT * FROM support_tickets WHERE userId = :userId ORDER BY createdAt DESC")
  fun getTicketsByUser(userId: String): Flow<List<SupportTicketEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTicket(ticket: SupportTicketEntity)

  @Update
  suspend fun updateTicket(ticket: SupportTicketEntity)
}

@Dao
interface AdminUserDao {
  @Query("SELECT * FROM admin_users ORDER BY createdAt DESC")
  fun getAllAdminUsers(): Flow<List<AdminUserEntity>>

  @Query("SELECT * FROM admin_users WHERE email = :email LIMIT 1")
  suspend fun getAdminByEmail(email: String): AdminUserEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAdminUser(admin: AdminUserEntity)

  @Update
  suspend fun updateAdminUser(admin: AdminUserEntity)

  @Query("DELETE FROM admin_users WHERE id = :id")
  suspend fun deleteAdminUser(id: String)
}

@Dao
interface AuditLogDao {
  @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
  fun getAllLogs(): Flow<List<AuditLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLog(log: AuditLogEntity)
}

@Dao
interface AppSettingDao {
  @Query("SELECT * FROM app_settings")
  fun getAllSettings(): Flow<List<AppSettingEntity>>

  @Query("SELECT value FROM app_settings WHERE `key` = :key LIMIT 1")
  suspend fun getSettingValue(key: String): String?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSetting(setting: AppSettingEntity)
}
