package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class MoreServicesMaster(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ArrayList<MoreServicesTrans>
)