package myappnew.com.appforweartherapi.repositories

import android.content.Context
import android.util.Log
import com.github.matteobattilana.weather.PrecipType
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import myappnew.com.appforweartherapi.data.local.LocationDao
import myappnew.com.appforweartherapi.data.remotel.ApiService
import myappnew.com.appforweartherapi.helper.GPSTracker
import myappnew.com.appforweartherapi.helper.Resource
import myappnew.com.appforweartherapi.helper.SessionManager
import myappnew.com.appforweartherapi.model.Alerts
import myappnew.com.appforweartherapi.model.Location
import myappnew.com.appforweartherapi.model.WeatherAnimation
import myappnew.com.appforweartherapi.model.WeatherResponse
import myappnew.com.appforweartherapi.ui.viewmodels.HomeViewModel
import myappnew.com.appforweartherapi.uitils.safeCall
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@ViewModelScoped
class MainRepository @Inject constructor(
    private val apiService : ApiService ,
    private val sessionManager : SessionManager ,
    private var simpleDateFormat : SimpleDateFormat ,
    private var date : Date ,
    private var gpsTracker : GPSTracker ,
    private val locationDao : LocationDao,
    private val context : Context
) {
    suspend fun getCurrentWeatherByCity(
        cityName : String ,
        appId : String
    ) : Resource<WeatherResponse> = withContext(Dispatchers.IO) {
        safeCall {
            val result = apiService.getCurrentWeatherByCity(cityName , appId)
            Resource.Success(result)
        }
    }

    suspend fun getCurrentWeatherByLocationOnCall(lat : Double , lon : Double , appId : String) : Resource<WeatherResponse> = withContext(Dispatchers.IO) {
        safeCall {

            Log.i("denied" , "getCurrentWeatherByLocationOnCall: $lat $lon")
            val result = apiService.getCurrentWeatherByLocationOnCall(
                lat ,
                lon ,
                "minutely" ,
                "metric" ,
                sessionManager.getLang(),
                appId
            )
            Resource.Success(result)
        }

    }

    suspend fun setTempUnit(tempUnit : String) : Boolean = withContext(Dispatchers.IO) {
        sessionManager.settempUnit(tempUnit)
    }

    suspend fun geTtempUnit() : String = withContext(Dispatchers.IO) {
        sessionManager.gettempUnit()

    }

    suspend fun setWindUnit(windUnit : String) : Boolean = withContext(Dispatchers.IO) {
        sessionManager.setWindUnit(windUnit)
    }

    suspend fun getWindUnit() : String = withContext(Dispatchers.IO) {
        sessionManager.getwindUnit()
    }

    suspend fun getTimeAndData() : String = withContext(Dispatchers.IO) {
        simpleDateFormat.format(date)
    }

    suspend fun setLang(lang:String):Boolean = withContext(Dispatchers.IO){
        sessionManager.setLang(lang)
    }

    suspend fun getLang() : String = withContext(Dispatchers.IO) {
        sessionManager.getLang()
    }

    suspend fun getLocation() : Resource<android.location.Location> = withContext(Dispatchers.IO) {
        safeCall {
            //   val location:Location?=gpsTracker.getLocation()
            val location : android.location.Location? = gpsTracker.getLocation()
//            location?.latitude=gpsTracker.getLatitude()
//            location?.longitude=gpsTracker.getLongitude()
            Log.i("denied" , "getCurrentWeatherByLocationGps: $location")

             gpsTracker.stopUsingGPS()
            Log.i("denied" , "getCurrentWeatherByLocatioStatusn: ${gpsTracker.canGetLocation()}")

            Resource.Success(location !!)
        }
    }

    suspend fun getAllLocationsSaved() : Resource<List<Location>> = withContext(Dispatchers.IO) {
        safeCall {
            val allNotes = locationDao.getAllLocations()
            Resource.Success(allNotes)
        }
    }

    suspend fun searchLocationsSaved(query : String) : Resource<List<Location>> = withContext(Dispatchers.IO) {
            safeCall {
                val allNotes = locationDao.searchLocations(query)
                Resource.Success(allNotes)
            }
        }

    suspend fun insertLocation(location : Location) : Resource<Long> = withContext(Dispatchers.IO) {
        safeCall {
            val noteInserted : Long = locationDao.insertLocation(location)
            Resource.Success(noteInserted)
        }
    }

    suspend fun deleteLocation(location : Location) : Resource<Int> = withContext(Dispatchers.IO) {
        safeCall {
            val noteDeleted : Int = locationDao.deleteLocation(location)
            Resource.Success(noteDeleted)
        }
    }

    suspend fun setWeatherAnmiation(weatherMain : String) : Resource<WeatherAnimation> = withContext(Dispatchers.IO) {
            var animationType : PrecipType
            var emissionRate = 100f
            safeCall {
                when (weatherMain) {
                    "Clear" -> {
                        animationType = PrecipType.CLEAR
                    }
                    "Clouds" -> {
                        animationType = PrecipType.CLEAR
                    }
                    "Drizzle" -> {
                        animationType = PrecipType.RAIN
                        emissionRate = 25f
                    }
                    "Rain" -> {
                        animationType = PrecipType.RAIN
                        emissionRate = 100f
                    }
                    "Snow" -> {
                        animationType = PrecipType.SNOW
                    }
                    "Thunderstorm" -> {
                        animationType = PrecipType.RAIN
                        emissionRate = 50f
                    }
                    else -> {
                        animationType = PrecipType.CLEAR

                    }
                }

                Resource.Success(WeatherAnimation(animationType , emissionRate))
            }
        }

   suspend fun setAlerts(homeViewModel : HomeViewModel) : Resource<List<Alerts>?> = withContext(Dispatchers.IO){
       safeCall {
           val alerts =homeViewModel.currentWeatherStatus.value?.peekContent()?.data?.alerts
           Resource.Success(alerts)
       }

    }
}