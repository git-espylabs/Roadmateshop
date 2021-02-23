package com.roadmate.shop.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.adapter.ShopOffersAdapter
import com.roadmate.shop.adapter.ShopStoreProductOfferAdapter
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.AppBannerMaster
import com.roadmate.shop.api.response.AppBannerTrans
import com.roadmate.shop.api.response.ShopOffersMaster
import com.roadmate.shop.api.response.ShopOffersTrans
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.startActivity
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.rmapp.AppSession
import com.roadmate.shop.ui.activities.CreateOfferActivity
import com.roadmate.shop.ui.activities.OfferDetailsActivity
import com.roadmate.shop.ui.activities.ProductOfferDetailsActivity
import com.roadmate.shop.ui.activities.ShopHomeActivity
import com.roadmate.shop.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_shop_offers.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


import java.util.HashMap

class ShopOffersFragment: BaseFragment() {

    private fun getProductOffers(){
        showProgress(true)
        lifecycleScope.launch{
            val response = APIManager.call<ApiServices, Response<AppBannerMaster>> {
                getStoreBanners(bannerJsonRequest())
            }
            if (response.isSuccessful && response.body()?.message =="Success"){
                populateBannerAsList(response.body()?.data!!)
            }else{
                productOfferLay.visibility = View.GONE
            }

            getOffersList()
        }
    }

    private fun bannerJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shop_id", ShopDetails().shopId)
            json.put("lat", AppSession.appLatitude)
            json.put("long", AppSession.appLongitude)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun populateBannerAsList(bannerList: java.util.ArrayList<AppBannerTrans>){
        productOfferLay.visibility = View.VISIBLE

        productList.layoutManager = LinearLayoutManager(activity!!, RecyclerView.HORIZONTAL, false)
        val adapter = ShopStoreProductOfferAdapter(activity!!, bannerList){it ->
            val intent = Intent(context, ProductOfferDetailsActivity::class.java)
            intent.putExtra("offerid", it!!.id)
            intent.putExtra("startsrc", "offerhome")
            startActivity(intent)
        }
        productList.adapter = adapter
    }

    private fun getOffersList(){

        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<ShopOffersMaster>> {
                getShopOffers(offerListJsonRequest())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateOffersList(response.body()?.data!!)
            }else{
                empty_caution.visibility = View.VISIBLE
                offersList.visibility = View.GONE
            }
            showProgress(false)
        }
    }

    private fun offerListJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("offertype", "1")
            json.put("shopid", ShopDetails().shopId)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun populateOffersList(list: ArrayList<ShopOffersTrans>){
        empty_caution.visibility = View.GONE
        offersList.visibility = View.VISIBLE
        offersList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        val adapter = ShopOffersAdapter(activity!!, list){offerObj ->
            val mMap = HashMap<String, String>()
            mMap["id"] = offerObj?.id!!
            mMap["shop_id"] = offerObj.shop_id
            mMap["shopname"] = offerObj.shopname
            mMap["shop_cat_id"] = offerObj.shop_cat_id
            mMap["title"] = offerObj.title
            mMap["small_desc"] = offerObj.small_desc
            mMap["normal_amount"] = offerObj.normal_amount
            mMap["offer_amount"] = offerObj.offer_amount
            mMap["vehicle_typeid"] = offerObj.vehicle_typeid
            mMap["brand_id"] = offerObj.brand_id
            mMap["model_id"] = offerObj.model_id
            mMap["offer_type"] = offerObj.offer_type
            mMap["pic"] = offerObj.pic
            mMap["category"] = offerObj.category
            mMap["brand_model"] = offerObj.brand_model
            mMap["vehice_type_name"] = offerObj.vehice_type_name
            mMap["brand"] = offerObj.brand
            mMap["discount_type"] = offerObj.offer_discount_type
            mMap["disc_percent"] = offerObj.discount_percentage

            val intent = Intent(context, OfferDetailsActivity::class.java)
            intent.putExtra("dataMap", mMap)
            startActivity(intent)
        }
        offersList.adapter = adapter
    }

    private fun showProgress(show: Boolean){
        show.doIfTrue {
            progress.visibility = View.VISIBLE
        }elseDo {
            progress.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_offers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnCreateOffer.setOnClickListener {
            activity?.startActivity<CreateOfferActivity>()
        }
    }

    override fun onResume() {
        super.onResume()

        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getProductOffers()
        }elseDo {
            productOfferLay.visibility = View.GONE
            serviceOfferLay.visibility = View.GONE
            activity!!.toast {
                message = "No Internet connectivity!!"
                duration = Toast.LENGTH_SHORT
            }
        }
    }
}