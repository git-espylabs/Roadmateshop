package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class TermsConditionsTrans(
    @SerializedName("id") val id: String,
    @SerializedName("tc_details") val tc_details: String
)