package com.ifkusyoba.dicogram.models

import com.google.gson.annotations.SerializedName
import com.ifkusyoba.dicogram.models.Users

data class Login(

	@field:SerializedName("loginResult")
	val loginResult: Users,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
