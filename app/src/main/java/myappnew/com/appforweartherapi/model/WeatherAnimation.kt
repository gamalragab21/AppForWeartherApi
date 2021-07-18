package myappnew.com.appforweartherapi.model

import com.github.matteobattilana.weather.PrecipType

data class WeatherAnimation(
    val type: PrecipType,
    val emissionRate: Float,
    val speed: Int = 500,
    val angel: Int = 20,
    val fadeOutPercent: Float = 1.0f
)
