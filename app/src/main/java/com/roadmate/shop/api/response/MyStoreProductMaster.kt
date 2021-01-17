package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName
import com.roadmate.shop.api.response.MyStoreProductTrans

data class MyStoreProductMaster(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ArrayList<MyStoreProductTrans>
)