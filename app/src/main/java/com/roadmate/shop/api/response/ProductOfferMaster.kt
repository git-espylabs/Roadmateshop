package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class ProductOfferMaster(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ArrayList<ProductOfferTrans>
)