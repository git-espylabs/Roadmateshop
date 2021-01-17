package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class BookedPackageDetailsTrans(
    @SerializedName("id") val id: String,
    @SerializedName("package_type") val package_type: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("package_for") val package_for: String,
    @SerializedName("fuel") val fuel: String,
    @SerializedName("image") val image: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("offer_amount") val offer_amount: String,
    @SerializedName("shop_amount") val shop_amount: String,
    @SerializedName("vehmodel") val vehmodel: String,
    @SerializedName("vehtype") val vehtype: String,
    @SerializedName("feature") val feature: String,
    @SerializedName("category") val category: String,
    @SerializedName("fuel_type") val fuel_type: String,
    @SerializedName("brand_model") val brand_model: String,
    @SerializedName("veh_type") val veh_type: String
)