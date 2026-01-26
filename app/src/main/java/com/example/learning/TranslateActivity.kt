package com.example.learning

import com.example.learning.R
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

class TranslateActivity : AppCompatActivity() {
    private var enTr: Translator? = null
    private var trEn: Translator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translate)

        val etSource = findViewById<EditText>(R.id.etSource)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val btnEnTr = findViewById<Button>(R.id.btnEnTr)
        val btnTrEn = findViewById<Button>(R.id.btnTrEn)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        btnEnTr.setOnClickListener {
            val text = etSource.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener
            val t = getEnTr()
            btnEnTr.isEnabled = false
            t.downloadModelIfNeeded()
                .addOnSuccessListener {
                    t.translate(text)
                        .addOnSuccessListener { out -> tvResult.text = out }
                        .addOnFailureListener { Toast.makeText(this, "Çeviri başarısız", Toast.LENGTH_SHORT).show() }
                        .addOnCompleteListener { btnEnTr.isEnabled = true }
                }
                .addOnFailureListener { btnEnTr.isEnabled = true }
        }

        btnTrEn.setOnClickListener {
            val text = etSource.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener
            val t = getTrEn()
            btnTrEn.isEnabled = false
            t.downloadModelIfNeeded()
                .addOnSuccessListener {
                    t.translate(text)
                        .addOnSuccessListener { out -> tvResult.text = out }
                        .addOnFailureListener { Toast.makeText(this, "Çeviri başarısız", Toast.LENGTH_SHORT).show() }
                        .addOnCompleteListener { btnTrEn.isEnabled = true }
                }
                .addOnFailureListener { btnTrEn.isEnabled = true }
        }
    }

    private fun getEnTr(): Translator {
        if (enTr == null) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.TURKISH)
                .build()
            enTr = Translation.getClient(options)
        }
        return enTr!!
    }

    private fun getTrEn(): Translator {
        if (trEn == null) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.TURKISH)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build()
            trEn = Translation.getClient(options)
        }
        return trEn!!
    }

    override fun onDestroy() {
        super.onDestroy()
        try { enTr?.close() } catch (_: Exception) {}
        try { trEn?.close() } catch (_: Exception) {}
    }
}


