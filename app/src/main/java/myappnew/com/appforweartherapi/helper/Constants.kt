package myappnew.com.appforweartherapi.helper

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import myappnew.com.appforweartherapi.R
import java.util.*

object Constants {
    val LANG : String?="LANGUAGES"
    val BASE_URL : String="https://api.openweathermap.org/data/2.5/"
    val APP_ID : String= R.string.API_WEATHER_KEY.toString()
    val TEMP_UNIT : String="TEMP_UNIT"
    val WIND_UNIT : String="WIND_UNIT"
    val SEARCH_TIME_DELAY =500L


}