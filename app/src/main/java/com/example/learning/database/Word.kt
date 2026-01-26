package com.example.learning.database

data class Word(
    val id: Int = 0,
    val english: String,
    val turkish: String,
    val category: String, // noun, verb, adjective, etc.
    val level: String,    // A1, A2, B1, B2, C1
    val phonetic: String? = null,
    val exampleSentence: String? = null,
    val isLearned: Boolean = false,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val lastReviewed: Long = 0,
    // 🧠 Spaced Repetition System
    val reviewScore: Int = 0,  // 0-5 arası (ne kadar iyi bilindiği)
    val nextReviewDate: Long = 0,  // Bir sonraki tekrar tarihi
    val reviewInterval: Int = 1,   // Gün cinsinden tekrar aralığı
    val reviewCount: Int = 0       // Kaç kez tekrar edildi
) 