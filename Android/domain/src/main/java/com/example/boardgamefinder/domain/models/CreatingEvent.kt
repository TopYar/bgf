package com.example.boardgamefinder.domain.models

data class CreatingEvent (
    val title: String,
    val description: String,
    val eventDate: String,
    val location: String,
    val visitorsLimit: Int,
    val latitude: Double,
    val longitude: Double,
)