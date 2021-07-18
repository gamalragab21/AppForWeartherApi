package myappnew.com.appforweartherapi.ui.viewmodels

import android.Manifest
import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import myappnew.com.appforweartherapi.R
import myappnew.com.appforweartherapi.helper.Event
import myappnew.com.appforweartherapi.helper.Resource
import myappnew.com.appforweartherapi.model.WeatherResponse
import myappnew.com.appforweartherapi.repositories.MainRepository
import javax.inject.Inject


@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val repository : MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val context : Context
)
    :ViewModel(){
        private val _currentWeatherStatus=MutableLiveData<Event<Resource<WeatherResponse>>>()
       val currentWeatherStatus:LiveData<Event<Resource<WeatherResponse>>> = _currentWeatherStatus

    private val _currentLocationStatus=MutableLiveData<Event<Resource<Location>>>()
    val currentLocationStatus:LiveData<Event<Resource<Location>>> = _currentLocationStatus


      fun getCurrentWeatherByCity(cityName:String,appId:String){
          if (cityName.isEmpty()) {
              val error=context.getString(R.string.invaild_city)
              _currentWeatherStatus.postValue(Event(Resource.Error(error)))
          }else{
              _currentWeatherStatus.postValue(Event(Resource.Loading()))
              viewModelScope.launch (dispatcher){
                  val results=repository.getCurrentWeatherByCity(cityName,appId)
                  _currentWeatherStatus.postValue(Event(results))
              }
          }
      }

      fun getCurrentWeatherByLocationOnCall(location : Location?,appId:String){
          location?.let {

                  _currentWeatherStatus.postValue(Event(Resource.Loading()))
                  viewModelScope.launch (dispatcher){
                      val results=repository.getCurrentWeatherByLocationOnCall(location.latitude,location.longitude,appId)
                      _currentWeatherStatus.postValue(Event(results))
              }
          }

      }

    fun getCurrentWeatherByLocationMap(location : myappnew.com.appforweartherapi.model.Location?,appId:String){
        location?.let {

            _currentWeatherStatus.postValue(Event(Resource.Loading()))
            viewModelScope.launch (dispatcher){
                val results=repository.getCurrentWeatherByLocationOnCall(location.lat,location.lon,appId)
                _currentWeatherStatus.postValue(Event(results))
            }
        }

    }





    fun getCurrentLocation(){
        _currentLocationStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch (dispatcher){
            val location=repository.getLocation()
            _currentLocationStatus.postValue(Event(location))
        }
    }






}