package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AdminUserDao
import com.example.data.dao.AppSettingDao
import com.example.data.dao.ApplicationDao
import com.example.data.dao.AuditLogDao
import com.example.data.dao.MatchResultDao
import com.example.data.dao.NotificationDao
import com.example.data.dao.RewardDao
import com.example.data.dao.SupportTicketDao
import com.example.data.dao.TournamentDao
import com.example.data.dao.UserDao
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
import com.example.data.dao.WithdrawalRequestDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    UserEntity::class,
    TournamentEntity::class,
    ApplicationEntity::class,
    MatchResultEntity::class,
    RewardEntity::class,
    WithdrawalRequestEntity::class,
    NotificationEntity::class,
    SupportTicketEntity::class,
    AdminUserEntity::class,
    AuditLogEntity::class,
    AppSettingEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
  abstract fun tournamentDao(): TournamentDao
  abstract fun applicationDao(): ApplicationDao
  abstract fun matchResultDao(): MatchResultDao
  abstract fun rewardDao(): RewardDao
  abstract fun withdrawalRequestDao(): WithdrawalRequestDao
  abstract fun notificationDao(): NotificationDao
  abstract fun supportTicketDao(): SupportTicketDao
  abstract fun adminUserDao(): AdminUserDao
  abstract fun auditLogDao(): AuditLogDao
  abstract fun appSettingDao(): AppSettingDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "fj_fire_database.db"
        )
          .addCallback(AppDatabaseCallback(scope))
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class AppDatabaseCallback(
      private val scope: CoroutineScope
    ) : Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database)
          }
        }
      }
    }

    suspend fun populateInitialData(database: AppDatabase) {
      val tournamentDao = database.tournamentDao()
      val userDao = database.userDao()
      val adminDao = database.adminUserDao()
      val settingDao = database.appSettingDao()
      val appDao = database.applicationDao()
      val rewardDao = database.rewardDao()
      val notifDao = database.notificationDao()
      val resultDao = database.matchResultDao()

      // Default Admin Settings
      settingDao.insertSetting(AppSettingEntity("whatsapp_support_number", "+1234567890"))
      settingDao.insertSetting(AppSettingEntity("whatsapp_display_name", "FJ_Fire Official Support"))
      settingDao.insertSetting(AppSettingEntity("support_message", "Hello FJ_Fire Support, I need help regarding my tournament/reward request."))
      settingDao.insertSetting(AppSettingEntity("rules_summary", "100% Free Entry • Anti-Cheat Active • Mobile Only • No Emulators"))

      // Default Admins (Separate login credentials)
      adminDao.insertAdminUser(
        AdminUserEntity(
          id = "ADMIN-01",
          username = "SuperAdmin",
          email = "superadmin@fjfire.com",
          password = "admin123",
          role = "SUPER_ADMIN",
          permissions = "VIEW_USERS,VIEW_APPLICATIONS,MANAGE_TOURNAMENTS,VIEW_RESULTS,MANAGE_RESULTS,MANAGE_REWARDS,MANAGE_SUPPORT,MANAGE_SETTINGS,MANAGE_STAFF"
        )
      )
      adminDao.insertAdminUser(
        AdminUserEntity(
          id = "ADMIN-02",
          username = "EsportsManager",
          email = "moderator@fjfire.com",
          password = "admin123",
          role = "ADMIN",
          permissions = "VIEW_USERS,VIEW_APPLICATIONS,MANAGE_TOURNAMENTS,VIEW_RESULTS,MANAGE_RESULTS,MANAGE_REWARDS,MANAGE_SUPPORT"
        )
      )
      adminDao.insertAdminUser(
        AdminUserEntity(
          id = "ADMIN-03",
          username = "RoomCoordinator",
          email = "staff@fjfire.com",
          password = "staff123",
          role = "STAFF_DISTRIBUTOR",
          permissions = "VIEW_USERS,VIEW_APPLICATIONS,MANAGE_TOURNAMENTS,VIEW_RESULTS"
        )
      )

      // Default Normal User
      val defaultUser = UserEntity(
        id = "USER-1001",
        fullName = "Zane Phoenix",
        username = "FlamePhoenix",
        mobile = "9876543210",
        email = "phoenix@freefire.com",
        ffUid = "8492049102",
        ffIgn = "FJ_PhoenixX",
        password = "pass123",
        isVerified = true
      )
      userDao.insertUser(defaultUser)

      // Initial Free Fire Tournaments
      val t1 = TournamentEntity(
        id = "FF-TOURN-101",
        name = "Free Fire Bermuda Grand Scrims",
        type = "Solo",
        mapName = "Bermuda",
        dateStr = "Today",
        timeStr = "7:00 PM",
        rules = "1. Mobile devices only. Emulators strictly prohibited.\n2. Fair play strictly enforced; third-party config files or cheats lead to permanent ban.\n3. 100% Free Entry tournament.\n4. Join room minimum 5 minutes before scheduled match start time.\n5. Point system: Rank 1: 12 pts, Rank 2: 9 pts, Rank 3: 8 pts, Each kill: 1 pt.",
        maxPlayers = 100,
        currentPlayers = 68,
        status = "REGISTRATION_OPEN",
        roomId = "982341",
        roomPassword = "FJ77",
        isRoomReleased = false,
        rewardInfo = "Champion Trophy, Custom Esports Badge & 500 Reward Points (Free promotional contest where permitted)"
      )
      val t2 = TournamentEntity(
        id = "FF-TOURN-102",
        name = "Fire Battle Squad Clash Series",
        type = "Squad",
        mapName = "Purgatory",
        dateStr = "Tomorrow",
        timeStr = "8:30 PM",
        rules = "1. 4-Player squad match.\n2. Squad leader applies and registers full team.\n3. Free entry, mobile only.\n4. Microphone coordination encouraged.\n5. Points distributed based on team total kills + survival rank.",
        maxPlayers = 48,
        currentPlayers = 44,
        status = "REGISTRATION_OPEN",
        roomId = "551920",
        roomPassword = "SQUAD9",
        isRoomReleased = false,
        rewardInfo = "Top Squad Gold Trophy & 1,000 Squad Reward Points"
      )
      val t3 = TournamentEntity(
        id = "FF-TOURN-103",
        name = "Alpine Survival Custom Scrims",
        type = "Custom",
        mapName = "Alpine",
        dateStr = "Yesterday",
        timeStr = "6:00 PM",
        rules = "Custom scrims with competitive esports circle speeds and standard competitive loot pool. Free entry.",
        maxPlayers = 50,
        currentPlayers = 50,
        status = "COMPLETED",
        roomId = "441029",
        roomPassword = "ALPFJ",
        isRoomReleased = true,
        rewardInfo = "Exclusive FJ_Fire Winner Banner & 300 Reward Points"
      )
      val t4 = TournamentEntity(
        id = "FF-TOURN-104",
        name = "Kalahari Desert Rush Solo",
        type = "Solo",
        mapName = "Kalahari",
        dateStr = "Tomorrow",
        timeStr = "9:15 PM",
        rules = "High intensity solo battle on Kalahari map. Standard Free Fire competitive settings. 100% Free Entry.",
        maxPlayers = 100,
        currentPlayers = 35,
        status = "REGISTRATION_OPEN",
        roomId = "773120",
        roomPassword = "DESERT",
        isRoomReleased = false,
        rewardInfo = "Winner Recognition, Profile Star & 400 Reward Points"
      )

      tournamentDao.insertTournament(t1)
      tournamentDao.insertTournament(t2)
      tournamentDao.insertTournament(t3)
      tournamentDao.insertTournament(t4)

      // User registered in T1
      appDao.insertApplication(
        ApplicationEntity(
          id = "FJ-APP-8821",
          tournamentId = t1.id,
          tournamentName = t1.name,
          tournamentType = t1.type,
          userId = defaultUser.id,
          playerName = defaultUser.fullName,
          ffUid = defaultUser.ffUid,
          ffIgn = defaultUser.ffIgn,
          mobile = defaultUser.mobile,
          status = "CONFIRMED"
        )
      )

      // User registered in completed T3
      appDao.insertApplication(
        ApplicationEntity(
          id = "FJ-APP-7712",
          tournamentId = t3.id,
          tournamentName = t3.name,
          tournamentType = t3.type,
          userId = defaultUser.id,
          playerName = defaultUser.fullName,
          ffUid = defaultUser.ffUid,
          ffIgn = defaultUser.ffIgn,
          mobile = defaultUser.mobile,
          status = "CONFIRMED"
        )
      )

      // Published results for completed T3
      resultDao.insertResult(
        MatchResultEntity(
          id = "RES-101",
          tournamentId = t3.id,
          tournamentName = t3.name,
          position = 1,
          playerName = "Raven_King",
          ffUid = "7192837190",
          ffIgn = "FJ_Raven",
          kills = 9,
          points = 21,
          rewardStatus = "Completed",
          isPublished = true
        )
      )
      resultDao.insertResult(
        MatchResultEntity(
          id = "RES-102",
          tournamentId = t3.id,
          tournamentName = t3.name,
          position = 2,
          playerName = defaultUser.fullName,
          ffUid = defaultUser.ffUid,
          ffIgn = defaultUser.ffIgn,
          kills = 7,
          points = 16,
          rewardStatus = "Awarded",
          isPublished = true
        )
      )
      resultDao.insertResult(
        MatchResultEntity(
          id = "RES-103",
          tournamentId = t3.id,
          tournamentName = t3.name,
          position = 3,
          playerName = "BlazeHunter",
          ffUid = "9928172910",
          ffIgn = "Blaze_FF",
          kills = 4,
          points = 12,
          rewardStatus = "Awarded",
          isPublished = true
        )
      )

      // Initial Rewards
      rewardDao.insertReward(
        RewardEntity(
          id = "RWD-8910",
          userId = defaultUser.id,
          tournamentId = t3.id,
          tournamentName = t3.name,
          title = "2nd Place Runner-Up Reward",
          value = "200 Reward Points",
          points = 200,
          dateStr = "Yesterday",
          status = "AVAILABLE"
        )
      )

      // Initial Notifications
      notifDao.insertNotification(
        NotificationEntity(
          id = "NOTIF-1",
          targetUserId = defaultUser.id,
          title = "Tournament Registration Confirmed",
          message = "You have successfully registered for 'Free Fire Bermuda Grand Scrims'. Match starts at 7:00 PM.",
          type = "TOURNAMENT"
        )
      )
      notifDao.insertNotification(
        NotificationEntity(
          id = "NOTIF-2",
          targetUserId = defaultUser.id,
          title = "Results Published!",
          message = "Results for 'Alpine Survival Custom Scrims' have been verified and published! You placed #2.",
          type = "RESULT_PUBLISHED"
        )
      )
      notifDao.insertNotification(
        NotificationEntity(
          id = "NOTIF-3",
          targetUserId = "ALL",
          title = "Welcome to FJ_Fire",
          message = "Exclusive Free Fire tournaments platform. Play, Compete, Win! Check out today's scrims.",
          type = "SYSTEM"
        )
      )
    }
  }
}
