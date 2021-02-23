package com.example.wetherforecastapp.model.remote

import com.example.wetherforecastapp.model.entity.WeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "657a3a141a21f4b9151316c7a77c0d5e"


//https://api.openweathermap.org/data/2.5/onecall?lat=33.441792&lon=-94.037689&exclude=minutely&appid=657a3a141a21f4b9151316c7a77c0d5e
interface WetherApi {

    @GET("data/2.5/onecall")
    suspend fun getCurrentWeatherByLatLng(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "minutely",
        @Query("appid") appid: String = API_KEY
    ): Response<WeatherData>
    //suspend fun getCountries(): Response<List<WetherData>>
   /* companion object {
        operator fun invoke(): WetherApi {
            val requestInterceptor = Interceptor { chain ->

                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("key", API_KEY)
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.openweathermap.org/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WetherApi::class.java)
        }
    }*/
}