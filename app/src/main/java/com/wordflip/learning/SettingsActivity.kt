package com.wordflip.learning

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.wordflip.learning.utils.LocaleHelper
import com.wordflip.learning.utils.DatabaseResetHelper
import com.wordflip.learning.database.WordDao

class SettingsActivity : AppCompatActivity() {
    private lateinit var btnBack: ImageButton
    private lateinit var switchAudio: SwitchCompat
    private lateinit var seekBarSpeed: SeekBar
    private lateinit var radioGroupTheme: RadioGroup
    private lateinit var radioLight: RadioButton
    private lateinit var radioDark: RadioButton
    private lateinit var radioGroupSorting: RadioGroup
    private lateinit var radioRandom: RadioButton
    private lateinit var radioLeastLearned: RadioButton
    private lateinit var radioRecentStudy: RadioButton
    private lateinit var radioGroupLanguage: RadioGroup
    private lateinit var radioTurkish: RadioButton
    private lateinit var radioEnglish: RadioButton
    private lateinit var tvDailyGoal: TextView
    private lateinit var btnEditGoal: ImageButton
    private lateinit var btnResetProgress: Button
    
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var wordDao: WordDao

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Önce tema ayarını uygula
        applyCurrentTheme()
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // SharedPreferences'ı başlat
        sharedPreferences = getSharedPreferences("WordFlipSettings", Context.MODE_PRIVATE)
        
        // WordDao'yu başlat
        wordDao = WordDao(this)
        
        // View'ları başlat
        initViews()
        
        // Ayarları yükle
        loadSettings()
        
        // Click listener'ları ayarla
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        switchAudio = findViewById(R.id.switchAudio)
        seekBarSpeed = findViewById(R.id.seekBarSpeed)
        radioGroupTheme = findViewById(R.id.radioGroupTheme)
        radioLight = findViewById(R.id.radioLight)
        radioDark = findViewById(R.id.radioDark)
        radioGroupSorting = findViewById(R.id.radioGroupSorting)
        radioRandom = findViewById(R.id.radioRandom)
        radioLeastLearned = findViewById(R.id.radioLeastLearned)
        radioRecentStudy = findViewById(R.id.radioRecentStudy)
        radioGroupLanguage = findViewById(R.id.radioGroupLanguage)
        radioTurkish = findViewById(R.id.radioTurkish)
        radioEnglish = findViewById(R.id.radioEnglish)
        tvDailyGoal = findViewById(R.id.tvDailyGoal)
        btnEditGoal = findViewById(R.id.btnEditGoal)
        btnResetProgress = findViewById(R.id.btnResetProgress)
    }

    private fun loadSettings() {
        // Sesli okuma ayarını yükle
        switchAudio.isChecked = sharedPreferences.getBoolean("audio_enabled", true)
        
        // Animasyon hızı ayarını yükle
        seekBarSpeed.progress = sharedPreferences.getInt("animation_speed", 50)
        
        // Tema ayarını yükle
        val isDarkTheme = sharedPreferences.getBoolean("dark_theme", false)
        if (isDarkTheme) {
            radioDark.isChecked = true
        } else {
            radioLight.isChecked = true
        }
        
        // Kart sıralama ayarını yükle
        val sortOrder = sharedPreferences.getString("card_sort_order", "random")
        when (sortOrder) {
            "random" -> radioRandom.isChecked = true
            "least_learned" -> radioLeastLearned.isChecked = true
            "recent_study" -> radioRecentStudy.isChecked = true
            else -> radioRandom.isChecked = true
        }
        
        // Dil ayarını yükle
        val currentLanguage = sharedPreferences.getString("selected_language", "tr") ?: "tr"
        when (currentLanguage) {
            "tr" -> radioTurkish.isChecked = true
            "en" -> radioEnglish.isChecked = true
            else -> radioTurkish.isChecked = true
        }
        
        // Günlük hedef ayarını yükle
        val dailyGoal = sharedPreferences.getInt("daily_goal", 10)
        tvDailyGoal.text = dailyGoal.toString()
    }

    private fun setupClickListeners() {
        // Geri butonu
        btnBack.setOnClickListener {
            finish()
        }

        // Sesli okuma switch'i
        switchAudio.setOnCheckedChangeListener { _, isChecked ->
            saveAudioSetting(isChecked)
            showToast(if (isChecked) getString(R.string.audio_enabled) else getString(R.string.audio_disabled))
        }

        // Animasyon hızı seekbar'ı
        seekBarSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    saveAnimationSpeed(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val speed = when {
                    seekBarSpeed.progress < 33 -> getString(R.string.speed_slow)
                    seekBarSpeed.progress < 67 -> "Normal"
                    else -> getString(R.string.speed_fast)
                }
                showToast("${getString(R.string.animation_speed)}: $speed")
            }
        })

        // Tema radio group
        radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val isDarkTheme = checkedId == R.id.radioDark
            saveThemeSetting(isDarkTheme)
            showToast(getString(R.string.theme_changed))
        }

        // Kart sıralama radio group
        radioGroupSorting.setOnCheckedChangeListener { _, checkedId ->
            val sortOrder = when (checkedId) {
                R.id.radioRandom -> "random"
                R.id.radioLeastLearned -> "least_learned"
                R.id.radioRecentStudy -> "recent_study"
                else -> "random"
            }
            saveSortOrderSetting(sortOrder)
            val sortName = when (sortOrder) {
                "random" -> getString(R.string.sort_random_selected)
                "least_learned" -> getString(R.string.sort_least_learned_selected)
                "recent_study" -> getString(R.string.sort_recent_study_selected)
                else -> getString(R.string.sort_random_selected)
            }
            showToast("${getString(R.string.card_sorting)}: $sortName")
        }

        // Dil radio group
        radioGroupLanguage.setOnCheckedChangeListener { _, checkedId ->
            val language = when (checkedId) {
                R.id.radioTurkish -> "tr"
                R.id.radioEnglish -> "en"
                else -> "tr"
            }
            saveLanguageSetting(language)
            showToast(getString(R.string.language_changed))
        }

        // Günlük hedef düzenleme butonu
        btnEditGoal.setOnClickListener {
            showEditGoalDialog()
        }

        // Veri sıfırlama butonu
        btnResetProgress.setOnClickListener {
            showResetConfirmationDialog()
        }
    }

    private fun saveAudioSetting(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean("audio_enabled", enabled)
            .apply()
    }

    private fun saveAnimationSpeed(speed: Int) {
        sharedPreferences.edit()
            .putInt("animation_speed", speed)
            .apply()
    }

    private fun saveThemeSetting(isDarkTheme: Boolean) {
        android.util.Log.d("ThemeChange", "Tema değişikliği: ${if (isDarkTheme) "Koyu" else "Açık"}")
        
        sharedPreferences.edit()
            .putBoolean("dark_theme", isDarkTheme)
            .apply()
        
        // Temayı anında uygula
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        
        // Tema değişikliği bildirimini gönder
        sendThemeChangeBroadcast()
        
        // Activity'yi yeniden başlat ki tema değişikliği anında görünsün
        recreate()
    }

    private fun saveSortOrderSetting(sortOrder: String) {
        sharedPreferences.edit()
            .putString("card_sort_order", sortOrder)
            .apply()
    }

    private fun saveLanguageSetting(language: String) {
        sharedPreferences.edit()
            .putString("selected_language", language)
            .apply()
        
        // LocaleHelper kullanarak dili uygula
        com.wordflip.learning.utils.LocaleHelper.setLocale(this, language)
        
        // Activity'yi yeniden başlat
        recreate()
    }

    private fun saveDailyGoal(goal: Int) {
        sharedPreferences.edit()
            .putInt("daily_goal", goal)
            .apply()
        tvDailyGoal.text = goal.toString()
    }

    private fun showEditGoalDialog() {
        val currentGoal = sharedPreferences.getInt("daily_goal", 10)
        
        val input = EditText(this)
        input.setText(currentGoal.toString())
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.daily_goal_dialog_title))
            .setMessage(getString(R.string.daily_goal_dialog_message))
            .setView(input)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val newGoal = input.text.toString().toIntOrNull()
                if (newGoal != null && newGoal > 0 && newGoal <= 100) {
                    saveDailyGoal(newGoal)
                    showToast(getString(R.string.daily_goal_updated, newGoal))
                } else {
                    showToast(getString(R.string.daily_goal_error))
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun showResetConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.reset_confirmation_title))
            .setMessage(getString(R.string.reset_confirmation_message))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                resetAllProgress()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun resetAllProgress() {
        try {
            // Google Play yayını için tüm kullanıcı verilerini sıfırla
            val success = DatabaseResetHelper.resetAllUserData(this)
            if (success) {
                showToast("Tüm veriler başarıyla sıfırlandı! Uygulama yayına hazır.")
                
                // Başarılı olduğunu göstermek için butonu geçici olarak devre dışı bırak
                btnResetProgress.isEnabled = false
                btnResetProgress.postDelayed({
                    btnResetProgress.isEnabled = true
                }, 3000)
            } else {
                showToast("Sıfırlama işlemi başarısız oldu.")
            }
        } catch (e: Exception) {
            android.util.Log.e("SettingsActivity", "Reset error: ${e.message}")
            showToast("Sıfırlama sırasında hata oluştu: ${e.message}")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun applyCurrentTheme() {
        val prefs = getSharedPreferences("WordFlipSettings", Context.MODE_PRIVATE)
        val isDarkTheme = prefs.getBoolean("dark_theme", false)
        
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun sendThemeChangeBroadcast() {
        android.util.Log.d("ThemeChange", "Broadcast gönderiliyor: $THEME_CHANGED_ACTION")
        val intent = Intent(THEME_CHANGED_ACTION)
        sendBroadcast(intent)
        android.util.Log.d("ThemeChange", "Broadcast gönderildi")
    }

    companion object {
        const val THEME_CHANGED_ACTION = "com.wordflip.uygulamaproje.THEME_CHANGED"
        
        // Ayarları almak için static metodlar
        fun isAudioEnabled(context: Context): Boolean {
            val prefs = context.getSharedPreferences("WordFlipSettings", Context.MODE_PRIVATE)
            return prefs.getBoolean("audio_enabled", true)
        }

        fun getAnimationSpeed(context: Context): Int {
            val prefs = context.getSharedPreferences("WordFlipSettings", Context.MODE_PRIVATE)
            return prefs.getInt("animation_speed", 50)
        }

        fun isDarkTheme(context: Context): Boolean {
            val prefs = context.getSharedPreferences("WordFlipSettings", Context.MODE_PRIVATE)
            return prefs.getBoolean("dark_theme", false)
        }

        fun getDailyGoal(context: Context): Int {
            val prefs = context.getSharedPreferences("WordFlipSettings", Context.MODE_PRIVATE)
            return prefs.getInt("daily_goal", 10)
        }
    }
} 