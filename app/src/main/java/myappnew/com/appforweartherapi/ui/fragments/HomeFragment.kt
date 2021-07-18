package myappnew.com.appforweartherapi.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet_fragment.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.coroutines.CoroutineDispatcher
import myappnew.com.appforweartherapi.R
import myappnew.com.appforweartherapi.helper.*
import myappnew.com.appforweartherapi.ui.activities.MainActivity
import myappnew.com.appforweartherapi.ui.viewmodels.AlertViewModwl
import myappnew.com.appforweartherapi.ui.viewmodels.HomeViewModel
import myappnew.com.appforweartherapi.ui.viewmodels.SettingsViewModel
import myappnew.com.appforweartherapi.ui.viewmodels.WeatherViewModel
import myappnew.com.appforweartherapi.uitils.snackbar
import myappnew.com.conserve.ui.adapters.DailyAdapter
import myappnew.com.conserve.ui.adapters.HourlyAdapter
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.roundToInt
import kotlin.system.exitProcess


@AndroidEntryPoint
class HomeFragment :Fragment(R.layout.home_fragment){
    private  val TAG = "MainActivityScroll"
    @Inject
    lateinit var glide: RequestManager

    @Inject
    @Named("appId")
    lateinit var appId:String

    @Inject
    lateinit var internetState : InternetState
    @Inject
    lateinit var gpsTracker : GPSTracker

    @Inject
    lateinit var sessionManager : SessionManager

    @Inject
    lateinit var dispatcher : CoroutineDispatcher

    @Inject
    lateinit var hourlyAdapter : HourlyAdapter
    @Inject
    lateinit var dailyAdapter : DailyAdapter
    private val  currentWeatherViewModel: HomeViewModel by activityViewModels()
    private val  alertsViewModel: AlertViewModwl by activityViewModels()

    private val  weatherViewModel: WeatherViewModel by activityViewModels()


    private val settingsViewModel:SettingsViewModel by viewModels()


    fun showSettingsAlert() {


        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val dialog: AlertDialog = builder.setTitle("GPS is settings")
            .setMessage("GPS is not enabled. Do you want to go to settings menu?")
            .setPositiveButton("Settings") {
                    dialog, which ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }.setCancelable(false)
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
               exitProcess(0)
            }
            .create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE)
    }

    override fun onViewCreated(view : View , savedInstanceState : Bundle?) {
        super.onViewCreated(view , savedInstanceState)
         swip_reference.setOnRefreshListener {
             checkPermissionGetLocation()

         }

        if (internetState.isConnected(requireContext())){
            checkPermissionGetLocation()
            setUpAdds()
        }else{
            snackbar("Please, Check internet state")
            tvCurrentAddress.text = getString(R.string.no_internet_connection)
        }


        setupRecyclerViewForHourly()
        setupRecyclerViewForDaily()

        subscribeToObservers()
        scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v , scrollX , scrollY , oldScrollX , oldScrollY ->
            if (scrollY > oldScrollY) {
                (activity as MainActivity?)!!.hide()
            }
            if (scrollY < oldScrollY) {
                (activity as MainActivity?)!!.show()
            }
            if (scrollY == 0) {
              //  Log.i(TAG , "TOP SCROLL")
            }
            if (scrollY == v.measuredHeight - v.getChildAt(0).measuredHeight) {
              //  Log.i(TAG , "BOTTOM SCROLL")
            }
        })


    }

    private fun setUpAdds() {
        MobileAds.initialize(requireContext()) {}

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        adView.adListener = object: AdListener(){
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.

            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
                snackbar(adError.message)
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
    }

    private fun checkPermissionGetLocation() {
        Dexter.withContext(context)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
                override fun onPermissionGranted(response : PermissionGrantedResponse?) { /* ... */

                    currentWeatherViewModel.getCurrentLocation()
                }

                override fun onPermissionDenied(response : PermissionDeniedResponse?) { /* ... */
                    snackbar(getString(R.string.denied_message))
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0 : com.karumi.dexter.listener.PermissionRequest? ,
                    p1 : PermissionToken?
                ) {
                    p1?.let {
                        it.continuePermissionRequest()
                    }
                }

            }).check()
    }

    private fun subscribeToObservers() {
//        settingsViewModel.langStatus.observe(viewLifecycleOwner,{lang->
//            when(lang){
//                "ar"->{
//                    sessionManager.setLocal("ar",requireContext())
//                }
//                "en"->{
//                    sessionManager.setLocal("en" , requireContext() )
//                }
//                else->{
//                    sessionManager.setLocal("en" , requireContext() )
//                }
//            }
//
//        })

        // get location and weather
        currentWeatherViewModel.currentLocationStatus.observe(viewLifecycleOwner,EventObserver(
            onError = {
                snackbar(it)
                showSettingsAlert()
                ic_progress_bar.isVisible=false
                Log.i(TAG , "getCurrentWeatherByLocationViewModel: ${it.toString()}")
                swip_reference.isRefreshing=false
            },
            onLoading = {
                ic_progress_bar.isVisible=true
                swip_reference.isRefreshing=true

            }
        ){location->
            swip_reference.isRefreshing=false

            Log.i(TAG , "getCurrentWeatherByLocationViewModel: ${location.toString()}")
             if (location!=null){
            ic_progress_bar.isVisible=false
            tvCurrentAddress.text=gpsTracker.getCityName(requireContext(),location.latitude,location.longitude)
            currentWeatherViewModel.getCurrentWeatherByLocationOnCall(location,appId)
             }else{
                 showSettingsAlert()
             }
        })

        settingsViewModel.timeStatus.observe(viewLifecycleOwner, { date->
    tvDate.text=date
 })

        // subscribe to mutablieLiveData
        currentWeatherViewModel.currentWeatherStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                Log.i(TAG , "subscribeToObservers: $it")
                snackbar(it)
                ic_progress_bar.isVisible=false

            },onLoading = {
                ic_progress_bar.isVisible=true

            }
        ){currentWeather->
            Log.i("TAG" , "onBindViewHolder: ${currentWeather.alerts.toString()}")

            ic_progress_bar.isVisible=false

            hourlyAdapter.hourlies=currentWeather.hourly
            hourlyAdapter.tempUnitStatus= settingsViewModel.tempUnitStatus.value.toString()

            dailyAdapter.dailies=currentWeather.daily
            dailyAdapter.tempUnitStatus= settingsViewModel.tempUnitStatus.value.toString()

            settingsViewModel.tempUnitStatus.observe(viewLifecycleOwner, { temp->
                Log.i(TAG , "subscribeToObserversUnit: $temp")
                when (temp) {
                    getString(R.string.celsius) -> {
                        tvTempUnit.text = "°C"
                    }
                    getString(R.string.kelvin) -> {
                        currentWeather.current.temp += 273.15
                        tvTempUnit.text = "°K"
                    }
                    getString(R.string.fahrenheit) -> {
                        currentWeather.current.temp = (currentWeather.current.temp * 1.8) + 32
                        tvTempUnit.text = "°F"
                    }
                    else ->{
                        tvTempUnit.text = "°C"
                    }
                }
            })

            settingsViewModel.windUnitStatus.observe(viewLifecycleOwner, {wind->
                Log.i(TAG , "subscribeToObserversUnit: $wind")
                when (wind) {
                    getString(R.string.meter_sec)-> tvWindSpeed.text = "${currentWeather.current.wind_speed} ${getString(R.string.m_s)}"
                    getString(R.string.mile_hour) -> tvWindSpeed.text = "${"%.2f".format(currentWeather.current.wind_speed * 2.236936)} ${getString(R.string.mph)}"
                    else ->{
                        tvWindSpeed.text = "${currentWeather.current.wind_speed} ${getString(R.string.m_s)}"
                    }
                }
            })

            tvTemp.text = currentWeather.current.temp.roundToInt().toString()
            tvDescription.text = currentWeather.current.weather[0].description.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }

            tvPressure.text = "${currentWeather.current.pressure} ${getString(R.string.hpa)}"
            tvHumidity.text = "${currentWeather.current.humidity} %"
            tvClouds.text = "${currentWeather.current.clouds} %"
            tvUltraviolet.text = currentWeather.current.uvi.toString()
            tvVisibility.text = "${currentWeather.current.visibility} ${getString(R.string.meter)}"
            glide.load("https://openweathermap.org/img/w/${currentWeather.current.weather[0].icon}.png").into(ivIcon)
            weatherViewModel.setWeatherAnimation(currentWeather.current.weather[0].main)
            alertsViewModel.setCurrentAlerts(currentWeatherViewModel)

        })
    }


    private fun setupRecyclerViewForHourly() =rvHourly.apply {
        itemAnimator=null
        isNestedScrollingEnabled = false
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter=hourlyAdapter

    }
    private fun setupRecyclerViewForDaily() =rvDaily.apply {
        itemAnimator=null
        isNestedScrollingEnabled = false
        layoutManager = LinearLayoutManager(requireContext())
        adapter=dailyAdapter

    }
}