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
import com.roadmate.shop.adapter.ShopTimeSlotListAdapter
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.RoadmateApiResponse
import com.roadmate.shop.api.response.ShopTimeSlotMaster
import com.roadmate.shop.api.response.ShopTimeSlotTrans
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.startActivity
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.log.AppLog
import com.roadmate.shop.log.AppLogger
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.ui.activities.CreateTimeSlotActivity
import com.roadmate.shop.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_shop_time_slots.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class ShopTimeSlotsFragment: BaseFragment() {

    private fun getShopTimeSlotList(){
        showProgress(true)

        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<ShopTimeSlotMaster>> {
                getShopTimeSlotsList(timeSLotListListJsonRequest())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateShopTimeSLotsList(response.body()?.data!!)
            }else{
                empty_caution.visibility = View.VISIBLE
                timeSlotList.visibility = View.GONE
                prompt.visibility = View.GONE
                btnShare.visibility = View.GONE
            }
            showProgress(false)
        }
    }

    private fun removeTimeSlot(timeSlotId: String){
        lifecycleScope.launch{
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                deleteTimeSlot(removeTimeSlotJsonRequest(timeSlotId))
            }
            if (response.isSuccessful && response.body()?.message == "success"){
                AppLogger.error("response-> " + response.body()?.message)
            }
        }
    }

    private fun populateShopTimeSLotsList(list: ArrayList<ShopTimeSlotTrans>){
        empty_caution.visibility = View.GONE
        timeSlotList.visibility = View.VISIBLE
        prompt.visibility = View.VISIBLE
        btnShare.visibility = View.VISIBLE
        timeSlotList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        val adapter = ShopTimeSlotListAdapter(activity!!, list){ obj ->
            for (item in list){
                if (item.id == obj!!.id){
                    list.remove(obj)
                    removeTimeSlot(obj!!.id)
                    break
                }
            }

            if (list.isEmpty()){
                empty_caution.visibility = View.VISIBLE
                timeSlotList.visibility = View.GONE
                prompt.visibility = View.GONE
                btnShare.visibility = View.GONE
            }
        }
        timeSlotList.adapter = adapter
    }

    private fun timeSLotListListJsonRequest() : RequestBody {

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

    private fun removeTimeSlotJsonRequest(timeSlotId: String) : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("timeslotid", timeSlotId)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun inviteFriends(){
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Roadmate")
        val shareMessage = "Now you can easily manage your vehicle services on your convenient time.\n" +
                "Book your vehicle services online at " + ShopDetails().shopName + " through RoadMate "
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareMessage + "https://play.google.com/store/apps/details?id=com.roadmate.app")
        startActivity(Intent.createChooser(sharingIntent, "Share with"))
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
        return inflater.inflate(R.layout.fragment_shop_time_slots, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnCreateTimeSlot.setOnClickListener {
            activity?.startActivity<CreateTimeSlotActivity>()
        }

        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getShopTimeSlotList()
        }elseDo {
            empty_caution.visibility = View.VISIBLE
            timeSlotList.visibility = View.GONE
            btnShare.visibility = View.GONE
            prompt.visibility = View.GONE
            activity!!.toast {
                message = "No Internet connectivity!!"
                duration = Toast.LENGTH_SHORT
            }
        }

        btnShare.setOnClickListener {
            inviteFriends()
        }
    }
}