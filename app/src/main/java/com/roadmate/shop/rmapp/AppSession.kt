package com.roadmate.shop.rmapp

import android.content.Context
import com.razorpay.Checkout
import com.roadmate.shop.api.response.*
import com.roadmate.shop.preference.Preference

class AppSession {
    companion object{

        var otpTemp = ""

        var userMobile = ""
        var appLatitude = "0.0"
        var appLongitude = "0.0"
        var selectedShopLocation = ""
        var selectedShopPhone = ""
        var appBannerList: ArrayList<AppBannerTrans> = arrayListOf()
        var packageBannerList: ArrayList<AppBannerTrans> = arrayListOf()
        var storeBannerList: ArrayList<AppBannerTrans> = arrayListOf()

        var shopPackageCount = 0

        fun clearUserSession(context: Context){
            Preference.cleanPreferences()
            Checkout.clearUserData(context)
            appLatitude = ""
            appLongitude = ""
            appBannerList.clear()
            packageBannerList.clear()
            storeBannerList.clear()
        }
    }
}