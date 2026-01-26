package com.example.learning.utils

import android.content.Context
import com.example.learning.database.WordDao
import com.example.learning.database.GamificationDao
import com.example.learning.database.QuestionDao

/**
 * Google Play Store yayını için veritabanı sıfırlama yardımcı sınıfı
 * Bu sınıf uygulamanın temiz bir durumda başlamasını sağlar
 */
object DatabaseResetHelper {
    
    /**
     * Tüm kullanıcı verilerini sıfırlar (ilk yayın için)
     */
    fun resetAllUserData(context: Context): Boolean {
        return try {
            // Gamification verilerini sıfırla
            val gamificationDao = GamificationDao(context)
            gamificationDao.resetUserProgress()
            
            // Kelime öğrenme verilerini sıfırla
            val wordDao = WordDao(context)
            wordDao.resetUserProgress()
            
            // Soru verilerini sıfırla
            val questionDao = QuestionDao(context)
            questionDao.resetQuestionStats()
            
            true
        } catch (e: Exception) {
            android.util.Log.e("DatabaseReset", "Sıfırlama hatası: ${e.message}")
            false
        }
    }
    
    /**
     * Sadece kullanıcı ilerlemesini sıfırlar, kelime veritabanını korur
     */
    fun resetUserProgressOnly(context: Context): Boolean {
        return try {
            val wordDao = WordDao(context)
            wordDao.resetUserProgressOnly()
            
            val gamificationDao = GamificationDao(context)
            gamificationDao.resetUserProgress()
            
            true
        } catch (e: Exception) {
            android.util.Log.e("DatabaseReset", "İlerleme sıfırlama hatası: ${e.message}")
            false
        }
    }
}
