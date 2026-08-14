package com.wordflip.learning

import com.wordflip.learning.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wordflip.learning.utils.LocaleHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    
    private val themeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == SettingsActivity.THEME_CHANGED_ACTION) {
                android.util.Log.d("ThemeChange", "MainActivity: Tema değişikliği broadcast'i alındı")
                recreate()
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Koyu mod ayarını kontrol et ve uygula
        applyThemeSettings()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_translate -> {
                    startActivity(Intent(this, TranslateActivity::class.java))
                    true
                }
                R.id.nav_progress -> {
                    startActivity(Intent(this, ProgressActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNav.selectedItemId = R.id.nav_home

		val cardStartLearning = findViewById<androidx.cardview.widget.CardView>(R.id.cardStartLearning)
		val cardDailyChallenge = findViewById<androidx.cardview.widget.CardView>(R.id.cardDailyChallenge)
		val cardExit = findViewById<androidx.cardview.widget.CardView>(R.id.cardExit)

        cardStartLearning.setOnClickListener {
            val intent = Intent(this, LevelSelectionActivity::class.java)
            startActivity(intent)
        }

        cardDailyChallenge.setOnClickListener {
            val intent = Intent(this, DailyChallengeActivity::class.java)
            startActivity(intent)
        }

        cardExit.setOnClickListener {
            finishAffinity()
        }


        
    }

    override fun onResume() {
        super.onResume()
        // Ayarlar ekranından geri döndüğünde tema kontrolü yap
        applyThemeSettings()
        
        // Tema değişikliği broadcast'ini dinlemeye başla
        val filter = IntentFilter(SettingsActivity.THEME_CHANGED_ACTION)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(themeChangeReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(themeChangeReceiver, filter)
        }
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

    private fun applyThemeSettings() {
        val isDarkTheme = SettingsActivity.isDarkTheme(this)
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


}