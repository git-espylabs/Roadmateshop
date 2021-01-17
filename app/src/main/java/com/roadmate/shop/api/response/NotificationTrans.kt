package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class NotificationTrans(
    @SerializedName("id") val id: String,
    @SerializedName("notificationtable_typeid") val notificationtable_typeid: String,
    @SerializedName("notification_title") val notification_title: String,
    @SerializedName("user_id") val user_id: String,
    @SerializedName("notification_message") val notification_message: String
)