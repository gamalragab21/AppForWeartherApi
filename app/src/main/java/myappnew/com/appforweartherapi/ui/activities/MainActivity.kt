package myappnew.com.appforweartherapi.ui.activities

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import myappnew.com.appforweartherapi.R
import myappnew.com.appforweartherapi.helper.EventObserver
import myappnew.com.appforweartherapi.helper.SessionManager
import myappnew.com.appforweartherapi.ui.viewmodels.HomeViewModel
import myappnew.com.appforweartherapi.ui.viewmodels.SettingsViewModel
import myappnew.com.appforweartherapi.ui.viewmodels.WeatherViewModel
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
open class MainActivity : AppCompatActivity() {


private val weatherViewModel : WeatherViewModel by viewModels()
    private val settingsViewModel:SettingsViewModel by viewModels()
    @Inject
    lateinit var sessionManager : SessionManager
    private  val TAG = "MainActivity"
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeToObservers()
        setContentView(R.layout.activity_main)



        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment)
                as NavHostFragment
        bottomNavigationView.apply {
            background = null
            menu.getItem(2).isEnabled = false
            setupWithNavController(navHostFragment.findNavController())
            setOnNavigationItemReselectedListener { Unit }
        }

        fabNewlocation.setOnClickListener {
            findNavController(R.id.navHostFragment).navigate(R.id.mapsFragment)
        }






    }

    private fun subscribeToObservers() {
        settingsViewModel.langStatus.observe(this ,{lang->
            when(lang){
                "ar"->{
                    sessionManager.setLocal("ar",this)

                }
                "en"->{
                    sessionManager.setLocal("en" , this )
                }
                else->{
                    sessionManager.setLocal("en" , this)
                }
            }

        })
    weatherViewModel.weatherAnimationStatus.observe(this,EventObserver(
        onError = {
            Toast.makeText(this , it , Toast.LENGTH_SHORT).show()
        },onSuccess = {weatherAnmiation->
            wvWeatherView.apply {
                    setWeatherData(weatherAnmiation.type)
                    emissionRate = weatherAnmiation.emissionRate
                    speed = weatherAnmiation.speed
                    angle = weatherAnmiation.angel
                    fadeOutPercent = weatherAnmiation.fadeOutPercent
                Log.i(TAG , "subscribeToObservers: $weatherAnmiation")
            }

        },onLoading = {

        }
    ))

    }

    //(activity as MainActivity?)!!.hide()
     open fun show() {
        bottomAppBar.visibility= View.VISIBLE
        fabNewlocation.visibility= View.VISIBLE
    }
    open fun hide() {
        bottomAppBar.visibility= View.GONE
        fabNewlocation.visibility= View.GONE
    }

     fun setLocal(lang:String,context : Context){
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val res: Resources = context.resources
        val config: Configuration = res.configuration
        config.setLocale(locale)
        res.updateConfiguration(config, res.displayMetrics)
    }

}