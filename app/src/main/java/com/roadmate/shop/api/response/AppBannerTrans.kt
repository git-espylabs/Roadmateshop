package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class AppBannerTrans (
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("normal_amount") val normal_amount: String,
    @SerializedName("discount_amount") val discount_amount: String,
    @SerializedName("distance") val distance: String,
    @SerializedName("banner_image") val bannerImage: String
)