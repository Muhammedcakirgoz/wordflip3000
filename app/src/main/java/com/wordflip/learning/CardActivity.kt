package com.wordflip.learning

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import java.util.Locale
import com.google.android.material.textfield.TextInputEditText
import com.wordflip.learning.database.Word
import com.wordflip.learning.database.WordDao
import com.wordflip.learning.database.GamificationDao
import com.wordflip.learning.utils.LocaleHelper
import java.text.Normalizer
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.wordflip.learning.animations.FlipCardAnimator

class CardActivity : AppCompatActivity() {
    
    private val themeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == SettingsActivity.THEME_CHANGED_ACTION) {
                android.util.Log.d("ThemeChange", "CardActivity: Tema değişikliği broadcast'i alındı")
                recreate()
            }
        }
    }
    private lateinit var cardView: CardView
    private lateinit var tvCardTextEnglish: TextView
    private lateinit var tvPhonetic: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvLevel: TextView
    private lateinit var etTranslation: TextInputEditText
    private lateinit var btnCheck: Button
    private lateinit var btnShowAnswer: Button
    private lateinit var btnTranslate: Button
    private lateinit var btnNext: Button
    private lateinit var btnBack: ImageButton
    private lateinit var btnPlaySound: ImageButton
    private lateinit var resultLayout: LinearLayout
    private lateinit var frontLayout: LinearLayout
    private lateinit var tvResult: TextView
    private lateinit var tvCorrectAnswer: TextView
    private lateinit var tvStats: TextView

    private lateinit var wordDao: WordDao
    private lateinit var gamificationDao: GamificationDao
    private var currentWords: List<Word> = emptyList()
    private var currentWordIndex = 0
    private var currentWord: Word? = null
    
    // TextToSpeech
    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false
    private var translator: Translator? = null
    
    // İstatistikler
    private var correctCount = 0
    private var wrongCount = 0
    private var totalAnswered = 0
    private var isFlipping = false

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Tema ayarını uygula
        applyThemeSettings()
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        // Veritabanını başlat
        wordDao = WordDao(this)
        gamificationDao = GamificationDao(this)
        
        // View'ları başlat
        initViews()
        
        // Intent'ten gelen seviyeyi al, yoksa varsayılan A1 kullan
        val selectedLevel = intent.getStringExtra("selected_level") ?: "A1"
        android.util.Log.d("CardActivity", "Seçilen seviye: $selectedLevel")
        
        // Seçilen seviyeyi Toast ile göster
        Toast.makeText(this, "📚 $selectedLevel seviyesi yükleniyor...", Toast.LENGTH_SHORT).show()
        
        // Kelimeleri yükle
        loadWords(selectedLevel)
        
        // Click listener'ları ayarla
        setupClickListeners()
        
        // TextToSpeech'i başlat
        initializeTextToSpeech()
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

    override fun onDestroy() {
        super.onDestroy()
        // TextToSpeech'i temizle
        textToSpeech?.shutdown()
        try {
            translator?.close()
        } catch (_: Exception) {}
    }

    private fun initViews() {
        cardView = findViewById(R.id.cardView)
        // 3D flip için kamera mesafesini artır
        val scale = resources.displayMetrics.density
        frontLayout = findViewById(R.id.frontLayout)
        frontLayout.cameraDistance = 8000f * scale
        tvCardTextEnglish = findViewById(R.id.tvCardTextEnglish)
        tvPhonetic = findViewById(R.id.tvPhonetic)
        tvCategory = findViewById(R.id.tvCategory)
        tvLevel = findViewById(R.id.tvLevel)
        etTranslation = findViewById(R.id.etTranslation)
        btnCheck = findViewById(R.id.btnCheck)
        btnShowAnswer = findViewById(R.id.btnShowAnswer)
        btnTranslate = findViewById(R.id.btnTranslate)
        btnNext = findViewById(R.id.btnNext)
        btnBack = findViewById(R.id.btnBack)
        btnPlaySound = findViewById(R.id.btnPlaySound)
        resultLayout = findViewById(R.id.resultLayout)
        resultLayout.cameraDistance = 8000f * scale
        tvResult = findViewById(R.id.tvResult)
        tvCorrectAnswer = findViewById(R.id.tvCorrectAnswer)
        tvStats = findViewById(R.id.tvStats)
    }

    private fun loadWords(level: String) {
        // Oturum hedefi: kullanıcı günlük hedefinden veya varsayılan 10 kelime
        val sharedPreferences = getSharedPreferences("WordFlipSettings", Context.MODE_PRIVATE)
        val dailyTarget = sharedPreferences.getInt("daily_goal", 10)
        val sessionTarget = dailyTarget.coerceIn(5, 25)

        // Adaptif seçim + SM-2: öncelik sırasına göre kelimeleri getir
        currentWords = wordDao.getWordsForStudy(level, sessionTarget)

        val sortMessage = getString(R.string.words_loaded_adaptive)
        
        if (currentWords.isNotEmpty()) {
            currentWordIndex = 0
            correctCount = 0
                wrongCount = 0
            totalAnswered = 0
            loadCurrentWord()
            updateStats()
            
            // Kısa bir bilgilendirme mesajı göster
            Toast.makeText(this, getString(R.string.level_loaded, level, sortMessage), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.no_words_found), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupClickListeners() {
        // Geri butonu
        btnBack.setOnClickListener {
            finish()
        }

        // Kontrol et butonu
        btnCheck.setOnClickListener {
            checkAnswer()
        }

        // Cevabı göster butonu
        btnShowAnswer.setOnClickListener {
            showAnswer()
        }

        // Sonraki kelime butonu
        btnNext.setOnClickListener {
            nextWord()
        }

        // Ses çalma butonu
        btnPlaySound.setOnClickListener {
            playWordPronunciation()
        }

        // Çeviri butonu
        btnTranslate.setOnClickListener {
            translateAuto()
        }

        // Enter tuşu ile kontrol
        etTranslation.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkAnswer()
                true
            } else {
                false
            }
        }
    }

    private fun loadCurrentWord() {
        if (currentWordIndex < currentWords.size) {
            currentWord = currentWords[currentWordIndex]
            currentWord?.let { word ->
                tvCardTextEnglish.text = word.english
                tvPhonetic.text = "/${word.phonetic ?: ""}"
                tvCategory.text = word.category.uppercase()
                tvLevel.text = word.level
                
                // Input alanını temizle ve aktif et
                etTranslation.text?.clear()
                etTranslation.isEnabled = true
                btnCheck.isEnabled = true
                btnShowAnswer.isEnabled = true
                
                // Yüzleri başlangıç durumuna getir
                resultLayout.visibility = View.GONE
                frontLayout.visibility = View.VISIBLE
                frontLayout.alpha = 1f
                resultLayout.alpha = 1f
                // Flip dönüşlerini sıfırla (aksi halde bir sonraki kelimede görünmez kalabilir)
                cardView.rotationY = 0f
                frontLayout.rotationY = 0f
                resultLayout.rotationY = 0f
                isFlipping = false
                tvCorrectAnswer.visibility = View.GONE
                
                // Klavyeyi göster ve focus ver
                etTranslation.requestFocus()
                showKeyboard()
                
                // Ses butonu görünürlüğünü ayarlama
                btnPlaySound.visibility = if (isAudioEnabled()) View.VISIBLE else View.GONE
                
                // Otomatik ses çalma (eğer ayar açıksa)
                if (isAudioEnabled()) {
                    playWordPronunciation()
                }
                initTranslator()
            }
        } else {
            // Kelimeler bitti
            showCompletionDialog()
        }
    }

    private fun initTranslator() {
        try { translator?.close() } catch (_: Exception) {}
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.TURKISH)
            .build()
        translator = Translation.getClient(options)
        translator?.downloadModelIfNeeded()
            ?.addOnSuccessListener { }
            ?.addOnFailureListener { }
    }

    private fun translateAuto() {
        val currentTurkish = etTranslation.text?.toString()?.trim().orEmpty()
        if (currentTurkish.isNotEmpty()) {
            translateTrToEn(currentTurkish)
        } else {
            translateCurrentWord()
        }
    }

    private fun translateCurrentWord() {
        val text = tvCardTextEnglish.text?.toString()?.trim().orEmpty()
        if (text.isEmpty()) return
        val t = translator ?: return
        btnTranslate.isEnabled = false
        t.translate(text)
            .addOnSuccessListener { translated ->
                etTranslation.setText(translated)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Çeviri başarısız. İnternet/model gerekli olabilir.", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener { btnTranslate.isEnabled = true }
    }

    private fun translateTrToEn(src: String) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.TURKISH)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        val trEn = Translation.getClient(options)
        btnTranslate.isEnabled = false
        trEn.downloadModelIfNeeded()
            .addOnSuccessListener {
                trEn.translate(src)
                    .addOnSuccessListener { translated -> etTranslation.setText(translated) }
                    .addOnFailureListener { Toast.makeText(this, "Çeviri başarısız (TR→EN)", Toast.LENGTH_SHORT).show() }
                    .addOnCompleteListener {
                        btnTranslate.isEnabled = true
                        try { trEn.close() } catch (_: Exception) {}
                    }
            }
            .addOnFailureListener {
                btnTranslate.isEnabled = true
                Toast.makeText(this, "Model indirilemedi (TR→EN)", Toast.LENGTH_SHORT).show()
                try { trEn.close() } catch (_: Exception) {}
            }
    }

    private fun checkAnswer() {
        val userAnswer = etTranslation.text.toString().trim()
        if (userAnswer.isEmpty()) {
            Toast.makeText(this, "Lütfen bir cevap yazın", Toast.LENGTH_SHORT).show()
            return
        }

        currentWord?.let { word ->
            val isCorrect = isAnswerCorrect(userAnswer, word.turkish)
            totalAnswered++
            
                if (isCorrect) {
                	correctCount++
                	wordDao.applyReviewResult(word.id, true, qualityOverride = 4)
                
                // 🎮 Gamification: Geçici olarak devre dışı - Google Play yayını için
                // gamificationDao.addXP(10) // Doğru cevap için 10 XP
                // gamificationDao.addCoins(2) // Doğru cevap için 2 coin
                // gamificationDao.updateDailyProgress(0, 1) // Günlük doğru cevap sayısını artır
                
                showResult(true, word.turkish)
                } else {
                	wrongCount++
                	wordDao.applyReviewResult(word.id, false, qualityOverride = 2)
                
                // 🎮 Gamification: Geçici olarak devre dışı - Google Play yayını için
                // gamificationDao.addXP(1) // Yanlış cevap için 1 XP (effort için)
                
                showResult(false, word.turkish)
            }
            
            updateStats()
            hideKeyboard()
        }
    }

    private fun isAnswerCorrect(userAnswer: String, correctAnswer: String): Boolean {
        // Normalize both strings (remove accents, lowercase, trim)
        val normalizedUser = normalizeString(userAnswer)
        val normalizedCorrect = normalizeString(correctAnswer)
        
        // Exact match
        if (normalizedUser == normalizedCorrect) return true
        
        // Check if user answer is one of the alternative translations
        val alternatives = normalizedCorrect.split(",", "|", "/")
        return alternatives.any { alternative ->
            normalizedUser == alternative.trim()
        }
    }

    private fun normalizeString(input: String): String {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("[\\p{InCombiningDiacriticalMarks}]".toRegex(), "")
            .lowercase(Locale.getDefault())
            .trim()
    }

    private fun showAnswer() {
        currentWord?.let { word ->
            etTranslation.setText(word.turkish)
            etTranslation.isEnabled = false
            btnCheck.isEnabled = false
            btnShowAnswer.isEnabled = false
            
            wrongCount++
            totalAnswered++
            wordDao.applyReviewResult(word.id, false, qualityOverride = 1)
            
            showResult(false, word.turkish, true)
            updateStats()
            hideKeyboard()
        }
    }

    private fun showResult(isCorrect: Boolean, correctAnswer: String, wasShown: Boolean = false) {
        // Ön yüzü arka yüze çevirerek sonucu göster (3D flip)
        if (!isFlipping) {
            flipToBack3DLayouts()
        }
        resultLayout.visibility = View.VISIBLE
        
        if (isCorrect) {
            tvResult.text = "Doğru! 🎉"
            tvResult.setBackgroundResource(R.drawable.result_background_correct)
            tvCorrectAnswer.visibility = View.GONE
        } else {
            if (wasShown) {
                tvResult.text = "Cevap Gösterildi 📖"
                tvResult.setBackgroundResource(R.drawable.result_background_wrong)
            } else {
                tvResult.text = "Yanlış ❌"
                tvResult.setBackgroundResource(R.drawable.result_background_wrong)
            }
            tvCorrectAnswer.text = "Doğru cevap: $correctAnswer"
            tvCorrectAnswer.visibility = View.VISIBLE
        }
        
        etTranslation.isEnabled = false
        btnCheck.isEnabled = false
        btnShowAnswer.isEnabled = false
    }

    private fun flipToBack3DLayouts() {
        if (isFlipping) return
        isFlipping = true
        val animator = FlipCardAnimator.createFlipAnimation(
            frontView = frontLayout,
            backView = resultLayout,
            isShowingFront = true,
            onAnimationMiddle = {
                // visibility handled in animator sequence
            },
            onAnimationEnd = {
                isFlipping = false
            }
        )
        animator.start()
    }

    private fun nextWord() {
        currentWordIndex++
        if (currentWordIndex >= currentWords.size) {
            showCompletionDialog()
        } else {
            loadCurrentWord()
        }
    }

    private fun updateStats() {
        val remaining = currentWords.size - totalAnswered
        tvStats.text = "Doğru: $correctCount | Yanlış: $wrongCount | Kalan: $remaining"
    }

    private fun showCompletionDialog() {
        val stats = wordDao.getProgressStats()
        val successRate = if (totalAnswered > 0) (correctCount * 100) / totalAnswered else 0
        val currentLevel = currentWords.firstOrNull()?.level ?: "A1"
        val nextLevel = getNextLevel()
        
        val performanceEmoji = when {
            successRate >= 90 -> "🏆 Mükemmel!"
            successRate >= 75 -> "🎯 Harika!"
            successRate >= 60 -> "👍 İyi!"
            successRate >= 40 -> "📚 Fena değil!"
            else -> "💪 Pratik gerek!"
        }
        
        val message = """
            $currentLevel Seviyesi Tamamlandı! 🎉
            
            📊 Bu Oturumda:
            • ✅ Doğru: $correctCount
            • ❌ Yanlış: $wrongCount
            • 📈 Başarı oranı: %$successRate
            
            $performanceEmoji
            
            📚 Toplam İlerleme: ${stats["learned"]} / ${stats["total"]} kelime
        """.trimIndent()
        
        val builder = android.app.AlertDialog.Builder(this)
            .setTitle("🎓 Tebrikler!")
            .setMessage(message)
            .setCancelable(false)
        
        if (nextLevel != null) {
            builder.setPositiveButton("🚀 $nextLevel Seviyesine Geç") { _, _ ->
                Toast.makeText(this, "📈 $nextLevel seviyesi yükleniyor...", Toast.LENGTH_SHORT).show()
                loadWords(nextLevel)
            }
        } else {
            builder.setPositiveButton("🏅 C1 Tamamlandı - A1'e Dön") { _, _ ->
                Toast.makeText(this, "🔄 A1 seviyesi yükleniyor...", Toast.LENGTH_SHORT).show()
                loadWords("A1")
            }
        }
        
        builder.setNeutralButton("🔄 $currentLevel Tekrar Et") { _, _ ->
            Toast.makeText(this, "📚 $currentLevel seviyesi yeniden yükleniyor...", Toast.LENGTH_SHORT).show()
            loadWords(currentLevel)
        }
        
        builder.setNegativeButton("🏠 Ana Menü") { _, _ ->
            finish()
        }
        
        builder.show()
    }

    private fun getNextLevel(): String? {
        val currentLevel = currentWords.firstOrNull()?.level ?: "A1"
        return when (currentLevel) {
            "A1" -> "A2"
            "A2" -> "B1"
            "B1" -> "B2"
            "B2" -> "C1"
            else -> null
        }
    }

    private fun showKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(etTranslation, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etTranslation.windowToken, 0)
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    android.util.Log.e("TTS", "Language not supported")
                    isTtsInitialized = false
                } else {
                    isTtsInitialized = true
                    // TTS ayarları
                    textToSpeech?.setSpeechRate(0.8f) // Biraz yavaş konuş
                    textToSpeech?.setPitch(1.0f) // Normal ton
                }
            } else {
                android.util.Log.e("TTS", "TTS initialization failed")
                isTtsInitialized = false
            }
        }
    }

    private fun playWordPronunciation() {
        val sharedPreferences = getSharedPreferences("WordFlipSettings", Context.MODE_PRIVATE)
        val isAudioEnabled = sharedPreferences.getBoolean("audio_enabled", true)
        
        if (!isAudioEnabled) {
            return // Ses özelliği kapalı
        }
        
        if (!isTtsInitialized || textToSpeech == null) {
            Toast.makeText(this, getString(R.string.tts_not_available), Toast.LENGTH_SHORT).show()
            return
        }
        
        currentWord?.let { word ->
            val utteranceId = "word_${word.id}"
            textToSpeech?.speak(word.english, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    private fun isAudioEnabled(): Boolean {
        val sharedPreferences = getSharedPreferences("WordFlipSettings", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("audio_enabled", true)
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