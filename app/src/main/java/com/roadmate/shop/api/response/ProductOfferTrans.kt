package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class ProductOfferTrans(
    @SerializedName("id") val id: String,
    @SerializedName("shop_id") val shop_id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("normal_amount") val normal_amount: String,
    @SerializedName("discount_amount") val discount_amount: String,
    @SerializedName("end_date") val end_date: String,
    @SerializedName("product_picture") val product_picture: String,
    @SerializedName("shopname") val shopname: String
)