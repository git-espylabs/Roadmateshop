package com.roadmate.shop.rmapp

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.razorpay.Checkout
import com.roadmate.shop.constants.SharedPreferenceConstants.Companion.PREF_NAME
import com.roadmate.shop.location.AppLocation
import com.roadmate.shop.log.AppLogger
import com.roadmate.shop.preference.Preference
import org.jetbrains.anko.getStackTraceString


/**
 * App - Android application class
 */
class App : Application() {

    companion object {
        lateinit var mApp: App
    }

    /**
     * onCreate
     *
     * Initialize Room database, initialize Preference and set un caught exception
     */
    override fun onCreate() {
        mApp = this

        super.onCreate()

        Preference.init(this, PREF_NAME)

        AppLocation.init(this)

        Checkout.preload(this)

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            AppLogger.error(e.getStackTraceString())
        }

    }

    override fun onTerminate() {
        super.onTerminate()
    }

    fun getAppVersion(): String{
        val manager: PackageManager = mApp.packageManager
        val info: PackageInfo = manager.getPackageInfo(
            mApp.packageName, 0
        )
        return info.versionName
    }
}