package com.example.wetherforecastapp.model.remote

import com.example.wetherforecastapp.model.entity.WeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "31cceaa80d19afe5ea2ec0f5b270311b"




//https://api.openweathermap.org/data/2.5/onecall?lat=33.441792&lon=-94.037689&lang=ar&units=standard&exclude=minutely&appid=31cceaa80d19afe5ea2ec0f5b270311b
interface WetherApi {
    @GET("data/2.5/onecall")
    suspend fun getCurrentWeatherByLatLng(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("exclude") exclude: String = "minutely",
        @Query("appid") appid: String = API_KEY

    ): Response<WeatherData>
}