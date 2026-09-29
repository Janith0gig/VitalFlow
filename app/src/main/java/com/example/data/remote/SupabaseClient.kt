package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class SupabaseClient(private val config: SupabaseConfig) {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val key = config.supabaseAnonKey
        val requestBuilder = original.newBuilder()
            .header("apikey", key)
            .header("Authorization", "Bearer $key")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
        val request = requestBuilder.build()
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    fun getApi(): SupabaseApi {
        val baseUrl = if (config.supabaseUrl.endsWith("/")) config.supabaseUrl else "${config.supabaseUrl}/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SupabaseApi::class.java)
    }

    suspend fun testConnection(): Pair<Boolean, String> {
        return try {
            val response = getApi().getBloodBags(select = "id", order = "id.asc")
            if (response.isSuccessful) {
                Pair(true, "Successfully connected to Supabase PostgreSQL (HTTP ${response.code()})")
            } else {
                Pair(false, "Supabase returned HTTP ${response.code()}: ${response.message()}")
            }
        } catch (e: Exception) {
            Pair(false, "Connection error: ${e.localizedMessage ?: "Unknown network error"}")
        }
    }
}
