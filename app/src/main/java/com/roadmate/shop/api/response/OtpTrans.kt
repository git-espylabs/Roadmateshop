package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class OtpTrans(
    @SerializedName("id") val shop_id: String,
    @SerializedName("shopname") val shopname: String,
    @SerializedName("pay_status") val paystatus: String,
    @SerializedName("type") val type: String
)