package com.underarmour.challenge.util

import android.content.Context
import android.net.ConnectivityManager

object ConnectionUtils {

    fun isOnline(context: Context?): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val netInfo = cm?.activeNetworkInfo
        return netInfo?.isConnectedOrConnecting ?: false
    }

}
