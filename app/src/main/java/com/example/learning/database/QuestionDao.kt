package com.example.learning.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class DbQuestion(
    val id: String,
    val level: String,
    val category: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val weight: Int,
    val lastUsed: Long
)

class QuestionDao(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "wordflip.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_QUESTIONS = "questions"
        private const val COL_ID = "id"
        private const val COL_LEVEL = "level"
        private const val COL_CATEGORY = "category"
        private const val COL_PROMPT = "prompt"
        private const val COL_OPTIONS = "options"
        private const val COL_CORRECT_INDEX = "correct_index"
        private const val COL_WEIGHT = "weight"
        private const val COL_LAST_USED = "last_used"
    }

    private val createTableIfNotExists = """
        CREATE TABLE IF NOT EXISTS $TABLE_QUESTIONS (
            $COL_ID TEXT PRIMARY KEY,
            $COL_LEVEL TEXT NOT NULL,
            $COL_CATEGORY TEXT NOT NULL,
            $COL_PROMPT TEXT NOT NULL,
            $COL_OPTIONS TEXT NOT NULL,
            $COL_CORRECT_INDEX INTEGER NOT NULL,
            $COL_WEIGHT INTEGER NOT NULL DEFAULT 1,
            $COL_LAST_USED INTEGER NOT NULL DEFAULT 0
        )
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createTableIfNotExists)
        insertInitialQuestionsIfEmpty(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(createTableIfNotExists)
    }

    init {
        // Ensure table exists even if DB was created by another helper
        writableDatabase.execSQL(createTableIfNotExists)
        insertInitialQuestionsIfEmpty(writableDatabase)
    }

    private fun insertInitialQuestionsIfEmpty(db: SQLiteDatabase) {
        val c = db.rawQuery("SELECT COUNT(*) FROM $TABLE_QUESTIONS", null)
        val count = if (c.moveToFirst()) c.getInt(0) else 0
        c.close()
        if (count > 0) return

        val seed = listOf(
            // A1
            seed("a1_apple", "A1", "vocab", "'apple' kelimesinin Türkçe karşılığı nedir?", listOf("elma","armut","üzüm","kavun"), 0, 2),
            seed("a1_house", "A1", "vocab", "'house' nedir?", listOf("ev","iş","okul","kalem"), 0, 2),
            seed("a1_go", "A1", "grammar", "I ____ to school every day.", listOf("go","goes","went","am go"), 0, 2),
            seed("a1_drink", "A1", "vocab", "'drink' fiilinin anlamı hangisi?", listOf("içmek","uyumak","yazmak","koşmak"), 0, 2),
            // A2
            seed("a2_advice", "A2", "vocab", "'advice' sözcüğünün anlamı nedir?", listOf("tavsiye","ilan","alışveriş","örnek"), 0, 3),
            seed("a2_airport", "A2", "vocab", "'airport' ne demek?", listOf("havaalanı","otobüs","tren","liman"), 0, 3),
            seed("a2_arrive", "A2", "vocab", "'arrive' fiili nedir?", listOf("varmak","ayrılmak","beklemek","girmek"), 0, 3),
            seed("a2_believe", "A2", "vocab", "'believe' nedir?", listOf("inanmak","düşünmek","şüphe etmek","önermek"), 0, 3),
            // B1
            seed("b1_amazing_syn", "B1", "vocab", "Pick a synonym for 'amazing'", listOf("ordinary","boring","incredible","tiny"), 2, 4),
            seed("b1_approach", "B1", "vocab", "'approach' fiilinin anlamı hangisi?", listOf("yaklaşmak","kaçmak","unutmak","yapıştırmak"), 0, 4),
            seed("b1_conditional2", "B1", "grammar", "If I had time, I ____ travel more.", listOf("will","would","am","did"), 1, 5),
            seed("b1_available", "B1", "vocab", "'available' kelimesinin anlamı?", listOf("mevcut","kayıp","pahalı","yorgun"), 0, 4),
            // B2
            seed("b2_which_clause", "B2", "grammar", "The results, ____ were unexpected, changed our plan.", listOf("which","what","who","whom"), 0, 6),
            seed("b2_abstract", "B2", "vocab", "'abstract' kelimesi?", listOf("soyut","somut","sahte","düz"), 0, 5),
            seed("b2_accelerate", "B2", "vocab", "'accelerate' fiilinin anlamı?", listOf("hızlandırmak","yavaşlamak","hesaplamak","toplamak"), 0, 5),
            seed("b2_accurate", "B2", "vocab", "'accurate' yakın anlamlısı?", listOf("kesin","hızlı","uygun","geniş"), 0, 5),
            // C1
            seed("c1_nuance", "C1", "vocab", "'nuance' en yakın anlam?", listOf("ince fark","büyük değişim","gürültü","süs"), 0, 6),
            seed("c1_cohesion", "C1", "vocab", "'cohesion' ne demek?", listOf("uyum","kopuş","akış","dağınıklık"), 0, 6),
            seed("c1_concession", "C1", "grammar", "Choose best linking word: ____ he worked hard, he failed.", listOf("Although","Because","Unless","So"), 0, 6),
            seed("c1_idiom", "C1", "idiom", "Idiom: 'break the ice' ne demektir?", listOf("ortamı yumuşatmak","kavgayı başlatmak","hızla kaçmak","soğumak"), 0, 6),
            // Mixed
            seed("mix_collocation", "B2", "collocation", "Doğru kolokasyon hangisi?", listOf("make a decision","do a decision","make a homework","do a mistake"), 0, 5),
            seed("mix_phrasal", "B1", "phrasal", "'give up' ne demek?", listOf("vazgeçmek","devam etmek","bağırmak","çözmek"), 0, 4),
            seed("mix_tense", "B1", "grammar", "He ____ here since 2010.", listOf("has worked","worked","is working","works"), 0, 5),
            seed("mix_passive", "B1", "grammar", "The cake ____ by Mary.", listOf("was baked","baked","is bake","has bake"), 0, 5)
        )

        seed.forEach { insertQuestion(it) }
    }

    private fun seed(id: String, level: String, category: String, prompt: String, options: List<String>, correctIndex: Int, weight: Int): DbQuestion {
        return DbQuestion(id, level, category, prompt, options, correctIndex, weight, 0)
    }

    fun insertQuestion(q: DbQuestion): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ID, q.id)
            put(COL_LEVEL, q.level)
            put(COL_CATEGORY, q.category)
            put(COL_PROMPT, q.prompt)
            put(COL_OPTIONS, q.options.joinToString("||"))
            put(COL_CORRECT_INDEX, q.correctIndex)
            put(COL_WEIGHT, q.weight)
            put(COL_LAST_USED, q.lastUsed)
        }
        return db.insertWithOnConflict(TABLE_QUESTIONS, null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun getBalancedSample(totalCount: Int, quotasByLevel: Map<String, Int>): List<DbQuestion> {
        val db = readableDatabase
        val result = mutableListOf<DbQuestion>()
        quotasByLevel.forEach { (level, limit) ->
            val cursor = db.rawQuery(
                """
                    SELECT * FROM $TABLE_QUESTIONS
                    WHERE $COL_LEVEL = ?
                    ORDER BY CASE WHEN $COL_LAST_USED = 0 THEN 0 ELSE 1 END ASC,
                             $COL_LAST_USED ASC,
                             RANDOM()
                    LIMIT $limit
                """.trimIndent(),
                arrayOf(level)
            )
            result.addAll(mapCursor(cursor))
            cursor.close()
        }

        if (result.size < totalCount) {
            val remaining = totalCount - result.size
            val exclude = result.map { it.id }.toTypedArray()
            val whereNotIn = if (exclude.isNotEmpty()) {
                " AND $COL_ID NOT IN (${exclude.joinToString(",") { "?" }})"
            } else ""
            val args = if (exclude.isNotEmpty()) exclude else emptyArray()
            val cursor = db.rawQuery(
                """
                    SELECT * FROM $TABLE_QUESTIONS
                    WHERE 1=1 $whereNotIn
                    ORDER BY CASE WHEN $COL_LAST_USED = 0 THEN 0 ELSE 1 END ASC,
                             $COL_LAST_USED ASC,
                             RANDOM()
                    LIMIT $remaining
                """.trimIndent(),
                args
            )
            result.addAll(mapCursor(cursor))
            cursor.close()
        }

        return result.take(totalCount)
    }

    private fun mapCursor(c: Cursor): List<DbQuestion> {
        val list = mutableListOf<DbQuestion>()
        with(c) {
            while (moveToNext()) {
                val id = getString(getColumnIndexOrThrow(COL_ID))
                val level = getString(getColumnIndexOrThrow(COL_LEVEL))
                val category = getString(getColumnIndexOrThrow(COL_CATEGORY))
                val prompt = getString(getColumnIndexOrThrow(COL_PROMPT))
                val optionsStr = getString(getColumnIndexOrThrow(COL_OPTIONS))
                val options = optionsStr.split("||")
                val correctIndex = getInt(getColumnIndexOrThrow(COL_CORRECT_INDEX))
                val weight = getInt(getColumnIndexOrThrow(COL_WEIGHT))
                val lastUsed = getLong(getColumnIndexOrThrow(COL_LAST_USED))
                list.add(DbQuestion(id, level, category, prompt, options, correctIndex, weight, lastUsed))
            }
        }
        return list
    }

    fun markUsed(questionIds: List<String>) {
        if (questionIds.isEmpty()) return
        val now = System.currentTimeMillis()
        val db = writableDatabase
        db.beginTransaction()
        try {
            questionIds.forEach { id ->
                val values = ContentValues().apply { put(COL_LAST_USED, now) }
                db.update(TABLE_QUESTIONS, values, "$COL_ID = ?", arrayOf(id))
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
    
    /**
     * Google Play yayını için soru istatistiklerini sıfırlar
     */
    fun resetQuestionStats(): Boolean {
        return try {
            val db = writableDatabase
            
            // Tüm soruların last_used değerini sıfırla
            val values = ContentValues().apply {
                put(COL_LAST_USED, 0)
            }
            
            db.update(TABLE_QUESTIONS, values, null, null)
            true
        } catch (e: Exception) {
            android.util.Log.e("QuestionDao", "Error resetting question stats: ${e.message}")
            false
        }
    }
}


