package com.example.boardgamefinder.domain.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class Event(
    val id: Int,
    val title: String,
    val imageUrl: String?,
    val eventDate: Date,
    val tags: List<Tag>?,
    val location: String,
    val visitorsCount: Int,
    val visitorsLimit: Int,
    val distance: Int,
    val creator: User,
    var likes: Int,
    var liked: Boolean,
    var subscriptionStatus: SubscriptionStatus,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val isCreator: Boolean
)

{
    enum class SubscriptionStatus{
        @SerializedName("not_submitted")
        NOT_SUBMITTED,
        @SerializedName("requested")
        REQUESTED,
        @SerializedName("accepted")
        ACCEPTED
    }
}