package com.wordflip.learning.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class GamificationDao(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "gamification.db"
        private const val DATABASE_VERSION = 1
        
        // User Progress Table
        private const val TABLE_USER_PROGRESS = "user_progress"
        private const val COLUMN_ID = "id"
        private const val COLUMN_LEVEL = "level"
        private const val COLUMN_XP = "xp"
        private const val COLUMN_COINS = "coins"
        private const val COLUMN_TOTAL_WORDS = "total_words"
        private const val COLUMN_STREAK = "streak"
        private const val COLUMN_LAST_STUDY_DATE = "last_study_date"
        private const val COLUMN_DAILY_WORDS_LEARNED = "daily_words_learned"
        private const val COLUMN_DAILY_CORRECT_ANSWERS = "daily_correct_answers"
        private const val COLUMN_DAILY_TARGET = "daily_target"
        private const val COLUMN_LAST_CHALLENGE_DATE = "last_challenge_date"
        private const val COLUMN_UNLOCKED_BADGES = "unlocked_badges"
        private const val COLUMN_UNLOCKED_THEMES = "unlocked_themes"
        private const val COLUMN_CURRENT_THEME = "current_theme"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserProgressTable = """
            CREATE TABLE $TABLE_USER_PROGRESS (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_LEVEL INTEGER DEFAULT 1,
                $COLUMN_XP INTEGER DEFAULT 0,
                $COLUMN_COINS INTEGER DEFAULT 100,
                $COLUMN_TOTAL_WORDS INTEGER DEFAULT 0,
                $COLUMN_STREAK INTEGER DEFAULT 0,
                $COLUMN_LAST_STUDY_DATE INTEGER DEFAULT 0,
                $COLUMN_DAILY_WORDS_LEARNED INTEGER DEFAULT 0,
                $COLUMN_DAILY_CORRECT_ANSWERS INTEGER DEFAULT 0,
                $COLUMN_DAILY_TARGET INTEGER DEFAULT 10,
                $COLUMN_LAST_CHALLENGE_DATE INTEGER DEFAULT 0,
                $COLUMN_UNLOCKED_BADGES TEXT DEFAULT '',
                $COLUMN_UNLOCKED_THEMES TEXT DEFAULT 'default',
                $COLUMN_CURRENT_THEME TEXT DEFAULT 'default'
            )
        """.trimIndent()
        
        db.execSQL(createUserProgressTable)
        
        // Initial user progress
        val initialValues = ContentValues().apply {
            put(COLUMN_ID, 1)
            put(COLUMN_COINS, 100) // Başlangıç coin'i
            put(COLUMN_UNLOCKED_THEMES, "default")
        }
        db.insert(TABLE_USER_PROGRESS, null, initialValues)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER_PROGRESS")
        onCreate(db)
    }

    // 🎮 Gamification Methods
    fun getUserProgress(): UserProgress {
        val db = readableDatabase
        val cursor = db.query(TABLE_USER_PROGRESS, null, "$COLUMN_ID = ?", arrayOf("1"), null, null, null)
        
        return if (cursor.moveToFirst()) {
            UserProgress(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                level = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LEVEL)),
                xp = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_XP)),
                coins = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COINS)),
                totalWords = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_WORDS)),
                streak = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STREAK)),
                lastStudyDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LAST_STUDY_DATE)),
                dailyWordsLearned = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DAILY_WORDS_LEARNED)),
                dailyCorrectAnswers = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DAILY_CORRECT_ANSWERS)),
                dailyTarget = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DAILY_TARGET)),
                lastChallengeDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LAST_CHALLENGE_DATE)),
                unlockedBadges = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNLOCKED_BADGES)) ?: "",
                unlockedThemes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNLOCKED_THEMES)) ?: "default",
                currentTheme = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_THEME)) ?: "default"
            )
        } else {
            UserProgress()
        }.also { cursor.close() }
    }

    fun addXP(xp: Int): Boolean {
        val currentProgress = getUserProgress()
        val newXP = currentProgress.xp + xp
        val newLevel = calculateLevel(newXP)
        
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_XP, newXP)
            put(COLUMN_LEVEL, newLevel)
        }
        
        return db.update(TABLE_USER_PROGRESS, values, "$COLUMN_ID = ?", arrayOf("1")) > 0
    }

    fun addCoins(coins: Int): Boolean {
        val currentProgress = getUserProgress()
        val newCoins = currentProgress.coins + coins
        
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_COINS, newCoins)
        }
        
        return db.update(TABLE_USER_PROGRESS, values, "$COLUMN_ID = ?", arrayOf("1")) > 0
    }

    fun spendCoins(coins: Int): Boolean {
        val currentProgress = getUserProgress()
        if (currentProgress.coins < coins) return false
        
        val newCoins = currentProgress.coins - coins
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_COINS, newCoins)
        }
        
        return db.update(TABLE_USER_PROGRESS, values, "$COLUMN_ID = ?", arrayOf("1")) > 0
    }

    private fun calculateLevel(xp: Int): Int {
        return when {
            xp < 100 -> 1
            xp < 300 -> 2
            xp < 600 -> 3
            xp < 1000 -> 4
            xp < 1500 -> 5
            xp < 2200 -> 6
            xp < 3000 -> 7
            xp < 4000 -> 8
            xp < 5500 -> 9
            else -> 10
        }
    }

    // 📅 Daily Challenge Methods
    fun updateDailyProgress(wordsLearned: Int = 0, correctAnswers: Int = 0) {
        val today = getTodayString()
        val currentProgress = getUserProgress()
        val lastChallengeDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(currentProgress.lastChallengeDate))
        
        val db = writableDatabase
        val values = ContentValues()
        
        if (today != lastChallengeDate) {
            // Yeni gün, sayaçları sıfırla
            values.put(COLUMN_DAILY_WORDS_LEARNED, wordsLearned)
            values.put(COLUMN_DAILY_CORRECT_ANSWERS, correctAnswers)
            values.put(COLUMN_LAST_CHALLENGE_DATE, System.currentTimeMillis())
        } else {
            // Aynı gün, sayaçları artır
            values.put(COLUMN_DAILY_WORDS_LEARNED, currentProgress.dailyWordsLearned + wordsLearned)
            values.put(COLUMN_DAILY_CORRECT_ANSWERS, currentProgress.dailyCorrectAnswers + correctAnswers)
        }
        
        db.update(TABLE_USER_PROGRESS, values, "$COLUMN_ID = ?", arrayOf("1"))
    }

    fun getTodaysChallenges(): List<DailyChallenge> {
        val progress = getUserProgress()
        return listOf(
            DailyChallenge(
                id = "daily_words",
                title = "📚 Günlük Kelime Hedefi",
                description = "Bugün ${progress.dailyTarget} kelime öğren",
                type = "learn_words",
                target = progress.dailyTarget,
                current = progress.dailyWordsLearned,
                xpReward = 50,
                coinReward = 10,
                isCompleted = progress.dailyWordsLearned >= progress.dailyTarget
            ),
            DailyChallenge(
                id = "daily_correct",
                title = "🎯 Doğru Cevap Hedefi",
                description = "Bugün 15 doğru cevap ver",
                type = "correct_answers",
                target = 15,
                current = progress.dailyCorrectAnswers,
                xpReward = 30,
                coinReward = 5,
                isCompleted = progress.dailyCorrectAnswers >= 15
            ),
            DailyChallenge(
                id = "daily_streak",
                title = "🔥 Günlük Seri",
                description = "Günlük serinizi koruyun",
                type = "study_streak",
                target = 1,
                current = if (isStudiedToday()) 1 else 0,
                xpReward = 20,
                coinReward = 3,
                isCompleted = isStudiedToday()
            )
        )
    }

    private fun getTodayString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun isStudiedToday(): Boolean {
        val progress = getUserProgress()
        val today = getTodayString()
        val lastStudyDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(progress.lastStudyDate))
        return today == lastStudyDate
    }

    // 🏆 Badge System
    fun unlockBadge(badgeId: String): Boolean {
        val progress = getUserProgress()
        val unlockedBadges = try {
            JSONArray(progress.unlockedBadges)
        } catch (e: Exception) {
            JSONArray()
        }
        
        // Badge zaten açık mı kontrol et
        for (i in 0 until unlockedBadges.length()) {
            if (unlockedBadges.getString(i) == badgeId) return false
        }
        
        unlockedBadges.put(badgeId)
        
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_UNLOCKED_BADGES, unlockedBadges.toString())
        }
        
        return db.update(TABLE_USER_PROGRESS, values, "$COLUMN_ID = ?", arrayOf("1")) > 0
    }

    fun getAvailableBadges(): List<Badge> {
        return listOf(
            Badge("first_word", "🥇 İlk Kelime", "İlk kelimeni öğrendin!", "🥇", "first_word", 25, 5),
            Badge("first_100", "💯 Yüzlük", "100 kelime öğrendin!", "💯", "first_100_words", 100, 50),
            Badge("streak_5", "🔥 5 Günlük Seri", "5 gün üst üste çalıştın!", "🔥", "5_day_streak", 75, 25),
            Badge("perfectionist", "⭐ Mükemmeliyetçi", "10 doğru cevap üst üste!", "⭐", "10_correct_streak", 50, 15),
            Badge("level_5", "🚀 Seviye 5", "5. seviyeye ulaştın!", "🚀", "reach_level_5", 200, 100),
            Badge("speed_learner", "⚡ Hızlı Öğrenci", "Bir günde 50 kelime öğrendin!", "⚡", "50_words_day", 150, 75)
        )
    }

    // 🛍️ Shop System
    fun unlockTheme(themeId: String): Boolean {
        val progress = getUserProgress()
        val unlockedThemes = progress.unlockedThemes.split(",").toMutableSet()
        unlockedThemes.add(themeId)
        
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_UNLOCKED_THEMES, unlockedThemes.joinToString(","))
        }
        
        return db.update(TABLE_USER_PROGRESS, values, "$COLUMN_ID = ?", arrayOf("1")) > 0
    }

    fun setCurrentTheme(themeId: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CURRENT_THEME, themeId)
        }
        
        return db.update(TABLE_USER_PROGRESS, values, "$COLUMN_ID = ?", arrayOf("1")) > 0
    }
    
    /**
     * Google Play yayını için kullanıcı ilerlemesini sıfırlar
     */
    fun resetUserProgress(): Boolean {
        return try {
            val db = writableDatabase
            
            // Kullanıcı ilerlemesini başlangıç değerlerine sıfırla
            val values = ContentValues().apply {
                put(COLUMN_LEVEL, 1)
                put(COLUMN_XP, 0)
                put(COLUMN_COINS, 100) // Başlangıç coin'i
                put(COLUMN_TOTAL_WORDS, 0)
                put(COLUMN_STREAK, 0)
                put(COLUMN_LAST_STUDY_DATE, 0)
                put(COLUMN_DAILY_WORDS_LEARNED, 0)
                put(COLUMN_DAILY_CORRECT_ANSWERS, 0)
                put(COLUMN_DAILY_TARGET, 10)
                put(COLUMN_LAST_CHALLENGE_DATE, 0)
                put(COLUMN_UNLOCKED_BADGES, "")
                put(COLUMN_UNLOCKED_THEMES, "default")
                put(COLUMN_CURRENT_THEME, "default")
            }
            
            db.update(TABLE_USER_PROGRESS, values, "$COLUMN_ID = ?", arrayOf("1")) > 0
        } catch (e: Exception) {
            android.util.Log.e("GamificationDao", "Error resetting user progress: ${e.message}")
            false
        }
    }
} 