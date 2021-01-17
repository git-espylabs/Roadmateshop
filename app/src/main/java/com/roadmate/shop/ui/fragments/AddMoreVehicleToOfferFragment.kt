package com.roadmate.shop.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.api.response.VehicleFuelTypeMaster
import com.roadmate.app.api.response.VehicleFuelTypeTrans
import com.roadmate.shop.R
import com.roadmate.shop.adapter.AddedOfferVehiclesListAdapter
import com.roadmate.shop.adapter.AlertDialogAdapter
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.*
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.constants.AppConstants.Companion.VEHICLE_BRAND_ITINERARY
import com.roadmate.shop.constants.AppConstants.Companion.VEHICLE_FUEL_ITINERARY
import com.roadmate.shop.constants.AppConstants.Companion.VEHICLE_MODEL_ITINERARY
import com.roadmate.shop.constants.AppConstants.Companion.VEHICLE_TYPE_ITINERARY
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.models.AddedOfferVehicle
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_add_more_vehicle_to_offer.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class AddMoreVehicleToOfferFragment: Fragment(), View.OnClickListener {


    var selectedVehicleBrand = ""
    var selectedVehicleBrandName = ""
    var selectedVehicleModel = ""
    var selectedVehicleModelName = ""
    var selectedFuelType = ""
    var selectedFuelTypeName = ""

    var vehicleBrandList: ArrayList<VehicleBrandTrans> = arrayListOf()
    var vehicleModelList: ArrayList<VehicleModelTrans> = arrayListOf()
    var vehicleFuelTypeList: ArrayList<VehicleFuelTypeTrans> = arrayListOf()

    var addedVehicleList: ArrayList<AddedOfferVehicle> = arrayListOf()

    lateinit var adapter: AddedOfferVehiclesListAdapter

    private fun setListeners(){
       v_brand.setOnClickListener(this)
        v_model.setOnClickListener(this)
        v_fuel.setOnClickListener(this)
        btnAddVeh.setOnClickListener(this)
        save_vehicle.setOnClickListener(this)
    }



    private fun getVehicleBrands(vehTypeId: String){
        showProgress(true)
        Log.i("dsad",vehTypeId);

        v_model.text = "Select"
        v_fuel.text = "Select"

        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<VehicleBrandMaster>> {
                getVehicleBrands(vehItineraryJsonRequest("vehtype", vehTypeId))
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                vehicleBrandList = response.body()?.data!!
            }
            showProgress(false)
        }
    }

    private fun getVehicleModels(vehBrandId: String){
        showProgress(true)
        v_fuel.text = "Select"

        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<VehicleModelMaster>> {
                getVehicleModels(vehItineraryJsonRequest("brand", vehBrandId))
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                vehicleModelList = response.body()?.data!!
            }
            showProgress(false)
        }
    }

    private fun getVehicleFuelTypes(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<VehicleFuelTypeMaster>> {
                getVehicleFuelTypes()
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                vehicleFuelTypeList = response.body()?.data!!
            }
            showProgress(false)
        }
    }

    private inline fun<reified T> getItineraryNamesList(inputList: ArrayList<T>): ArrayList<String>{
        var nameList: ArrayList<String> = arrayListOf()

        for (T in inputList){
            when (T) {

                is VehicleBrandTrans -> {
                    nameList.add(T.vehicleBrand)
                }
                is VehicleModelTrans -> {
                    nameList.add(T.vehicleModel)
                }
                is VehicleFuelTypeTrans -> {
                    nameList.add(T.vehicleFuelType)
                }
            }
        }

        return nameList
    }

    private fun populateVehicleItinerary(dialogTitle: String, itineraryList: ArrayList<String>, textViewItinerary: TextView, itineraryListType: Int){
        val dialog: AlertDialog
        val builder = AlertDialog.Builder(activity)
        val layoutInflater = layoutInflater
        val view = layoutInflater.inflate(R.layout.custom_alert_dialog, null)
        builder.setView(view)
        dialog = builder.create()
        val textView = view.findViewById<TextView>(R.id.alert_heading)
        textView.text = dialogTitle
        val recyclerView: RecyclerView = view.findViewById(R.id.customAlertRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )
        val adapter = AlertDialogAdapter(activity!!, itineraryList){position, itineraryName ->
            textViewItinerary.text = itineraryName
            when(itineraryListType){
                VEHICLE_TYPE_ITINERARY -> {
                    v_brand.text = "Select"
                    v_model.text = "Select"
                    v_fuel.text = "Select"

                    vehicleBrandList.clear()
                    getVehicleBrands(arguments!!["vehice_type_name"].toString())
                }

                VEHICLE_BRAND_ITINERARY -> {
                    v_model.text = "Select"
                    v_fuel.text = "Select"
                    selectedVehicleBrand = vehicleBrandList[position].vehicleBrandId
                    selectedVehicleBrandName = vehicleBrandList[position].vehicleBrand
                    vehicleModelList.clear()
                    getVehicleModels(selectedVehicleBrand)
                }

                VEHICLE_MODEL_ITINERARY -> {
                    v_fuel.text = "Select"
                    selectedVehicleModel = vehicleModelList[position].vehicleModelId
                    selectedVehicleModelName = vehicleModelList[position].vehicleModel
                    vehicleFuelTypeList.clear()
                    getVehicleFuelTypes()
                }

                VEHICLE_FUEL_ITINERARY -> {
                    selectedFuelType = vehicleFuelTypeList[position].vehicleFuelTypeId
                    selectedFuelTypeName = vehicleFuelTypeList[position].vehicleFuelType
                }
            }
            dialog.dismiss()
        }
        recyclerView.adapter = adapter
        dialog.show()
    }


    private fun saveNewVehicle(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                insertOfferVehicleVehicle(vehSaveJsonRequest())
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                activity!!.toast {
                    message = "Vehicle list added"
                    duration = Toast.LENGTH_LONG }
                activity?.finish()
            }
            showProgress(false)
        }
    }

    private fun isInputsValid(obj: AddedOfferVehicle): Boolean{
        when {

            selectedVehicleBrand.isEmpty() -> {
                activity!!.toast {
                    message = "Please select a vehicle Brand"
                    duration = Toast.LENGTH_LONG }
                return false
            }
            selectedVehicleModel.isEmpty() -> {
                activity!!.toast {
                    message = "Please select a vehicle Model"
                    duration = Toast.LENGTH_LONG }
                return false
            }
            selectedFuelType.isEmpty() -> {
                activity!!.toast {
                    message = "Please select a fuel Type"
                    duration = Toast.LENGTH_LONG }
                return false
            }
            isRepeated(obj) ->{
                activity!!.toast {
                    message = "Vehicle already added!"
                    duration = Toast.LENGTH_LONG }
                return false
            }
            else -> return true
        }

    }

    private fun isRepeated(obj: AddedOfferVehicle): Boolean{
        for (item in addedVehicleList){
            if (item.vehicletype == obj.vehicletype
                && item.brand == obj.brand
                && item.model == obj.model
                && item.fuel_type == obj.fuel_type){
                return true
            }
        }
        return false
    }


    private fun vehItineraryJsonRequest(param: String, vehTypeId: String) : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put(param, vehTypeId)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun vehSaveJsonRequest() : RequestBody {

        var jsonData = ""
        var json = JSONObject()
        var jsonArray = JSONArray()
        try {
            for (item in addedVehicleList){
                var jsonObj = JSONObject()
                jsonObj.put("shopid", item.shopId)
                jsonObj.put("offerid", arguments!!["offerid"])
                jsonObj.put("vehicletype",arguments!!["vehice_type_name"])
                jsonObj.put("brand", item.brand)
                jsonObj.put("model", item.model)
                jsonObj.put("fuel_type", item.fuel_type)
                jsonArray.put(jsonObj)
            }
            json.put("shopoffermodel", jsonArray)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun clearAllItineraries(){
        vehicleBrandList.clear()
        vehicleModelList.clear()
        vehicleFuelTypeList.clear()
        addedVehicleList.clear()
    }

    private fun addVehicle(){
        val obj = AddedOfferVehicle(
            ShopDetails().shopId,
            arguments!!["offerid"].toString(),
            arguments!!["vehice_type_name"].toString(),
            selectedVehicleBrand,
            selectedVehicleBrandName,
            selectedVehicleModel,
            selectedVehicleModelName,
            selectedFuelType,
            selectedFuelTypeName)
        isInputsValid(obj).doIfTrue {
            addedVehicleList.add(obj)
            adapter.notifyDataSetChanged()
        }

        addedVehicleList.isNotEmpty().doIfTrue {
            save_vehicle.visibility = View.VISIBLE
        }elseDo {
            save_vehicle.visibility = View.GONE
        }
    }

    private fun populateAddedVehicleList(){
        vList.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        adapter = AddedOfferVehiclesListAdapter(activity!!, addedVehicleList){ it->
            removeAddedVehicles(it!!)
        }
        vList.adapter = adapter
    }

    private fun removeAddedVehicles(position: Int){
        addedVehicleList.removeAt(position)
        adapter.notifyDataSetChanged()

        addedVehicleList.isNotEmpty().doIfTrue {
            save_vehicle.visibility = View.VISIBLE
        }elseDo {
            save_vehicle.visibility = View.GONE
        }
    }

    private fun showProgress(visible:Boolean){
        if (visible){
            spin_kit.visibility = View.VISIBLE
        }else{
            spin_kit.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_more_vehicle_to_offer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()

        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            clearAllItineraries()
            populateAddedVehicleList()
            getVehicleBrands(arguments!!["vehice_type_name"].toString())
        }elseDo {
            activity!!.toast {
                message = "No Internet connectivity!"
                duration = Toast.LENGTH_LONG }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.v_brand -> {

                    populateVehicleItinerary(
                        "Select Vehicle Brand",
                        getItineraryNamesList(vehicleBrandList),
                        v_brand,
                        VEHICLE_BRAND_ITINERARY
                    )

            }

            R.id.v_model -> {
                vehicleModelList.isNotEmpty().doIfTrue {
                    populateVehicleItinerary(
                        "Select Vehicle Model",
                        getItineraryNamesList(vehicleModelList),
                        v_model,
                        VEHICLE_MODEL_ITINERARY
                    )
                }elseDo {
                    activity!!.toast {
                        message = "Please select a vehicle Brand!"
                        duration = Toast.LENGTH_LONG }
                }
            }

            R.id.v_fuel -> {
                vehicleFuelTypeList.isNotEmpty().doIfTrue {
                    populateVehicleItinerary(
                        "Select Vehicle Fuel Type",
                        getItineraryNamesList(vehicleFuelTypeList),
                        v_fuel,
                        VEHICLE_FUEL_ITINERARY
                    )
                }elseDo {
                    activity!!.toast {
                        message = "Service not available at this moment!"
                        duration = Toast.LENGTH_LONG }
                }
            }

            R.id.save_vehicle -> {
                NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
                    if (addedVehicleList.isNotEmpty()){
                        saveNewVehicle()
                    }else{
                        activity!!.toast {
                            message = "Please add a vehicle"
                            duration = Toast.LENGTH_LONG }
                    }
                }elseDo {
                    activity!!.toast {
                        message = "No Internet connectivity!"
                        duration = Toast.LENGTH_LONG }
                }
            }

            R.id.btnAddVeh -> {
                addVehicle()
            }
        }
    }
}