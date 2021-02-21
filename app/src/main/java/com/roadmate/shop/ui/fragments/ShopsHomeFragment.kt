package com.roadmate.shop.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.roadmate.shop.R
import com.roadmate.shop.adapter.BannerAdapter
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.AppBannerMaster
import com.roadmate.shop.api.response.AppBannerTrans
import com.roadmate.shop.api.response.BookedPackageDetailsMaster
import com.roadmate.shop.api.response.RoadmateApiResponse
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.*
import com.roadmate.shop.location.AppLocation
import com.roadmate.shop.location.AppLocationListener
import com.roadmate.shop.log.AppLogger
import com.roadmate.shop.preference.AppEvents
import com.roadmate.shop.preference.FcmDetails
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.rmapp.AppSession
import com.roadmate.shop.ui.activities.*
import kotlinx.android.synthetic.main.fragment_shop_home.*
import kotlinx.android.synthetic.main.fragment_shop_register_first.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.util.*


class ShopsHomeFragment: BaseFragment(), View.OnClickListener, AppLocationListener {

    var bannerHandler = Handler()
    var bannerSwipeTimer = Timer()
    private var currentPage = 0
    private var NUM_PAGES = 0
    private var isBannerTimerRunning = false

    private fun setListeners(){
        getDetails.setOnClickListener(this)
        btnAddBookingTime.setOnClickListener(this)
        btnShopOffers.setOnClickListener(this)
        btnBookedDetails.setOnClickListener(this)
    }


    private fun registerForFcm(){
        FcmDetails().isFcmRegistered.doIfFalse {
            if (FcmDetails().fcmToken != ""){
                processFcmRegistration()
            }else{
                requestFcmToke()
            }
        }elseDo {
            processAppBanner()
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
                processAppBanner()
            }
        })
    }

    private fun processFcmRegistration(){
        lifecycleScope.launch {
            try {
                val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                    registerForFcm(fcmRegistrationJsonRequest())
                }
                if (response.isSuccessful && response.body()?.message == "Success"){
                    FcmDetails().isFcmRegistered = true
                    AppLogger.info("FCM Registered")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            processAppBanner()
        }
    }


    private fun fcmRegistrationJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shopid", ShopDetails().shopId)
            json.put("fcmid", FcmDetails().fcmToken)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun processAppBanner(){
        lifecycleScope.launch{
            val response = APIManager.call<ApiServices, Response<AppBannerMaster>> {
                getAppBanners()
            }
            if (response.isSuccessful && response.body()?.message =="Success"){
                AppSession.appBannerList = response.body()?.data!!
                populateAppBanner(response.body()?.data!!)
            }
//            checkPackageExist()
        }
    }

    private fun checkPackageExist(){

        lifecycleScope.launch{
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                shopackagexists(getPackageExistanceRequest())
            }
            if (response.isSuccessful && response.body()?.message =="Success"){
                optionalLay1.visibility = View.VISIBLE
            }else{
                optionalLay1.visibility = View.GONE
            }
        }
    }

    private fun populateAppBanner(bannerList: ArrayList<AppBannerTrans>){
        NUM_PAGES = bannerList.size
        val viewPagerAdapter = BannerAdapter(activity!!, bannerList)
        viewPager.adapter = viewPagerAdapter

        bannerSwipeTimer.schedule(object : TimerTask() {
            override fun run() {
                isBannerTimerRunning = true
                bannerHandler.post {
                    if (currentPage == NUM_PAGES) {
                        currentPage = 0
                    }
                    viewPager.setCurrentItem(currentPage++, true)
                }
            }
        }, 0, 3000)

    }

    private fun listBookedPackageDetails(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<BookedPackageDetailsMaster>> {
                getBookedPackageDetails(getPackageDetailsRequest())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                val mMap = HashMap<String, String>()
                mMap["image"] = response.body()?.data!![0].image
                mMap["pName"] = response.body()?.data!![0].title
                mMap["pDesc"] = response.body()?.data!![0].description
                mMap["pAmount"] = response.body()?.data!![0].amount
                mMap["pOfferAmount"] = response.body()?.data!![0].offer_amount
                mMap["pFor"] = response.body()?.data!![0].package_for
                mMap["pType"] = response.body()?.data!![0].category
                mMap["pVehType"] = response.body()?.data!![0].veh_type
                mMap["pVehModel"] = response.body()?.data!![0].brand_model
                mMap["pVehFuel"] = response.body()?.data!![0].fuel_type

                val intent = Intent(context, PackageDetailsActivity::class.java)
                intent.putExtra("packageMap", mMap)
                startActivity(intent)
            }else{
                activity!!.toast {
                    message = "No records found!"
                    duration = Toast.LENGTH_LONG
                }
            }
            pcodeEditText.setText("")
            showProgress(false)
        }
    }

    private fun getPackageExistanceRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shopid", ShopDetails().shopId)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun getPackageDetailsRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("randomnumber", pcodeEditText.text.toString())
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun showProgress(show: Boolean){
        if (show){
            getDetails.visibility = View.GONE
            spin_kit.visibility = View.VISIBLE
        }else{
            spin_kit.visibility = View.GONE
            getDetails.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (activity != null) {
            activity!!.window
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        }
        return inflater.inflate(R.layout.fragment_shop_home, container, false)
    }

    private fun getLocation(){
        AppLocation().getAppLocation(this, context!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getLocation()
        setListeners()
        registerForFcm()
    }

    override fun onStop() {
        super.onStop()
        bannerHandler.removeCallbacks(null)
        bannerSwipeTimer.cancel()
        isBannerTimerRunning = false
    }

    override fun onResume() {
        super.onResume()
        if (AppSession.appBannerList.isNotEmpty() && !isBannerTimerRunning){
            bannerHandler= Handler()
            bannerSwipeTimer = Timer()
            populateAppBanner(AppSession.appBannerList)
        }
    }

    override fun onClick(v: View?) {

        when(v?.id){
            R.id.getDetails -> {
                if (pcodeEditText.text.isNotEmpty()){
                    listBookedPackageDetails()
                }
            }
            R.id.btnShopOffers -> {
                activity?.startActivity<ShopOffersActivity>()
            }
            R.id.btnAddBookingTime -> {
                activity?.startActivity<ShopTimeSlotsActivity>()
            }
            R.id.btnBookedDetails -> {
                activity?.startActivity<BookedCustomersListActivity>()
            }
        }
    }

    override fun onLocationReceived(location: Location) {
        (location != null).doIfTrue {
            AppSession.appLatitude = ""+location.latitude
            AppSession.appLongitude = ""+location.longitude
        }elseDo {
            AppLogger.error("Location not received!")
        }
    }

    override fun onLocationSettingsSatisfied() {
        TODO("Not yet implemented")
    }
}