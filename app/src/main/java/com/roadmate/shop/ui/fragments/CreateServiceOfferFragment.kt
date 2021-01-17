package com.roadmate.shop.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.roadmate.app.api.response.VehicleFuelTypeMaster
import com.roadmate.app.api.response.VehicleFuelTypeTrans
import com.roadmate.shop.R
import com.roadmate.shop.adapter.AlertDialogAdapter
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.*
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.launchActivity
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.models.ServiceInsertModel
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.ui.activities.AddNewServiceActivity
import com.roadmate.shop.ui.activities.CreateOfferActivity
import com.roadmate.shop.utils.CommonUtils
import com.roadmate.shop.utils.ImageUtils
import com.roadmate.shop.utils.PermissionUtils
import com.roadmate.shop.utils.compressImageFile
import com.yalantis.ucrop.util.FileUtils
import kotlinx.android.synthetic.main.fragment_create_product_offer.*
import kotlinx.android.synthetic.main.fragment_create_sevice_offer.*
import kotlinx.android.synthetic.main.fragment_create_sevice_offer.btnSubmit
import kotlinx.android.synthetic.main.fragment_create_sevice_offer.etDesc
import kotlinx.android.synthetic.main.fragment_create_sevice_offer.etNormalAmount
import kotlinx.android.synthetic.main.fragment_create_sevice_offer.etOfferAmount
import kotlinx.android.synthetic.main.fragment_create_sevice_offer.etTitle
import kotlinx.android.synthetic.main.fragment_create_sevice_offer.image
import kotlinx.android.synthetic.main.fragment_create_sevice_offer.spin_kit_submit
import kotlinx.android.synthetic.main.fragment_create_sevice_offer.tvEndDate
import kotlinx.android.synthetic.main.fragment_sell_here_tab.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.io.*
import java.util.*

private const val RES_IMAGE = 100
private const val VEH_TYPE = 107

class CreateServiceOfferFragment: BaseFragment(), View.OnClickListener {

    private var selectedShopTypeId = ""
    private var selectedVehicleTypeId = ""
    private var selectedVehicleBrandId = ""
    private var selectedVehicleModelId = ""
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

    private fun setListeners(){
        selShopCat.setOnClickListener(this)
        selVehType.setOnClickListener(this)
        selBrand.setOnClickListener(this)
        selModel.setOnClickListener(this)
        tvEndDate.setOnClickListener(this)
        image.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
        selFuelType.setOnClickListener(this)
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
                is MoreServicesTrans -> {
                    selectedShopTypeId = (itineraryList[position] as MoreServicesTrans).serviceId
                }
                is VehicleTypeTrans -> {
                    selectedVehicleTypeId = (itineraryList[position] as VehicleTypeTrans).vehicleTypeId
                    selBrand.hint = "Select"
                    selBrand.text = ""

                    selModel.hint = "Select"
                    selModel.text = ""
                }
                is VehicleBrandTrans -> {
                    selectedVehicleBrandId = (itineraryList[position] as VehicleBrandTrans).vehicleBrandId
                    selModel.hint = "Select"
                    selModel.text = ""
                }
                is VehicleModelTrans -> {
                    selectedVehicleModelId = (itineraryList[position] as VehicleModelTrans).vehicleModelId
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

    private inline fun<reified T> getItineraryNamesList(inputList: ArrayList<T>): ArrayList<String>{
        var nameList: ArrayList<String> = arrayListOf()

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

    private fun getMultiParImageBody(imageFile: String?, fileName: String): MultipartBody.Part{
        var file: File? = null
        var inputStream: InputStream? = null
        if (imageFile != null && imageFile.isNotEmpty()) {
            inputStream  = FileInputStream(imageFile);
        } else {
            val am = context!!.assets
            try {
                inputStream = am.open("defaults/ic_launcher.png")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val bin = BufferedInputStream(inputStream)
        file = CommonUtils.stream2file(bin)

        val requestFile = file!!.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(fileName, file.name, requestFile)
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

    private fun getVehicleTypesList(){
        showProgress(R.id.spinBar2, true)

        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<VehicleTypeMaster>> {
                getVehicleTypes()
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateItinerariesList("Select Vehicle Category", response.body()?.data!!, selVehType, VehicleTypeTrans())
            }
            showProgress(R.id.spinBar2, false)
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

    private fun getVehicleModels(vehBrandId: String){
        showProgress(R.id.spinBar4, true)

        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<VehicleModelMaster>> {
                getVehicleModels(vehItineraryJsonRequest("brand", vehBrandId))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateItinerariesList("Select Vehicle Model", response.body()?.data!!, selModel, VehicleModelTrans())
            }
            showProgress(R.id.spinBar4, false)
        }
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
            OnDateSetListener { _, year, monthOfYear, dayOfMonth -> tvEndDate.text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.show()
    }

    private fun initImageAccessFromStorage(){
        if (!PermissionUtils.checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            if (PermissionUtils.isPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                PermissionUtils.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionUtils.EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE)
            }else{
                PermissionUtils.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionUtils.EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE)
            }
        }else{
            val pickPhoto = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(pickPhoto, PHOTO_PICKER_ACTIVITY_REQUEST_CODE)

        }
    }

    private fun submitData(){

        try {
            showProgress(R.id.spin_kit_submit, true)
            btnSubmit.visibility = View.GONE

            val shop_id = MultipartBody.Part.createFormData("shop_id", ShopDetails().shopId)
            val shop_cat_id = MultipartBody.Part.createFormData("shop_cat_id", selectedShopTypeId)
            val title = MultipartBody.Part.createFormData("title", etTitle.text.toString())
            val small_desc = MultipartBody.Part.createFormData("small_desc", etDesc.text.toString())
            val normal_amount = MultipartBody.Part.createFormData("normal_amount", etNormalAmount.text.toString())
            val offer_amount = MultipartBody.Part.createFormData("offer_amount", etOfferAmount.text.toString())
            val vehicle_typeid = MultipartBody.Part.createFormData("vehicle_typeid", selectedVehicleTypeId)
            val brand_id = MultipartBody.Part.createFormData("brand_id", selectedVehicleBrandId)
            val model_id = MultipartBody.Part.createFormData("model_id", selectedVehicleModelId)
            val fuel_type = MultipartBody.Part.createFormData("fuel_type", selectedVehicleFuelTypelId)
            val offer_type = MultipartBody.Part.createFormData("offer_type", "1")
            val offer_end_date = MultipartBody.Part.createFormData("offer_end_date", CommonUtils.formatDate_ddMMyyyy(tvEndDate.text.toString())!!)
            var imageStatus = if (image1Added){
                "1"
            }else{
                "0"
            }
            val img_status = MultipartBody.Part.createFormData("image_uploded_status", imageStatus)

            val image1Multipart = if (selectFirstImagePath.isNotEmpty()) {
                getMultiParImageBody(selectFirstImagePath, "image")
            } else {
                getMultiParImageBody(null, "image")
            }

            lifecycleScope.launch {
                val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                    insertShopServiceOffers(shop_id, shop_cat_id, title, small_desc, normal_amount, offer_amount,
                        vehicle_typeid, brand_id, model_id, offer_type, image1Multipart, offer_end_date, fuel_type, img_status)
                }
                if (response.isSuccessful && response.body()?.message!! == "Success"){
                    activity!!.toast {
                        message = "Offer successfully added"
                        duration = Toast.LENGTH_SHORT
                    }
                    (activity as CreateOfferActivity?)?.exitOfferCreation()
                }else{
                    activity!!.toast {
                        message = "OOPS! Something went wrong!"
                        duration = Toast.LENGTH_SHORT
                    }
                }

                showProgress(R.id.spin_kit_submit, false)
                btnSubmit.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            showProgress(R.id.spin_kit_submit, false)
            btnSubmit.visibility = View.VISIBLE
        }
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
            R.id.spinBar3 -> spinBar3.visibility = viewVisibility
            R.id.spinBar4 -> spinBar4.visibility = viewVisibility
            R.id.spin_kit_submit -> spin_kit_submit.visibility = viewVisibility
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

    private fun chooseImage() {
        startActivityForResult(getPickImageIntent(), RES_IMAGE)
    }

    private fun getPickImageIntent(): Intent? {
        var chooserIntent: Intent? = null

        var intentList: MutableList<Intent> = ArrayList()

        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        intentList = addIntentsToList(activity!!, intentList, pickIntent)

        if (intentList.size > 0) {
            chooserIntent = Intent.createChooser(
                intentList.removeAt(intentList.size - 1),
                "Select"
            )
            chooserIntent!!.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                intentList.toTypedArray<Parcelable>()
            )
        }

        return chooserIntent
    }

    private fun addIntentsToList(
        context: Context,
        list: MutableList<Intent>,
        intent: Intent
    ): MutableList<Intent> {
        val resInfo = context.packageManager.queryIntentActivities(intent, 0)
        for (resolveInfo in resInfo) {
            val packageName = resolveInfo.activityInfo.packageName
            val targetedIntent = Intent(intent)
            targetedIntent.setPackage(packageName)
            list.add(targetedIntent)
        }
        return list
    }


    private fun handleImageRequest(data: Intent?) {
        val exceptionHandler = CoroutineExceptionHandler { _, t ->
            t.printStackTrace()
            activity!!.toast {
                message = "some error"
                duration = Toast.LENGTH_SHORT
            }
        }

        GlobalScope.launch(Dispatchers.Main + exceptionHandler) {

            if (data?.data != null) {     //Photo from gallery
                imageUri = data.data
                queryImageUrl = imageUri?.path!!
                queryImageUrl = activity!!.compressImageFile(queryImageUrl, false, imageUri!!)
            } else {
                queryImageUrl = imgPath ?: ""
                activity!!.compressImageFile(queryImageUrl, uri = imageUri!!)
            }
            imageUri = Uri.fromFile(File(queryImageUrl))
            var fileName = fileCopyToNative(imageUri!!.path)

            var imageView: ImageView? = null
            selectFirstImagePath = activity!!.getExternalFilesDir(null).toString()+"/IMG_TMP/"+fileName
            image1Added = true
            imageView = image

            if (queryImageUrl.isNotEmpty()) {

                Glide.with(activity!!)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .load(queryImageUrl)
                    .into(imageView!!)
            }
        }

    }

    private fun fileCopyToNative(imageSource: String?): String{
        val source = File(imageSource)
        val destination = File(activity!!.getExternalFilesDir(null), "/IMG_TMP")
        if (!destination.exists())
        {
            destination.mkdir()
        }
        try {
            FileUtils.copyFile(imageSource!!, activity!!.getExternalFilesDir(null).toString()+"/IMG_TMP/"+source.name)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return source.name
    }

    private fun isAllFieldsValid(): Boolean{
        if (selectedShopTypeId.isEmpty()){
            activity!!.toast {
                message = "Please select a shop category"
                duration = Toast.LENGTH_SHORT
            }
            return  false
        }else if (selectedVehicleTypeId.isEmpty()){
            activity!!.toast {
                message = "Please select a vehicle type"
                duration = Toast.LENGTH_SHORT
            }
            return  false
        }else if (selectedVehicleBrandId.isEmpty()){
            activity!!.toast {
                message = "Please select a vehicle brand"
                duration = Toast.LENGTH_SHORT
            }
            return  false
        }else if (selectedVehicleModelId.isEmpty()){
            activity!!.toast {
                message = "Please select a vehicle model"
                duration = Toast.LENGTH_SHORT
            }
            return  false
        }else if (selectedVehicleModelId.isEmpty()){
            activity!!.toast {
                message = "Please select a vehicle fuel type"
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
        }else if (etNormalAmount.text.toString().trim().isEmpty()){
            activity!!.toast {
                message = "Please enter Normal amount"
                duration = Toast.LENGTH_SHORT
            }
            return  false
        }else if (etOfferAmount.text.toString().trim().isEmpty()){
            activity!!.toast {
                message = "Please enter Offer amount"
                duration = Toast.LENGTH_SHORT
            }
            return  false
        }else if (tvEndDate.text.toString().trim().isEmpty()){
            activity!!.toast {
                message = "Please enter a end date"
                duration = Toast.LENGTH_SHORT
            }
            return  false
        }
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (activity != null) {
            activity!!.window
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        }
        return inflater.inflate(R.layout.fragment_create_sevice_offer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.selShopCat -> {
                getShopTypesList()
            }
            R.id.selVehType -> {
//                getVehicleTypesList()
                /*activity!!.launchActivity<AddNewServiceActivity> {
                    putExtra(AddNewServiceActivity.EXTRA_TITLE, "Select Offer Vehicles")
                    putExtra(AddNewServiceActivity.EXTRA_SUBMIT_BTN_NAME, "Done")
                }*/

                var intent = Intent(requireContext(), AddNewServiceActivity::class.java)
                intent.putExtra(AddNewServiceActivity.EXTRA_TITLE, "Select Offer Vehicles")
                intent.putExtra(AddNewServiceActivity.EXTRA_SUBMIT_BTN_NAME, "Done")
                startActivityForResult(intent, VEH_TYPE)

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
            R.id.selModel -> {
                selectedVehicleBrandId.isNotEmpty().doIfTrue {
                    getVehicleModels(selectedVehicleBrandId)
                }elseDo {
                    activity!!.toast {
                        message = "Please select a vehicle brand!"
                        duration = Toast.LENGTH_SHORT
                    }
                }
            }
            R.id.selFuelType -> {
                getVehicleFuelTypes()
            }
            R.id.tvEndDate -> {
                openDatePicker()
            }
            R.id.image -> {
                chooseImage()
            }
            R.id.btnSubmit ->{
                if (isAllFieldsValid()) {
                    submitData()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RES_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    handleImageRequest(data)
                }
            }
            VEH_TYPE ->{
                if (resultCode == 25){
                    activity?.let {
                        it.toast {
                            message = "YES YES"
                            duration = Toast.LENGTH_LONG
                        }
                    }
                    data?.let {
                        val list = it.getParcelableArrayListExtra<ServiceInsertModel>("datalist")
                    }
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PermissionUtils.EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImage()
                }
            }
        }
    }
}