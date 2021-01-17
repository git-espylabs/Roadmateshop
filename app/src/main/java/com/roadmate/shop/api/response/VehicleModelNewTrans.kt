package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class VehicleModelNewTrans(
    @SerializedName("model_id") val vehicleModelId: String = "",
    @SerializedName("model_name") val vehicleModel: String = "",
    @SerializedName("brand_id") val brandId: String = "",
    var isSelected: Boolean = false

){
    constructor() : this("", "")
}