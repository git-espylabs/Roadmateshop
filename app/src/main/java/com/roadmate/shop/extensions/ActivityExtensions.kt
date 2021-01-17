package com.roadmate.shop.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.roadmate.shop.rmapp.App

/**
 * Activity extension methods
 */

/**
 * Extension method to start an activity
 */
inline fun <reified T: Activity> Context.startActivity(){
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

/**
 * Extension method to begin fragment transaction
 */
inline fun FragmentManager.beginTransaction(op: FragmentTransaction.() -> FragmentTransaction){
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.op()
    fragmentTransaction.commit()
}

/**
 * Extension method to add fragment to FragmentManager
 */
fun AppCompatActivity.addFragment(tag: String, fragment: Fragment, frameId: Int){
    supportFragmentManager.beginTransaction { add(frameId, fragment, tag) }
}

/**
 * Extension method to return App class
 */
val Activity.app: App
    get() = application as App

/**
 * Extension method to check whether permission dialog should be shown to the user
 */
fun Activity.isUserCheckNeverAskAgain(permission: String) =
    !ActivityCompat.shouldShowRequestPermissionRationale(
        this,
        permission
    )

inline fun <reified T : Any> Activity.launchActivity(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivityForResult(intent, requestCode, options)
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)