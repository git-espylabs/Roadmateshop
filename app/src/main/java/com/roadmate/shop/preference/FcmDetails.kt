package com.roadmate.shop.preference

import com.roadmate.shop.constants.SharedPreferenceConstants

class FcmDetails {
    var fcmToken by Preference(SharedPreferenceConstants.FCM_TOKEN, "")
    var isFcmRegistered by Preference(SharedPreferenceConstants.FCM_REGISTERED, false)
}