package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class ShopServicesTrans(
    @SerializedName("id") val id: String,
    @SerializedName("shop_category") val shop_category: String,
    @SerializedName("brand_model") val brand_model: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("veh_type") val veh_type: String
)