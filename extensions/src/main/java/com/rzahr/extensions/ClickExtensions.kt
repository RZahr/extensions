package com.rzahr.extensions

import android.view.View

var MIN_GUARD_INTERVAL = 500
private var lastEventTime = System.currentTimeMillis()
private var initialized = false

fun guard(code: () -> Unit) {

    val eventTime = System.currentTimeMillis()

    if (eventTime - lastEventTime > MIN_GUARD_INTERVAL || !initialized) {
        initialized = true
        lastEventTime = eventTime
        code()
    }
}

fun View?.onClick(action: () -> Unit) {

    this?.setOnClickListener {

        guard { action() }
    }
}