package myappnew.com.appforweartherapi.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_saved.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import myappnew.com.appforweartherapi.R
import myappnew.com.appforweartherapi.helper.Constants.SEARCH_TIME_DELAY
import myappnew.com.appforweartherapi.helper.EventObserver
import myappnew.com.appforweartherapi.helper.InternetState
import myappnew.com.appforweartherapi.helper.SessionManager
import myappnew.com.appforweartherapi.model.Location
import myappnew.com.appforweartherapi.ui.activities.MainActivity
import myappnew.com.appforweartherapi.ui.viewmodels.SavedViewModel
import myappnew.com.appforweartherapi.ui.viewmodels.SettingsViewModel
import myappnew.com.appforweartherapi.uitils.snackbar
import myappnew.com.conserve.ui.adapters.SavedAdapter
import javax.inject.Inject

@AndroidEntryPoint
class SavedFragment : Fragment(R.layout.fragment_saved) {
    private val TAG = "SavedFragment"
    val savedViewModel : SavedViewModel by viewModels()
    private var mInterstitialAd : InterstitialAd? = null

    @Inject
    lateinit var savedAdapter : SavedAdapter

    private val settingsViewModel : SettingsViewModel by viewModels()

    @Inject
    lateinit var internetState : InternetState

    @Inject
    lateinit var sessionManager : SessionManager

    override fun onViewCreated(view : View , savedInstanceState : Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        subscribeToObservers()
        setupRecyclerView()
        if ( internetState.isConnected(requireContext())) {
            setUpAdds()
        } else {
            snackbar(getString(R.string.no_internet_connection))
        }
        savedAdapter.setOnLocationClickListener { location ->
            val action =
                SavedFragmentDirections.actionSavedFragmentToHomeOtherCityFragment(location)
            findNavController().navigate(action)
        }

        savedAdapter.setOnMenuClickListener { location , view ->
            val popupMenu = PopupMenu(requireContext() , view)
            popupMenu.inflate(R.menu.favorite_menu)
            popupMenu.setOnMenuItemClickListener {
                deletePlacesFormRoom(location)
                true
            }
            popupMenu.show()


        }

        fabAddFavorite.setOnClickListener {
            findNavController().navigate(R.id.mapsFragment)
        }
        var job : Job? = null
        searchlocations.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(SEARCH_TIME_DELAY)
                editable?.let {
                    if (it.isEmpty()) {
                        savedViewModel.getAllLocationSaved()
                    } else savedViewModel.searchLcationSaved(it.toString())
                }
            }
        }

        rvFavorites.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView , dx : Int , dy : Int) {
                super.onScrolled(recyclerView , dx , dy)
                if (dy > 0) {
                    // Scrolling up
                    (activity as MainActivity?) !!.hide()
                } else {
                    // Scrolling down
                    (activity as MainActivity?) !!.show()
                }

            }
        })


    }

    private fun deletePlacesFormRoom(location : Location) {
        savedViewModel.deleteLocation(location)
    }

    private fun setUpAdds() {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext() , getString(R.string.AddFullScreen) , adRequest ,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError : LoadAdError) {
                    snackbar(adError.message)
                    mInterstitialAd = null
                    Log.i("TAG" , "onAdFailedToLoad: ${adError.message}")
                }

                override fun onAdLoaded(interstitialAd : InterstitialAd) {

                    mInterstitialAd = interstitialAd
                }
            })

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {

            }

            override fun onAdFailedToShowFullScreenContent(adError : AdError?) {
                Log.i("TAG" , "onAdFailedToLoad: ${adError?.message}")
            }

            override fun onAdShowedFullScreenContent() {
                Log.i("TAG" , "onAdFailedToLoad:")

                mInterstitialAd = null
            }
        }
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(requireActivity())
        }
    }

    private fun subscribeToObservers() {
        settingsViewModel.langStatus.observe(viewLifecycleOwner , { lang ->

            when (lang) {
                "ar" -> {
                    sessionManager.setLocal("ar" , requireContext())
                }
                "en" -> {
                    sessionManager.setLocal("en" , requireContext())
                }

                else -> {
                    sessionManager.setLocal("en" , requireContext())
                }
            }

        })
        savedViewModel.listLocationSavedStatus.observe(viewLifecycleOwner , EventObserver(
            onLoading = {
                ic_progress_Locations.isVisible = true

            } ,
            onError = {
                snackbar(it)
                ic_progress_Locations.isVisible = false
            }
        ) { locations ->
            favoritesEmpty.isVisible = locations.isEmpty()
            ic_progress_Locations.isVisible = false
            savedAdapter.locions = locations
            Log.i(TAG , "subscribeToObservers: $locations")
        })
        savedViewModel.SearchLocationSavedStatus.observe(viewLifecycleOwner , EventObserver(
            onLoading = {
                ic_progress_Locations.isVisible = true

            } ,
            onError = {
                snackbar(it)
                ic_progress_Locations.isVisible = false
            }
        ) { locations ->
            favoritesEmpty.isVisible = locations.isEmpty()
            ic_progress_Locations.isVisible = false
            savedAdapter.locions = locations
        })

        savedViewModel.deleteLocationStatus.observe(viewLifecycleOwner , EventObserver(
            onLoading = {
                ic_progress_Locations.isVisible = true

            } ,
            onError = {
                snackbar(it)
                ic_progress_Locations.isVisible = false
            }
        ) { coulm ->
            ic_progress_Locations.isVisible = false
            snackbar("Deleted Done")
            savedViewModel.getAllLocationSaved()
        })


    }

    private fun setupRecyclerView() = rvFavorites.apply {
        itemAnimator = null
        isNestedScrollingEnabled = false
        layoutManager = LinearLayoutManager(requireContext())
        adapter = savedAdapter

    }
}