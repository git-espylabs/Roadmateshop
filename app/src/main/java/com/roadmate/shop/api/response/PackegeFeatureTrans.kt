package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class PackegeFeatureTrans(
    @SerializedName("id") val id: Int,
    @SerializedName("featurid") val featurid: Int,
    @SerializedName("feature") val feature: String

    )
{
    constructor() : this(0,0,"");
}