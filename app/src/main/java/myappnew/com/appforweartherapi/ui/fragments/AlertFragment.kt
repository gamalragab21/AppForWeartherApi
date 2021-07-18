package myappnew.com.appforweartherapi.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_alerts.*
import kotlinx.android.synthetic.main.home_fragment.*
import myappnew.com.appforweartherapi.R
import myappnew.com.appforweartherapi.helper.EventObserver
import myappnew.com.appforweartherapi.helper.InternetState
import myappnew.com.appforweartherapi.helper.SessionManager
import myappnew.com.appforweartherapi.ui.viewmodels.AlertViewModwl
import myappnew.com.appforweartherapi.uitils.snackbar
import myappnew.com.conserve.ui.adapters.AlertAdapter
import javax.inject.Inject

@AndroidEntryPoint
class AlertFragment:Fragment(R.layout.fragment_alerts) {

    private val  alertsViewModel: AlertViewModwl by activityViewModels()
    @Inject
    lateinit var internetState : InternetState
    @Inject
    lateinit var alertsAdapter:AlertAdapter



    @Inject
    lateinit var sessionManager : SessionManager

    override fun onViewCreated(view : View , savedInstanceState : Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        if (!internetState.isConnected(requireContext())){
            snackbar(getString(R.string.no_internet_connection))
        }else{
            setUpAdds()
        }


        setupRecyclerView()

        subscribeToObservers()



    }

    private fun setUpAdds() {
        MobileAds.initialize(requireContext()) {}

        val adRequest = AdRequest.Builder().build()
        adView_alert.loadAd(adRequest)
        adView_alert.adListener = object: AdListener(){
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



    private fun subscribeToObservers() {
        alertsViewModel.currentAletStatus.observe(viewLifecycleOwner,EventObserver(
            onLoading = {
                ic_progress_alert.isVisible=true
            }
        ,onSuccess = {alerts->
                alerts?.let {
                    ic_progress_alert.isVisible=false
                    alertsEmpty.isVisible=alerts.isEmpty()
                    alertsAdapter.alerts=alerts
                }

            },
            onError = {
                ic_progress_alert.isVisible=false
                snackbar(it)
            }
        ))

//        settingsViewModel.langStatus.observe(viewLifecycleOwner,{lang->
//
//            when(lang){
//                "ar"->{
//                    sessionManager.setLocal("ar" , requireContext() )
//                }
//                "en"->{
//                    sessionManager.setLocal("en" , requireContext() )
//                }
//
//                else->{
//                    sessionManager.setLocal("en" , requireContext() )
//                }
//            }
//
//        })
    }

    private fun setupRecyclerView() =rvAlerts.apply {
        itemAnimator=null
        isNestedScrollingEnabled = false
        layoutManager = LinearLayoutManager(requireContext())
        adapter=alertsAdapter
    }

}