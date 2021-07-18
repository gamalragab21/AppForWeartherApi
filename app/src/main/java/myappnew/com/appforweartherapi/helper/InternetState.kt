package myappnew.com.appforweartherapi.helper

import android.content.Context
import android.net.ConnectivityManager
import com.google.android.gms.location.LocationRequest
import javax.inject.Inject

class InternetState @Inject constructor() {
    var cm : ConnectivityManager? = null

    fun isConnected(context : Context) : Boolean {
        try {
            cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        } catch (e : NullPointerException) {
        }
        val locationRequest = LocationRequest()
        val activeNetwork = cm !!.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}