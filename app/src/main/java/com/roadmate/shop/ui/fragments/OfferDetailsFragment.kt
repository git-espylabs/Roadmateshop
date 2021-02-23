package com.roadmate.shop.ui.fragments

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.roadmate.shop.BuildConfig
import com.roadmate.shop.R
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.ProductOfferMaster
import com.roadmate.shop.api.response.ProductOfferTrans
import com.roadmate.shop.api.response.RoadmateApiResponse
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.startActivity
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.ui.activities.AddMoreVehicleToOfferActivity
import com.roadmate.shop.ui.activities.PackageDetailsActivity
import com.roadmate.shop.utils.CommonUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.offer_details_fragment.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.util.*


class OfferDetailsFragment: Fragment() {

    var shopid = ""
    var offerid = ""
    var imagename = ""
    var offertyp = ""

    var shopName = ""
    var offerTitle = ""
    var offerAmount = ""
    var vehice_type_name=""
    var normalAMount = ""

    var discountType = "1"
    var discountPerc = "0"
    var catName = ""

    private fun populateOfferDetails(){
        if (arguments != null && arguments!!.containsKey("dataMap")){
            var mMap: HashMap<String, String> = this.arguments!!.getSerializable("dataMap") as HashMap<String, String>
            title.text = mMap["title"]!!
            ofDesc.text = mMap["small_desc"]!!

            discountType = mMap["discount_type"]!!.toString()
            discountPerc = mMap["disc_percent"]!!.toString()

            if (mMap["discount_type"]!!.toString() == "1") {
                item_discount_perc.visibility = View.GONE
                item_price.visibility = View.VISIBLE
                item_strikeprice.visibility = View.VISIBLE
                item_price.text = getString(R.string.Rs) + mMap["offer_amount"]!!.toString()
                item_strikeprice.text = getString(R.string.Rs) + mMap["normal_amount"]!!.toString()
            } else {
                item_discount_perc.visibility = View.VISIBLE
                item_price.visibility = View.VISIBLE
                item_strikeprice.visibility = View.GONE
                item_discount_perc.text = mMap["disc_percent"]!!.toString() + "% " + "Discount"
            }

            Picasso.with(context).load(BuildConfig.OFFER_URL_ENDPOINT + mMap["pic"]!!).into(offerImage)
            imagename = mMap["pic"]!!
            offerid = mMap["id"]!!
            shopid = mMap["shop_id"]!!
            offertyp = mMap["offer_type"]!!
            vehice_type_name= mMap["vehicle_typeid"] !!
            Log.i("sad",vehice_type_name)

            shopName =  mMap["shopname"]!!
            offerTitle = mMap["title"]!!
            offerAmount = mMap["offer_amount"]!!
            normalAMount = mMap["normal_amount"]!!
            catName = mMap["category"]!!

        }else if (arguments != null && arguments!!.containsKey("offerid")){
            getOfferDetails()
        }
    }

    private fun populateDetailsLive(obj: ProductOfferTrans){
        title.text = obj.title
        ofDesc.text = obj.description
        item_price.text = getString(R.string.Rs) + obj.normal_amount
        item_strikeprice.text = getString(R.string.Rs) + obj.discount_amount
        Picasso.with(context).load(BuildConfig.OFFER_URL_ENDPOINT + obj.product_picture).into(offerImage)
        imagename = obj.product_picture
        offerid = obj.id
        shopid = obj.shop_id
        offertyp = ""
    }

    private fun getOfferDetails(){
        showProgress(true)
        lifecycleScope.launch {
            val response  = APIManager.call<ApiServices, Response<ProductOfferMaster>> {
                getProductOfferDetails(offerJsonRequest())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateDetailsLive(response.body()?.data!![0])
            }
            showProgress(false)
        }
    }

    private fun offerJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("id",arguments!!["offerid"])
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun updateEndDate(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                updateOfferEndDate(endDateUpdateRequest())
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                activity!!.toast {
                    message = "Updated!"
                    duration = Toast.LENGTH_SHORT
                }
            }else{
                activity!!.toast {
                    message = "OOPS! Something went wrong"
                    duration = Toast.LENGTH_SHORT
                }
            }
            showProgress(false)
        }
    }

    private fun endDateUpdateRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("offertype", "1")
            json.put("offerid", "1")
            json.put("endate", CommonUtils.formatDate_ddMMyyyy(endDate.text.toString()))
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun openDatePicker(){
        val c = Calendar.getInstance()
        var mYear = c.get(Calendar.YEAR);
        var mMonth = c.get(Calendar.MONTH);
        var mDay = c.get(Calendar.DAY_OF_MONTH);
        val datePickerDialog = DatePickerDialog(
            activity!!,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                var dates = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                endDate.text = CommonUtils.formatDate_ddMMyyyy(dates)
            },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.show()
    }


    private fun shareLink(){
        var shareText = if (discountType == "1") {
            "Hurrayyy..!!!\n" +
                    shopName + " special offer " + offerTitle + " "+ resources.getString(R.string.Rs) + normalAMount + "/-" +
                    ", now at just " + resources.getString(R.string.Rs) + offerAmount + "/-" + " through RoadMate.\n" +
                    "Hurry up... don't be late, Book your vehicle slot now.\n" +
                    "https://play.google.com/store/apps/details?id=com.roadmate.app"
        } else {
            "Hurrayyy..!!!\n" +
                    offerTitle + "\n"+ shopName + " special offer " + discountPerc + "% discount on "+ catName + " through RoadMate.\n" +
                    "Hurry up... don't be late, Book your vehicle slot now.\n" +
                    "https://play.google.com/store/apps/details?id=com.roadmate.app"
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            "Share Offer"
        )
        intent.putExtra(Intent.EXTRA_TEXT, shareText)
        startActivity(Intent.createChooser(intent, "Share Using"))
    }

    private fun showProgress(show: Boolean){
        if (show){
            progressLay.visibility = View.VISIBLE
        }else{
            progressLay.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.offer_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        populateOfferDetails()

        confirmButton.setOnClickListener {
            shareLink()
        }

        updateEndDate.setOnClickListener {
            if (endDate.text.toString().trim().isNotEmpty()){
                updateEndDate()
            }
        }

        endDate.setOnClickListener {
            openDatePicker()
        }

        addMoreVehicleBtn.setOnClickListener {
            val intent = Intent(context, AddMoreVehicleToOfferActivity::class.java)
            intent.putExtra("offerid", offerid)
            intent.putExtra("vehice_type_name",vehice_type_name)
            startActivity(intent)
        }
    }
}