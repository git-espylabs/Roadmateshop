package com.roadmate.shop.log

import android.util.Log
import com.roadmate.shop.BuildConfig
import com.roadmate.shop.extensions.doIfTrue

/**
 * AppLog - Class that handles application logs
 */
interface AppLog{
    // Application tag
    private val appLogTag: String
        get() = "RMLOG"


    /**
     * Generic log method, this method checks Log is enabled in Build Config, if it is enabled call
     * the log function
     */
    private fun log(tag: String, message: String, f: (tag: String, message: String) -> Int){
        BuildConfig.IS_LOG_ENABLED.doIfTrue{ f(tag, message) }
    }

    /**
     * Log info messages
     */
    fun info(tag: String, message: String) = log(tag, message) { t, m -> Log.i(t, m)}
    fun info(message: String) = info(appLogTag, message)

    /**
     * Log warning messages
     */
    fun warn(tag: String, message: String) = log(tag, message) { t, m -> Log.w(t, m)}
    fun warn(message: String) = warn(appLogTag, message)

    /**
     * Log error messages
     */
    fun error(tag: String, message: String) = log(tag, message) { t, m -> Log.e(t, m)}
    fun error(message: String) = error(appLogTag, message)

    /**
     * Log verbose messages
     */
    fun verbose(tag: String, message: String) = log(tag, message) { t, m -> Log.v(t, m)}
    fun verbose(message: String) = verbose(appLogTag, message)
}