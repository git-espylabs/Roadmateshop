package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName
import com.roadmate.app.api.response.VehicleFuelTypeTrans

data class PackageFeatureMaster (
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ArrayList<PackegeFeatureTrans>
)