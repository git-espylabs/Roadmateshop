package com.roadmate.shop.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.roadmate.app.api.response.ProductDetailsMaster
import com.roadmate.shop.BuildConfig
import com.roadmate.shop.R
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.ProductOfferMaster
import com.roadmate.shop.api.response.ProductOfferTrans
import com.roadmate.shop.api.response.RoadmateApiResponse
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.utils.CommonUtils
import com.roadmate.shop.utils.NetworkUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_product_offer_details.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class ProductOfferDetailsFragment: BaseFragment() {

    var shopName = ""
    var offerTitle = ""
    var offerAmount = ""
    var normalAMount = ""
    var offerId = ""



    private fun getOfferDetails(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<ProductOfferMaster>> {
                getProductOfferDetails(offerDetailsRequest())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateOfferDetails(response.body()?.data!![0])
            }
            showProgress(false)
        }
    }

    private fun populateOfferDetails(data: ProductOfferTrans){
        offerId = data.id
        title.text = data.title
        offerTitle = data.title
        ofDesc.text = data.description
        item_price.text = getString(R.string.Rs) + data.discount_amount
        offerAmount = data.discount_amount;
        item_strikeprice.text = getString(R.string.Rs) + data.normal_amount
        normalAMount = data.normal_amount
        endDate.text = CommonUtils.formatDate_yyyyMMdd(data.end_date)
        shopName = data.shopname
        Picasso.with(context).load(BuildConfig.BANNER_URL_ENDPOINT + data.product_picture).into(offerImage)
    }

    private fun offerDetailsRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("id", arguments!!["offerid"])
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun shareLink(){
        var shareText = "Hurrayyy..!!!\n" +
                shopName + " special offer " + offerTitle + " "+ resources.getString(R.string.Rs) + normalAMount + "/-" +
                ", now at just " + resources.getString(R.string.Rs) + offerAmount + "/-" + " through RoadMate.\n" +
                "Hurry up... don't be late, Buy now.\n" +
                "https://play.google.com/store/apps/details?id=com.roadmate.app"
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            "Share Offer"
        )
        intent.putExtra(Intent.EXTRA_TEXT, shareText)
        startActivity(Intent.createChooser(intent, "Share Using"))
    }

    private fun markSoldOut(){
        showProgress(true)
        lifecycleScope.launch {
            val response= APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                setProductOfferSoldout(productSoldoutJsonRequest())
            }
            if (response.isSuccessful && response.body()?.message!!.equals("success", true)){
                activity!!.toast {
                    message = "Product Sold Out"
                    duration = Toast.LENGTH_SHORT
                }
            }
            showProgress(false)
        }
    }

    private fun productSoldoutJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("productoffid", offerId)
            json.put("sale_status", "1")
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
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
        return inflater.inflate(R.layout.fragment_product_offer_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getOfferDetails()
        }elseDo {
            activity!!.toast {
                message = "No Internet connectivity!"
                duration = Toast.LENGTH_SHORT
            }
        }

        btnShare.setOnClickListener {
            shareLink()
        }

        btnSoldOut.setOnClickListener {
            markSoldOut()
        }
    }
}