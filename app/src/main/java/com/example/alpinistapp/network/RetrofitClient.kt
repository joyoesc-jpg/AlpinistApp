package com.example.alpinistapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.alpinistapp.ApiService


object RetrofitClient {

    private const val BASE_URL =
        "https://alpinist-backend.onrender.com/"

    val api: ApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ApiService::class.java)

    }
}