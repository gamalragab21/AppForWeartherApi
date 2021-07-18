package myappnew.com.appforweartherapi.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet_fragment.*
import myappnew.com.appforweartherapi.R
import myappnew.com.appforweartherapi.helper.EventObserver
import myappnew.com.appforweartherapi.helper.GPSTracker
import myappnew.com.appforweartherapi.helper.SessionManager
import myappnew.com.appforweartherapi.model.Location
import myappnew.com.appforweartherapi.ui.activities.MainActivity
import myappnew.com.appforweartherapi.ui.viewmodels.MapsViewModel
import myappnew.com.appforweartherapi.ui.viewmodels.SettingsViewModel
import myappnew.com.appforweartherapi.uitils.snackbar
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetFragment : BottomSheetDialogFragment() {
    private  val TAG = "BottomSheetFragment"
    @Inject
    lateinit var gpsTracker : GPSTracker
    val mapsViewModel:MapsViewModel by activityViewModels()
    private val settingsViewModel : SettingsViewModel by viewModels()

    @Inject
    lateinit var sessionManager : SessionManager
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

            val view=inflater.inflate( R.layout.bottom_sheet_fragment , container , false)


        return view

    }

//    private fun roundDouble(double: Double): Double {
//        val d = DecimalFormat("#.####")
//        return BigDecimal(double).setScale(4, RoundingMode.HALF_UP).toDouble()
////        return "%.4f".format(double).toDouble()
//    }



    override fun onViewCreated(view : View , savedInstanceState : Bundle?) {
        super.onViewCreated(view , savedInstanceState)

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
        mapsViewModel.currentLocationStatus.observe(viewLifecycleOwner, {latLng->

            latLng?.let {
                val lat = it.latitude
                val lon = it.longitude
                Log.i(TAG , "setOnMapClickListener: $lat $lon")
                tvCity.text = gpsTracker.getCityName(requireContext() , lat, lon )
                tvAddress.text = gpsTracker.getAddress(requireContext() , lat , lon)


                btnSaveLocation.setOnClickListener {
                    val location=Location(lat,lon,tvCity.text.toString())

                    mapsViewModel.insertLocation(location)
                    mapsViewModel.InsertLocationStatus.observe(viewLifecycleOwner,EventObserver(
                        onLoading = {

                        },onError = {
                            snackbar(it)
                        },onSuccess = {
                            val location=Location(lat,lon,tvCity.text.toString())
                            val action=MapsFragmentDirections.actionMapsFragmentToHomeOtherCityFragment(location)
                            findNavController().navigate(action)
                            dismiss()
//                            (requireActivity() as MainActivity).bottomNavigationView.
                        }
                    ))

                }
                btnSkipLocation.setOnClickListener {
                    val location=Location(lat,lon,tvCity.text.toString())
                    val action=MapsFragmentDirections.actionMapsFragmentToHomeOtherCityFragment(location)
                    findNavController().navigate(action)
                    dismiss()
                }
            }

        })







    }
}
