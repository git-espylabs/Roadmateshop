package com.roadmate.shop.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.roadmate.shop.R
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.RoadmateApiResponse
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.startActivity
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.rmapp.AppSession
import com.roadmate.shop.ui.activities.CheckOtpActivity
import com.roadmate.shop.utils.CommonUtils
import com.roadmate.shop.utils.PermissionUtils
import com.roadmate.shop.utils.compressImageFile
import com.yalantis.ucrop.util.FileUtils
import kotlinx.android.synthetic.main.fragment_shop_register_second.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import retrofit2.http.Part
import java.io.*
import java.util.*

private const val RES_IMAGE = 100

class SignUpFragmentSecond : BaseFragment(), View.OnClickListener, OnMyLocationButtonClickListener,
    OnMyLocationClickListener, OnMapReadyCallback {

    var progressHandler: Handler = Handler()
    var image1Added = false
    var selectImage1: Uri? = null
    val PHOTO_PICKER_ACTIVITY_REQUEST_CODE = 10
    private var mMap: GoogleMap? = null
    private var onCameraIdleListener: OnCameraIdleListener? = null
    var lattitude = "0.0"
    var longitude = "0.0"
    var handler = Handler()
    private var imgPath: String = ""
    private var imageUri: Uri? = null
    private var queryImageUrl: String = ""

    var selectFirstImagePath = ""

    private fun initMapView(){
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        configureCameraIdle()
    }

    private fun askAppLocationPermission(){
        if (PermissionUtils.isPermissionRationale(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )){
            PermissionUtils.requestPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                PermissionUtils.ACCESS_FINE_LOCATION
            )
        }else{
            PermissionUtils.requestPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                PermissionUtils.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun processSignUp() {
        if (isImageAvailable()){
            progressHandler.postDelayed({
                multiPartRequest()
            }, 500)
        }
    }

    private fun multiPartRequest(){
        showProgress(true, "Processing image..")

        try {
            Thread(Runnable {

                var mMap: HashMap<String, String> =
                    this.arguments!!.getSerializable("regMap") as HashMap<String, String>

                val type = MultipartBody.Part.createFormData("type", mMap["shoptypeId"]!!)
                val shopname = MultipartBody.Part.createFormData("shopname", mMap["shopnameReg"]!!)
                val phnum = MultipartBody.Part.createFormData("phnum", mMap["shopMobile"]!!)
                val phnum2 = MultipartBody.Part.createFormData("phnum2", mMap["shoplandline"]!!)
                val desc = MultipartBody.Part.createFormData("desc", mMap["shopdesc"]!!)
                val opentime = MultipartBody.Part.createFormData(
                    "opentime",
                    mMap["shop_open_time"]!!
                )
                val closetime = MultipartBody.Part.createFormData(
                    "closetime",
                    mMap["shop_close_time"]!!
                )
                val agrimentverification_status = MultipartBody.Part.createFormData(
                    "agrimentverification_status",
                    "1"
                )

                val address = MultipartBody.Part.createFormData(
                    "address",
                    edt_address.text.toString()
                )
                val pincode = MultipartBody.Part.createFormData(
                    "pincode",
                    edt_pincode.text.toString()
                )
                val latitude = MultipartBody.Part.createFormData("latitude", lattitude!!)
                val logitude = MultipartBody.Part.createFormData("logitude", longitude!!)
                val trans_id = MultipartBody.Part.createFormData("trans_id", "0")

                val image1Multipart = if (selectFirstImagePath.isNotEmpty()) {
                    getMultiParImageBody(selectFirstImagePath, "image")
                } else {
                    getMultiParImageBody(null, "image")
                }

                handler.post(Runnable {
                    showProgress(true, "Uploading..")
                    initiateApiCall(
                        image1Multipart,
                        type,
                        shopname,
                        phnum,
                        phnum2,
                        desc,
                        opentime,
                        closetime,
                        agrimentverification_status,
                        address,
                        pincode,
                        latitude,
                        logitude,
                        trans_id,
                        mMap["shopMobile"]!!
                    )
                })
            }).start()
        } catch (e: Exception) {
            showProgress(false, "OOPS! Something went wrong")
        }
    }

    private fun initiateApiCall(
        @Part image1Multipart: MultipartBody.Part,
        @Part type: MultipartBody.Part,
        @Part shopname: MultipartBody.Part,
        @Part phnum: MultipartBody.Part,
        @Part phnum2: MultipartBody.Part,
        @Part desc: MultipartBody.Part,
        @Part opentime: MultipartBody.Part,
        @Part closetime: MultipartBody.Part,
        @Part agrimentverification_status: MultipartBody.Part,
        @Part address: MultipartBody.Part,
        @Part pincode: MultipartBody.Part,
        @Part latitude: MultipartBody.Part,
        @Part logitude: MultipartBody.Part,
        @Part trans_id: MultipartBody.Part,
        userMobil: String
    ){


        try {
            lifecycleScope.launch {
                val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                    shopSignUp(
                        image1Multipart,
                        type,
                        shopname,
                        phnum,
                        phnum2,
                        desc,
                        opentime,
                        closetime,
                        agrimentverification_status,
                        address,
                        pincode,
                        latitude,
                        logitude,
                        trans_id
                    )
                }
                if (response.isSuccessful && response.body()?.message!! == "Success"){

                    AppSession.userMobile = userMobil
                    AppSession.otpTemp = response.body()?.data!!
                    moveToOtpValidation()
                    activity!!.toast {
                        message = "Registration Success"
                        duration = Toast.LENGTH_LONG
                    }
                }else if (response.isSuccessful){
                    activity!!.toast {
                        message = response.body()?.message!!
                        duration = Toast.LENGTH_LONG
                    }
                }
                showProgress(false, "")
            }
        } catch (e: Exception) {
            showProgress(false, "OOPS! Something went wrong")
            Log.e("Check", e.localizedMessage.toString())
        }
    }

    private fun moveToOtpValidation() {
        ShopDetails().isShopRegistered = true
        activity?.startActivity<CheckOtpActivity>()
        activity?.finish()
    }

    private fun isImageAvailable(): Boolean{
        if (!image1Added){
            activity!!.toast {
                message = "Choose at least one image"
                duration = Toast.LENGTH_LONG
            }
            return false
        }
        return  true
    }

    private fun initImageAccessFromStorage(){
        if (!PermissionUtils.checkPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            if (PermissionUtils.isPermissionRationale(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )){
                PermissionUtils.requestPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    PermissionUtils.EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE
                )
            }else{
                PermissionUtils.requestPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    PermissionUtils.EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE
                )
            }
        }else{
            val pickPhoto = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(pickPhoto, PHOTO_PICKER_ACTIVITY_REQUEST_CODE)

        }
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

        val filec = File(imageFile)
        val requestFile2: RequestBody = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            filec
        )

        val body = MultipartBody.Part.createFormData(fileName, filec.name, requestFile2)

        return body/*MultipartBody.Part.createFormData(fileName, file.name, requestFile)*/
    }

    private fun configureCameraIdle(){
        onCameraIdleListener = OnCameraIdleListener {
            val latLng = mMap!!.cameraPosition.target
            val geocoder = Geocoder(activity)
            val point = CameraUpdateFactory.newLatLngZoom(
                LatLng(latLng.latitude, latLng.longitude),
                10f
            )
            mMap!!.moveCamera(point)
            mMap!!.animateCamera(point)

            try {
                val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addressList != null && addressList.size > 0) {
                    val locality = addressList[0].getAddressLine(0)
                    val country = addressList[0].countryName
                    lattitude = "" + latLng.latitude
                    longitude = "" + latLng.longitude
                    edt_address.setText(locality)
                    edt_pincode.setText(addressList[0].postalCode)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun showProgress(visible: Boolean, text: String) {
        if (visible) {
            spin_kit.visibility = View.VISIBLE
            btn_save.visibility = View.GONE
            processtv.visibility = View.VISIBLE
            processtv.text = text
        } else {
            spin_kit.visibility = View.GONE
            btn_save.visibility = View.VISIBLE
            processtv.visibility = View.GONE
        }
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
            FileUtils.copyFile(
                imageSource!!,
                activity!!.getExternalFilesDir(null).toString() + "/IMG_TMP/" + source.name
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return source.name
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_register_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btn_save.setOnClickListener(this)
        image.setOnClickListener(this)

        initMapView()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_save -> {
                processSignUp()
            }
            R.id.image -> {
                chooseImage()
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
            PermissionUtils.ACCESS_FINE_LOCATION -> {
                if (mMap != null && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap!!.isMyLocationEnabled = true
                    mMap!!.uiSettings.isMyLocationButtonEnabled = true
                    mMap!!.setOnMyLocationButtonClickListener(this)
                    mMap!!.setOnMyLocationClickListener(this)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (progressHandler != null){
            progressHandler.removeCallbacksAndMessages(null)
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        TODO("Not yet implemented")
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap!!.mapType = MAP_TYPE_NORMAL
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(0.0, 0.0), 0f))
        mMap!!.setOnCameraIdleListener(onCameraIdleListener)

        if (PermissionUtils.checkPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (mMap != null) {
                mMap!!.isMyLocationEnabled = true
                mMap!!.uiSettings.isMyLocationButtonEnabled = true
                mMap!!.setOnMyLocationButtonClickListener(this)
                mMap!!.setOnMyLocationClickListener(this)
            }
        }else{
            askAppLocationPermission()
        }
    }
}