package com.wordflip.learning.database

data class UserProgress(
    val id: Int = 1,
    // 🎮 Gamification System
    val level: Int = 1,
    val xp: Int = 0,
    val coins: Int = 0,
    val totalWords: Int = 0,
    val streak: Int = 0,  // Günlük seri
    val lastStudyDate: Long = 0,
    // 📅 Daily Challenges
    val dailyWordsLearned: Int = 0,
    val dailyCorrectAnswers: Int = 0,
    val dailyTarget: Int = 10,
    val lastChallengeDate: Long = 0,
    // 🏆 Achievements/Badges
    val unlockedBadges: String = "", // JSON string of unlocked badge IDs
    val unlockedThemes: String = "", // JSON string of unlocked theme IDs
    val currentTheme: String = "default"
)

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val requirement: String, // "first_100_words", "5_day_streak", etc.
    val xpReward: Int = 0,
    val coinReward: Int = 0
)

data class DailyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val type: String, // "learn_words", "correct_answers", "study_time"
    val target: Int,
    val current: Int = 0,
    val xpReward: Int,
    val coinReward: Int,
    val isCompleted: Boolean = false
) 