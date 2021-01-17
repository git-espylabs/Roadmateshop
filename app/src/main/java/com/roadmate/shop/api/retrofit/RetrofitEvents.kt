package com.roadmate.shop.api.retrofit

/**
 * Created by Jobsin
 */

/**
 * Retrofit Events class to hold the CallState enum
 */
object RetrofitEvents {
    enum class CallState{
        STATE_CALL_START { override fun toInt(): Int { return 1 } },
        STATE_CALL_END { override fun toInt(): Int { return 2 } },
        STATE_CALL_FAILURE { override fun toInt(): Int { return 3 } };

        abstract fun toInt(): Int
    }
}