package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class VehicleTypeTrans (
    @SerializedName("id") val vehicleTypeId: String = "",
    @SerializedName("veh_type") val vehicleType: String = ""


) {
    override fun toString(): String {
        return vehicleType
    }

    constructor() : this("", "")
}