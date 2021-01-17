package com.roadmate.shop.ui.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.roadmate.shop.R
import com.roadmate.shop.constants.FragmentConstants
import com.roadmate.shop.preference.ShopDetails
import kotlinx.android.synthetic.main.fragment_shop_edit_first.*
import java.util.*

class ShopEditFragmentFirst: BaseFragment(), View.OnClickListener {
    var shopTypeId:String?=""

    private fun setDefaults(){
        edt_username.setText(ShopDetails().shopName)
        edt_number.setText(arguments!!["phone_number"].toString())
        edt_description.setText(arguments!!["descriptionShop"].toString())
        edt_opentime.text = arguments!!["open_time"].toString()
        edt_closetime.text = arguments!!["close_time"].toString()
    }

    private fun isInputsValid(): Boolean {
        if (edt_username.text.toString() == "") {
            edt_username.error = "Enter ShopName"
            return false
        } else if (edt_number.text.toString() == "") {
            edt_number.error = "Enter mobile number"
            return false
        }
        return true
    }

    private fun processShopEdit() {
        if (isInputsValid()) {
            val mMap = HashMap<String, String>()
            mMap["shopnameReg"] = edt_username.text.toString()
            mMap["shopMobile"] = edt_number.text.toString()
            mMap["shopdesc"] = edt_description.text.toString()
            mMap["shop_open_time"] = edt_opentime.text.toString()
            mMap["shop_close_time"] = edt_closetime.text.toString()
            mMap["address"] = arguments!!["address"].toString()
            mMap["pincode"] = arguments!!["pincode"].toString()
            mMap["imageName"] = arguments!!["imageName"].toString()
            mMap["lattitude"] = arguments!!["lattitude"].toString()
            mMap["logitude"] = arguments!!["logitude"].toString()

            val bundle = Bundle()
            bundle.putSerializable("regMap", mMap)

            setFragment(ShopEditFragmentSecond(), FragmentConstants.SHOP_EDIT_FRAGMENT_SECOND, bundle, true, R.id.container)
        }
    }

    private fun showProgress(visible: Boolean) {
        if (visible) {
            spinBar.visibility = View.VISIBLE
        } else {
            spinBar.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_edit_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setDefaults()
        btn_save.setOnClickListener(this)
        login.setOnClickListener(this)
        tandclayout.setOnClickListener(this)
        edt_shoptype.setOnClickListener(this)

        edt_opentime.setOnClickListener(this)
        edt_closetime.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.btn_save -> {
                processShopEdit()
            }
            R.id.edt_opentime -> {

                // TODO Auto-generated method stub
                val mcurrentTime = Calendar.getInstance()
                val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
                val minute = mcurrentTime[Calendar.MINUTE]
                val mTimePicker: TimePickerDialog
                mTimePicker = TimePickerDialog(
                    activity,
                    { timePicker, selectedHour, selectedMinute ->
                        val AM_PM: String
                        AM_PM = "am"
                        /*if(selectedHour < 12) {

                                        } else {
                                            AM_PM = "pm";
                                        }*/edt_opentime.text =
                        "$selectedHour:$selectedMinute $AM_PM"
                    }, hour, minute, true
                ) //Yes 24 hour time

                mTimePicker.setTitle("Select Time")
                mTimePicker.show()
            }
            R.id.edt_closetime -> {

                // TODO Auto-generated method stub
                val mcurrentTime = Calendar.getInstance()
                val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
                val minute = mcurrentTime[Calendar.MINUTE]
                val mTimePicker: TimePickerDialog
                mTimePicker = TimePickerDialog(
                    activity,
                    { timePicker, selectedHour, selectedMinute ->
                        val AM_PM: String
                        AM_PM = "pm"
                        edt_closetime.text = "$selectedHour:$selectedMinute $AM_PM"
                    }, hour, minute, true
                ) //Yes 24 hour time

                mTimePicker.setTitle("Select Time")
                mTimePicker.show()
            }
            else -> null
        }
    }
}