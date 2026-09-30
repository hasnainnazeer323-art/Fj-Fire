package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey val id: String,
  val fullName: String,
  val username: String,
  val mobile: String,
  val email: String = "",
  val ffUid: String,
  val ffIgn: String,
  val password: String,
  val isVerified: Boolean = true,
  val isSuspended: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tournaments")
data class TournamentEntity(
  @PrimaryKey val id: String,
  val name: String,
  val type: String, // "Solo", "Squad", "Custom"
  val mapName: String, // "Bermuda", "Purgatory", "Kalahari", "Alpine", "Nexterra"
  val dateStr: String,
  val timeStr: String,
  val rules: String,
  val maxPlayers: Int,
  val currentPlayers: Int,
  val status: String, // "REGISTRATION_OPEN", "REGISTRATION_CLOSED", "LIVE", "COMPLETED", "CANCELLED"
  val roomId: String = "",
  val roomPassword: String = "",
  val isRoomReleased: Boolean = false,
  val rewardInfo: String,
  val bannerUrl: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "applications")
data class ApplicationEntity(
  @PrimaryKey val id: String,
  val tournamentId: String,
  val tournamentName: String,
  val tournamentType: String,
  val userId: String,
  val playerName: String,
  val ffUid: String,
  val ffIgn: String,
  val mobile: String,
  val appliedAt: Long = System.currentTimeMillis(),
  val status: String = "CONFIRMED" // "CONFIRMED", "REJECTED", "CANCELLED"
)

@Entity(tableName = "match_results")
data class MatchResultEntity(
  @PrimaryKey val id: String,
  val tournamentId: String,
  val tournamentName: String,
  val position: Int,
  val playerName: String,
  val ffUid: String,
  val ffIgn: String,
  val kills: Int,
  val points: Int,
  val rewardStatus: String = "Awarded",
  val isPublished: Boolean = true,
  val publishedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "rewards")
data class RewardEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val tournamentId: String,
  val tournamentName: String,
  val title: String,
  val value: String,
  val points: Int = 100,
  val dateStr: String,
  val status: String = "AVAILABLE" // "AVAILABLE", "PENDING", "COMPLETED", "REJECTED"
)

@Entity(tableName = "withdrawal_requests")
data class WithdrawalRequestEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val userName: String,
  val userMobile: String,
  val rewardTitle: String,
  val requestedValue: String,
  val paymentMethod: String,
  val accountDetails: String,
  val dateStr: String,
  val status: String = "PENDING", // "PENDING", "PROCESSING", "COMPLETED", "REJECTED"
  val adminNotes: String = "",
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
  @PrimaryKey val id: String,
  val targetUserId: String = "ALL", // "ALL" or userId or tournamentId
  val title: String,
  val message: String,
  val type: String = "SYSTEM", // "TOURNAMENT", "MATCH", "ROOM_RELEASED", "RESULT_PUBLISHED", "REWARD_UPDATE", "SYSTEM"
  val createdAt: Long = System.currentTimeMillis(),
  val isRead: Boolean = false
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val userName: String,
  val subject: String,
  val message: String,
  val status: String = "OPEN", // "OPEN", "ANSWERED", "CLOSED"
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_users")
data class AdminUserEntity(
  @PrimaryKey val id: String,
  val username: String,
  val email: String,
  val password: String,
  val role: String, // "SUPER_ADMIN", "ADMIN", "STAFF_DISTRIBUTOR"
  val permissions: String, // Comma separated permissions
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
  @PrimaryKey val id: String,
  val adminId: String,
  val adminName: String,
  val action: String,
  val target: String,
  val oldValue: String = "",
  val newValue: String = "",
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingEntity(
  @PrimaryKey val key: String,
  val value: String
)
