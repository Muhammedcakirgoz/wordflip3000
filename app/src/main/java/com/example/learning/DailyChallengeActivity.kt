package com.example.learning

import com.example.learning.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.learning.database.GamificationDao
import com.example.learning.database.DailyChallenge
import com.example.learning.utils.LocaleHelper

class DailyChallengeActivity : AppCompatActivity() {
    
    private val themeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == SettingsActivity.THEME_CHANGED_ACTION) {
                recreate()
            }
        }
    }

    private lateinit var gamificationDao: GamificationDao
    private lateinit var btnBack: ImageButton
    private lateinit var tvUserLevel: TextView
    private lateinit var tvUserXP: TextView
    private lateinit var tvUserCoins: TextView
    private lateinit var challengeContainer: LinearLayout

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applyThemeSettings()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_challenge)

        gamificationDao = GamificationDao(this)
        
        initViews()
        setupClickListeners()
        loadUserProgress()
        loadDailyChallenges()
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(SettingsActivity.THEME_CHANGED_ACTION)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(themeChangeReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(themeChangeReceiver, filter)
        }
        loadUserProgress()
        loadDailyChallenges()
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(themeChangeReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver zaten unregister edilmiş
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvUserLevel = findViewById(R.id.tvUserLevel)
        tvUserXP = findViewById(R.id.tvUserXP)
        tvUserCoins = findViewById(R.id.tvUserCoins)
        challengeContainer = findViewById(R.id.challengeContainer)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadUserProgress() {
        // Ödül sistemi geçici olarak devre dışı - Google Play yayını için
        // val progress = gamificationDao.getUserProgress()
        // tvUserLevel.text = "Seviye ${progress.level}"
        // tvUserXP.text = "${progress.xp} XP"
        // tvUserCoins.text = "${progress.coins} 💰"
    }

    private fun loadDailyChallenges() {
        challengeContainer.removeAllViews()
        val challenges = gamificationDao.getTodaysChallenges()
        
        challenges.forEach { challenge ->
            addChallengeView(challenge)
        }
    }

    private fun addChallengeView(challenge: DailyChallenge) {
        val challengeView = layoutInflater.inflate(R.layout.item_daily_challenge, null)
        
        val cardView = challengeView.findViewById<CardView>(R.id.cardChallenge)
        val tvTitle = challengeView.findViewById<TextView>(R.id.tvChallengeTitle)
        val tvDescription = challengeView.findViewById<TextView>(R.id.tvChallengeDescription)
        val progressBar = challengeView.findViewById<ProgressBar>(R.id.challengeProgressBar)
        val tvProgress = challengeView.findViewById<TextView>(R.id.tvChallengeProgress)
        val tvReward = challengeView.findViewById<TextView>(R.id.tvChallengeReward)
        val ivStatus = challengeView.findViewById<ImageView>(R.id.ivChallengeStatus)

        tvTitle.text = challenge.title
        tvDescription.text = challenge.description
        progressBar.max = challenge.target
        progressBar.progress = challenge.current
        tvProgress.text = "${challenge.current} / ${challenge.target}"
        // Ödül bilgisi geçici olarak gizlendi - Google Play yayını için
        // tvReward.text = "+${challenge.xpReward} XP, +${challenge.coinReward} 💰"

        if (challenge.isCompleted) {
            ivStatus.setImageResource(android.R.drawable.checkbox_on_background)
            ivStatus.setColorFilter(ContextCompat.getColor(this, R.color.button_success))
            cardView.alpha = 0.7f
        } else {
            ivStatus.setImageResource(android.R.drawable.checkbox_off_background)
            ivStatus.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary))
            cardView.alpha = 1.0f
        }

        challengeContainer.addView(challengeView)
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