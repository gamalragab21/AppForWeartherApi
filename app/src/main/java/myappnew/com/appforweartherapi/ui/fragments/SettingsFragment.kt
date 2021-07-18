package myappnew.com.appforweartherapi.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.*
import myappnew.com.appforweartherapi.R
import myappnew.com.appforweartherapi.helper.InternetState
import myappnew.com.appforweartherapi.helper.SessionManager
import myappnew.com.appforweartherapi.ui.viewmodels.SettingsViewModel
import myappnew.com.appforweartherapi.uitils.snackbar
import okhttp3.internal.addHeaderLenient
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val TAG = "SettingsFragment"
    private var mRewardedAd: RewardedAd? = null

    private val settingsViewModel : SettingsViewModel by viewModels()
    @Inject
    lateinit var internetState : InternetState

    @Inject
    lateinit var sessionManager : SessionManager
    override fun onViewCreated(view : View , savedInstanceState : Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        if (!internetState.isConnected(requireContext())){

            snackbar(getString(R.string.no_internet_connection))
        }

        settingsViewModel.tempUnitStatus.observe(viewLifecycleOwner , { tempUnit ->
            Log.i(TAG , "onViewCreated:$tempUnit ")
            when (tempUnit) {
                getString(R.string.celsius) -> {
                    rbCelsius.isChecked = true
                }
                getString(R.string.kelvin) -> {
                    rbKelvin.isChecked = true
                }
                getString(R.string.fahrenheit) -> {
                    rbFahrenheit.isChecked = true
                }
                else -> {
                    rbCelsius.isChecked = true
                }
            }

        })
        settingsViewModel.windUnitStatus.observe(viewLifecycleOwner , { windUnit ->
            Log.i(TAG , "onViewCreated:$windUnit ")

            when (windUnit) {
                getString(R.string.meter_sec) -> {
                    rbMeter.isChecked = true
                }
                getString(R.string.mile_hour) -> {
                    rbMile.isChecked = true
                }

                else -> {
                    rbMeter.isChecked = true
                }
            }

        })
        settingsViewModel.langStatus.observe(viewLifecycleOwner , { lang ->
            Log.i(TAG , "onViewCreated:$lang ")

            when (lang) {
                "ar" -> {
                    rbArabic.isChecked = true
                    sessionManager.setLocal("ar" , requireContext())

                }
                "en" -> {
                    rbEnglish.isChecked = true
                    sessionManager.setLocal("en" , requireContext())

                }

                else -> {
                    rbEnglish.isChecked = true
                    sessionManager.setLocal("en" , requireContext())
                }
            }

        })

        rbMeter.setOnCheckedChangeListener { compoundButton , b ->
            if (b) settingsViewModel.setWindUnit(getString(R.string.meter_sec))
        }

        rbMile.setOnCheckedChangeListener { compoundButton , b ->
            if (b) {
                settingsViewModel.setWindUnit(getString(R.string.mile_hour))

            }

        }
        rbArabic.setOnCheckedChangeListener { compoundButton , b ->
            if (b) {
                settingsViewModel.setLang("ar")
                sessionManager.setLocal("ar" , requireContext())
                 settingsViewModel.getLang()
            }

        }
        rbEnglish.setOnCheckedChangeListener { compoundButton , b ->
            if (b) {
                settingsViewModel.setLang("en")
              sessionManager.setLocal("en" , requireContext())

            }
        }

        rbCelsius.setOnCheckedChangeListener { compoundButton , b ->
            if (b) settingsViewModel.setTempUnit(getString(R.string.celsius))
        }
        rbKelvin.setOnCheckedChangeListener { compoundButton , b ->
            if (b) settingsViewModel.setTempUnit(getString(R.string.kelvin))

        }
        rbFahrenheit.setOnCheckedChangeListener { compoundButton , b ->
            if (b) settingsViewModel.setTempUnit(getString(R.string.fahrenheit))
        }

    }


}