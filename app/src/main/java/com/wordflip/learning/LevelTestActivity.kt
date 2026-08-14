package com.wordflip.learning

import com.wordflip.learning.R
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wordflip.learning.database.QuestionDao
import com.wordflip.learning.utils.LocaleHelper

class LevelTestActivity : AppCompatActivity() {

    private lateinit var questionText: TextView
    private lateinit var optionA: Button
    private lateinit var optionB: Button
    private lateinit var optionC: Button
    private lateinit var optionD: Button
    private lateinit var progress: ProgressBar

    private lateinit var questions: List<LevelQuestion>
    private val QUESTIONS_PER_TEST = 7
    private val PREF_LAST_IDS = "level_test_last_ids"
    private val prefs by lazy { getSharedPreferences("level_test", Context.MODE_PRIVATE) }
    private var currentIndex = 0
    private var score = 0

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_test)

        questionText = findViewById(R.id.tvQuestion)
        optionA = findViewById(R.id.btnA)
        optionB = findViewById(R.id.btnB)
        optionC = findViewById(R.id.btnC)
        optionD = findViewById(R.id.btnD)
        progress = findViewById(R.id.progress)

        listOf(optionA, optionB, optionC, optionD).forEachIndexed { idx, button ->
            button.setOnClickListener { onAnswer(idx) }
        }

        val db = QuestionDao(this)
        val quotasByLevel = mapOf("A1" to 2, "A2" to 1, "B1" to 2, "B2" to 1, "C1" to 1)
        val sample = db.getBalancedSample(QUESTIONS_PER_TEST, quotasByLevel)
        questions = sample.map { LevelQuestion(it.id, it.prompt, it.options, it.correctIndex, it.weight) }
        db.markUsed(sample.map { it.id })
        render()
    }

    private fun render() {
        val q = questions[currentIndex]
        questionText.text = q.prompt
        optionA.text = q.options[0]
        optionB.text = q.options[1]
        optionC.text = q.options[2]
        optionD.text = q.options[3]
        progress.max = questions.size
        progress.progress = currentIndex + 1
    }

    private fun onAnswer(selectedIndex: Int) {
        val q = questions[currentIndex]
        if (selectedIndex == q.correctIndex) score += q.weight
        if (currentIndex < questions.lastIndex) {
            currentIndex += 1
            render()
        } else {
            showResultScreen()
        }
    }

    private fun showResultScreen() {
        val level = when {
            score >= 28 -> "C1"
            score >= 22 -> "B2"
            score >= 16 -> "B1"
            score >= 10 -> "A2"
            else -> "A1"
        }

        val intent = android.content.Intent(this, LevelTestResultActivity::class.java)
        intent.putExtra("computed_level", level)
        intent.putExtra("score", score)
        startActivity(intent)
        finish()
    }

    // DB'den geldiği için havuz inşası kaldırıldı

    private fun loadLastQuestionIds(): Set<String> {
        val raw = prefs.getString(PREF_LAST_IDS, "") ?: ""
        if (raw.isBlank()) return emptySet()
        return raw.split(',').filter { it.isNotBlank() }.toSet()
    }

    private fun saveLastQuestionIds(list: List<LevelQuestion>) {
        val value = list.joinToString(",") { it.id }
        prefs.edit().putString(PREF_LAST_IDS, value).apply()
    }

    private fun pickQuestions(pool: List<LevelQuestion>, count: Int): List<LevelQuestion> {
        if (pool.isEmpty()) return emptyList()
        val last = loadLastQuestionIds()
        val filtered = pool.filter { it.id !in last }
        val first = filtered.shuffled().take(count)
        val selected = if (first.size < count) {
            val remainingNeeded = count - first.size
            val remainingPool = pool.filter { q -> first.none { it.id == q.id } }
            first + remainingPool.shuffled().take(remainingNeeded)
        } else first

        // Try to avoid identical set as last run
        val selectedIds = selected.map { it.id }.toSet()
        if (selectedIds == last && pool.size > count) {
            // force a different sample
            val alt = pool.shuffled().take(count)
            return alt
        }
        return selected
    }
}

data class LevelQuestion(
    val id: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val weight: Int
)


