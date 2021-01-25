package com.rzahr.extensions

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import java.util.*
import kotlin.math.roundToInt

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
