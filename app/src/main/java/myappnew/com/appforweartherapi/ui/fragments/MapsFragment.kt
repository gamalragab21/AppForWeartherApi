package myappnew.com.appforweartherapi.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import myappnew.com.appforweartherapi.R
import myappnew.com.appforweartherapi.helper.GPSTracker
import myappnew.com.appforweartherapi.helper.InternetState
import myappnew.com.appforweartherapi.helper.SessionManager
import myappnew.com.appforweartherapi.ui.viewmodels.MapsViewModel
import myappnew.com.appforweartherapi.ui.viewmodels.SettingsViewModel
import myappnew.com.appforweartherapi.uitils.snackbar
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class MapsFragment : Fragment() {
    @Inject
    lateinit var gpsTracker : GPSTracker

     val mapsViewModel : MapsViewModel by activityViewModels()
    private val bottomSheetFragment = BottomSheetFragment()
    @Inject
    lateinit var internetState : InternetState
    @Inject
    lateinit var sessionManager : SessionManager
    private val callback = OnMapReadyCallback { googleMap ->
        val issuccess : Boolean = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext() , R.raw.uber_style))
        if (!issuccess) snackbar("Error")

            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(gpsTracker.getLatitude() , gpsTracker.getLongitude()) ,
                    7f
                )
            )


        googleMap.setOnMapClickListener {
            try {
                googleMap.clear()
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 17f))
                googleMap.addMarker(
                    MarkerOptions().position(it)
                )
                    mapsViewModel.setLocation(it)
                    bottomSheetFragment.show(childFragmentManager, "BottomSheetFragment")

            } catch (e: IOException) {
                snackbar(getString(R.string.no_internet_connection))
            }
        }
    }

    override fun onCreateView(
        inflater : LayoutInflater ,
        container : ViewGroup? ,
        savedInstanceState : Bundle?
    ) : View? {
        return inflater.inflate(R.layout.fragment_maps , container , false)
    }

    override fun onViewCreated(view : View , savedInstanceState : Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

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

        if (!internetState.isConnected(requireContext())) snackbar(getString(R.string.no_internet_connection))
    }

}