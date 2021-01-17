package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class TimeSlotTrans(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)