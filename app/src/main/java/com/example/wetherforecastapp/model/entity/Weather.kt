package com.example.wetherforecastapp.model.entity


data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)