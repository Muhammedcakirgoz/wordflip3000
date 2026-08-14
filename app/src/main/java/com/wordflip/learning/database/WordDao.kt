package com.wordflip.learning.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class WordDao(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "wordflip.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_WORDS = "words"
        
        // Columns
        private const val COLUMN_ID = "id"
        private const val COLUMN_ENGLISH = "english"
        private const val COLUMN_TURKISH = "turkish"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_LEVEL = "level"
        private const val COLUMN_PHONETIC = "phonetic"
        private const val COLUMN_EXAMPLE = "example_sentence"
        private const val COLUMN_IS_LEARNED = "is_learned"
        private const val COLUMN_CORRECT_COUNT = "correct_count"
        private const val COLUMN_WRONG_COUNT = "wrong_count"
        private const val COLUMN_LAST_REVIEWED = "last_reviewed"
        // Spaced Repetition (SM-2) columns
        private const val COLUMN_REVIEW_SCORE = "review_score" // last quality (0-5)
        private const val COLUMN_NEXT_REVIEW_DATE = "next_review_date" // epoch millis
        private const val COLUMN_REVIEW_INTERVAL = "review_interval" // days
        private const val COLUMN_REVIEW_COUNT = "review_count" // repetition count
        private const val COLUMN_REVIEW_EASE = "review_ease" // ease factor
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_WORDS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ENGLISH TEXT NOT NULL,
                $COLUMN_TURKISH TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_LEVEL TEXT NOT NULL,
                $COLUMN_PHONETIC TEXT,
                $COLUMN_EXAMPLE TEXT,
                $COLUMN_IS_LEARNED INTEGER DEFAULT 0,
                $COLUMN_CORRECT_COUNT INTEGER DEFAULT 0,
                $COLUMN_WRONG_COUNT INTEGER DEFAULT 0,
                $COLUMN_LAST_REVIEWED INTEGER DEFAULT 0,
                $COLUMN_REVIEW_SCORE INTEGER DEFAULT 0,
                $COLUMN_NEXT_REVIEW_DATE INTEGER DEFAULT 0,
                $COLUMN_REVIEW_INTERVAL INTEGER DEFAULT 1,
                $COLUMN_REVIEW_COUNT INTEGER DEFAULT 0,
                $COLUMN_REVIEW_EASE REAL DEFAULT 2.5
            )
        """.trimIndent()
        
        db.execSQL(createTable)
        insertInitialWords(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE $TABLE_WORDS ADD COLUMN $COLUMN_REVIEW_SCORE INTEGER DEFAULT 0")
            } catch (_: Exception) {}
            try {
                db.execSQL("ALTER TABLE $TABLE_WORDS ADD COLUMN $COLUMN_NEXT_REVIEW_DATE INTEGER DEFAULT 0")
            } catch (_: Exception) {}
            try {
                db.execSQL("ALTER TABLE $TABLE_WORDS ADD COLUMN $COLUMN_REVIEW_INTERVAL INTEGER DEFAULT 1")
            } catch (_: Exception) {}
            try {
                db.execSQL("ALTER TABLE $TABLE_WORDS ADD COLUMN $COLUMN_REVIEW_COUNT INTEGER DEFAULT 0")
            } catch (_: Exception) {}
            try {
                db.execSQL("ALTER TABLE $TABLE_WORDS ADD COLUMN $COLUMN_REVIEW_EASE REAL DEFAULT 2.5")
            } catch (_: Exception) {}
        }
    }

    fun insertWord(word: Word): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ENGLISH, word.english)
            put(COLUMN_TURKISH, word.turkish)
            put(COLUMN_CATEGORY, word.category)
            put(COLUMN_LEVEL, word.level)
            put(COLUMN_PHONETIC, word.phonetic)
            put(COLUMN_EXAMPLE, word.exampleSentence)
            put(COLUMN_IS_LEARNED, if (word.isLearned) 1 else 0)
            put(COLUMN_CORRECT_COUNT, word.correctCount)
            put(COLUMN_WRONG_COUNT, word.wrongCount)
            put(COLUMN_LAST_REVIEWED, word.lastReviewed)
            put(COLUMN_REVIEW_SCORE, word.reviewScore)
            put(COLUMN_NEXT_REVIEW_DATE, word.nextReviewDate)
            put(COLUMN_REVIEW_INTERVAL, word.reviewInterval)
            put(COLUMN_REVIEW_COUNT, word.reviewCount)
            // ease left default
        }
        return db.insert(TABLE_WORDS, null, values)
    }

    fun getWordsByLevel(level: String, sortOrder: String = "random"): List<Word> {
        val words = mutableListOf<Word>()
        val db = readableDatabase
        
        val orderBy = when (sortOrder) {
            "random" -> "RANDOM()"
            "least_learned" -> "$COLUMN_CORRECT_COUNT ASC, $COLUMN_WRONG_COUNT DESC"
            "recent_study" -> "$COLUMN_LAST_REVIEWED DESC"
            else -> "RANDOM()"
        }
        
        val cursor = db.query(
            TABLE_WORDS,
            null,
            "$COLUMN_LEVEL = ?",
            arrayOf(level),
            null,
            null,
            orderBy
        )

        with(cursor) {
            while (moveToNext()) {
                words.add(
                    Word(
                        id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                        english = getString(getColumnIndexOrThrow(COLUMN_ENGLISH)),
                        turkish = getString(getColumnIndexOrThrow(COLUMN_TURKISH)),
                        category = getString(getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        level = getString(getColumnIndexOrThrow(COLUMN_LEVEL)),
                        phonetic = getString(getColumnIndexOrThrow(COLUMN_PHONETIC)),
                        exampleSentence = getString(getColumnIndexOrThrow(COLUMN_EXAMPLE)),
                        isLearned = getInt(getColumnIndexOrThrow(COLUMN_IS_LEARNED)) == 1,
                        correctCount = getInt(getColumnIndexOrThrow(COLUMN_CORRECT_COUNT)),
                        wrongCount = getInt(getColumnIndexOrThrow(COLUMN_WRONG_COUNT)),
                        lastReviewed = getLong(getColumnIndexOrThrow(COLUMN_LAST_REVIEWED)),
                        reviewScore = getInt(getColumnIndexOrThrow(COLUMN_REVIEW_SCORE)),
                        nextReviewDate = getLong(getColumnIndexOrThrow(COLUMN_NEXT_REVIEW_DATE)),
                        reviewInterval = getInt(getColumnIndexOrThrow(COLUMN_REVIEW_INTERVAL)),
                        reviewCount = getInt(getColumnIndexOrThrow(COLUMN_REVIEW_COUNT))
                    )
                )
            }
        }
        cursor.close()
        return words
    }

    fun getWordsByCategory(category: String, sortOrder: String = "random"): List<Word> {
        val words = mutableListOf<Word>()
        val db = readableDatabase
        
        val orderBy = when (sortOrder) {
            "random" -> "RANDOM()"
            "least_learned" -> "$COLUMN_CORRECT_COUNT ASC, $COLUMN_WRONG_COUNT DESC"
            "recent_study" -> "$COLUMN_LAST_REVIEWED DESC"
            else -> "RANDOM()"
        }
        
        val cursor = db.query(
            TABLE_WORDS,
            null,
            "$COLUMN_CATEGORY = ?",
            arrayOf(category),
            null,
            null,
            orderBy
        )

        with(cursor) {
            while (moveToNext()) {
                words.add(
                    Word(
                        id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                        english = getString(getColumnIndexOrThrow(COLUMN_ENGLISH)),
                        turkish = getString(getColumnIndexOrThrow(COLUMN_TURKISH)),
                        category = getString(getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        level = getString(getColumnIndexOrThrow(COLUMN_LEVEL)),
                        phonetic = getString(getColumnIndexOrThrow(COLUMN_PHONETIC)),
                        exampleSentence = getString(getColumnIndexOrThrow(COLUMN_EXAMPLE)),
                        isLearned = getInt(getColumnIndexOrThrow(COLUMN_IS_LEARNED)) == 1,
                        correctCount = getInt(getColumnIndexOrThrow(COLUMN_CORRECT_COUNT)),
                        wrongCount = getInt(getColumnIndexOrThrow(COLUMN_WRONG_COUNT)),
                        lastReviewed = getLong(getColumnIndexOrThrow(COLUMN_LAST_REVIEWED)),
                        reviewScore = getInt(getColumnIndexOrThrow(COLUMN_REVIEW_SCORE)),
                        nextReviewDate = getLong(getColumnIndexOrThrow(COLUMN_NEXT_REVIEW_DATE)),
                        reviewInterval = getInt(getColumnIndexOrThrow(COLUMN_REVIEW_INTERVAL)),
                        reviewCount = getInt(getColumnIndexOrThrow(COLUMN_REVIEW_COUNT))
                    )
                )
            }
        }
        cursor.close()
        return words
    }

    fun applyReviewResult(wordId: Int, wasCorrect: Boolean, qualityOverride: Int? = null) {
        val now = System.currentTimeMillis()
        val db = writableDatabase
        // Read current SRS state
        val cursor = db.query(
            TABLE_WORDS,
            arrayOf(COLUMN_REVIEW_COUNT, COLUMN_REVIEW_INTERVAL, COLUMN_REVIEW_EASE),
            "$COLUMN_ID = ?",
            arrayOf(wordId.toString()),
            null,
            null,
            null
        )
        var repetition = 0
        var interval = 1
        var ease = 2.5
        if (cursor.moveToFirst()) {
            repetition = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_COUNT))
            interval = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_INTERVAL))
            ease = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_REVIEW_EASE))
        }
        cursor.close()

        // Map to SM-2 quality (0..5)
        val quality = qualityOverride ?: if (wasCorrect) 4 else 2

        // SM-2 algorithm
        if (quality >= 3) {
            if (repetition == 0) {
                interval = 1
            } else if (repetition == 1) {
                interval = 6
            } else {
                interval = kotlin.math.round((interval * ease)).toInt().coerceAtLeast(1)
            }
            repetition += 1
        } else {
            repetition = 0
            interval = 1
        }

        var newEase = ease + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))
        if (newEase < 1.3) newEase = 1.3

        val nextReview = now + interval * 24L * 60L * 60L * 1000L

        // Update counts and SRS fields
        if (wasCorrect) {
            db.execSQL(
                "UPDATE $TABLE_WORDS SET $COLUMN_CORRECT_COUNT = $COLUMN_CORRECT_COUNT + 1, " +
                "$COLUMN_LAST_REVIEWED = $now, " +
                "$COLUMN_IS_LEARNED = CASE WHEN $COLUMN_CORRECT_COUNT + 1 >= 3 THEN 1 ELSE $COLUMN_IS_LEARNED END, " +
                "$COLUMN_REVIEW_SCORE = $quality, $COLUMN_REVIEW_COUNT = $repetition, $COLUMN_REVIEW_INTERVAL = $interval, $COLUMN_NEXT_REVIEW_DATE = $nextReview, $COLUMN_REVIEW_EASE = $newEase " +
                "WHERE $COLUMN_ID = ?",
                arrayOf(wordId.toString())
            )
        } else {
            db.execSQL(
                "UPDATE $TABLE_WORDS SET $COLUMN_WRONG_COUNT = $COLUMN_WRONG_COUNT + 1, " +
                "$COLUMN_LAST_REVIEWED = $now, " +
                "$COLUMN_REVIEW_SCORE = $quality, $COLUMN_REVIEW_COUNT = $repetition, $COLUMN_REVIEW_INTERVAL = $interval, $COLUMN_NEXT_REVIEW_DATE = $nextReview, $COLUMN_REVIEW_EASE = $newEase " +
                "WHERE $COLUMN_ID = ?",
                arrayOf(wordId.toString())
            )
        }
    }

    fun getWordsForStudy(level: String, targetCount: Int): List<Word> {
        val result = mutableListOf<Word>()
        val db = readableDatabase
        val now = System.currentTimeMillis()

        fun mapCursor(c: android.database.Cursor, list: MutableList<Word>) {
            with(c) {
                while (moveToNext()) {
                    list.add(
                        Word(
                            id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                            english = getString(getColumnIndexOrThrow(COLUMN_ENGLISH)),
                            turkish = getString(getColumnIndexOrThrow(COLUMN_TURKISH)),
                            category = getString(getColumnIndexOrThrow(COLUMN_CATEGORY)),
                            level = getString(getColumnIndexOrThrow(COLUMN_LEVEL)),
                            phonetic = getString(getColumnIndexOrThrow(COLUMN_PHONETIC)),
                            exampleSentence = getString(getColumnIndexOrThrow(COLUMN_EXAMPLE)),
                            isLearned = getInt(getColumnIndexOrThrow(COLUMN_IS_LEARNED)) == 1,
                            correctCount = getInt(getColumnIndexOrThrow(COLUMN_CORRECT_COUNT)),
                            wrongCount = getInt(getColumnIndexOrThrow(COLUMN_WRONG_COUNT)),
                            lastReviewed = getLong(getColumnIndexOrThrow(COLUMN_LAST_REVIEWED)),
                            reviewScore = getInt(getColumnIndexOrThrow(COLUMN_REVIEW_SCORE)),
                            nextReviewDate = getLong(getColumnIndexOrThrow(COLUMN_NEXT_REVIEW_DATE)),
                            reviewInterval = getInt(getColumnIndexOrThrow(COLUMN_REVIEW_INTERVAL)),
                            reviewCount = getInt(getColumnIndexOrThrow(COLUMN_REVIEW_COUNT))
                        )
                    )
                }
            }
        }

        // 1) Due words first
        val dueCursor = db.query(
            TABLE_WORDS,
            null,
            "$COLUMN_LEVEL = ? AND ($COLUMN_NEXT_REVIEW_DATE = 0 OR $COLUMN_NEXT_REVIEW_DATE <= ?)",
            arrayOf(level, now.toString()),
            null,
            null,
            "$COLUMN_NEXT_REVIEW_DATE ASC",
            targetCount.toString()
        )
        mapCursor(dueCursor, result)
        dueCursor.close()

        if (result.size < targetCount) {
            // 2) Then hardest words
            val remaining = (targetCount - result.size).toString()
            val hardCursor = db.query(
                TABLE_WORDS,
                null,
                "$COLUMN_LEVEL = ?",
                arrayOf(level),
                null,
                null,
                "$COLUMN_WRONG_COUNT DESC, $COLUMN_CORRECT_COUNT ASC",
                remaining
            )
            mapCursor(hardCursor, result)
            hardCursor.close()
        }

        if (result.size < targetCount) {
            // 3) Then new/unseen words
            val remaining = (targetCount - result.size).toString()
            val newCursor = db.query(
                TABLE_WORDS,
                null,
                "$COLUMN_LEVEL = ? AND $COLUMN_LAST_REVIEWED = 0",
                arrayOf(level),
                null,
                null,
                "RANDOM()",
                remaining
            )
            mapCursor(newCursor, result)
            newCursor.close()
        }

        return result.take(targetCount)
    }

    fun getProgressStats(): Map<String, Int> {
        val db = readableDatabase
        val stats = mutableMapOf<String, Int>()
        
        val totalCursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_WORDS", null)
        totalCursor.moveToFirst()
        stats["total"] = totalCursor.getInt(0)
        totalCursor.close()
        
        val learnedCursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_WORDS WHERE $COLUMN_IS_LEARNED = 1", null)
        learnedCursor.moveToFirst()
        stats["learned"] = learnedCursor.getInt(0)
        learnedCursor.close()
        
        return stats
    }

    /**
     * Esnek öğrenilmiş sayacı: en az [minCorrect] doğruya ulaşmış kelimeleri sayar.
     * Varsayılan mantık (IS_LEARNED) 3 doğru gerektirdiğinden, ilerlemeyi daha erken göstermek için kullanılabilir.
     */
    fun getLearnedCount(minCorrect: Int = 1): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_WORDS WHERE $COLUMN_CORRECT_COUNT >= ?",
            arrayOf(minCorrect.toString())
        )
        return try {
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        } finally {
            cursor.close()
        }
    }

    fun resetAllProgress(): Boolean {
        return try {
            val db = writableDatabase
            
            // Tüm ilerleme verilerini sıfırla
            val resetQuery = """
                UPDATE $TABLE_WORDS SET 
                $COLUMN_IS_LEARNED = 0,
                $COLUMN_CORRECT_COUNT = 0,
                $COLUMN_WRONG_COUNT = 0,
                $COLUMN_LAST_REVIEWED = 0,
                $COLUMN_REVIEW_SCORE = 0,
                $COLUMN_NEXT_REVIEW_DATE = 0,
                $COLUMN_REVIEW_INTERVAL = 1,
                $COLUMN_REVIEW_COUNT = 0,
                $COLUMN_REVIEW_EASE = 2.5
            """.trimIndent()
            
            db.execSQL(resetQuery)
            true
        } catch (e: Exception) {
            android.util.Log.e("WordDao", "Error resetting progress: ${e.message}")
            false
        }
    }
    
    /**
     * Google Play yayını için kullanıcı ilerlemesini sıfırlar
     */
    fun resetUserProgress(): Boolean {
        return resetAllProgress()
    }
    
    /**
     * Sadece kullanıcı ilerlemesini sıfırlar, kelime veritabanını korur
     */
    fun resetUserProgressOnly(): Boolean {
        return resetAllProgress()
    }

    private fun insertInitialWords(db: SQLiteDatabase) {
        // Oxford 3000'den seçilmiş temel kelimeler
        val initialWords = listOf(
            // A1 Level - Temel kelimeler
            Word(english = "apple", turkish = "elma", category = "noun", level = "A1", phonetic = "ˈæpəl"),
            Word(english = "book", turkish = "kitap", category = "noun", level = "A1", phonetic = "bʊk"),
            Word(english = "house", turkish = "ev", category = "noun", level = "A1", phonetic = "haʊs"),
            Word(english = "water", turkish = "su", category = "noun", level = "A1", phonetic = "ˈwɔːtər"),
            Word(english = "food", turkish = "yemek", category = "noun", level = "A1", phonetic = "fuːd"),
            Word(english = "friend", turkish = "arkadaş", category = "noun", level = "A1", phonetic = "frend"),
            Word(english = "family", turkish = "aile", category = "noun", level = "A1", phonetic = "ˈfæməli"),
            Word(english = "school", turkish = "okul", category = "noun", level = "A1", phonetic = "skuːl"),
            Word(english = "work", turkish = "iş", category = "noun", level = "A1", phonetic = "wɜːrk"),
            Word(english = "time", turkish = "zaman", category = "noun", level = "A1", phonetic = "taɪm"),
            
            // A1 Verbs
            Word(english = "go", turkish = "gitmek", category = "verb", level = "A1", phonetic = "ɡoʊ"),
            Word(english = "come", turkish = "gelmek", category = "verb", level = "A1", phonetic = "kʌm"),
            Word(english = "see", turkish = "görmek", category = "verb", level = "A1", phonetic = "siː"),
            Word(english = "eat", turkish = "yemek", category = "verb", level = "A1", phonetic = "iːt"),
            Word(english = "drink", turkish = "içmek", category = "verb", level = "A1", phonetic = "drɪŋk"),
            Word(english = "sleep", turkish = "uyumak", category = "verb", level = "A1", phonetic = "sliːp"),
            Word(english = "run", turkish = "koşmak", category = "verb", level = "A1", phonetic = "rʌn"),
            Word(english = "walk", turkish = "yürümek", category = "verb", level = "A1", phonetic = "wɔːk"),
            Word(english = "read", turkish = "okumak", category = "verb", level = "A1", phonetic = "riːd"),
            Word(english = "write", turkish = "yazmak", category = "verb", level = "A1", phonetic = "raɪt"),
            
            // A1 Adjectives
            Word(english = "good", turkish = "iyi", category = "adjective", level = "A1", phonetic = "ɡʊd"),
            Word(english = "bad", turkish = "kötü", category = "adjective", level = "A1", phonetic = "bæd"),
            Word(english = "big", turkish = "büyük", category = "adjective", level = "A1", phonetic = "bɪɡ"),
            Word(english = "small", turkish = "küçük", category = "adjective", level = "A1", phonetic = "smɔːl"),
            Word(english = "hot", turkish = "sıcak", category = "adjective", level = "A1", phonetic = "hɑːt"),
            Word(english = "cold", turkish = "soğuk", category = "adjective", level = "A1", phonetic = "koʊld"),
            Word(english = "happy", turkish = "mutlu", category = "adjective", level = "A1", phonetic = "ˈhæpi"),
            Word(english = "sad", turkish = "üzgün", category = "adjective", level = "A1", phonetic = "sæd"),
            Word(english = "new", turkish = "yeni", category = "adjective", level = "A1", phonetic = "nuː"),
            Word(english = "old", turkish = "eski", category = "adjective", level = "A1", phonetic = "oʊld"),
            
            // A2 Level
            Word(english = "adventure", turkish = "macera", category = "noun", level = "A2", phonetic = "ədˈventʃər"),
            Word(english = "advice", turkish = "tavsiye", category = "noun", level = "A2", phonetic = "ədˈvaɪs"),
            Word(english = "airport", turkish = "havaalanı", category = "noun", level = "A2", phonetic = "ˈerˌpɔːrt"),
            Word(english = "animal", turkish = "hayvan", category = "noun", level = "A2", phonetic = "ˈænɪməl"),
            Word(english = "answer", turkish = "cevap", category = "noun", level = "A2", phonetic = "ˈænsər"),
            Word(english = "apartment", turkish = "daire", category = "noun", level = "A2", phonetic = "əˈpɑːrtmənt"),
            Word(english = "arrive", turkish = "varmak", category = "verb", level = "A2", phonetic = "əˈraɪv"),
            Word(english = "artist", turkish = "sanatçı", category = "noun", level = "A2", phonetic = "ˈɑːrtɪst"),
            Word(english = "beautiful", turkish = "güzel", category = "adjective", level = "A2", phonetic = "ˈbjuːtɪfəl"),
            Word(english = "believe", turkish = "inanmak", category = "verb", level = "A2", phonetic = "bɪˈliːv"),
            
            // B1 Level
            Word(english = "absolutely", turkish = "kesinlikle", category = "adverb", level = "B1", phonetic = "ˈæbsəˌluːtli"),
            Word(english = "academic", turkish = "akademik", category = "adjective", level = "B1", phonetic = "ˌækəˈdemɪk"),
            Word(english = "accident", turkish = "kaza", category = "noun", level = "B1", phonetic = "ˈæksɪdənt"),
            Word(english = "achieve", turkish = "başarmak", category = "verb", level = "B1", phonetic = "əˈtʃiːv"),
            Word(english = "activity", turkish = "etkinlik", category = "noun", level = "B1", phonetic = "ækˈtɪvəti"),
            Word(english = "advantage", turkish = "avantaj", category = "noun", level = "B1", phonetic = "ədˈvæntɪdʒ"),
            Word(english = "amazing", turkish = "şaşırtıcı", category = "adjective", level = "B1", phonetic = "əˈmeɪzɪŋ"),
            Word(english = "approach", turkish = "yaklaşmak", category = "verb", level = "B1", phonetic = "əˈproʊtʃ"),
            Word(english = "arrange", turkish = "düzenlemek", category = "verb", level = "B1", phonetic = "əˈreɪndʒ"),
            Word(english = "available", turkish = "mevcut", category = "adjective", level = "B1", phonetic = "əˈveɪləbəl"),
            
            // B2 Level
            Word(english = "abandon", turkish = "terk etmek", category = "verb", level = "B2", phonetic = "əˈbændən"),
            Word(english = "ability", turkish = "yetenek", category = "noun", level = "B2", phonetic = "əˈbɪləti"),
            Word(english = "abroad", turkish = "yurtdışı", category = "adverb", level = "B2", phonetic = "əˈbrɔːd"),
            Word(english = "absorb", turkish = "emmek", category = "verb", level = "B2", phonetic = "əbˈzɔːrb"),
            Word(english = "abstract", turkish = "soyut", category = "adjective", level = "B2", phonetic = "ˈæbstrækt"),
            Word(english = "accelerate", turkish = "hızlandırmak", category = "verb", level = "B2", phonetic = "ækˈseləreɪt"),
            Word(english = "accomplish", turkish = "gerçekleştirmek", category = "verb", level = "B2", phonetic = "əˈkɑːmplɪʃ"),
            Word(english = "accurate", turkish = "doğru", category = "adjective", level = "B2", phonetic = "ˈækjərət"),
            Word(english = "acquire", turkish = "elde etmek", category = "verb", level = "B2", phonetic = "əˈkwaɪər"),
            Word(english = "adequate", turkish = "yeterli", category = "adjective", level = "B2", phonetic = "ˈædɪkwət")
        )
        
        initialWords.forEach { word ->
            val values = ContentValues().apply {
                put(COLUMN_ENGLISH, word.english)
                put(COLUMN_TURKISH, word.turkish)
                put(COLUMN_CATEGORY, word.category)
                put(COLUMN_LEVEL, word.level)
                put(COLUMN_PHONETIC, word.phonetic)
                put(COLUMN_EXAMPLE, word.exampleSentence)
                put(COLUMN_IS_LEARNED, 0)
                put(COLUMN_CORRECT_COUNT, 0)
                put(COLUMN_WRONG_COUNT, 0)
                put(COLUMN_LAST_REVIEWED, 0)
            }
            db.insert(TABLE_WORDS, null, values)
        }
    }
} 