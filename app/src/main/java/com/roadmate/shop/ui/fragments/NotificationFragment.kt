package com.roadmate.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.adapter.NotificationListAdapter
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.NotificationMaster
import com.roadmate.shop.api.response.NotificationTrans
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class NotificationFragment: Fragment() {

    private fun getNotificationsList(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<NotificationMaster>> {
                getUserNotifications(requestJSON())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateList(response.body()?.data!!)
            }else{
                empty_caution.visibility = View.VISIBLE
                notification_recycler.visibility = View.GONE
            }
            showProgress(false)
        }
    }

    private fun populateList(list: ArrayList<NotificationTrans>){
        empty_caution.visibility = View.GONE
        notification_recycler.visibility = View.VISIBLE

        notification_recycler.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        val adapter =  NotificationListAdapter(activity!!, list){

        }
        notification_recycler.adapter = adapter
    }

    private fun requestJSON() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("userid", ShopDetails().shopId)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun showProgress(show:Boolean){
        if (show){
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
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getNotificationsList()
        }elseDo {
            empty_caution.visibility = View.VISIBLE
            notification_recycler.visibility = View.GONE
            activity!!.toast {
                message = "No internet connectivity!"
                duration = Toast.LENGTH_SHORT
            }
        }
    }
}