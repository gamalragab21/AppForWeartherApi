package myappnew.com.appforweartherapi.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import myappnew.com.appforweartherapi.helper.Event
import myappnew.com.appforweartherapi.helper.Resource
import myappnew.com.appforweartherapi.model.WeatherAnimation
import myappnew.com.appforweartherapi.repositories.MainRepository
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val dispatcher : CoroutineDispatcher,
    private val repository : MainRepository
): ViewModel() {
    private val _weatherAnimationStatus = MutableLiveData<Event<Resource<WeatherAnimation>>>()
    val weatherAnimationStatus : LiveData<Event<Resource<WeatherAnimation>>> = _weatherAnimationStatus

    fun setWeatherAnimation(mainWeather : String) {

        _weatherAnimationStatus.postValue(Event(Resource.Loading()) )
        viewModelScope.launch (dispatcher){
            val result=repository.setWeatherAnmiation(mainWeather)
            _weatherAnimationStatus.postValue(Event(result))
        }
    }
}