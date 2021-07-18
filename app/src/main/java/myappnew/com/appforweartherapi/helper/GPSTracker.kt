package myappnew.com.appforweartherapi.helper

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.*
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings;
import android.util.Log
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import myappnew.com.appforweartherapi.R
import java.io.IOException
import java.util.*
import javax.inject.Inject


class GPSTracker @Inject constructor(
   @ApplicationContext private  var mContext : Context) : Service() , LocationListener {
    private  val TAG = "GPSTracker"
    // flag for GPS status
    var isGPSEnabled = false

    // flag for network status
    var isNetworkEnabled = false

    // flag for GPS status
    var canGetLocation = false
    private var location : Location? = null
    private var latitude = 0.0
   private var longitude = 0.0
    var cityName : String? = null
    var geocoderMaxResults = 1

    // Declaring a Location Manager
    protected var locationManager : LocationManager? = null
    @SuppressLint("MissingPermission")
    fun getLocation() : Location? {
        try {
            locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

            // getting GPS status
            isGPSEnabled = locationManager !!.isProviderEnabled(LocationManager.GPS_PROVIDER)

            // getting network status
            isNetworkEnabled =
                locationManager !!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (! isGPSEnabled && ! isNetworkEnabled) {
                Toast.makeText(mContext,"Null Location",Toast.LENGTH_SHORT).show()
                Log.i(TAG , "getLocation:if ")
                showSettingsAlert()
            } else {
                Log.i(TAG , "getLocation: else")
                canGetLocation = true
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    Log.i(TAG , "getLocation: isNetworkEnabled")
                    locationManager !!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER ,
                        0 , 0f , this
                    )
                    Log.d("Network" , "Network")
                    if (locationManager != null) {
                        Log.i(TAG , "getLocation:locationManager ")
                        location =
                            locationManager !!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            location?.let {location->
                                latitude = location.latitude
                                longitude = location.longitude
                            }
                        }
                    }
                }
                if (location == null) {
                    Log.i(TAG , "getLocation:location ")
                    locationManager !!.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER ,
                        0 , 0f , this
                    )
                    Log.d("GPS Enabled" , "GPS Enabled")
                    if (locationManager != null) {
                        Log.i(TAG , "getLocation:locationManager ")
                        location = locationManager !!
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            location?.let {location->
                                latitude = location.latitude
                                longitude = location.longitude
                            }
                        } else {
                            location = locationManager !!
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            location?.let {location->
                                latitude = location.latitude
                                longitude = location.longitude
                            }
                        }
                    }
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
        return location
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager !!.removeUpdates(this@GPSTracker)
        }
    }

    /**
     * Function to get latitude
     */
    fun getLatitude() : Double {
        if (location != null) {
            location?.let {location->
                latitude = location.latitude
            }
        }

        // return latitude
        return latitude
    }

    /**
     * Function to get longitude
     */
    fun getLongitude() : Double {
        if (location != null) {
            location?.let {location->
                longitude = location.longitude
            }
        }

        // return longitude
        return longitude
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    fun canGetLocation() : Boolean {
        return canGetLocation
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    fun showSettingsAlert() {
        val alertDialog : AlertDialog.Builder =AlertDialog. Builder(mContext)

        // SettingsActivity Dialog Title
        alertDialog.setTitle("GPS is settings")

        // SettingsActivity Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings" ,
            DialogInterface.OnClickListener { dialog , which ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                mContext.startActivity(intent)
            })

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel" ,
            DialogInterface.OnClickListener { dialog , which -> dialog.cancel() })

        // Showing Alert Message
        val dialog=alertDialog.create()
        dialog.show()
    }

    fun getLocality(context : Context?) : String? {
        val addresses : List<Address>? = getGeocoderAddress(context)
        return if (addresses != null && addresses.size > 0) {
            val address : Address = addresses[0]
            address.getLocality()
        } else {
            null
        }
    }

    fun getGeocoderAddress(context : Context?) : List<Address>? {
        if (location != null) {
            val geocoder = Geocoder(context , Locale.ENGLISH)
            try {
                return geocoder.getFromLocation(latitude , longitude , geocoderMaxResults)
            } catch (e : IOException) {
                //e.printStackTrace();
                Log.e("TAG" , "Impossible to connect to Geocoder" , e)
            }
        }
        return null
    }

    fun getCountryName(context : Context?) : String? {
        val addresses : List<Address>? = getGeocoderAddress(context)
        return if (addresses != null && addresses.size > 0) {
            val address : Address = addresses[0]
            address.getCountryName()
        } else {
            null
        }
    }
    fun getAddress(context : Context?,lat:Double,lon:Double):String{
        val geocoder = Geocoder(context , Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat , lon , 1)
        if (! addresses.isNullOrEmpty()) {
            return if (! addresses[0].getAddressLine(0).isNullOrEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                getString(R.string.unknown)
            }
        }else return getString(R.string.unknown)

    }

    fun getCityName(context : Context?,lat:Double,lon:Double) : String? {
        val geocoder = Geocoder(context , Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat , lon , 1)
        if (! addresses.isNullOrEmpty()) {
            return  if (! addresses[0].locality.isNullOrEmpty()) {
                addresses[0].locality
            } else if (! addresses[0].adminArea.isNullOrEmpty()) {
                addresses[0].adminArea
            } else if (! addresses[0].getAddressLine(0).isNullOrEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                getString(R.string.unknown)
            }.toString()
        }else return  getString(R.string.unknown)
    }

    fun getCityName(context : Context?) : String? {
        val addresses : List<Address>? = getGeocoderAddress(context)
        return if (addresses != null && addresses.size > 0) {
            val address : Address = addresses[0]
            address.getAddressLine(0)
        } else {
            null
        }
    }

    override fun onLocationChanged(p0 : Location) {}
    override fun onProviderDisabled(provider : String) {}
    override fun onProviderEnabled(provider : String) {}
    override fun onStatusChanged(provider : String , status : Int , extras : Bundle) {}
    override fun onBind(arg0 : Intent?) : IBinder? {
        return null
    }

    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES : Long = 10 // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = (1000 * 60 * 1 // 1 minute
                ).toLong()
    }

    init {
        getLocation()
    }
}