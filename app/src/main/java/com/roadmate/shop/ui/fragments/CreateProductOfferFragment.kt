package com.roadmate.shop.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.roadmate.shop.R
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.RoadmateApiResponse
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.ui.activities.CreateOfferActivity
import com.roadmate.shop.utils.CommonUtils
import com.roadmate.shop.utils.ImageUtils
import com.roadmate.shop.utils.PermissionUtils
import com.roadmate.shop.utils.compressImageFile
import com.yalantis.ucrop.util.FileUtils
import kotlinx.android.synthetic.main.fragment_create_product_offer.*
import kotlinx.android.synthetic.main.fragment_create_product_offer.btnSubmit
import kotlinx.android.synthetic.main.fragment_create_product_offer.etDesc
import kotlinx.android.synthetic.main.fragment_create_product_offer.etNormalAmount
import kotlinx.android.synthetic.main.fragment_create_product_offer.etOfferAmount
import kotlinx.android.synthetic.main.fragment_create_product_offer.etTitle
import kotlinx.android.synthetic.main.fragment_create_product_offer.image
import kotlinx.android.synthetic.main.fragment_create_product_offer.spin_kit_submit
import kotlinx.android.synthetic.main.fragment_create_product_offer.tvEndDate
import kotlinx.android.synthetic.main.fragment_create_sevice_offer.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.*
import java.util.*

private const val RES_IMAGE = 100

class CreateProductOfferFragment: Fragment(), View.OnClickListener {

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

    private fun setListeners(){
        tvEndDate.setOnClickListener(this)
        image.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
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

    private fun showProgress(show: Boolean){
        show.doIfTrue {
            btnSubmit.visibility = View.GONE
            spin_kit_submit.visibility = View.VISIBLE
        }elseDo {
            btnSubmit.visibility = View.VISIBLE
            spin_kit_submit.visibility = View.GONE
        }
    }

    private fun submitData(){

        try {
            showProgress(true)

            val shopid = MultipartBody.Part.createFormData("shopid", ShopDetails().shopId)
            val title = MultipartBody.Part.createFormData("title", etTitle.text.toString())
            val description = MultipartBody.Part.createFormData("description", etDesc.text.toString())
            val normal = MultipartBody.Part.createFormData("normal", etNormalAmount.text.toString())
            val discount = MultipartBody.Part.createFormData("discount", etOfferAmount.text.toString())
            /*val offer_type = MultipartBody.Part.createFormData("offer_type", "2")*/
            val enddate = MultipartBody.Part.createFormData("enddate", CommonUtils.formatDate_ddMMyyyy(tvEndDate.text.toString())!!)

            val image1Multipart = if (selectFirstImagePath.isNotEmpty()) {
                getMultiParImageBody(selectFirstImagePath, "image")
            } else {
                getMultiParImageBody(null, "image")
            }


            lifecycleScope.launch {
                val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                    insertShopProductOffers(image1Multipart, shopid, title, normal, discount, description, enddate)
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

                showProgress(false)
                btnSubmit.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            showProgress(false)
            btnSubmit.visibility = View.VISIBLE
            activity!!.toast {
                message = "OOPS! Something went wrong!"
                duration = Toast.LENGTH_SHORT
            }
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (activity != null) {
            activity!!.window
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        }
        return inflater.inflate(R.layout.fragment_create_product_offer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tvEndDate -> {
                openDatePicker()
            }
            R.id.image -> {
                chooseImage()
            }
            R.id.btnSubmit ->{
                submitData()
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
        }
    }
}