package com.roadmate.shop.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


class NetworkUtils {

    companion object{

        private const val TYPE_WIFI = 1
        private const val TYPE_MOBILE = 2
        private const val TYPE_NOT_CONNECTED = 0

        private fun getConnectivityStatus(context: Context): Int {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                val capabilities: NetworkCapabilities = cm.getNetworkCapabilities(cm.activeNetwork)
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return TYPE_MOBILE
                }
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return TYPE_WIFI
                }
                return TYPE_NOT_CONNECTED
            }else{
                val activeNetwork = cm.activeNetworkInfo
                if (null != activeNetwork) {
                    if (activeNetwork.type == ConnectivityManager.TYPE_WIFI && activeNetwork.isConnectedOrConnecting) return TYPE_WIFI
                    if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE && activeNetwork.isConnectedOrConnecting) return TYPE_MOBILE
                }
                return TYPE_NOT_CONNECTED
            }
        }

        fun isNetworkConnected(context: Context?): Boolean {
            return when(getConnectivityStatus(context!!)){
                TYPE_WIFI-> true
                TYPE_MOBILE-> true
                else -> false
            }
        }

    }
}