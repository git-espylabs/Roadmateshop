package com.roadmate.shop.ui.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.adapter.PackageSelectAdapter
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.PackageFeatureMaster
import com.roadmate.shop.api.response.PackageMaster
import com.roadmate.shop.api.response.PackageTrans
import com.roadmate.shop.api.response.RoadmateApiResponse
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_add_package.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.util.*


class AddPackageFragment: BaseFragment(), View.OnClickListener{

    var packageSelectedList: ArrayList<PackageTrans> = arrayListOf()
    private lateinit var adapterPackage: PackageSelectAdapter
    lateinit var adapter: ArrayAdapter<String>
    lateinit var listView: ListView
    lateinit var alertDialog: AlertDialog.Builder
    lateinit var dialog: AlertDialog
    private fun setListeners(){
        select.setOnClickListener(this)
        next.setOnClickListener(this)
    }

    private fun getAllPackagesList(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<PackageMaster>> {

                getAllPackageList(shopImageRequestJSON())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populatePackagesList(response.body()?.data!!)
            }else{
                empty_caution.visibility = View.VISIBLE
                rv.visibility = View.GONE
            }
            showProgress(false)
        }
    }

    private fun shopImageRequestJSON() : RequestBody {
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
    private fun populatePackagesList(list: ArrayList<PackageTrans>){
        empty_caution.visibility = View.GONE
        rv.visibility = View.VISIBLE
        next.visibility = View.VISIBLE
        rv.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        adapterPackage = PackageSelectAdapter(activity!!, list){ obj, isSelected ,isShowMore->

            if(isShowMore) {
                showProgress(true)
                lifecycleScope.launch {
                    val response = APIManager.call<ApiServices, Response<PackageFeatureMaster>> {

                        getFetureListOfPackage(packageFeatureRequestJSON(obj!!.package_type))
                    }

                    if (response.isSuccessful ) {
                        // add list items
                        val listItems = ArrayList<String>();

                        var features= "";
                        response.body()!!.data.forEach {
                            listItems.add(it.feature)
                            if(response.body()!!.data.indexOf(it)==0)
                            {
                                features = it.feature + "\n";

                            }else{
                                features = features + it.feature +  "\n"

                            }
                        }


                        AlertDialog.Builder(context)
                            .setTitle(obj!!.title +" for " +obj!!.brand_model + " "+"Features")
                            .setMessage(features)
                             .show()



                    } else {

                        empty_caution.visibility = View.GONE
                        rv.visibility = View.VISIBLE
                    }
                    showProgress(false)
                }
            }
            else{
                if (isSelected){
                    packageSelectedList.add(obj!!)
                }else{
                    for (packObj in packageSelectedList){
                        if (packObj.id == obj!!.id){
                            packageSelectedList.remove(packObj)
                            break
                        }
                    }
                }
            }

            adapterPackage.notifyDataSetChanged()
        }
        rv.adapter = adapterPackage
    }

    private fun showProgress(show: Boolean){
        if (show){
            loadingFrame.visibility = View.VISIBLE
        }else{
            loadingFrame.visibility = View.GONE
        }
    }

    private fun packageFeatureRequestJSON(id:String) : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("package_id", id)

            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }
    private fun selectedListSubmitRequest() : RequestBody {
        var jsonData = ""
        var json = JSONObject()

        var jsonArray = JSONArray()
        try {
            for (obj in packageSelectedList){
                var jsonObj = JSONObject()
                jsonObj.put("shopid",ShopDetails().shopId)
                jsonObj.put("pkid",obj.id)
                jsonArray.put(jsonObj)
            }
            json.put("packagelist", jsonArray)
            jsonData = json.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("sdsa",e.printStackTrace().toString())
        }

        return jsonData.toRequestBody()
    }

    private fun submitSelectedList(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                addPackage(selectedListSubmitRequest())
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                getAllPackagesList()
                activity!!.toast {
                    message = "Successfully added packages"
                    duration = Toast.LENGTH_LONG
                }
            }else{
                activity!!.toast {
                    message = "OOPS! Something went wrong"
                    duration = Toast.LENGTH_LONG
                }
            }
            showProgress(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_package, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()
        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getAllPackagesList()
        }elseDo {
            empty_caution.visibility = View.VISIBLE
            rv.visibility = View.GONE
            next.visibility = View.GONE
            activity!!.toast {
                message = "No internet connectivity!"
                duration = Toast.LENGTH_LONG
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.select ->{

            }
            R.id.next ->{
                packageSelectedList.isNotEmpty().doIfTrue {
                    submitSelectedList()
                }elseDo {
                    activity!!.toast {
                        message = "Please select a package"
                        duration = Toast.LENGTH_LONG
                    }
                }
            }
        }
    }
}