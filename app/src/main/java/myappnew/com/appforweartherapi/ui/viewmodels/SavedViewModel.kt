package myappnew.com.appforweartherapi.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch
import myappnew.com.appforweartherapi.helper.Event
import myappnew.com.appforweartherapi.helper.Resource
import myappnew.com.appforweartherapi.model.Location
import myappnew.com.appforweartherapi.repositories.MainRepository
import javax.inject.Inject

@HiltViewModel
class SavedViewModel
@Inject constructor(
    private val repository : MainRepository ,
    private val dispatcher : CoroutineDispatcher
):ViewModel() {
    private val _listLocationSavedStatus= MutableLiveData<Event<Resource<List<Location>>>>()
    val listLocationSavedStatus: LiveData<Event<Resource<List<Location>>>> =_listLocationSavedStatus

    private val _SearchLocationSavedStatus= MutableLiveData<Event<Resource<List<Location>>>>()
    val SearchLocationSavedStatus: LiveData<Event<Resource<List<Location>>>> =_SearchLocationSavedStatus

    private val _deleteLocationStatus= MutableLiveData<Event<Resource<Int>>>()
    val deleteLocationStatus: LiveData<Event<Resource<Int>>> =_deleteLocationStatus


     fun getAllLocationSaved(){
        _listLocationSavedStatus.postValue(Event(Resource.Loading()))

        viewModelScope.launch(dispatcher) {
            val result=repository.getAllLocationsSaved()
            _listLocationSavedStatus.postValue(Event(result))
        }
    }

    fun deleteLocation(location : Location){
        _deleteLocationStatus.postValue(Event(Resource.Loading()))

        viewModelScope.launch(dispatcher) {
            val result=repository.deleteLocation(location)
            _deleteLocationStatus.postValue(Event(result))
        }

    }

    fun searchLcationSaved(query : String) {
        if (query.isEmpty()) return
        _SearchLocationSavedStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher ) {
            val result=repository.searchLocationsSaved(query)
            _SearchLocationSavedStatus.postValue(Event(result))
        }
    }

    init {
        getAllLocationSaved()
    }


}