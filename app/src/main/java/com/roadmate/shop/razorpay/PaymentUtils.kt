package com.roadmate.app.razorpay

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.razorpay.Checkout
import com.roadmate.shop.BuildConfig
import com.roadmate.shop.R
import com.roadmate.shop.preference.ShopDetails
import org.json.JSONObject

class PaymentUtils internal constructor(private val activity: Activity, private val payableAmount: Double){

    fun proceedPayment(){
        val co = Checkout()
        try {
            co.setImage(R.mipmap.ic_launcher_round)

            if (BuildConfig.FLAVOR == "staging" || BuildConfig.DEBUG){
                co.setKeyID(activity!!.resources.getString(R.string.razor_pay_key_staging));
            }else{
                co.setKeyID(activity!!.resources.getString(R.string.razor_pay_key_production));
            }

            val options = JSONObject()
            options.put("name", activity.resources.getString(R.string.app_name));
            options.put("description", "Roadmate Bill Payment");
            options.put("image", ContextCompat.getDrawable(activity!!, R.drawable.road_mate_plain));
            options.put("theme.color", "#D8BE11");
            options.put("currency", "INR");

            if (BuildConfig.FLAVOR == "staging" || BuildConfig.DEBUG){
                options.put("amount", 100);
            }else{
                options.put("amount", payableAmount*100);
            }

            val preFill = JSONObject()
            preFill.put("email", "")
            preFill.put("contact", ShopDetails().userMobile)

            options.put("prefill", preFill)

            co.open(activity, options);
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
}