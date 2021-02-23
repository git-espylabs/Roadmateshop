package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class ShopOffersTrans(
    @SerializedName("id") val id: String,
    @SerializedName("shop_id") val shop_id: String,
    @SerializedName("shopname") val shopname: String,
    @SerializedName("shop_cat_id") val shop_cat_id: String,
    @SerializedName("title") val title: String,
    @SerializedName("small_desc") val small_desc: String,
    @SerializedName("normal_amount") val normal_amount: String,
    @SerializedName("offer_amount") val offer_amount: String,
    @SerializedName("vehicle_typeid") val vehicle_typeid: String,
    @SerializedName("brand_id") val brand_id: String,
    @SerializedName("model_id") val model_id: String,
    @SerializedName("offer_type") val offer_type: String,
    @SerializedName("pic") val pic: String,
    @SerializedName("category") val category: String,
    @SerializedName("brand_model") val brand_model: String,
    @SerializedName("vehice_type_name") val vehice_type_name: String,
    @SerializedName("offer_discount_type") val offer_discount_type: String,
    @SerializedName("discount_percentage") val discount_percentage: String,
    @SerializedName("brand") val brand: String
)