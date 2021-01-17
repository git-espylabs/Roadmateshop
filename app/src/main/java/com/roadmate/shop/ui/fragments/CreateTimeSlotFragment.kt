package com.roadmate.shop.ui.fragments

import android.R.attr.country
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.adapter.TimeSlotListAdapter
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.RoadmateApiResponse
import com.roadmate.shop.api.response.TimeSlotTrans
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.utils.CommonUtils
import kotlinx.android.synthetic.main.fragment_create_time_slot.*
import kotlinx.android.synthetic.main.fragment_create_time_slot.spin_kit
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


class CreateTimeSlotFragment: BaseFragment() {

    lateinit var timeSlotsAdapter: TimeSlotListAdapter
    var timeSlotList: ArrayList<TimeSlotTrans> = arrayListOf()
    var hoursList: ArrayList<String> = arrayListOf()
    var minutesList: ArrayList<String> = arrayListOf()
    var ampmlist: ArrayList<String> = arrayListOf()

    private fun createSpinners(){

        hoursList.add("00")
        hoursList.add("01")
        hoursList.add("02")
        hoursList.add("03")
        hoursList.add("04")
        hoursList.add("05")
        hoursList.add("06")
        hoursList.add("07")
        hoursList.add("08")
        hoursList.add("09")
        hoursList.add("10")
        hoursList.add("11")
        hoursList.add("12")
        val hrAda = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, hoursList)
        hrAda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        hrSpin.adapter = hrAda;

        minutesList.add("00")
        minutesList.add("30")
        val minAda = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, minutesList)
        minAda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        minSpin.adapter = minAda;

        ampmlist.add("AM")
        ampmlist.add("PM")
        val ampmAda = ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, ampmlist)
        ampmAda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ampmSpin.adapter = ampmAda;
    }

    private fun addSlot(){
        val ids = timeSlotList.size +1;
        val selHr = hrSpin.selectedItem.toString()
        val selMin = minSpin.selectedItem.toString()
        val selPart = ampmSpin.selectedItem.toString()
        val time = "$selHr:$selMin $selPart"
        var timeSlot = TimeSlotTrans(ids.toString(), time)
        noDuplicates(time).doIfTrue {
            timeSlotList.add(timeSlot)
            lay3.visibility = View.VISIBLE
            timeSlotsAdapter.notifyDataSetChanged()
        }elseDo {
            activity!!.toast {
                message = "Already Added"
                duration = Toast.LENGTH_SHORT
            }
        }
    }

    private fun noDuplicates(time: String): Boolean{
        return timeSlotList.none { it.name == time }
    }
    
    private fun createAdapter(){
        rvlist.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        timeSlotsAdapter = TimeSlotListAdapter(activity!!, timeSlotList){obj ->
            for (item in timeSlotList){
                if (item.id == obj!!.id){
                    timeSlotList.remove(obj)
                    break
                }
            }
            if (timeSlotList.isNotEmpty()){
                lay3.visibility = View.VISIBLE
            }else{
                lay3.visibility = View.GONE
            }
            timeSlotsAdapter.notifyDataSetChanged()
        }
        rvlist.adapter = timeSlotsAdapter
    }

    private fun submitSlots(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                insertTimeSlots(submitJsonRequest())
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                activity!!.toast {
                    message = "Time slots updated"
                    duration = Toast.LENGTH_SHORT
                }
                activity!!.finish()
            }else{
                activity!!.toast {
                    message = "OOPS! Something went wrong"
                    duration = Toast.LENGTH_SHORT
                }
            }
            showProgress(false)
        }
    }

    private fun submitJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        var jArray = JSONArray()
        try {
            for (item in timeSlotList){
                var jObj = JSONObject()
                jObj.put("shopid",ShopDetails().shopId)
                jObj.put("timeslot",CommonUtils.formatTime_HmmA(item.name))
                jArray.put(jObj)
            }
            json = JSONObject()
            json.put("timeslot", jArray)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun showProgress(show: Boolean){
        if (show){
            submit.visibility = View.GONE
            spin_kit.visibility = View.VISIBLE
        }else{
            submit.visibility = View.VISIBLE
            spin_kit.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_time_slot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createSpinners()
        createAdapter()

        lay2.setOnClickListener {
            addSlot()
        }

        submit.setOnClickListener {
            submitSlots()
        }
    }
}