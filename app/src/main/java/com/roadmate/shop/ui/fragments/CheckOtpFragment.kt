package com.roadmate.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.roadmate.shop.R
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.*
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.*
import com.roadmate.shop.log.AppLogger
import com.roadmate.shop.preference.FcmDetails
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.rmapp.AppSession
import com.roadmate.shop.ui.activities.PaymentActivity
import com.roadmate.shop.ui.activities.ShopHomeActivity
import com.roadmate.shop.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_check_otp.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class CheckOtpFragment : Fragment(), View.OnClickListener {

    private fun otpJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("otp", otp_view.text.toString())
//            json.put("otp", AppSession.otpTemp)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun processVerification(){
        if (otp_view.text.toString() != ""){
            btnVerifyOtp.visibility = View.GONE
            spin_kit.visibility = View.VISIBLE
            lifecycleScope.launch{
                val  response = APIManager.call<ApiServices, Response<OtpMaster>> {
                    verifyOTP(otpJsonRequest())  }

                if (response.isSuccessful && response.body()?.message == "Success"){
                    ShopDetails().isOtpVerified = true
                    getMyAddedCategories(response.body()?.data!![0])
                }
            }
        }else{
            activity?.toast {
                message = "Enter a valid OTP"
                duration = Toast.LENGTH_LONG
            }
        }
    }

    private fun getMyAddedCategories(data: OtpTrans){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<MoreServicesMaster>> {
                getAddedShopTypes(shopTypesRequestJSON(data.shop_id))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                for (item in response.body()?.data!!){
                    if (item.serviceId == "1"){
                        ShopDetails().isMechanic = true
                        break
                    }
                }
            }
            getShopPackageCount(data)
        }
    }

    private fun getShopPackageCount(data: OtpTrans){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<ShopPackageCountMatser>> {
                geShopPackageCount(packageCountRequest(data.shop_id))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                try {
                    AppSession.shopPackageCount = response.body()?.data!![0].shopackagecount.toInt()
                } catch (e: Exception) {
                }
            }
            initializeUserSettings(data)
        }
    }

    private fun packageCountRequest(shopid: String) : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shopid", shopid)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun shopTypesRequestJSON(shopid:String) : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shopid", shopid)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun registerForFcm(){
        if (FcmDetails().fcmToken != ""){
            FcmDetails().isFcmRegistered.doIfFalse {
                processFcmRegistration()
            }
        }else{
            requestFcmToke()
        }
    }

    private fun requestFcmToke(){
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener {
            it.isSuccessful.doIfTrue {
                AppLogger.info("FCM Token", it.result!!.token)
                if (it.result!!.token != "") {
                    FcmDetails().fcmToken = it.result!!.token
                    processFcmRegistration()
                }
            }elseDo {
                AppLogger.info("FCM Token", "getInstanceId failed: " + it.exception)
                moveToNexScreen()
            }
        })
    }

    private fun processFcmRegistration(){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                registerForFcm(fcmRegistrationJsonRequest())
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                AppLogger.info("FCM Registered")
            }
            moveToNexScreen()
        }
    }

    private fun fcmRegistrationJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("fcm_token", FcmDetails().fcmToken)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun initializeUserSettings(data: OtpTrans){
        ShopDetails().isUserLoggedIn = true
        ShopDetails().shopId = data.shop_id
        ShopDetails().shopType = data.type
        ShopDetails().shopName = data.shopname
        ShopDetails().isActiveUser = data.paystatus == "1"
        moveToNexScreen()
    }
    private fun moveToNexScreen(){
        activity?.toast {
            message = "Phone number verified"
            duration = Toast.LENGTH_LONG
        }
        ShopDetails().isActiveUser.doIfTrue {
            activity?.startActivity<ShopHomeActivity>()
        }elseDo {
            activity?.startActivity<PaymentActivity>()
        }
        activity?.finish()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_check_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnVerifyOtp.setOnClickListener(this)

        phone.text = getString(R.string.otp_prompt) + AppSession.userMobile
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnVerifyOtp ->{
                NetworkUtils.isNetworkConnected(activity).doIfTrue {
                    processVerification()
                }elseDo {
                    activity!!.toast {
                    message = "No Internet Connectivity!"
                    duration = Toast.LENGTH_LONG }}
            }
        }
    }
}