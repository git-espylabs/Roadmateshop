package com.roadmate.shop.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.adapter.AlertDialogAdapter
import com.roadmate.shop.adapter.NewServiceBrandListAdapter
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.*
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.models.ServiceInsertModel
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.ui.activities.AddNewServiceActivity
import com.roadmate.shop.utils.CommonUtils
import com.roadmate.shop.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_add_new_service.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class AddNewServiceFragment: Fragment() {

    companion object {var selectedVehicleTypeId = ""}
    private var userSelectedServiceList: ArrayList<ServiceInsertModel> = arrayListOf()
    private var vehicleSelectedModelId = 6


    private fun setListeners(){
        v_type.setOnClickListener {
            getVehicleTypesList()
        }

        addButton.setOnClickListener {
            NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
                prepareSubmitData()
            }elseDo {
                activity!!.toast {
                    message = "No Internet connectivity!!"
                    duration = Toast.LENGTH_SHORT
                }
            }
        }

        rdGroup.setOnCheckedChangeListener { group, checkedId ->
            when {
                radioAll.isChecked -> {
                    vehicleSelectedModelId = 0
                    vtypeLay.visibility = View.GONE
                    linearBrand.visibility = View.GONE
                    clearBrandSelection()
                }
                radiotwo.isChecked -> {
                    vehicleSelectedModelId = 1
                    vtypeLay.visibility = View.GONE
                    linearBrand.visibility = View.GONE
                    clearBrandSelection()
                }
                radioThree.isChecked -> {
                    vehicleSelectedModelId = 2
                    vtypeLay.visibility = View.GONE
                    linearBrand.visibility = View.GONE
                    clearBrandSelection()
                }
                radioFour.isChecked -> {
                    vehicleSelectedModelId = 3
                    vtypeLay.visibility = View.GONE
                    linearBrand.visibility = View.GONE
                    clearBrandSelection()
                }
                radioHeavy.isChecked -> {
                    vehicleSelectedModelId = 4
                    vtypeLay.visibility = View.GONE
                    linearBrand.visibility = View.GONE
                    clearBrandSelection()
                }
                radiobrand.isChecked -> {
                    vehicleSelectedModelId = 6
                    vtypeLay.visibility = View.VISIBLE
                    linearBrand.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun clearBrandSelection(){
        selectedVehicleTypeId = ""
        userSelectedServiceList.clear()
    }

    private fun getVehicleTypesList(){
        showVehicleTypeProgress(true)

        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<VehicleTypeMaster>> {
                getVehicleTypes()
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateVehicleTypesList(response.body()?.data!!)
            }
            showVehicleTypeProgress(false)
        }
    }

    private fun getVehicleBrands(vehTypeId: String){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<VehicleBrandModelMaster>> {
                getVehicleBrandsForService(vehItineraryJsonRequest("vehtype", vehTypeId))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateVehicleBrandList(response.body()?.data!!)
            }
            showProgress(false)
        }
    }

    private fun populateVehicleTypesList(itineraryList: ArrayList<VehicleTypeTrans>){
        val dialog: AlertDialog
        val builder = AlertDialog.Builder(activity)
        val layoutInflater = layoutInflater
        val view = layoutInflater.inflate(R.layout.custom_alert_dialog, null)
        builder.setView(view)
        dialog = builder.create()
        val textView = view.findViewById<TextView>(R.id.alert_heading)
        textView.text = "Select Vehicle Type"
        val recyclerView: RecyclerView = view.findViewById(R.id.customAlertRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )
        var itineraryStringList: ArrayList<String> = getItineraryNamesList(itineraryList)
        val adapter = AlertDialogAdapter(activity!!, itineraryStringList){position, itineraryName ->
            v_type.text = itineraryName
            selectedVehicleTypeId = itineraryList[position].vehicleTypeId
            dialog.dismiss()
            getVehicleBrands(selectedVehicleTypeId)
        }
        recyclerView.adapter = adapter
        dialog.show()
    }

    private inline fun<reified T> getItineraryNamesList(inputList: ArrayList<T>): ArrayList<String> {
        var nameList: java.util.ArrayList<String> = arrayListOf()

        for (T in inputList){
            when (T) {
                is MoreServicesTrans -> {
                    nameList.add(T.serviceName)
                }
                is VehicleTypeTrans -> {
                    nameList.add(T.vehicleType)
                }
                is VehicleBrandTrans -> {
                    nameList.add(T.vehicleBrand)
                }
                is VehicleModelTrans -> {
                    nameList.add(T.vehicleModel)
                }
            }
        }

        return nameList
    }

    private fun populateVehicleBrandList(brandList: ArrayList<VehicleBrandModelTrans>){
        linearBrand.visibility = View.VISIBLE
        var brandListClear: ArrayList<VehicleBrandModelTrans> = arrayListOf()
        for (item in brandList){
            if (item.models.isNotEmpty()){
                brandListClear.add(item)
            }
        }
        brandmodel_recycler.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        var adapter = NewServiceBrandListAdapter(activity!!, brandListClear){userSelectedModels ->
            userSelectedServiceList = userSelectedModels!!
        }
        brandmodel_recycler.adapter = adapter
    }

    private fun vehItineraryJsonRequest(param: String, vehItineraryId: String) : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put(param, vehItineraryId)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun showVehicleTypeProgress(show: Boolean){
        show.doIfTrue {
            spinBar2.visibility = View.VISIBLE
        }elseDo {
            spinBar2.visibility = View.GONE
        }
    }

    private fun prepareSubmitData(){

        lifecycleScope.launch {
            showProgress(true)
            if (userSelectedServiceList.isNotEmpty()){
            }
            if (userSelectedServiceList.isNotEmpty() && vehicleSelectedModelId == 6){
                var response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                    insertServiceSHop(submitJson1())
                }
                if (response.isSuccessful && response.body()?.message == "Success"){
                    activity!!.toast {
                        message = "Successfully update!"
                        duration = Toast.LENGTH_SHORT
                    }
                }else{
                    activity!!.toast {
                        message = "OOPS! Something went wrong"
                        duration = Toast.LENGTH_SHORT
                    }
                }
            }else if (vehicleSelectedModelId != 6){
                var response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                    insertServiceSHop2(submitJson2())
                }
                if (response.isSuccessful && response.body()?.message == "success"){
                    activity!!.toast {
                        message = "Successfully update!"
                        duration = Toast.LENGTH_SHORT
                    }
                }else{
                    activity!!.toast {
                        message = "OOPS! Something went wrong"
                        duration = Toast.LENGTH_SHORT
                    }
                }
            }
            showProgress(false)
            (activity as AddNewServiceActivity).onBackPressed()
        }
    }

    private fun submitJson1() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        var jArray = JSONArray()
        try {
            for (item in userSelectedServiceList){
                var jObj = JSONObject()
                jObj.put("shopid",ShopDetails().shopId)
                jObj.put("shopcat", ShopDetails().shopType)
                jObj.put("vehicle", item.vehicle)
                jObj.put("model", item.model)
                jObj.put("brand", item.brand)
                jArray.put(jObj)
            }
            json = JSONObject()
            json.put("servicelist", jArray)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun submitJson2() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shopid", ShopDetails().shopId)
            json.put("shopcat", ShopDetails().shopType)
            json.put("vehicle", vehicleSelectedModelId)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun showProgress(show: Boolean){
        show.doIfTrue {
            progressLay.visibility = View.VISIBLE
        }elseDo {
            progressLay.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_new_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vtypeLay.visibility = View.VISIBLE
        linearBrand.visibility = View.GONE
        setListeners()
    }
}