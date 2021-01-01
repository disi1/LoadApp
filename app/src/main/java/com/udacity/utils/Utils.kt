package com.udacity.utils

import android.app.DownloadManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Patterns
import com.udacity.R

fun isUrlValid(url: String) =
        Patterns.WEB_URL.matcher(url).matches()

fun downloadFile(fileTitle: String, url: String, context: Context) = DownloadManager.Request(Uri.parse(url))
        .setTitle(fileTitle)
        .setDescription(context.getString(R.string.app_description))
        .setRequiresCharging(false)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
        else -> false
    }
}