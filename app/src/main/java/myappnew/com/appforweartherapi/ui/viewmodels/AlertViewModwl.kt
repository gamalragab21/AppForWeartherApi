package myappnew.com.appforweartherapi.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import myappnew.com.appforweartherapi.helper.Event
import myappnew.com.appforweartherapi.helper.Resource
import myappnew.com.appforweartherapi.model.Alerts
import myappnew.com.appforweartherapi.repositories.MainRepository
import javax.inject.Inject

@HiltViewModel
class AlertViewModwl @Inject constructor(
    private val repository : MainRepository ,
    private val dispatcher: CoroutineDispatcher ,
    private val context : Context
)
    : ViewModel(){
    private val _currentAletStatus= MutableLiveData<Event<Resource<List<Alerts>?>>>()
    val currentAletStatus: LiveData<Event<Resource<List<Alerts>?>>> = _currentAletStatus

    fun setCurrentAlerts(homeViewModel  : HomeViewModel) {
        _currentAletStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch (dispatcher){
            val result=repository.setAlerts(homeViewModel)
            _currentAletStatus.postValue(Event(result))
        }
    }

}