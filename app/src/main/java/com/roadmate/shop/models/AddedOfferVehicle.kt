package com.roadmate.shop.models

data class AddedOfferVehicle(
    val shopId: String = "",
    val offerid: String = "",
    val vehicletype: String = "",
    val brand: String = "",
    val brandName: String = "",
    val model: String = "",
    val modelName: String = "",
    val fuel_type: String = "",
    val fuel_typeName: String = ""
){
    constructor() : this("", "", "", "","", "", "", "", "")
}