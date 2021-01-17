package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName
import com.roadmate.shop.api.response.VehicleTypeTrans

data class VehicleTypeMaster(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ArrayList<VehicleTypeTrans>
)