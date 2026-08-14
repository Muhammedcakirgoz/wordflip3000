package com.wordflip.learning.translate

import com.wordflip.learning.BuildConfig
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object TranslateNetwork {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        // source/format/api_key default değerlerinde de gövdeye yazılsın;
        // LibreTranslate bu alanları bekliyor.
        encodeDefaults = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
        }
        .build()

    val api: LibreTranslateApi = Retrofit.Builder()
        .baseUrl(BuildConfig.LIBRETRANSLATE_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(LibreTranslateApi::class.java)
}
