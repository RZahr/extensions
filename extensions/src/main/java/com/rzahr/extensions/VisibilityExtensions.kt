package com.rzahr.extensions

import android.view.View

/**
 * show or hide a view
 */
fun View?.toggle() {

    if (this?.visibility == View.VISIBLE) this.visibility = View.GONE

    else this?.visibility = View.VISIBLE
}

/**
 * sets view visibility to gone
 */
fun View?.hide() {

    this?.visibility = View.GONE
}

/**
 * sets view visibility to invisible
 */
fun View?.invisible() {

    this?.visibility = View.INVISIBLE
}

/**
 * returns true if the view is visible
 * @return boolean value representing if the view is visible or not
 */
fun View?.show(): Boolean {

    return this?.visibility == View.VISIBLE
}