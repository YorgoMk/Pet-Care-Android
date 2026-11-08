package com.usj.petcare.data.remote

import android.util.Log
import com.usj.petcare.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DogApiClient {

    private const val BASE_URL = "https://api.thedogapi.com/v1/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->

            val apiKey = BuildConfig.DOG_API_KEY.trim()

            Log.d("DogApiClient", "API key length: ${apiKey.length}")

            val request = chain.request()
                .newBuilder()
                .header("x-api-key", apiKey)
                .build()

            Log.d(
                "DogApiClient",
                "API key header attached: ${request.header("x-api-key") != null}"
            )

            chain.proceed(request)
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