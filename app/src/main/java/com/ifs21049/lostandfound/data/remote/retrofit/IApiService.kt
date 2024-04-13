package com.ifs21049.lostandfound.data.remote.retrofit

import com.ifs21049.lostandfound.data.remote.response.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface IApiService {
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): DelcomResponse

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): DelcomLoginResponse

    @GET("users/me")
    suspend fun getMe(): DelcomUserResponse

    @FormUrlEncoded
    @POST("lost-founds")
    suspend fun postLostFound(
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("status") status: String?,
    ): DelcomAddLostFoundResponse

    @Multipart
    @POST("lost-founds/{id}/cover")
    suspend fun postCoverLostFound(
        @Part("cover") cover: MultipartBody.Part
    ): DelcomResponse

    @FormUrlEncoded
    @PUT("lost-founds/{id}")
    suspend fun putLostFound(
        @Path("id") lostfoundId: String,
        @Field("title") title: String,
        @Field("description") description: String,
        @Query("status") status: String?,
        @Query("is_completed") isCompleted: Int?
    ): DelcomResponse

    @GET("lost-founds")
    suspend fun getLostFounds(
        @Query("is_completed") isCompleted: Int?,
        @Query("is_me") isMe: Int?,
        @Query("status") status: String?
    ): DelcomLostFoundsResponse

    @GET("lost-founds/{id}")
    suspend fun getLostFound(
        @Path("id") lostfoundId: String,
    ): DelcomLostFoundResponse

    @DELETE("lost-founds/{id}")
    suspend fun deleteLostFound(
        @Path("id") lostfoundId: String,
    ): DelcomResponse
}
