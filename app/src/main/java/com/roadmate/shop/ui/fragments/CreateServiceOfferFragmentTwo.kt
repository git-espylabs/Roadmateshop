package com.roadmate.shop.ui.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.net.Uri
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
import com.roadmate.app.api.response.VehicleFuelTypeMaster
import com.roadmate.app.api.response.VehicleFuelTypeTrans
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
import com.roadmate.shop.ui.activities.CreateOfferActivity
import com.roadmate.shop.utils.CommonUtils
import com.roadmate.shop.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_create_service_offer_two.*
import kotlinx.android.synthetic.main.fragment_create_service_offer_two.btnSubmit
import kotlinx.android.synthetic.main.fragment_create_service_offer_two.etDesc
import kotlinx.android.synthetic.main.fragment_create_service_offer_two.etNormalAmount
import kotlinx.android.synthetic.main.fragment_create_service_offer_two.etOfferAmount
import kotlinx.android.synthetic.main.fragment_create_service_offer_two.etTitle
import kotlinx.android.synthetic.main.fragment_create_service_offer_two.selFuelType
import kotlinx.android.synthetic.main.fragment_create_service_offer_two.selShopCat
import kotlinx.android.synthetic.main.fragment_create_service_offer_two.spinBar1
import kotlinx.android.synthetic.main.fragment_create_service_offer_two.spinBar2
import kotlinx.android.synthetic.main.fragment_create_service_offer_two.spin_kit_submit
import kotlinx.android.synthetic.main.fragment_create_service_offer_two.tvEndDate
import kotlinx.android.synthetic.main.fragment_create_sevice_offer.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class CreateServiceOfferFragmentTwo: Fragment() {
    private var userSelectedServiceList: ArrayList<ServiceInsertModel> = arrayListOf()
    private var vehicleSelectedModelId = 6
    private var selectedShopTypeId = ""
    private var selectedVehicleFuelTypelId = ""

    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    val PHOTO_PICKER_ACTIVITY_REQUEST_CODE = 10
    var selectImage1: Uri? = null

    private var imgPath: String = ""
    private var imageUri: Uri? = null
    private var queryImageUrl: String = ""

    var selectFirstImagePath = ""
    var image1Added = false
    private var offerDiscountType = 1


    private fun setListeners(){
        v_type.setOnClickListener {
            getVehicleTypesList()
        }

        btnSubmit.setOnClickListener {
            NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
                if (isAllFieldsValid()) {
                    submitDataNew()
                }
            }elseDo {
                activity!!.toast {
                    message = "No Internet connectivity!!"
                    duration = Toast.LENGTH_SHORT
                }
            }
        }
        selShopCat.setOnClickListener(View.OnClickListener {
            getShopTypesList()
        })
        tvEndDate.setOnClickListener(View.OnClickListener { openDatePicker() })
        selFuelType.setOnClickListener(View.OnClickListener {
            getVehicleFuelTypes()
        })

        rdGroup1.setOnCheckedChangeListener { group, checkedId ->
            when{
                rbPrice.isChecked -> {
                    offerDiscountType = 1
                    etNormalAmount.visibility = View.VISIBLE
                    etOfferAmount.visibility = View.VISIBLE
                    etPercentage.visibility = View.GONE
                }
                rbPerc.isChecked -> {
                    offerDiscountType = 2
                    etNormalAmount.visibility = View.GONE
                    etOfferAmount.visibility = View.GONE
                    etPercentage.visibility = View.VISIBLE
                }
            }
        }


    }

    private fun clearBrandSelection(){
        AddNewServiceFragment.selectedVehicleTypeId = ""
        userSelectedServiceList.clear()
    }

    private fun getVehicleFuelTypes(){
        showProgress(R.id.spinBar5, true)

        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<VehicleFuelTypeMaster>> {
                getVehicleFuelTypes()
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateItinerariesList("Select Fuel type", response.body()?.data!!, selFuelType, VehicleFuelTypeTrans())
            }
            showProgress(R.id.spinBar5, false)
        }
    }

    private fun openDatePicker(){
        val c = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        val datePickerDialog = DatePickerDialog(
            activity!!,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                tvEndDate.text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
            },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.show()
    }

    private fun isAllFieldsValid(): Boolean{
        if (selectedShopTypeId.isEmpty()){
            activity!!.toast {
                message = "Please select a shop category"
                duration = Toast.LENGTH_SHORT
            }
            return  false
        }else if (etTitle.text.toString().trim().isEmpty()){
            activity!!.toast {
                message = "Please enter a offer title"
                duration = Toast.LENGTH_SHORT
            }
            return  false
        }else if (etDesc.text.toString().trim().isEmpty()){
            activity!!.toast {
                message = "Please enter a offer description"
                duration = Toast.LENGTH_SHORT
            }
            return  false
        }else if (offerDiscountType == 1 && etNormalAmount.text.toString().trim().isEmpty()){
            activity!!.toast {
                message = "Please enter Normal amount"
                duration = Toast.LENGTH_SHORT
            }
            return  false
        }else if (offerDiscountType == 1 && etOfferAmount.text.toString().trim().isEmpty()){
            activity!!.toast {
                message = "Please enter Offer amount"
                duration = Toast.LENGTH_SHORT
            }
            return  false
        }else if (offerDiscountType == 2 && etPercentage.text.toString().trim().isEmpty()){
            activity!!.toast {
                message = "Please enter Offer percentage"
                duration = Toast.LENGTH_SHORT
            }
            return  false
        }else if (tvEndDate.text.toString().trim().isEmpty()){
            activity!!.toast {
                message = "Please enter a end date"
                duration = Toast.LENGTH_SHORT
            }
            return  false
        }else if (userSelectedServiceList.isEmpty()){
            activity!!.toast {
                message = "Please select a vehicle"
                duration = Toast.LENGTH_SHORT
            }
            return  false
        }
        return true
    }

    private fun getShopTypesList(){
        showProgress(R.id.spinBar1, true)
        lifecycleScope.launch{
            val response =  APIManager.call<ApiServices, Response<MoreServicesMaster>> {
                getAddedShopTypes(shopTypesRequestJSON())
            }

            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateItinerariesList("Select Shop Category", response.body()?.data!!, selShopCat, MoreServicesTrans())
            }else{
                activity!!.toast {
                    message = "Services not available at this moment!"
                    duration = Toast.LENGTH_LONG }
            }
            showProgress(R.id.spinBar1, false)
        }
    }

    private fun shopTypesRequestJSON() : RequestBody {
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

    private inline fun<reified T> populateItinerariesList(dialogTitle: String, itineraryList: java.util.ArrayList<T>,
                                                          textViewItinerary: TextView, itineraryListType: T){
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
        var itineraryStringList: java.util.ArrayList<String> = getItineraryNamesList2(itineraryList)
        val adapter = AlertDialogAdapter(activity!!, itineraryStringList){position, itineraryName ->
            textViewItinerary.text = itineraryName
            when (itineraryListType) {
                is MoreServicesTrans -> {
                    selectedShopTypeId = (itineraryList[position] as MoreServicesTrans).serviceId
                }
                is VehicleFuelTypeTrans -> {
                    selectedVehicleFuelTypelId = (itineraryList[position] as VehicleFuelTypeTrans).vehicleFuelTypeId
                }
            }
            dialog.dismiss()
        }
        recyclerView.adapter = adapter
        dialog.show()
    }

    private inline fun<reified T> getItineraryNamesList2(inputList: java.util.ArrayList<T>): java.util.ArrayList<String> {
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
                is VehicleFuelTypeTrans -> {
                    nameList.add(T.vehicleFuelType)
                }
            }
        }

        return nameList
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
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<VehicleBrandModelMaster>> {
                getVehicleBrandsForService(vehItineraryJsonRequest("vehtype", vehTypeId))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateVehicleBrandList(response.body()?.data!!)
            }
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
            AddNewServiceFragment.selectedVehicleTypeId = itineraryList[position].vehicleTypeId
            dialog.dismiss()
            getVehicleBrands(AddNewServiceFragment.selectedVehicleTypeId)
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
            showProgress(R.id.spin_kit_submit,true)
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
            showProgress(R.id.spin_kit_submit,false)
            (activity as AddNewServiceActivity).onBackPressed()
        }
    }

    private fun submitDataNew(){
        btnSubmit.visibility = View.GONE
        showProgress(R.id.spin_kit_submit, true)
        lifecycleScope.launch {
            var response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                insertServiceOfferNew(submitJsonNew())
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
            showProgress(R.id.spin_kit_submit, false)
            btnSubmit.visibility = View.VISIBLE
            activity?.finish()
        }
    }

    private fun submitJson1() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        var jArray = JSONArray()
        try {
            for (item in userSelectedServiceList){
                var jObj = JSONObject()
                jObj.put("shopid", ShopDetails().shopId)
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

    private fun submitJsonNew() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        var jArray = JSONArray()
        try {
            json = JSONObject()

            json.put("shop_id", ShopDetails().shopId)
            json.put("shop_cat_id", selectedShopTypeId)
            json.put("title", etTitle.text.toString())
            json.put("small_desc", etDesc.text.toString())
            if (etNormalAmount.text.toString().isNotEmpty() && etNormalAmount.text.toString().isNotBlank()) {
                json.put("normal_amount", etNormalAmount.text.toString())
            } else {
                json.put("normal_amount", "0")
            }
            if (etOfferAmount.text.toString().isNotEmpty() && etOfferAmount.text.toString().isNotBlank()) {
                json.put("offer_amount", etOfferAmount.text.toString())
            } else {
                json.put("offer_amount", "0")
            }
            json.put("vehicle_typeid", "0")
            json.put("brand_id", "0")
            json.put("model_id", "0")
            json.put("offer_type", "1")
            json.put("offer_discount_type", offerDiscountType.toString())
            if (etPercentage.text.toString().isNotEmpty() && etPercentage.text.toString().isNotBlank()) {
                json.put("discount_percentage", etPercentage.text.toString())
            } else {
                json.put("discount_percentage", "0")
            }
            json.put("offer_end_date", CommonUtils.formatDate_ddMMyyyy(tvEndDate.text.toString())!!)
            json.put("fuel_type", "0")

            for (item in userSelectedServiceList){
                var jObj = JSONObject()
                jObj.put("shopid", ShopDetails().shopId)
                jObj.put("offerid", item.vehicle)
                jObj.put("vehicletype", item.vehicle)
                jObj.put("brand", item.brand)
                jObj.put("model", item.model)
                jObj.put("fuel_type", "0")
                jArray.put(jObj)
            }
            json.put("shopoffermodel", jArray)

            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }


    private fun showProgress(id: Int, show: Boolean){
        var viewVisibility: Int = if (show){
            View.VISIBLE
        }else{
            View.GONE
        }
        when(id){
            R.id.spinBar1 -> spinBar1.visibility = viewVisibility
            R.id.spinBar2 -> spinBar2.visibility = viewVisibility
            R.id.spin_kit_submit -> {
                spin_kit_submit.visibility = viewVisibility
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_service_offer_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vtypeLay.visibility = View.VISIBLE
        linearBrand.visibility = View.GONE
        setListeners()
    }

}