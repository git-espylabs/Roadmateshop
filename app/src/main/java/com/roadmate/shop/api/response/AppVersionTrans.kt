package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class AppVersionTrans(
    @SerializedName("version_code") val version_code: String,
    @SerializedName("version_name") val version_name: String
)