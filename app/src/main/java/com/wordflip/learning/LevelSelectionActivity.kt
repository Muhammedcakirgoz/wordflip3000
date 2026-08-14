package com.wordflip.learning

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import com.wordflip.learning.utils.LocaleHelper

class LevelSelectionActivity : AppCompatActivity() {
    
    private val themeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == SettingsActivity.THEME_CHANGED_ACTION) {
                recreate()
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applyThemeSettings()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_selection)

        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(SettingsActivity.THEME_CHANGED_ACTION)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(themeChangeReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(themeChangeReceiver, filter)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(themeChangeReceiver)
        } catch (e: IllegalArgumentException) {
            // Receiver zaten unregister edilmiş
        }
    }

    private fun setupClickListeners() {
        // Geri butonu
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Seviye butonları
        findViewById<CardView>(R.id.cardA1).setOnClickListener {
            startLearningWithLevel("A1", "A1 - Başlangıç")
        }
        
        findViewById<CardView>(R.id.cardA2).setOnClickListener {
            startLearningWithLevel("A2", "A2 - Temel")
        }
        
        findViewById<CardView>(R.id.cardB1).setOnClickListener {
            startLearningWithLevel("B1", "B1 - Orta Alt")
        }
        
        findViewById<CardView>(R.id.cardB2).setOnClickListener {
            startLearningWithLevel("B2", "B2 - Orta Üst")
        }
        
        findViewById<CardView>(R.id.cardC1).setOnClickListener {
            startLearningWithLevel("C1", "C1 - İleri")
        }

        // Seviye testi kartı -> LevelTestActivity'ye yönlendir
        findViewById<CardView>(R.id.cardLevelTest).setOnClickListener {
            val intent = Intent(this, LevelTestActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startLearningWithLevel(levelCode: String, levelName: String) {
        val intent = Intent(this, CardActivity::class.java)
        intent.putExtra("selected_level", levelCode)
        intent.putExtra("level_name", levelName)
        startActivity(intent)
        finish() // Bu activity'yi kapat
    }

    private fun showLevelTestInfo() {
        android.app.AlertDialog.Builder(this)
            .setTitle("📋 Seviye Belirleme Yardımı")
            .setMessage("Hangi seviyeyi seçeceğinizi bilmiyorsanız:\n\n" +
                    "🌱 Hiç İngilizce bilmiyorum → A1\n" +
                    "🌿 Temel kelimeler biliyorum → A2\n" +
                    "🌳 Orta düzey konuşabilirim → B1\n" +
                    "🏔️ İş yerinde kullanırım → B2\n" +
                    "🚀 Çok rahat konuşurum → C1")
            .setPositiveButton("Anladım") { _, _ -> }
            .show()
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