package myappnew.com.appforweartherapi.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import myappnew.com.appforweartherapi.repositories.MainRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository : MainRepository ,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main ,
)
    : ViewModel(){

    private  val TAG = "SettingsViewModel"
    private val _tempUnitStatus= MutableLiveData<String>()
    val tempUnitStatus: LiveData<String> = _tempUnitStatus

    private val _windUnitStatus= MutableLiveData<String>()
    val windUnitStatus: LiveData<String> = _windUnitStatus

    private val _timeStatus = MutableLiveData<String>()
    val timeStatus : LiveData<String> = _timeStatus

    private val _langStatus = MutableLiveData<String>()
    val langStatus : LiveData<String> = _langStatus

    private fun geTempUnit(){
        viewModelScope.launch(dispatcher) {
            val result=repository.geTtempUnit()
            _tempUnitStatus.postValue(result)
        }
    }

    private fun getWindUnit(){
        viewModelScope.launch(dispatcher) {
            val result=repository.getWindUnit()
            _windUnitStatus.postValue(result)
        }
    }

    fun getTimeAndData() {
        viewModelScope.launch(dispatcher) {
            val result = repository.getTimeAndData()
            _timeStatus.postValue(result)
        }

    }

    fun getLang(){
        viewModelScope.launch(dispatcher) {
            val result=repository.getLang()
            _langStatus.postValue(result)
        }
    }


    fun setTempUnit(temp:String){
        viewModelScope.launch(dispatcher) {

            val result=repository.setTempUnit(temp)
            Log.i(TAG , "SettingsViewModeTemp: $result")
        }
    }

    fun setWindUnit(wind:String){
        viewModelScope.launch(dispatcher) {

            val result=repository.setWindUnit(wind)
           Log.i(TAG , "SettingsViewModeWind: $result")
        }
    }

    fun setLang(lang:String){
        viewModelScope.launch(dispatcher) {

            val result=repository.setLang(lang)
             Log.i(TAG , "SettingsViewModelLang: $result")
        }
    }

    init {
        getLang()
        getWindUnit()
        geTempUnit()
        getTimeAndData()
    }
}