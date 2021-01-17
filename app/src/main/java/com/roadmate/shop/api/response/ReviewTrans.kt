package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class ReviewTrans(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val user_id: String,
    @SerializedName("shop_id") val shop_id: String,
    @SerializedName("review_count") val review_count: String,
    @SerializedName("comment") val comment: String,
    @SerializedName("name") val name: String
)