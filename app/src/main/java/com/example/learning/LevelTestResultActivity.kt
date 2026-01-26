package com.example.learning

import com.example.learning.R
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class LevelTestResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_test_result)

        val levelCode = intent.getStringExtra("computed_level") ?: "A1"
        val score = intent.getIntExtra("score", 0)

        val title = findViewById<TextView>(R.id.tvLevelTitle)
        val desc = findViewById<TextView>(R.id.tvLevelDesc)
        val details = findViewById<TextView>(R.id.tvDetails)

        title.text = mapLevelTitle(levelCode)
        desc.text = mapLevelDescription(levelCode)
        details.text = getString(R.string.level_test_score_info, score)

        findViewById<MaterialButton>(R.id.btnStartLearning).setOnClickListener {
            val intent = Intent(this, CardActivity::class.java)
            intent.putExtra("selected_level", levelCode)
            intent.putExtra("level_name", mapLevelTitle(levelCode))
            startActivity(intent)
            finish()
        }

        findViewById<MaterialButton>(R.id.btnRetakeTest).setOnClickListener {
            val intent = Intent(this, LevelTestActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<MaterialButton>(R.id.btnChooseLevel).setOnClickListener {
            val intent = Intent(this, LevelSelectionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun mapLevelTitle(code: String): String = when (code) {
        "A1" -> "A1 - Başlangıç"
        "A2" -> "A2 - Temel"
        "B1" -> "B1 - Orta Alt"
        "B2" -> "B2 - Orta Üst"
        "C1" -> "C1 - İleri"
        else -> code
    }

    private fun mapLevelDescription(code: String): String = when (code) {
        "A1" -> "Günlük ifadeler ve temel kelimeler. Basit cümle kurma."
        "A2" -> "Sık kullanılan kelimeler ve kalıplar. Alışveriş, seyahat vb."
        "B1" -> "Sosyal ve iş yaşamında akıcı iletişim. Karmaşık cümleler."
        "B2" -> "Akademik/iş dili. Soyut kavramlar ve resmi yazışma."
        "C1" -> "Geniş kelime dağarcığı, nüans ve akıcılık."
        else -> ""
    }
}


