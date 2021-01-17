package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class VehicleFuelTypeTrans(
    @SerializedName("id") val vehicleFuelTypeId: String,
    @SerializedName("fuel_type") val vehicleFuelType: String
){

    constructor() : this("", "")
}