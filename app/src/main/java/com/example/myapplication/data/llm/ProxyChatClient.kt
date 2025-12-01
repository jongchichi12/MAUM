package com.example.myapplication.data.llm

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 서버 프록시를 통해 LLM을 호출한다.
 * - baseUrl은 BuildConfig.PROXY_BASE_URL 로 주입
 * - 실패 시 Result.failure 로 반환
 */
open class ProxyChatClient(
    baseUrl: String,
    apiKey: String? = null,
    client: OkHttpClient? = null,
    moshi: Moshi? = null
) {
    private val api: ProxyChatApi

    init {
        val m = moshi ?: Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

        val httpClient = client ?: OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.SECONDS)
            .writeTimeout(40, TimeUnit.SECONDS)
            .apply {
                apiKey?.let { key ->
                    addInterceptor(Interceptor { chain ->
                        val newReq = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer $key")
                            .build()
                        chain.proceed(newReq)
                    })
                }
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
                addInterceptor(logging)
            }
            .build()

        api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(m))
            .build()
            .create(ProxyChatApi::class.java)
    }

    open suspend fun send(messages: List<ChatMessage>): Result<String> = runCatching {
        val body = ProxyChatRequest(
            messages = messages.map { ProxyMessageDto(role = it.role.apiValue, content = it.content) }
        )
        api.sendChat(body).reply ?: throw IllegalStateException("Empty reply")
    }.onFailure { e ->
        if (e is HttpException) {
            Log.w("ProxyChatClient", "HTTP ${e.code()} while calling proxy", e)
        } else {
            Log.w("ProxyChatClient", "Proxy call failed", e)
        }
    }
}
