package com.example.alpinistapp

import com.example.alpinistapp.network.models.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // =========================
    // AUTH
    // =========================

    @POST("api/login")
    suspend fun login_user(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("api/register")
    suspend fun register_user(
        @Body request: RegisterRequest
    ): RegisterResponse


    // =========================
    // HOME
    // =========================

    @GET("api/users/{userId}/upcoming-expeditions")
    suspend fun getUpcomingExpeditions(
        @Path("userId") userId: Int
    ): List<UpcomingExpeditionResponse>


    // =========================
    // TRAILS
    // =========================

    @GET("api/trails")
    suspend fun getTrails(): List<TrailCardResponse>

    @GET("api/trails/{trailId}")
    suspend fun getTrail(
        @Path("trailId") trailId: Int
    ): TrailDetailResponse

    @GET("api/trails/{trailId}/expeditions")
    suspend fun getTrailExpeditions(
        @Path("trailId") trailId: Int
    ): List<ExpeditionResponse>

    @GET("api/trails/{trailId}/reviews")
    suspend fun getTrailReviews(
        @Path("trailId") trailId: Int
    ): List<ReviewResponse>


    // =========================
    // EXPEDITIONS
    // =========================

    @GET("api/expeditions")
    suspend fun getExpeditions(): List<ExpeditionCardResponse>

    @POST("api/expeditions")
    suspend fun createExpedition(
        @Body request: CreateExpeditionRequest
    ): ApiResponse

    @POST("api/expeditions/{expeditionId}/join")
    suspend fun joinExpedition(
        @Path("expeditionId") expeditionId: Int,
        @Body request: JoinExpeditionRequest
    ): ApiResponse

    @DELETE("api/expeditions/{expeditionId}/leave")
    suspend fun leaveExpedition(
        @Path("expeditionId") expeditionId: Int,
        @Query("user_id") userId: Int
    ): ApiResponse

    @GET("api/expeditions/{expeditionId}/participants")
    suspend fun getParticipants(
        @Path("expeditionId") expeditionId: Int
    ): List<ParticipantResponse>


    // =========================
    // REVIEWS
    // =========================

    @POST("api/reviews")
    suspend fun createReview(
        @Body request: CreateReviewRequest
    ): ApiResponse

    @GET("trails/{id}/gpx")
    suspend fun downloadGpx(
        @Path("id") trailId: Int
    ): Response<ResponseBody>
}
