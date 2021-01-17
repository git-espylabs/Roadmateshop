package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class VehicleModelTrans(
    @SerializedName("id") val vehicleModelId: String = "",
    @SerializedName("model") val vehicleModel: String = "",
    @SerializedName("brid") val brandId: String = "",
    var isSelected: Boolean = false

){
    constructor() : this("", "")
}