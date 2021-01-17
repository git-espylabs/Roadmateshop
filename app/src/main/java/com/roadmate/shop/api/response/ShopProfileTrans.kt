package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class ShopProfileTrans(
    @SerializedName("image") val image: String,
    @SerializedName("open_time") val open_time: String,
    @SerializedName("close_time") val close_time: String,
    @SerializedName("address") val address: String,
    @SerializedName("phone_number") val phone_number: String,
    @SerializedName("pincode") val pincode: String,
    @SerializedName("description") val description: String,
    @SerializedName("lattitude") val lattitude: String,
    @SerializedName("logitude") val logitude: String
)