package com.rzahr.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.io.File
import java.util.*
import kotlin.math.roundToInt


/**
 * checks the current application version name
 * @return the version name or empty string
 */
fun getVersionName(context: Context): String {
    return try {
        val version = context
            .packageManager.getPackageInfo(context.packageName, 0).versionName
        version
    } catch (e: Exception) {
        ""
    }
}


/**
 * tool used to hide the keyboard from an activity
 */
fun Activity.hideKeyboard() {

    if (currentFocus == null) View(this) else currentFocus?.let { hideKeyboard(it) }
}

/**
 * hiding keyboard anywhere
 * @param view: the view
 */
fun Context.hideKeyboard(view: View) {

    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * gets if the device is plugged in or not
 * @return boolean value representing if the device is plugged in or not
 */
fun isPluggedIn(context: Context): Boolean {

    try {

        val plugged = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))?.getIntExtra(
            BatteryManager.EXTRA_PLUGGED, -1) ?: -1

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
        else plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB
    }

    catch (exc: Exception) {

        Log.e("Error in isPluggedIn:", exc.toString())
    }

    return false
}

/**
 * gets the device name
 * @return the device name
 */
fun getDeviceName(): String {

    return try {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        if (model.startsWith(manufacturer)) {
            model
        } else {
            "$manufacturer $model"
        }
    } catch (ex: Exception) {
        "UNKNOWN"
    }
}

/**
 * @param context: the context
 * @return if service class is running or not
 */
fun Class<*>.isMyServiceRunning(context: Context): Boolean {
    try {
        @Suppress("DEPRECATION")
        for (service in (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
            Integer.MAX_VALUE
        ))
            if (this.name.toLowerCase(Locale.ENGLISH).contains(
                    service.service.className.toLowerCase(
                        Locale.ENGLISH
                    )
                )
            ) return true
    } catch (e: Exception) {
        Log.e("isMyServiceRunning",e.toString())
    }

    return false
}

fun Cursor.valueOf(columnName: String) = if (this.getColumnIndex(columnName) != -1) this.getString(this.getColumnIndex(columnName)) else null

/**
 * gets the battery level
 * @return the battery level
 */
fun getBatteryLevel(context: Context): Int {

    return try {

        val batteryIntent = context
            .registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val batteryLevel = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val batteryScale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

        if (batteryLevel == -1 || batteryScale == -1) 50f.roundToInt() else (batteryLevel.toFloat() / batteryScale.toFloat() * 100f).roundToInt()

    } catch (exc: Exception) {

        0
    }
}

/**
 * gets if the device has wifi or 3g
 * @return if the device is connected to a wifi or 3g
 */
@SuppressLint("MissingPermission")
@Suppress("DEPRECATION")
fun isOnline(context: Context): Boolean {

    val connectivityManager = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(
                NetworkCapabilities.TRANSPORT_ETHERNET)|| actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)-> true
            else -> false
        }
    }
    else try {
        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        return if (connectivityManager.activeNetworkInfo == null) false else connectivityManager.activeNetworkInfo.isConnected
    }catch (e: Exception) {
        return true
    }
}

/**
 * gets if the application is backgrounded by the user
 * @return if the application is in the background
 */
fun backgrounded(context: Context): Boolean {

    var isInBackground = true
    var tasksList: List<*>? = null
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    if (Build.VERSION.SDK_INT > 20) tasksList = activityManager.runningAppProcesses

    else {
        try {
            @Suppress("DEPRECATION")
            tasksList = activityManager.getRunningTasks(1)

        } catch (ignored: Exception) {
        }
    }

    if (tasksList != null && tasksList.isNotEmpty()) {

        when {

            Build.VERSION.SDK_INT > 22 -> {

                for (processInfo in activityManager.runningAppProcesses) {

                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {

                        for (activeProcess in processInfo.pkgList) {

                            if (activeProcess == context.packageName) isInBackground = false
                        }
                    }
                }

                return isInBackground
            }

            Build.VERSION.SDK_INT > 20 -> return activityManager.runningAppProcesses[0].processName != context.packageName

            else -> @Suppress("DEPRECATION") return activityManager.getRunningTasks(1)[0].topActivity?.packageName != context.packageName
        }
    }

    else return false
}
