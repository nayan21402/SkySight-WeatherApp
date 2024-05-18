package com.example.skysight.data.api

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface WeatherApiService {
    @GET("{location}")
    suspend fun getWeather(
        @Path("location") location: String,
        @Query("unitGroup") unitGroup: String = "us",
        @Query("elements") elements: String = "datetime,aqius,tempmax,tempmin,temp,feelslike,humidity,precip,precipprob,windspeed,winddir,cloudcover,uvindex,sunrise,sunset,moonphase,conditions,icon",
        @Query("include") include: String = "hourly,current",
        @Query("key") key: String = "CAB2KKL3SLZH7EW4TYK4C2ULA",
        @Query("contentType") contentType: String = "json"
    ): ApiResponse
    @GET("{location}/{date}")
    suspend fun getOldWeather(
        @Path("location") location: String,
        @Path("date") date: String,
        @Query("unitGroup") unitGroup: String = "us",
        @Query("elements") elements: String = "datetime,aqius,tempmax,tempmin,temp,feelslike,humidity,precip,precipprob,windspeed,winddir,cloudcover,uvindex,sunrise,sunset,moonphase,conditions,icon",
        @Query("include") include: String = "current",
        @Query("key") key: String = "LL5D2996H8JFDNT5T9KK76PFF",
        @Query("contentType") contentType: String = "json"
    ): OldApiResponse
}


object WeatherApi {
    val retrofitService: WeatherApiService = retrofit.create(WeatherApiService::class.java)
}
