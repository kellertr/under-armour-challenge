package com.underarmour.challenge.util

import android.content.Context
import android.net.ConnectivityManager

/**
 * The ConnectionUtils object is used to lookup a data connection for this application
 */
object ConnectionUtils {

    /**
     * This method will notify callers if the application currently has a data connection
     *
     * @return true if we have a data connection, false otherwise
     */
    fun isOnline(context: Context?): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val netInfo = cm?.activeNetworkInfo
        return netInfo?.isConnectedOrConnecting ?: false
    }

}
