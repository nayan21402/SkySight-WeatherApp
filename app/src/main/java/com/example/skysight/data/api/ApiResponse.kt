package com.example.skysight.data.api
import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("queryCost") val queryCost: Int,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("resolvedAddress") val resolvedAddress: String,
    @SerializedName("address") val address: String,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("tzoffset") val tzoffset: Double,
    @SerializedName("days") val days: List<Day>,
    @SerializedName("currentConditions") val currentConditions: CurrentConditions
)

data class Day(
    @SerializedName("datetime") val datetime: String,
    @SerializedName("tempmax") val tempmax: Double,
    @SerializedName("tempmin") val tempmin: Double,
    @SerializedName("temp") val temp: Double,
    @SerializedName("feelslike") val feelslike: Double,
    @SerializedName("humidity") val humidity: Double,
    @SerializedName("precip") val precip: Double?,
    @SerializedName("precipprob") val precipprob: Double,
    @SerializedName("windspeed") val windspeed: Double,
    @SerializedName("winddir") val winddir: Double,
    @SerializedName("cloudcover") val cloudcover: Double,
    @SerializedName("uvindex") val uvindex: Int,
    @SerializedName("sunrise") val sunrise: String,
    @SerializedName("sunset") val sunset: String,
    @SerializedName("moonphase") val moonphase: Double,
    @SerializedName("conditions") val conditions: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("aqius") val aqius: Int?
)

data class CurrentConditions(
    @SerializedName("datetime") val datetime: String,
    @SerializedName("temp") val temp: Double,
    @SerializedName("feelslike") val feelslike: Double,
    @SerializedName("humidity") val humidity: Double,
    @SerializedName("precip") val precip: Double?,
    @SerializedName("precipprob") val precipprob: Double,
    @SerializedName("windspeed") val windspeed: Double,
    @SerializedName("winddir") val winddir: Double,
    @SerializedName("cloudcover") val cloudcover: Double,
    @SerializedName("uvindex") val uvindex: Int,
    @SerializedName("aqius") val aqius: Int,
    @SerializedName("conditions") val conditions: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("sunrise") val sunrise: String,
    @SerializedName("sunset") val sunset: String,
    @SerializedName("moonphase") val moonphase: Double
)

data class OldApiResponse(
    @SerializedName("queryCost") val queryCost: Int,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("resolvedAddress") val resolvedAddress: String,
    @SerializedName("address") val address: String,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("tzoffset") val tzoffset: Double,
    @SerializedName("days") val days: List<OldDay>,
    @SerializedName("currentConditions") val currentConditions: List<String> = emptyList()
)

data class OldDay(
    @SerializedName("tempmax") val tempmax: Double,
    @SerializedName("tempmin") val tempmin: Double
)


