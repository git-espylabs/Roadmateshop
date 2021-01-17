package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class BookingTypeTrans(
    @SerializedName("id") val id: String,
    @SerializedName("package_type") val package_type: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("package_for") val package_for: String,
    @SerializedName("image") val image: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("offer_amount") val offer_amount: String,
    @SerializedName("shop_amount") val shop_amount: String,
    @SerializedName("veh_type") val veh_type: String,
    @SerializedName("brand_model") val brand_model: String,
    @SerializedName("small_desc") val small_desc: String,
    @SerializedName("normal_amount") val normal_amount: String,
    @SerializedName("pic") val pic: String,
    @SerializedName("offer_end_date") val offer_end_date: String
)