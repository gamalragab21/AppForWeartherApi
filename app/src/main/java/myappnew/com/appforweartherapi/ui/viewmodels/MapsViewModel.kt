package myappnew.com.appforweartherapi.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import myappnew.com.appforweartherapi.helper.Event
import myappnew.com.appforweartherapi.helper.Resource
import myappnew.com.appforweartherapi.model.Location
import myappnew.com.appforweartherapi.model.WeatherResponse
import myappnew.com.appforweartherapi.repositories.MainRepository
import javax.inject.Inject

@HiltViewModel
class MapsViewModel  @Inject constructor(
     private val repository : MainRepository,
     private val dispatcher : CoroutineDispatcher
): ViewModel() {

    private val _currentLocationStatus = MutableLiveData<LatLng>()
    val currentLocationStatus : LiveData<LatLng> = _currentLocationStatus

    private val _InsertLocationStatus = MutableLiveData<Event<Resource<Long>>>()
    val InsertLocationStatus : LiveData<Event<Resource<Long>>> = _InsertLocationStatus

    fun setLocation(latLng : LatLng){
    _currentLocationStatus.postValue(latLng)
    }

    fun insertLocation(location : Location){
        _InsertLocationStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch (dispatcher){
            val result=repository.insertLocation(location)
            _InsertLocationStatus.postValue(Event(result))
        }
    }

}
