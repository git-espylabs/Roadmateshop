package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class VehicleBrandModelMaster(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String,
    @SerializedName("brand_model_list") val data: ArrayList<VehicleBrandModelTrans>
)