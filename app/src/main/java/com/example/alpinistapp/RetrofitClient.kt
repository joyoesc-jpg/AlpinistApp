package com.example.alpinistapp

import android.util.Log
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String)

data class LoginResponse(val success: Boolean, val message: String, val user: UserNetworkData?)
data class RegisterResponse(val success: Boolean, val message: String, val user: UserNetworkData?)
data class UserNetworkData(val user_id: Int, val name: String, val email: String)
data class TrailDetailsResponse(
    val distance: String,
    val elevationGain: String,
    val estimatedTime: String,
    val routeType: String,
    val recommendedGroup: String
)

data class ReviewRequest(
    val user_id: Int,
    val trail_id: Int?,
    val rating: Int,
    val comment: String
)

private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .addInterceptor { chain ->
        val request = chain.request()
        Log.d("RETROFIT", "Request: ${request.method} ${request.url}")
        val response = chain.proceed(request)
        Log.d("RETROFIT", "Response: ${response.code}")
        response
    }
    .build()

interface AlpinistApiService {
    @GET("api/trails")
    suspend fun getTrails(): List<Trail>

    @GET("api/expeditions")
    suspend fun getExpeditions(): List<Expedition>

    @POST("api/login")
    suspend fun login_user(@Body request: LoginRequest): LoginResponse

    @POST("api/register")
    suspend fun register_user(@Body request: RegisterRequest): RegisterResponse

    // 👇 NUEVAS LÍNEAS PARA LOS ENDPOINTS DE MIEMBROS Y DETALLES
    @GET("api/expeditions/{expedition_id}/members")
    suspend fun getExpeditionMembers(@Path("expedition_id") expeditionId: Int): List<Participant>

    @GET("api/trails/{trail_id}/details")
    suspend fun getTrailDetails(@Path("trail_id") trailId: Int): TrailDetailsResponse

    @GET("api/trails/{trail_id}/reviews")
    suspend fun getTrailReviews(@Path("trail_id") trailId: Int?): List<Review>

    @POST("api/reviews")
    suspend fun postReview(@Body request: ReviewRequest): Any

    @POST("api/expeditions")
    suspend fun createExpedition(
        @Body request: CreateExpeditionRequest
    ): ApiResponse

    @GET("api/expeditions/trail/{trail_id}")
    suspend fun getExpeditionsByTrail(
        @Path("trail_id") trailId: Int,
        @Query("user_name") userName: String? = null
    ): List<ExpeditionResponse>

    @POST("api/expeditions/{expedition_id}/join")
    suspend fun joinExpedition(
        @Path("expedition_id") expeditionId: Int,
        @Body request: JoinExpeditionRequest
    ): ApiResponse

    @DELETE("api/expeditions/{expedition_id}/leave")
    suspend fun leaveExpedition(
        @Path("expedition_id") expeditionId: Int,
        @Query("user_name") userName: String
    ): ApiResponse
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