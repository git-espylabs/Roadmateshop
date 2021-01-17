package com.roadmate.shop.preference

import com.roadmate.shop.constants.SharedPreferenceConstants

class AppEvents {
    var isShowcasePresented by Preference(SharedPreferenceConstants.SHOW_CASE_PRESENTED, false)
    var isRegistrationBonusCreditAlertShown by Preference(SharedPreferenceConstants.SHOW_REGISTRATION_BONUS, false)
}