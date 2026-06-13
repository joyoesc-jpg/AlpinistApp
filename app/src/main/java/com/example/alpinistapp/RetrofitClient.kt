package com.example.alpinistapp

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String)

data class LoginResponse(val success: Boolean, val message: String, val user: UserNetworkData?)
data class RegisterResponse(val success: Boolean, val message: String, val user: UserNetworkData?)
data class UserNetworkData(val user_id: Int, val name: String, val email: String)

interface AlpinistApiService {
    @GET("api/trails")
    suspend fun getTrails(): List<Trail>

    // 👈 ESTA LÍNEA ES LA QUE LE FALTA A TU ARCHIVO PARA RESOLVER EL ERROR EN HOMESCREEN
    @GET("api/expeditions")
    suspend fun getExpeditions(): List<Expedition>

    @POST("api/login")
    suspend fun login_user(@Body request: LoginRequest): LoginResponse

    @POST("api/register")
    suspend fun register_user(@Body request: RegisterRequest): RegisterResponse
}


object RetrofitClient {
    private const val BASE_URL = "https://alpinist-api.onrender.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: AlpinistApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AlpinistApiService::class.java)
    }
}