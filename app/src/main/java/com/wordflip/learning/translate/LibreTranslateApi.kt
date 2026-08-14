package com.wordflip.learning.translate

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

@Serializable
data class TranslateRequest(
    val q: String,
    val source: String = "auto",
    val target: String,
    val format: String = "text",
    @SerialName("api_key") val apiKey: String = "",
)

@Serializable
data class TranslateResponse(
    val translatedText: String,
    val detectedLanguage: DetectedLanguage? = null,
)

@Serializable
data class DetectedLanguage(
    val confidence: Double = 0.0,
    val language: String = "",
)

@Serializable
data class Language(
    val code: String,
    val name: String,
    val targets: List<String> = emptyList(),
)

interface LibreTranslateApi {

    @POST("translate")
    suspend fun translate(@Body body: TranslateRequest): TranslateResponse

    @GET("languages")
    suspend fun languages(): List<Language>
}
