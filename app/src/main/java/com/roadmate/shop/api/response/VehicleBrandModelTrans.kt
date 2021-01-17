package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class VehicleBrandModelTrans(
    @SerializedName("brand_id") val vehicleBrandId: String = "",
    @SerializedName("brand_name") val vehicleBrand: String = "",
    @SerializedName("models") val models: ArrayList<VehicleModelNewTrans>,
    var isSelected: Boolean = false
)