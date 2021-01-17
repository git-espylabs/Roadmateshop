package com.roadmate.shop.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.roadmate.shop.R
import com.roadmate.shop.adapter.MyStoreProductAdapter
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.MyStoreProductMaster
import com.roadmate.shop.api.response.MyStoreProductTrans
import com.roadmate.shop.api.response.RoadmateApiResponse
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.ui.activities.MyStoreDetailActivity
import com.roadmate.shop.utils.CommonUtils
import com.roadmate.shop.utils.CommonUtils.Companion.stream2file
import com.roadmate.shop.utils.PermissionUtils
import com.roadmate.shop.utils.compressImageFile
import com.yalantis.ucrop.util.FileUtils
import kotlinx.android.synthetic.main.fragment_sell_here_tab.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.io.*

private const val RES_IMAGE = 100

class SellHereTabFragment: Fragment(), View.OnClickListener {

    var progressHandler: Handler = Handler()
    var image1Added = false
    var image2Added = false
    var image3Added = false
    var selectImage1: Uri? = null
    var selectImage2: Uri? = null
    var selectImage3: Uri? = null

    private var selected_image_code = 1

    private var queryImageUrl: String = ""
    private var imgPath: String = ""
    private var imageUri: Uri? = null

    var selectFirstImagePath = ""
    var selectSecondImagePath = ""
    var selectThirdImagePath = ""

    lateinit var mAdapter: MyStoreProductAdapter
    var ownProductlist: ArrayList<MyStoreProductTrans> = arrayListOf()

    private fun setListeners(){
        addproduct.setOnClickListener(this)
        category_next.setOnClickListener(this)
        category_cancel.setOnClickListener(this)
        name_cancel.setOnClickListener(this)
        name_next.setOnClickListener(this)
        image_cancel.setOnClickListener(this)
        image_next.setOnClickListener(this)
        image1.setOnClickListener(this)
        image2.setOnClickListener(this)
        image3.setOnClickListener(this)
    }

    private fun getMyProductsList(){
        showProgress(true)

        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<MyStoreProductMaster>> {
                getMyProducts(productListJsonRequest())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                ownProductlist.clear()
                ownProductlist.addAll(response.body()?.data!!)
                mAdapter.notifyDataSetChanged()
            }else{
                empty_caution.visibility = View.VISIBLE
                sellHereRecycler.visibility = View.GONE
            }
            showProgress(false)
        }
    }

    private fun populateProductList(){
        sellHereRecycler.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        mAdapter = MyStoreProductAdapter(activity!!, ownProductlist, true){pData, isSoldOut ->

            if (isSoldOut){
                markProductAsSoldOut(pData!!)
            }else{
                val intent1 = Intent(context, MyStoreDetailActivity::class.java)
                intent1.putExtra("p_id", pData?.pid)
                startActivity(intent1)
            }
        }
        sellHereRecycler.adapter = mAdapter
    }

    private fun markProductAsSoldOut(pData: MyStoreProductTrans){
        lifecycleScope.launch {
            showProgress(true)
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                setProductSoldout(productSoldoutJsonRequest(pData.pid))
            }
            if (response.isSuccessful && response.body()?.message.equals("success",true)){
                activity!!.toast {
                    message = "Product sold out!"
                    duration = Toast.LENGTH_SHORT
                }
                ownProductlist.remove(pData)
                mAdapter.notifyDataSetChanged()
            }else{
                activity!!.toast {
                    message = "OOPS! Something went wrong"
                    duration = Toast.LENGTH_SHORT
                }
            }
            showProgress(false)
        }
    }

    private fun populateCategoryList(){
        val category = resources.getStringArray(R.array.addstore_category)
        val arrayAdapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_dropdown_item,
            category
        )
        category_spinner.adapter = arrayAdapter
    }

    private fun prepareCategoryLayout(){
        addproduct.visibility = View.GONE
        process_layout.visibility = View.VISIBLE
        progressHandler.postDelayed({
            populateCategoryList()
            process_layout.visibility = View.GONE
            category_layout.visibility = View.VISIBLE
        }, 1000)
    }

    private fun dismissCategoryLayout(){
        process_layout.visibility = View.VISIBLE
        category_layout.visibility = View.GONE
        progressHandler.postDelayed({
            addproduct.visibility = View.VISIBLE
            process_layout.visibility = View.GONE
        }, 1000)
    }

    private fun prepareNameLayout(){
        category_layout.visibility = View.GONE
        process_layout.visibility = View.VISIBLE
        progressHandler.postDelayed({
            process_layout.visibility = View.GONE
            name_layout.visibility = View.VISIBLE
        }, 1000)
    }

    private fun dismissNameLayout(){
        process_layout.visibility = View.VISIBLE
        name_layout.visibility = View.GONE
        progressHandler.postDelayed({
            process_layout.visibility = View.GONE
            addproduct.visibility = View.VISIBLE
        }, 1000)
    }

    private fun prepareImageLayout(){
        name_layout.visibility = View.GONE
        process_layout.visibility = View.VISIBLE
        progressHandler.postDelayed({
            process_layout.visibility = View.GONE
            image_layout.visibility = View.VISIBLE
        }, 1000)

    }

    private fun dismissImageLayout(){
        if (image1Added){
            image1Added = false
        }
        if (image2Added){
            image2Added = false
        }
        if (image3Added){
            image3Added = false
        }
        image_layout.visibility = View.GONE
        process_layout.visibility = View.VISIBLE
        progressHandler.postDelayed({
            process_layout.visibility = View.GONE
            addproduct.visibility = View.VISIBLE
        }, 1000)
    }

    private fun sendImages(){
        image_layout.visibility = View.GONE
        process_layout.visibility = View.VISIBLE

        progressHandler.postDelayed({
            multiPartRequest()
        }, 500)
    }

    private fun productListJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            Log.i("shopId",ShopDetails().shopId)
            Log.i("shopType",ShopDetails().shopType)

            json = JSONObject()
            //json.put("shocatid", ShopDetails().shopType)
            //json.put("scat", ShopDetails().shopType)
            json.put("shop_id", ShopDetails().shopId)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun productSoldoutJsonRequest(pid: String) : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("storeid", pid)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
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

        return createFormData(fileName, file.name, requestFile)
    }

    private fun multiPartRequest(){
        try {
            val userId = createFormData("userid", ShopDetails().shopId)
            val productName = createFormData("pro_name", edtname.text.toString())
            val productPrice = createFormData("price", edtprice.text.toString())
            val productDescription = createFormData("description", edtDesc.text.toString())
            val productCategory = createFormData("category", category_spinner.selectedItemPosition.toString())
            val userType = createFormData("usertype", "2")


            val image1Multipart = if (selectFirstImagePath.isNotEmpty()) {
                getMultiParImageBody(selectFirstImagePath, "image1")
            } else {
                getMultiParImageBody(null, "image1")
            }

            val image2Multipart = if (selectSecondImagePath.isNotEmpty()) {
                getMultiParImageBody(selectSecondImagePath, "image2")
            } else {
                getMultiParImageBody(null, "image2")
            }

            val image3Multipart = if (selectThirdImagePath.isNotEmpty()) {
                getMultiParImageBody(selectThirdImagePath, "image3")
            } else {
                getMultiParImageBody(null, "image3")
            }

            try {
                lifecycleScope.launch {
                    val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                        insertProductToStore(userId, productName, productPrice, productCategory, productDescription, userType, image1Multipart, image2Multipart, image3Multipart)
                    }
                    if (response.isSuccessful && response.body()?.message!! == "Success"){
                        getMyProductsList()
                        activity!!.toast {
                            message = "Product added"
                            duration = Toast.LENGTH_LONG
                        }
                    }else{
                        activity!!.toast {
                            message = "Could not add product!"
                            duration = Toast.LENGTH_LONG
                        }
                    }
                    process_layout.visibility = View.GONE
                    addproduct.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                process_layout.visibility = View.GONE
                addproduct.visibility = View.VISIBLE
                activity!!.toast {
                    message = "OOPS! Something went wrong"
                    duration = Toast.LENGTH_LONG
                }
            }
        } catch (e: Exception) {
            process_layout.visibility = View.GONE
            addproduct.visibility = View.VISIBLE
            activity!!.toast {
                message = "OOPS! Something went wrong"
                duration = Toast.LENGTH_LONG
            }
        }
    }

    private fun isNameLayoutInputsValid(): Boolean{
        if (edtname.text.toString().isEmpty()){
            edtname.error = "Empty"
            return  false
        }else if (edtprice.text.toString().isEmpty()){
            edtprice.error = "Empty"
            return  false
        }

        return true
    }

    private fun isImageAvailable(): Boolean{
        if (!image1Added && !image2Added && !image3Added){
            activity!!.toast {
                message = "Choose at least one image"
                duration = Toast.LENGTH_LONG
            }
            return false
        }
        return  true
    }

    private fun initImageAccessFromStorage(requestCode: Int){
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
            startActivityForResult(pickPhoto, requestCode)

        }
    }

    private fun showProgress(visible:Boolean){
        if (visible){
            loadingFrame.visibility = View.VISIBLE
        }else{
            loadingFrame.visibility = View.GONE
        }
    }


    private fun chooseImage(imgCode: Int) {
        selected_image_code = imgCode
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

    private fun handleImageRequest(data: Intent?, selectedImage: Int) {
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
            when(selectedImage){
                1 -> {
                    selectFirstImagePath = activity!!.getExternalFilesDir(null).toString()+"/IMG_TMP/"+fileName
                    image1Added = true
                    imageView = image2
                }
                2 -> {
                    selectSecondImagePath = activity!!.getExternalFilesDir(null).toString() + "/IMG_TMP/" + fileName
                    image2Added = true
                    imageView = image1
                }
                3 -> {
                    selectThirdImagePath = activity!!.getExternalFilesDir(null).toString() + "/IMG_TMP/" + fileName
                    image3Added = true
                    imageView = image3
                }
            }

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sell_here_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()
        populateProductList()
        getMyProductsList()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.addproduct -> {
                prepareCategoryLayout()
            }
            R.id.category_next -> {
                prepareNameLayout()
            }
            R.id.category_cancel -> {
                dismissCategoryLayout()
            }
            R.id.name_next -> {
                if (isNameLayoutInputsValid()) {
                    prepareImageLayout()
                }
            }
            R.id.name_cancel -> {
                dismissNameLayout()
            }
            R.id.image_next -> {
                if (isImageAvailable()){
                    sendImages()
                }
            }
            R.id.image_cancel -> {
                dismissImageLayout()
            }
            R.id.image1 -> {
                chooseImage(2)
            }
            R.id.image2 -> {
                chooseImage(1)
            }
            R.id.image3 -> {
                chooseImage(3)
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RES_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    handleImageRequest(data, selected_image_code)
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
}