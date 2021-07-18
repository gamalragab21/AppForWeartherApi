package myappnew.com.appforweartherapi.helper

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import myappnew.com.appforweartherapi.R
import java.util.*
import javax.inject.Inject

class SessionManager @Inject constructor(
    var editor: SharedPreferences.Editor,
    var sharedPreferences : SharedPreferences,
    val context : Context
    ) {

    suspend fun settempUnit(tempUnit:String):Boolean = withContext(Dispatchers.IO){
        val result= editor.putString(Constants.TEMP_UNIT,tempUnit)
            result.apply()
            result.commit()
    }

   suspend fun gettempUnit():String = withContext(Dispatchers.IO) {
       val resultTtempUnit = sharedPreferences.getString(Constants.TEMP_UNIT , context.getString(R.string.celsius))
        resultTtempUnit.toString()
   }

    suspend fun setWindUnit(windUnit:String):Boolean = withContext(Dispatchers.IO){
        val result= editor.putString(Constants.WIND_UNIT,windUnit)
        result.apply()
        result.commit()
    }

    suspend fun getwindUnit():String = withContext(Dispatchers.IO) {
        val resultTtempUnit = sharedPreferences.getString(Constants.WIND_UNIT , context.getString(R.string.meter_sec))
        resultTtempUnit.toString()
    }

    suspend fun setLang(lang:String):Boolean = withContext(Dispatchers.IO){
        val result= editor.putString(Constants.LANG,lang)
        result.apply()
        result.commit()
    }

    suspend fun getLang():String = withContext(Dispatchers.IO) {
        val resultlang = sharedPreferences.getString(Constants.LANG , Locale.getDefault().displayLanguage)
        resultlang.toString()
    }

    fun setLocal(lang : String , context : Context ){

        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(
            config ,
            context.resources.displayMetrics
        )
    }


}