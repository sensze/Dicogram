package com.ifkusyoba.dicogram.models

import com.google.gson.annotations.SerializedName

data class Users(
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("userId")
    val userId: String,
    @field:SerializedName("token")
    val token: String,
    val isLogin: Boolean
)