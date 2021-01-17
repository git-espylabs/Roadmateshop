package com.roadmate.shop.preference

import com.roadmate.shop.constants.SharedPreferenceConstants

class DefaultVehicleDetails {

    var vehicleId by Preference(SharedPreferenceConstants.DEF_VEH_ID, "")
    var vehicleNo by Preference(SharedPreferenceConstants.DEF_VEH_NO, "")
    var vehicleModel by Preference(SharedPreferenceConstants.DEF_VEH_MODEL, "")
    var vehicleType by Preference(SharedPreferenceConstants.DEF_VEH_TYPE, "")
    var vehicleFuelType by Preference(SharedPreferenceConstants.DEF_VEH_FUEL_TYPE, "")
}