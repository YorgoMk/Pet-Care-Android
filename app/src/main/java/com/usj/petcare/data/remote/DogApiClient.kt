package com.usj.petcare.data.remote

import com.usj.petcare.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Builds Retrofit once for TheDogAPI and adds the API key to every request. */
object DogApiClient {

    private const val BASE_URL = "https://api.thedogapi.com/v1/"

    // Logs method, URL and status code only, and only in debug builds.
    // BASIC never prints headers, so the API key does not appear in Logcat.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val apiKey = BuildConfig.DOG_API_KEY.trim()
            val request = chain.request().newBuilder()
            // Without a key the API answers 401; the ViewModel turns that into a clear message.
            if (apiKey.isNotEmpty()) {
                request.header("x-api-key", apiKey)
            }
            chain.proceed(request.build())
        }
        .addInterceptor(loggingInterceptor)
        .build()

    val service: DogApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DogApiService::class.java)
    }
}
