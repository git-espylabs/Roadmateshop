package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class ProductDetailsTrans(
    @SerializedName("id") val pid: String,
    @SerializedName("product_name") val pname: String,
    @SerializedName("price") val pprice: String,
    @SerializedName("image_1") val pimg1: String = "",
    @SerializedName("image_2") val pimg2: String = "",
    @SerializedName("image_3") val pimg3: String = "",
    @SerializedName("store_prod_category") val category: String = "",
    @SerializedName("user_id") val userId: String,
    @SerializedName("phnum") val phone: String = "",
    @SerializedName("created_at") val createdat: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("cat_name") val categoryName: String = "",
    @SerializedName("sex") val sex: String = "",
    @SerializedName("name") val username: String = ""
)