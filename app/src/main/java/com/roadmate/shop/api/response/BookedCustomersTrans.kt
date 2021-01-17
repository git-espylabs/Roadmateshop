package com.roadmate.shop.api.response

import com.google.gson.annotations.SerializedName

data class BookedCustomersTrans(
    @SerializedName("work_status") val work_status: String,
    @SerializedName("pay_status") val pay_status: String,
    @SerializedName("id") val id: String,
    @SerializedName("customer_id") val customer_id: String,
    @SerializedName("name") val name: String,
    @SerializedName("timeslots") val timeslot: String,
    @SerializedName("book_id") val book_id: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("brand_model") val brand_model: String,
    @SerializedName("fuel_type") val fuel_type: String,
    @SerializedName("book_type") val book_type: String
)