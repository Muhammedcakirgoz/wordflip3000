package com.wordflip.learning.translate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TranslateUiState(
    val isLoading: Boolean = false,
    val result: String = "",
    val errorMessage: String? = null,
    val languages: List<Language> = FALLBACK_LANGUAGES,
) {
    companion object {
        // /languages çağrısı başarısız olursa ekran boş kalmasın diye asgari liste.
        val FALLBACK_LANGUAGES = listOf(
            Language(code = "en", name = "English"),
            Language(code = "tr", name = "Turkish"),
            Language(code = "de", name = "German"),
            Language(code = "fr", name = "French"),
            Language(code = "es", name = "Spanish"),
        )
    }
}

class TranslateViewModel(
    private val repository: TranslateRepository = TranslateRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(TranslateUiState())
    val uiState: StateFlow<TranslateUiState> = _uiState.asStateFlow()

    init {
        loadLanguages()
    }

    fun loadLanguages() {
        viewModelScope.launch {
            repository.supportedLanguages().onSuccess { languages ->
                if (languages.isNotEmpty()) {
                    _uiState.update { it.copy(languages = languages) }
                }
            }
            // Başarısızlıkta FALLBACK_LANGUAGES zaten ekranda; kullanıcıyı hata ile bölmüyoruz.
        }
    }

    fun translate(text: String, target: String, source: String = "auto") {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.translateText(text = trimmed, source = source, target = target)
                .onSuccess { translated ->
                    _uiState.update { it.copy(isLoading = false, result = translated) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }
}
