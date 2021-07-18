package myappnew.com.appforweartherapi.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import myappnew.com.appforweartherapi.model.Alerts
import myappnew.com.appforweartherapi.model.Current
import myappnew.com.appforweartherapi.model.Daily
import myappnew.com.appforweartherapi.model.Hourly

@Entity(tableName = "weather")
data class WeatherResponse(
    @PrimaryKey
    @SerializedName("lat") val lat: Double ,
    @SerializedName("lon") val lon: Double ,
    @SerializedName("timezone") val timezone: String ,
    @SerializedName("timezone_offset") val timezone_offset: Int ,
    @SerializedName("current") val current: Current ,
    @SerializedName("hourly") val hourly: List<Hourly> ,
    @SerializedName("daily") val daily: List<Daily> ,
    @SerializedName("alerts") val alerts: List<Alerts>? ,
    var isCurrent: Boolean = false
)
