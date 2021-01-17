package com.roadmate.shop.models

import android.os.Parcel
import android.os.Parcelable
import com.roadmate.shop.preference.ShopDetails
import kotlinx.android.parcel.Parcelize

@Parcelize
class ServiceInsertModel(
    var shopid: String = ShopDetails().shopId,
    var shopcat: String = ShopDetails().shopType,
    var vehicle: String = "",
    var model: String = "",
    var brand: String = ""
):Parcelable{
    /*var shopid: String = shopid
        get() = field

    var shopcat: String = shopcat
        get() = field

    var vehicle: String = vehicle
        get() = field

    var model: String = model
        get() = field

    var brand: String = brand
        get() = field*/

    /*constructor(parcel: Parcel) {
        this.shopid = parcel.readString().toString()
        this.shopcat = parcel.readString().toString()
        this.vehicle = parcel.readString().toString()
        this.model = parcel.readString().toString()
        this.brand = parcel.readString().toString()
    }*/

    /*override fun describeContents(): Int {
        return 0;
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(shopid)
        dest?.writeString(shopcat)
        dest?.writeString(vehicle)
        dest?.writeString(model)
        dest?.writeString(brand)
    }

    companion object CREATOR : Parcelable.Creator<ServiceInsertModel> {
        override fun createFromParcel(parcel: Parcel): ServiceInsertModel {
            return ServiceInsertModel(parcel)
        }

        override fun newArray(size: Int): Array<ServiceInsertModel?> {
            return arrayOfNulls(size)
        }
    }*/
}