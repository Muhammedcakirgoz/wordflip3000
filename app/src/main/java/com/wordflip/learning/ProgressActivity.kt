package com.wordflip.learning

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.wordflip.learning.database.WordDao
import com.wordflip.learning.utils.LocaleHelper

class ProgressActivity : AppCompatActivity() {
    
    private val themeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == SettingsActivity.THEME_CHANGED_ACTION) {
                recreate()
            }
        }
    }
    private lateinit var btnBack: ImageButton
    private lateinit var tvLearnedCount: TextView
    private lateinit var tvTotalCount: TextView
    private lateinit var tvSuccessRate: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var levelProgressContainer: LinearLayout
    
    private lateinit var wordDao: WordDao

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Tema ayarını uygula
        applyThemeSettings()
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        wordDao = WordDao(this)
        
        initViews()
        setupClickListeners()
        loadProgressData()
    }

    override fun onResume() {
        super.onResume()
        // Tema değişikliği broadcast'ini dinlemeye başla
        val filter = IntentFilter(SettingsActivity.THEME_CHANGED_ACTION)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(themeChangeReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(themeChangeReceiver, filter)
        }

        // İstatistikleri her dönüşte tazele
        loadProgressData()
    }

    override fun onPause() {
        super.onPause()
        // Broadcast receiver'ı kapat
        try {
            unregisterReceiver(themeChangeReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver zaten unregister edilmiş
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvLearnedCount = findViewById(R.id.tvLearnedCount)
        tvTotalCount = findViewById(R.id.tvTotalCount)
        tvSuccessRate = findViewById(R.id.tvSuccessRate)
        progressBar = findViewById(R.id.progressBar)
        levelProgressContainer = findViewById(R.id.levelProgressContainer)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadProgressData() {
        val stats = wordDao.getProgressStats()
        val total = stats["total"] ?: 0
        // Görsel ilerleme için en az 1 doğru yapılmış kelimeleri "öğrenildi" kabul et
        val learned = wordDao.getLearnedCount(minCorrect = 1)
        val successRate = if (total > 0) (learned * 100) / total else 0

        tvLearnedCount.text = learned.toString()
        tvTotalCount.text = total.toString()
        tvSuccessRate.text = "$successRate%"
        progressBar.max = total
        progressBar.progress = learned

        // Seviye bazında ilerleme
        loadLevelProgress()
    }

    private fun loadLevelProgress() {
        val levels = listOf("A1", "A2", "B1", "B2", "C1")
        levelProgressContainer.removeAllViews()

        levels.forEach { level ->
            val levelWords = wordDao.getWordsByLevel(level)
            val learnedWords = levelWords.count { it.correctCount >= 1 }
            val totalWords = levelWords.size

            if (totalWords > 0) {
                addLevelProgressView(level, learnedWords, totalWords)
            }
        }
    }

    private fun addLevelProgressView(level: String, learned: Int, total: Int) {
        val progressView = layoutInflater.inflate(R.layout.item_level_progress, null)
        
        val tvLevelName = progressView.findViewById<TextView>(R.id.tvLevelName)
        val tvLevelStats = progressView.findViewById<TextView>(R.id.tvLevelStats)
        val levelProgressBar = progressView.findViewById<ProgressBar>(R.id.levelProgressBar)

        tvLevelName.text = "Seviye $level"
        tvLevelStats.text = "$learned / $total kelime"
        levelProgressBar.max = total
        levelProgressBar.progress = learned

        levelProgressContainer.addView(progressView)
    }

    private fun applyThemeSettings() {
        val isDarkTheme = SettingsActivity.isDarkTheme(this)
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
} 