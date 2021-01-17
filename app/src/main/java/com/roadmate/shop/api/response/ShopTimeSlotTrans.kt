package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class ShopTimeSlotTrans(
    @SerializedName("id") val id: String,
    @SerializedName("timeslot") val timeslots: String,
    @SerializedName("shop_id") val shop_id: String
)