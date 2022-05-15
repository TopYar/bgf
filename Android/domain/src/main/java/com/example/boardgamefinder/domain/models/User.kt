package com.example.boardgamefinder.domain.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    @SerializedName("nickname")
    val username: String,
    val imageUrl: String?,
    var subscribersCount: Int?,
    val subscriptionsCount: Int?,
    val isMe: Boolean?,
    var isSubscription: Boolean?
)