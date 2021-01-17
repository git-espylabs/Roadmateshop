package com.roadmate.shop.ui.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.roadmate.shop.R
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.MoreServicesMaster
import com.roadmate.shop.api.response.RoadmateApiResponse
import com.roadmate.shop.api.response.ShopPackageCountMatser
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.constants.AppConstants.Companion.APP_STARTUP_DELAY
import com.roadmate.shop.extensions.doIfFalse
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.startActivity
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.rmapp.AppSession
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    private fun moveToNextScreen(){
        Handler().postDelayed({

            if (ShopDetails().isUserLoggedIn && ShopDetails().isActiveUser){
                this.startActivity<ShopHomeActivity>()
                this.finish()
            }else if (!ShopDetails().isUserLoggedIn){
                this.startActivity<LoginActivity>()
                this.finish()
            }else{
                this.startActivity<PaymentActivity>()
                this.finish()
            }
        }, APP_STARTUP_DELAY)
    }

    private fun getShopPackageCount(){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<ShopPackageCountMatser>> {
                geShopPackageCount(packageCountRequest())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                try {
                    AppSession.shopPackageCount = response.body()?.data!![0].shopackagecount.toInt()
                } catch (e: Exception) {
                }
            }
            getMyAddedCategories()
        }
    }

    private fun getMyAddedCategories(){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<MoreServicesMaster>> {
                getAddedShopTypes(shopCatsRequestJSON())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                for (item in response.body()?.data!!){
                    if (item.serviceId == "1"){
                        ShopDetails().isMechanic = true
                        break
                    }
                }
            }
            moveToNextScreen()
        }
    }

    private fun shopCatsRequestJSON() : RequestBody {
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

    private fun packageCountRequest() : RequestBody {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar!!.hide()

        if (ShopDetails().isUserLoggedIn){
            getShopPackageCount()
        }else{
            moveToNextScreen()
        }
    }
}