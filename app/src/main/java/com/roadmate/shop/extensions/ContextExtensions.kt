package com.roadmate.shop.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat

/**
 * Context extension methods
 */

/**
 * ToastInfo data class
 */
data class ToastInfo(var message: String = "", var duration: Int = Toast.LENGTH_SHORT)

/**
 * makeText extension method
 */
fun Context.makeText(message: String, duration: Int): Toast{
    return Toast.makeText(this, message, duration)
}

/**
 * toast extension method to show Toast messages
 */
fun Context.toast(block: ToastInfo.() -> Unit): Unit {
    val info = ToastInfo()

    info.block()

    makeText(info.message, info.duration).show()
}

/**
 * isPermissionGranted extension method to check whether a permission is granted or not
 */
fun Context.isPermissionGranted(permission:String):Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED