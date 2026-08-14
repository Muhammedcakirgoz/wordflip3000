package com.wordflip.learning.translate

import com.wordflip.learning.BuildConfig
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

/** Kullanıcıya gösterilebilir Türkçe mesaj taşıyan tek hata tipi. */
class TranslateException(message: String, cause: Throwable? = null) : Exception(message, cause)

class TranslateRepository(
    private val api: LibreTranslateApi = TranslateNetwork.api,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    // Aynı metin+dil çifti için tekrar API çağrısı yapılmasın diye erişim sıralı LRU cache.
    private val cache = object : LinkedHashMap<String, String>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>) =
            size > MAX_CACHE_ENTRIES
    }

    suspend fun translateText(
        text: String,
        source: String = "auto",
        target: String,
    ): Result<String> = withContext(ioDispatcher) {
        val cacheKey = "$source|$target|$text"
        synchronized(cache) { cache[cacheKey] }?.let { return@withContext Result.success(it) }

        try {
            val response = api.translate(
                TranslateRequest(
                    q = text,
                    source = source,
                    target = target,
                    apiKey = BuildConfig.LIBRETRANSLATE_API_KEY,
                )
            )
            synchronized(cache) { cache[cacheKey] = response.translatedText }
            Result.success(response.translatedText)
        } catch (e: CancellationException) {
            throw e // coroutine iptali hata değildir, yutulmamalı
        } catch (e: Exception) {
            Result.failure(e.toTranslateException())
        }
    }

    suspend fun supportedLanguages(): Result<List<Language>> = withContext(ioDispatcher) {
        try {
            Result.success(api.languages())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toTranslateException())
        }
    }

    private fun Throwable.toTranslateException(): TranslateException {
        val message = when (this) {
            is SocketTimeoutException ->
                "İstek zaman aşımına uğradı. Bağlantınızı kontrol edip tekrar deneyin."
            is UnknownHostException ->
                "Sunucuya ulaşılamadı. İnternet bağlantınızı kontrol edin."
            is HttpException -> when (code()) {
                400 -> "Geçersiz istek. Dil seçimini kontrol edin."
                401, 403 -> "API anahtarı eksik veya geçersiz."
                429 -> "Çok fazla istek gönderildi. Lütfen biraz bekleyip tekrar deneyin."
                in 500..599 -> "Sunucu hatası oluştu. Daha sonra tekrar deneyin."
                else -> "Sunucu beklenmeyen bir yanıt döndü (HTTP ${code()})."
            }
            is SerializationException ->
                "Sunucudan beklenmeyen bir yanıt alındı."
            is IOException ->
                "Ağ hatası oluştu. Bağlantınızı kontrol edin."
            else ->
                "Bilinmeyen bir hata oluştu."
        }
        return TranslateException(message, this)
    }

    private companion object {
        const val MAX_CACHE_ENTRIES = 100
    }
}
