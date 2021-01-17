package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class MyStoreProductTrans(
    @SerializedName("id") val pid: String,
    @SerializedName("product_name") val pname: String,
    @SerializedName("user_type") val user_type: String,
    @SerializedName("price") val pprice: String,
    @SerializedName("image_1") val pimg1: String = "",
    @SerializedName("image_2") val pimg2: String = "",
    @SerializedName("image_3") val pimg3: String = "",
    @SerializedName("store_prod_category") val category: String = "",
    @SerializedName("user_id") val user_id: String,
    @SerializedName("name") val username: String,
    @SerializedName("phnum") val phone: String = "",
    @SerializedName("shopname") val shopname: String = "",
    @SerializedName("shopnumber") val shopnumber: String = "",
    @SerializedName("created_at") val createdat: String = ""
)