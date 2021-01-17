package com.roadmate.shop.preference

import com.roadmate.shop.constants.SharedPreferenceConstants

class ShopRegDetails {
    var check_status by Preference(SharedPreferenceConstants.CHECK_STATUS, "")
    var shoptypeId by Preference(SharedPreferenceConstants.SHOP_TYPE_ID, "")
    var shopnameReg by Preference(SharedPreferenceConstants.SHOP_NAME_REG, "")

    var shopMobile by Preference(SharedPreferenceConstants.SHOP_MOB, "")
    var shoplandline by Preference(SharedPreferenceConstants.SHOP_LANDLINE, "")

    var shopdesc by Preference(SharedPreferenceConstants.SHOP_DESC, "")
    var shop_open_time by Preference(SharedPreferenceConstants.SHOP_OPEN_TIME, "")
    var shop_close_time by Preference(SharedPreferenceConstants.SHOP_CLOSE_TIME, "")

}