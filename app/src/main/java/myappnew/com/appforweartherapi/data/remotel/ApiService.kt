package myappnew.com.appforweartherapi.data.remotel

import myappnew.com.appforweartherapi.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    suspend fun getCurrentWeatherByCity(
        @Query("q") cityName:String ,
        @Query("appid") appId:String ,
    ): WeatherResponse

    @GET("weather")
    suspend fun getCurrentWeatherByLocation(
        @Query("lat") lat:Double ,
        @Query("lon") lon:Double ,
        @Query("appid") appId:String ,
    ): WeatherResponse
    @GET("onecall")
    suspend fun getCurrentWeatherByLocationOnCall(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("APPID") appId: String,
    ): WeatherResponse
}