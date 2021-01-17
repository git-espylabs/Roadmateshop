package com.roadmate.shop.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.adapter.AlertDialogAdapter
import com.roadmate.shop.adapter.PackageListAdapter
import com.roadmate.shop.adapter.ShopOffersAdapter
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.*
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.ui.activities.PackageDetailsActivity
import com.roadmate.shop.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_package_list.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.util.ArrayList
import java.util.HashMap

class PackageListFragment: BaseFragment(), View.OnClickListener{

    private var selectedVehicleTypeId = ""
    private var selectedVehicleBrandId = ""
    private var addedShopTypesList: ArrayList<PackageTrans> = arrayListOf()


    private fun setListeners(){
        selVehType.setOnClickListener(this)
        selBrand.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
    }

    private inline fun<reified T> populateItinerariesList(dialogTitle: String, itineraryList: ArrayList<T>,
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
        var itineraryStringList: ArrayList<String> = getItineraryNamesList(itineraryList)
        val adapter = AlertDialogAdapter(activity!!, itineraryStringList){position, itineraryName ->
            textViewItinerary.text = itineraryName
            when (itineraryListType) {
                is VehicleTypeTrans -> {
                    selectedVehicleTypeId = (itineraryList[position] as VehicleTypeTrans).vehicleTypeId
                    selBrand.hint = "Select"
                    selBrand.text = ""
                }
                is VehicleBrandTrans -> {
                    selectedVehicleBrandId = (itineraryList[position] as VehicleBrandTrans).vehicleBrandId
                }
            }
            dialog.dismiss()
        }
        recyclerView.adapter = adapter
        dialog.show()
    }

    private inline fun<reified T> getItineraryNamesList(inputList: ArrayList<T>): ArrayList<String> {
        var nameList: ArrayList<String> = arrayListOf()

        for (T in inputList){
            when (T) {
                is VehicleTypeTrans -> {
                    nameList.add(T.vehicleType)
                }
                is VehicleBrandTrans -> {
                    nameList.add(T.vehicleBrand)
                }
            }
        }

        return nameList
    }

    private fun getVehicleTypesList(){
        showProgress(R.id.spinBar1, true)

        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<VehicleTypeMaster>> {
                getVehicleTypes()
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateItinerariesList("Select Vehicle Category", response.body()?.data!!, selVehType, VehicleTypeTrans())
            }
            showProgress(R.id.spinBar1, false)
        }
    }

    private fun getVehicleBrands(vehTypeId: String){
        showProgress(R.id.spinBar3, true)

        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<VehicleBrandMaster>> {
                getVehicleBrands(vehItineraryJsonRequest("vehtype", vehTypeId))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateItinerariesList("Select Vehicle Brand", response.body()?.data!!, selBrand, VehicleBrandTrans())
            }
            showProgress(R.id.spinBar3, false)
        }
    }

    private fun getPackageList(){
        showProgress(R.id.progressLay, true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<PackageMaster>> {
                getShopPackageList(packageListJsonRequest())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){

                addedShopTypesList.clear()
                addedShopTypesList.addAll(response.body()?.data!!)
                populatePackagesList(addedShopTypesList)
            }else{
                empty_caution.visibility = View.VISIBLE
                packagesRecycler.visibility = View.GONE
            }
            showProgress(R.id.progressLay, false)
        }
    }

    private fun populatePackagesList(list: ArrayList<PackageTrans>){
        empty_caution.visibility = View.GONE
        packagesRecycler.visibility = View.VISIBLE
        packagesRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        val adapter = PackageListAdapter(activity!!, list){ index,obj ->


            removeCategory(index, obj)

//            val mMap = HashMap<String, String>()
//            mMap["image"] = obj!!.image
//            mMap["pName"] = obj!!.title
//            mMap["pDesc"] = obj!!.description
//            mMap["pAmount"] = obj!!.amount
//            mMap["pOfferAmount"] = obj!!.offer_amount
//            mMap["pFor"] = obj!!.package_for
//            mMap["pType"] = obj!!.package_type
//            mMap["pVehType"] = obj!!.veh_type
//            mMap["pVehModel"] = obj!!.brand
//            mMap["pVehFuel"] = obj!!.fuel_type
//
//            val intent = Intent(context, PackageDetailsActivity::class.java)
//            intent.putExtra("packageMap", mMap)
//            startActivity(intent)
        }
        packagesRecycler.adapter = adapter
    }


    private fun removeCategory(position: Int, obj: PackageTrans){
        showProgress(R.id.spinBar3, true)
        Log.i("dasd",position.toString())
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                deletePackage(removeCategoryRequestJSON(obj.rowid.toString()))
            }
            Log.i("da",obj.toString())
            if (response.isSuccessful && response.body()?.message.equals("success", true)){
                addedShopTypesList.removeAt(position)
                activity!!.toast {
                    message = "Package removed removed"
                    duration = Toast.LENGTH_SHORT
                }
            }
            showProgress(R.id.spinBar3, false)
        }
    }

    private fun removeCategoryRequestJSON(catid: String) : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("id", catid)
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
            R.id.spinBar3 -> spinBar3.visibility = viewVisibility
            R.id.progressLay -> progressLay.visibility = viewVisibility
        }
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

    private fun packageListJsonRequest() : RequestBody {

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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_package_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()
        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getPackageList()
        }elseDo {
            empty_caution.visibility = View.VISIBLE
            packagesRecycler.visibility = View.GONE
            activity!!.toast {
                message = "No internet connectivity!"
                duration = Toast.LENGTH_LONG
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.selVehType -> {
                getVehicleTypesList()
            }
            R.id.selBrand -> {
                selectedVehicleTypeId.isNotEmpty().doIfTrue {
                    getVehicleBrands(selectedVehicleTypeId)
                }elseDo {
                    activity!!.toast {
                        message = "Please select a vehicle type!"
                        duration = Toast.LENGTH_SHORT
                    }
                }
            }
            R.id.btnSubmit ->{
                getPackageList()
            }
        }
    }
}