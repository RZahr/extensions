package com.rzahr.extensions

import android.app.Activity
import android.content.Intent

fun Activity.shareText(textToShare: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, textToShare)
        type = "text/*"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}