package com.ifkusyoba.dicogram.models

import com.google.gson.annotations.SerializedName
import com.ifkusyoba.dicogram.models.Story

data class StoryList(

    @field:SerializedName("listStory")
    val listStory: List<Story>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)