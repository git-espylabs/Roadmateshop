package com.roadmate.shop.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.roadmate.shop.BuildConfig
import com.roadmate.shop.R
import com.roadmate.shop.adapter.*
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.*
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.startActivity
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.rmapp.AppSession
import com.roadmate.shop.ui.activities.FeedbackActivity
import com.roadmate.shop.ui.activities.LoginActivity
import com.roadmate.shop.ui.activities.PackageDetailsActivity
import com.roadmate.shop.ui.activities.ShopEditActivity
import kotlinx.android.synthetic.main.fragment_my_shop_profile.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


class MyShopProfileFragment: Fragment(), View.OnClickListener {

    private lateinit var shopTypeAdapter: ShopTypeSelectionAdapter
    private var shopTypeNewlyAddedList: ArrayList<MoreServicesTrans> = arrayListOf()
    private var addedShopTypesList: ArrayList<MoreServicesTrans> = arrayListOf()
    lateinit var addedCategoryListAdapter:AddedShopCategoriesAdapter

    var logitude = ""
    var lattitude = ""
    var descriptionShop = ""
    var pincode = ""
    var phone_number = ""
    var address = ""
    var close_time = ""
    var open_time = ""
    var imageName = ""

    private fun setListeners(){
        shopStatus.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            updateShopStatus(isChecked)
        })
        btnShare.setOnClickListener(this)
        btnInvite.setOnClickListener(this)
        feedback.setOnClickListener(this)
        terms.setOnClickListener(this)
        logoutButton.setOnClickListener(this)
        btnAddService.setOnClickListener(this)
        editButton.setOnClickListener(this)
    }

    private fun getShopImage(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<ShopProfileMaster>> {
                getShopImage(shopImageRequestJSON())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                Glide.with(activity!!).load(BuildConfig.BANNER_URL_ENDPOINT + response.body()?.data!![0].image)
                    .error(R.drawable.road_mate_plain).error(R.drawable.road_mate_plain)
                    .into(shopimage)
                logitude = response.body()?.data!![0].logitude
                lattitude = response.body()?.data!![0].lattitude
                descriptionShop = response.body()?.data!![0].description
                pincode = response.body()?.data!![0].pincode
                phone_number = response.body()?.data!![0].phone_number
                address = response.body()?.data!![0].address
                close_time = response.body()?.data!![0].close_time
                open_time = response.body()?.data!![0].open_time
                imageName = response.body()?.data!![0].image
            }
            populateShopContact()
            getMyAddedCategories(false)
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

    private fun shopStatusUpdateRequestJSON(isChecked: Boolean) : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null

        val status = if (isChecked){
            "1"
        }else{
            "0"
        }

        try {
            json = JSONObject()
            json.put("shopid", ShopDetails().shopId)
            json.put("status", status)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun populateShopContact(){
        tvName.text = ShopDetails().shopName
        tvContact.text = "Contact: $phone_number"
        AppSession.userMobile = phone_number


        shopStatus.isChecked = ShopDetails().isShopOpen
        if (ShopDetails().isShopOpen){
            shopStatus.text = "Shop Open"
        }else{
            shopStatus.text = "Shop Closed"
        }
    }

    private fun updateShopStatus(isChecked: Boolean){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                updateSHopStatus(shopStatusUpdateRequestJSON(isChecked))
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                if (isChecked){
                    shopStatus.text = "Shop Open"
                }else{
                    shopStatus.text = "Shop Closed"
                }
                ShopDetails().isShopOpen = isChecked
                activity!!.toast {
                    message = "Status Updated"
                    duration = Toast.LENGTH_LONG
                }
            }else{
                activity!!.toast {
                    message = "OOPS!! Something went wrong."
                    duration = Toast.LENGTH_LONG
                }
            }
            showProgress(false)
        }
    }

    private fun getMyAddedCategories(isRefreshCats: Boolean){
        lifecycleScope.launch {
            showProgress(true)
            val response = APIManager.call<ApiServices, Response<MoreServicesMaster>> {
                getAddedShopTypes(shopImageRequestJSON())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                addedShopTypesList.clear()
                addedShopTypesList.addAll(response.body()?.data!!)
                addedCategoryListAdapter.notifyDataSetChanged()
                for (item in response.body()?.data!!){
                    if (item.serviceId == "1"){
                        ShopDetails().isMechanic = true
                        break
                    }
                }
            }
            if (!isRefreshCats) {
                getReviewsAndRatings()
            }else{
                showProgress(false)
            }
        }
    }

    private fun populateAddedCategoriesList(list: ArrayList<MoreServicesTrans>){
        shCatList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        addedCategoryListAdapter = AddedShopCategoriesAdapter(activity!!, list){ pos, obj ->
            removeCategory(pos, obj)
        }
        shCatList.adapter = addedCategoryListAdapter
    }

    private fun removeCategory(position: Int, obj: MoreServicesTrans){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                deleteCategory(removeCategoryRequestJSON(obj.serviceId))
            }
            if (response.isSuccessful && response.body()?.message.equals("success", true)){
                addedShopTypesList.removeAt(position)
                addedCategoryListAdapter.notifyDataSetChanged()
                activity!!.toast {
                    message = "Category removed"
                    duration = Toast.LENGTH_SHORT
                }
            }
            showProgress(false)
        }
    }


    private fun removeCategoryRequestJSON(catid: String) : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shpcatid", catid)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }


    private fun getReviewsAndRatings(){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<ReviewMaster>> {
                getShopReviews(shopImageRequestJSON())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateReviewList(response.body()?.data!!)
            }
            showProgress(false)
        }
    }

    private fun populateReviewList(list: ArrayList<ReviewTrans>){
        try {
            var countSum = 0.0;
            for (obj in list){
                var rCount = obj.review_count.toDoubleOrNull()
                countSum += rCount!!
            }
            var countAvg = countSum/list.size
            ratingBar2.rating = countAvg.toFloat()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        shReviewList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        var adapter = ReviewListAdapter(activity!!, list)
        shReviewList.adapter = adapter
    }

    private fun getShopTypesList(){
        lifecycleScope.launch{
            categoryProgress.visibility = View.VISIBLE
            btnAddService.visibility = View.GONE
            shopTypeNewlyAddedList.clear()

            val response =  APIManager.call<ApiServices, Response<MoreServicesMaster>> {
                getShopTypes()
            }

            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateServicesList(response.body()?.data!!)
            }else{
                activity!!.toast {
                    message = "Services not available at this moment!"
                    duration = Toast.LENGTH_LONG }
            }
            categoryProgress.visibility = View.GONE
            btnAddService.visibility = View.VISIBLE
        }
    }

    private fun  populateServicesList(list: java.util.ArrayList<MoreServicesTrans>){

        val dialog: android.app.AlertDialog
        val builder = android.app.AlertDialog.Builder(activity)
        val layoutInflater = layoutInflater

        val view = layoutInflater.inflate(R.layout.custome_dialog_select_multiple_shop_type, null)
        builder.setView(view)
        dialog = builder.create()
        val textView = view.findViewById<TextView>(R.id.alert_heading)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        textView.text = "Select Shop Type:"
        val recyclerView: RecyclerView = view.findViewById(R.id.customAlertRecyclerView)
        btnSubmit.setOnClickListener {
            dialog.dismiss()
            submitNewCategoriesList()
        }
        dialog.show()
        recyclerView.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        shopTypeAdapter = ShopTypeSelectionAdapter(activity!!, list){ objM, isSelected ->
            for (obj in shopTypeNewlyAddedList){
                if (obj.serviceId == objM!!.serviceId){
                    shopTypeNewlyAddedList.remove(obj)
                    break
                }
            }
            if (isSelected){
                shopTypeNewlyAddedList.add(objM!!)
            }
            shopTypeAdapter.notifyDataSetChanged()
        }
        recyclerView.adapter = shopTypeAdapter

    }

    private fun submitNewCategoriesList(){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                insertNewShopCategories(selectedListSubmitRequest())
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                getMyAddedCategories(true)
                activity!!.toast {
                    message = "New categories added!"
                    duration = Toast.LENGTH_LONG
                }
            }else{
                activity!!.toast {
                    message = "OOPS! Something went wrong"
                    duration = Toast.LENGTH_LONG
                }

            }
        }
    }

    private fun selectedListSubmitRequest() : RequestBody {
        var jsonData = ""
        var json = JSONObject()

        var jsonArray = JSONArray()
        try {
            for (obj in shopTypeNewlyAddedList){
                var jsonObj = JSONObject()
                jsonObj.put("shopid",ShopDetails().shopId)
                jsonObj.put("catid",obj.serviceId)
                jsonArray.put(jsonObj)
            }
            json.put("shopprovidcategory", jsonArray)
            jsonData = json.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun openTermsAndConditions(termsText: String) {
        val dialogBuilder = AlertDialog.Builder(activity!!)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.terms_and_conditions, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(true)
        dialogView.findViewById<Button>(R.id.button).setOnClickListener { alertDialog.cancel() }
        alertDialog.show()
    }

    private fun getTermsAndConditions(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<TermsConditionsMaster>> {
                getShopTermsAndConditions()
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                openTermsAndConditions(response.body()?.data!![0].tc_details)
            }else{
                activity!!.toast {
                    message = "Something went wrong!"
                    duration = Toast.LENGTH_LONG
                }
            }
            showProgress(false)
        }
    }

    private fun showProgress(show: Boolean){
        if (show){
            progressView.visibility = View.VISIBLE
        }else{
            progressView.visibility = View.GONE
        }
    }

    private fun redirectToShopAppPlayStore(){
        val appPackageName = "com.roadmate.app"
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }


    private fun inviteFriends(){
        val appPackageName = context!!.packageName
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "I am inviting you to register your automobile shop with RoadMate. RoadMate Helps your customers to find your shop easily, receive online bookings, create your exciting offers, sell and buy vehicles and accessories and much more: https://play.google.com/store/apps/details?id=$appPackageName"
        )
        sendIntent.type = "text/plain"
        context!!.startActivity(sendIntent)

    }

    private fun shareMyShop(){
        val appPackageName = context!!.packageName
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            ShopDetails().shopName + " services are now available at RoadMate: https://play.google.com/store/apps/details?id=$appPackageName"
        )
        sendIntent.type = "text/plain"
        context!!.startActivity(sendIntent)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_shop_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()
//        getShopImage()
//        populateAddedCategoriesList(addedShopTypesList)
    }

    override fun onResume() {
        super.onResume()
        getShopImage()
        populateAddedCategoriesList(addedShopTypesList)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.logoutButton ->{
                AppSession.clearUserSession(activity!!)
                activity!!.startActivity<LoginActivity>()
                activity!!.finish()
            }
            R.id.btnShare ->{
                shareMyShop()
            }
            R.id.btnInvite ->{
                inviteFriends()
            }
            R.id.feedback ->{
                activity!!.startActivity<FeedbackActivity>()
            }
            R.id.terms ->{
                getTermsAndConditions()
            }
            R.id.btnAddService ->{
                getShopTypesList()
            }
            R.id.editButton ->{
                val intent = Intent(context, ShopEditActivity::class.java)
                intent.putExtra("logitude", logitude)
                intent.putExtra("lattitude", lattitude)
                intent.putExtra("descriptionShop", descriptionShop)
                intent.putExtra("pincode", pincode)
                intent.putExtra("phone_number", phone_number)
                intent.putExtra("address", address)
                intent.putExtra("close_time", close_time)
                intent.putExtra("open_time", open_time)
                intent.putExtra("imageName", imageName)
                startActivity(intent)
            }
        }
    }
}