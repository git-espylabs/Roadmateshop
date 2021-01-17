package com.roadmate.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.adapter.ShopServicesListAdapter
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.ShopServiceMaster
import com.roadmate.shop.api.response.ShopServicesTrans
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.*
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.ui.activities.AddNewServiceActivity
import com.roadmate.shop.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_shop_service.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class ShopServicesFragment: BaseFragment() {

    private fun getServicesList(){
        showProgress(true)
        try {
            lifecycleScope.launch {
                val response = APIManager.call<ApiServices, Response<ShopServiceMaster>> {
                    getShopServicesList(serviceListJsonRequest())
                }
                if (isAdded) {
                    if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                        populateServicesList(response.body()?.data!!)
                    }else{
                        rvServices.visibility = View.GONE
                        empty_caution.visibility = View.VISIBLE
                    }
                    showProgress(false)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateServicesList(list: ArrayList<ShopServicesTrans>){
        rvServices.visibility = View.VISIBLE
        empty_caution.visibility = View.GONE

        rvServices.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        val adapter = ShopServicesListAdapter(activity!!, list)
        rvServices.adapter = adapter
    }

    private fun serviceListJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shop", ShopDetails().shopId)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun showProgress(show: Boolean){
        if (show){
            progress.visibility = View.VISIBLE
        }else{
            progress.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getServicesList()
        }elseDo {
            rvServices.visibility = View.GONE
            empty_caution.visibility = View.VISIBLE
            activity!!.toast {
                message = "No Internet connectivity!!"
                duration = Toast.LENGTH_SHORT
            }
        }
        addvehicle.setOnClickListener {
            activity!!.launchActivity<AddNewServiceActivity> {
                putExtra(AddNewServiceActivity.EXTRA_TITLE, "Add Service")
                putExtra(AddNewServiceActivity.EXTRA_SUBMIT_BTN_NAME, "Add  vehicle")
            }
        }
    }
}