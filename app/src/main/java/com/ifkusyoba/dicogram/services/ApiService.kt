package com.ifkusyoba.dicogram.services

import com.ifkusyoba.dicogram.models.Login
import com.ifkusyoba.dicogram.models.Register
import com.ifkusyoba.dicogram.models.StoryList
import com.ifkusyoba.dicogram.models.StoryUpload
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @POST("register")
    @FormUrlEncoded
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Register

    @POST("login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Login

    @GET("stories")
    suspend fun getStoryList(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryList

    @GET("stories?location=1")
    suspend fun getStoryListLocation(
        @Header("Authorization") token: String,
        @Query("size") size: Int
    ): StoryList

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lng: Double?
    ): StoryUpload
}