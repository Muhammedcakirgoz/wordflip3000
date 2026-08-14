package com.wordflip.learning

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.wordflip.learning.databinding.ActivityTranslateBinding
import com.wordflip.learning.translate.Language
import com.wordflip.learning.translate.TranslateViewModel
import kotlinx.coroutines.launch

class TranslateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTranslateBinding
    private val viewModel: TranslateViewModel by viewModels()

    private var languages: List<Language> = emptyList()
    private var selectedTarget: String = "en"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.actTargetLang.setOnItemClickListener { _, _, position, _ ->
            selectedTarget = languages[position].code
        }

        binding.btnTranslate.setOnClickListener {
            viewModel.translate(
                text = binding.etSource.text?.toString().orEmpty(),
                target = selectedTarget,
            )
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressTranslate.isVisible = state.isLoading
                    binding.btnTranslate.isEnabled = !state.isLoading

                    if (state.languages != languages) {
                        languages = state.languages
                        bindLanguageDropdown(state.languages)
                    }

                    binding.tvResult.text = state.errorMessage ?: state.result
                }
            }
        }
    }

    private fun bindLanguageDropdown(languages: List<Language>) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            languages.map { "${it.name} (${it.code})" },
        )
        binding.actTargetLang.setAdapter(adapter)

        // Mevcut seçim yeni listede de varsa koru, yoksa ilk dile dön.
        val selected = languages.firstOrNull { it.code == selectedTarget } ?: languages.first()
        selectedTarget = selected.code
        binding.actTargetLang.setText("${selected.name} (${selected.code})", false)
    }
}
