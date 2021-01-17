package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class VehicleBrandTrans(
    @SerializedName("id") val vehicleBrandId: String = "",
    @SerializedName("brand") val vehicleBrand: String = "",
    var isSelected: Boolean = false
){
    constructor() : this("", "")
}