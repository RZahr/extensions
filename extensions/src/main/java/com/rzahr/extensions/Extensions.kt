package com.rzahr.extensions

import android.app.ActivityManager
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.util.Log
import java.util.*

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

fun Cursor.rzVal(columnName: String) = if (this.getColumnIndex(columnName) != -1) this.getString(this.getColumnIndex(columnName)) else null
