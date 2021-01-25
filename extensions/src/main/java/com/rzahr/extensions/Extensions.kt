package com.rzahr.extensions

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import java.util.*

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