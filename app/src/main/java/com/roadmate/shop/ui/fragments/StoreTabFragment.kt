package com.roadmate.shop.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.adapter.BannerAdapter
import com.roadmate.shop.adapter.MyStoreProductAdapter
import com.roadmate.shop.adapter.ShopStoreProductOfferAdapter
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.AppBannerMaster
import com.roadmate.shop.api.response.AppBannerTrans
import com.roadmate.shop.api.response.MyStoreProductMaster
import com.roadmate.shop.api.response.MyStoreProductTrans
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.rmapp.AppSession
import com.roadmate.shop.ui.activities.MyStoreDetailActivity
import com.roadmate.shop.ui.activities.OfferDetailsActivity
import com.roadmate.shop.ui.activities.ProductOfferDetailsActivity
import kotlinx.android.synthetic.main.fragment_shop_packge_main.*
import kotlinx.android.synthetic.main.fragment_store_tab.*
import kotlinx.android.synthetic.main.fragment_store_tab.viewPager
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class StoreTabFragment: Fragment(), OnItemSelectedListener{

    var bannerHandler = Handler()
    var bannerSwipeTimer = Timer()
    private var currentPage = 0
    private var NUM_PAGES = 0
    private var isBannerTimerRunning = false

    private fun processAppBanner(){
        lifecycleScope.launch{
            val response = APIManager.call<ApiServices, Response<AppBannerMaster>> {
                getStoreBanners(bannerJsonRequest())
            }
            if (response.isSuccessful && response.body()?.message =="Success"){
                populateBannerAsList(response.body()?.data!!)
            }
        }
    }

    private fun populateAppBanner(bannerList: java.util.ArrayList<AppBannerTrans>){
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

    private fun populateBannerAsList(bannerList: java.util.ArrayList<AppBannerTrans>){
        rvoffer.layoutManager = LinearLayoutManager(activity!!, RecyclerView.HORIZONTAL, false)
        val adapter = ShopStoreProductOfferAdapter(activity!!, bannerList){it ->
            val intent = Intent(context, ProductOfferDetailsActivity::class.java)
            intent.putExtra("offerid", it!!.id)
            startActivity(intent)
        }
        rvoffer.adapter = adapter
    }

    private fun populateCategoryList(){
        var category = resources.getStringArray(R.array.store_category)
        val arrayAdapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_dropdown_item,
            category
        )
        category_spinner.adapter = arrayAdapter
    }

    private fun getProductList(selectedCat: String){
        showProgress(true)

        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<MyStoreProductMaster>> {
                getStoreProducts(productListJsonRequest(selectedCat))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateProductList(response.body()?.data!!)
            }else{
                empty_caution.visibility = View.VISIBLE
                my_store_recyclerView.visibility = View.GONE
            }
            showProgress(false)
        }
    }

    private fun productListJsonRequest(selectedCat: String) : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("scat",selectedCat)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
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


    private fun populateProductList(list: ArrayList<MyStoreProductTrans>){
        empty_caution.visibility = View.GONE
        my_store_recyclerView.visibility = View.VISIBLE
        my_store_recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        var mAdapter = MyStoreProductAdapter(activity!!, list, false){pData, isSoldOut ->
            if (!isSoldOut) {
                val intent1 = Intent(context, MyStoreDetailActivity::class.java)
                intent1.putExtra("p_name", pData?.pname)
                intent1.putExtra("p_id", pData?.pid)
                startActivity(intent1)
            }
        }
        my_store_recyclerView.adapter = mAdapter
    }

    private fun showProgress(visible:Boolean){
        if (visible){
            loadingFrame.visibility = View.VISIBLE
        }else{
            loadingFrame.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_store_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        processAppBanner()
        populateCategoryList()

        try {
            category_spinner.onItemSelectedListener = this
        } catch (e: Exception) {
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        getProductList("0")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        /*if (position != 0) {
            getProductList(""+position)
        }else{
            empty_caution.visibility = View.VISIBLE
            my_store_recyclerView.visibility = View.GONE
        }*/
        getProductList(""+position)
    }

    override fun onStop() {
        super.onStop()
        /*bannerHandler.removeCallbacks(null)
        bannerSwipeTimer.cancel()
        isBannerTimerRunning = false*/
    }

    override fun onResume() {
        super.onResume()
        /*if (AppSession.storeBannerList.isNotEmpty() && !isBannerTimerRunning){
            bannerHandler= Handler()
            bannerSwipeTimer = Timer()
            populateAppBanner(AppSession.storeBannerList)
        }*/
    }
}