package com.wordflip.learning

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import com.wordflip.learning.utils.LocaleHelper
import com.wordflip.learning.animations.FlipCardAnimator

class WordFlipActivity : AppCompatActivity() {
    
    private val themeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == SettingsActivity.THEME_CHANGED_ACTION) {
                recreate()
            }
        }
    }
    private lateinit var cardView: CardView
    private lateinit var frontLayout: LinearLayout
    private lateinit var backLayout: LinearLayout
    private lateinit var wordEnglish: TextView
    private lateinit var wordTurkish: TextView
    private lateinit var showAnswerButton: Button
    private var isFrontVisible = true
    private var isAnimating = false

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Tema ayarını uygula
        applyThemeSettings()
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_flip)

        // View'ları başlat
        cardView = findViewById(R.id.cardView)
        frontLayout = findViewById(R.id.frontLayout)
        backLayout = findViewById(R.id.backLayout)
        wordEnglish = findViewById(R.id.wordEnglish)
        wordTurkish = findViewById(R.id.wordTurkish)
        showAnswerButton = findViewById(R.id.showAnswerButton)

        // İlk durumu ayarla
        setupInitialState()

        // Örnek kelime çifti
        wordEnglish.text = "Hello"
        wordTurkish.text = "Merhaba"

        // Kart'a tıklama olayı ekle
        cardView.setOnClickListener {
            if (!isAnimating) {
                val animator = FlipCardAnimator.createFlipAnimation(
                    frontView = frontLayout,
                    backView = backLayout,
                    isShowingFront = isFrontVisible,
                    onAnimationMiddle = {
                        // view visibility flip will be handled in animator
                    },
                    onAnimationEnd = {
                        isAnimating = false
                        isFrontVisible = !isFrontVisible
                        showAnswerButton.text = if (isFrontVisible) "Cevabı Göster" else "Tekrar Göster"
                    }
                )
                isAnimating = true
                animator.start()
            }
        }

        // "Cevabı Göster" butonuna tıklama olayı ekle
        showAnswerButton.setOnClickListener {
            if (!isAnimating) {
                showAnswer()
            }
        }
    }

    private fun setupInitialState() {
        Log.d("WordFlip", "setupInitialState çağrıldı")
        
        // Başlangıçta ön yüz görünsün
        frontLayout.visibility = View.VISIBLE
        frontLayout.scaleX = 1f
        frontLayout.alpha = 1f
        
        // Arka yüz gizli olsun
        backLayout.visibility = View.GONE
        backLayout.scaleX = 1f
        backLayout.alpha = 1f
        
        // Buton metni
        showAnswerButton.text = "Cevabı Göster"
        isFrontVisible = true
        
        Log.d("WordFlip", "İlk durum ayarlandı - frontLayout visible, backLayout gone")
    }

    private fun showAnswer() {
        Log.d("WordFlip", "showAnswer çağrıldı - isFrontVisible: $isFrontVisible, isAnimating: $isAnimating")
        
        if (isAnimating) {
            Log.d("WordFlip", "Animasyon devam ediyor, işlem iptal edildi")
            return
        }
        
        if (isFrontVisible) {
            // Ön yüzden arka yüze geçiş - cevabı göster
            Log.d("WordFlip", "Ön yüzden arka yüze geçiş başlatılıyor - BASIT YÖNTEM")
            val animator = FlipCardAnimator.createFlipAnimation(
                frontView = frontLayout,
                backView = backLayout,
                isShowingFront = true,
                onAnimationEnd = {
                    isAnimating = false
                    isFrontVisible = false
                    showAnswerButton.text = "Tekrar Göster"
                }
            )
            isAnimating = true
            animator.start()
        } else {
            // Zaten arka yüzdeyse, kartı tekrar arka yüze çevir (refresh efekti)
            Log.d("WordFlip", "Refresh animasyonu başlatılıyor")
            performRefreshAnimation()
        }
    }

    private fun performRefreshAnimation() {
        isAnimating = true
        
        // Kısa bir titreme animasyonu
        val shakeLeft = ObjectAnimator.ofFloat(cardView, "translationX", 0f, -10f)
        val shakeRight = ObjectAnimator.ofFloat(cardView, "translationX", -10f, 10f)
        val shakeCenter = ObjectAnimator.ofFloat(cardView, "translationX", 10f, 0f)
        
        val pulse = ObjectAnimator.ofFloat(cardView, "scaleX", 1f, 1.05f, 1f)
        val pulseY = ObjectAnimator.ofFloat(cardView, "scaleY", 1f, 1.05f, 1f)
        
        val animatorSet = AnimatorSet()
        animatorSet.apply {
            duration = 200
            playSequentially(shakeLeft, shakeRight, shakeCenter)
            play(pulse).with(shakeLeft)
            play(pulseY).with(shakeLeft)
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    isAnimating = false
                }
            })
            start()
        }
    }

    private fun flipCard() {
        if (isAnimating) return
        
        if (!isFrontVisible) {
            // Sadece arka yüzden ön yüze geçiş - soruyu tekrar göster
            val animator = FlipCardAnimator.createFlipAnimation(
                frontView = frontLayout,
                backView = backLayout,
                isShowingFront = false,
                onAnimationEnd = {
                    isAnimating = false
                    isFrontVisible = true
                    showAnswerButton.text = "Cevabı Göster"
                }
            )
            isAnimating = true
            animator.start()
        }
        // Ön yüzdeyse hiçbir şey yapma, sadece "Cevabı Göster" butonu kullanılsın
    }

    private fun performFlipAnimation(fromView: View, toView: View) {
        Log.d("WordFlip", "performFlipAnimation başladı - from: ${if(fromView == frontLayout) "front" else "back"}, to: ${if(toView == frontLayout) "front" else "back"}")
        
        isAnimating = true
        
        // Basit ama etkili flip animasyonu
        fromView.animate()
            .scaleX(0f)
            .setDuration(200)
            .withEndAction {
                Log.d("WordFlip", "İlk animasyon bitti, view'ları değiştiriyorum")
                
                // View'ları değiştir
                fromView.visibility = View.GONE
                fromView.scaleX = 1f // Reset
                
                toView.visibility = View.VISIBLE
                toView.scaleX = 0f // Başlangıç için küçük
                
                // İkinci animasyon - arka yüzü göster
                toView.animate()
                    .scaleX(1f)
                    .setDuration(200)
                    .withEndAction {
                        Log.d("WordFlip", "Animasyon tamamlandı")
                        isAnimating = false
                    }
                    .start()
            }
            .start()
    }

    // Basit flip fonksiyonu kaldırıldı; FlipCardAnimator kullanılmaktadır.

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

    private fun applyThemeSettings() {
        val isDarkTheme = SettingsActivity.isDarkTheme(this)
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
} 