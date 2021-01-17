package com.roadmate.shop.preference

import com.roadmate.shop.constants.SharedPreferenceConstants

class ShopDetails {

    var shopId by Preference(SharedPreferenceConstants.SHOP_ID, "")
    var shopType by Preference(SharedPreferenceConstants.SHOP_TYPE, "")
    var userMobile by Preference(SharedPreferenceConstants.USER_MOB, "")
    var shopName by Preference(SharedPreferenceConstants.SHOP_NAME, "")
    var isUserLoggedIn by Preference(SharedPreferenceConstants.USER_LOGGED_IN, false)
    var isShopOpen by Preference(SharedPreferenceConstants.SHOP_OPEN, true)
    var isActiveUser by Preference(SharedPreferenceConstants.IS_ACTIVE_USER, false)
    var isMechanic by Preference(SharedPreferenceConstants.IS_MECHANIC, false)
    var isShopRegistered by Preference(SharedPreferenceConstants.IS_USER_REGISTERED, false)
    var isOtpVerified by Preference(SharedPreferenceConstants.IS_OTP_VERIFIED, false)
}