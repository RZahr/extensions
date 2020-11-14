package com.rzahr.extensions

import android.view.View

/**
 * show or hide a view
 */
fun View?.toggleView() {

    if (this?.visibility == View.VISIBLE) this.visibility = View.GONE

    else this?.visibility = View.VISIBLE
}

/**
 * sets view visibility to gone
 */
fun View?.hideView() {

    this?.visibility = View.GONE
}

/**
 * sets view visibility to invisible
 */
fun View?.invisibleView() {

    this?.visibility = View.INVISIBLE
}

/**
 * returns true if the view is visible
 * @return boolean value representing if the view is visible or not
 */
fun View?.showView() {

    this?.visibility == View.VISIBLE
}

fun View?.isVisible(): Boolean {

    return this?.visibility == View.VISIBLE
}