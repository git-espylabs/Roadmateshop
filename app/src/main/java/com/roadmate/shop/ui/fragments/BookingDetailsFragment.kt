package com.roadmate.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.roadmate.shop.BuildConfig
import com.roadmate.shop.R
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.BookingTypeMaster
import com.roadmate.shop.api.response.BookingTypeTrans
import com.roadmate.shop.api.response.RoadmateApiResponse
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.utils.CommonUtils
import com.roadmate.shop.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_booking_details.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


class BookingDetailsFragment: Fragment() {

    private var paymentStatus = ""
    private var workstatus = ""
    private var bookingType = ""
    private var bookingId = ""
    private var bookingMasterId = ""
    private var payMode = "2"

    private fun setListeners(){
        cbCompletion.setOnCheckedChangeListener { buttonView, isChecked ->
            isChecked.doIfTrue {
                updateCompletionStatus()
            }
        }

        cbPayment.setOnCheckedChangeListener { buttonView, isChecked ->
            isChecked.doIfTrue {
                paymentDetailsLay.visibility = View.VISIBLE
            }elseDo {
                paymentDetailsLay.visibility = View.GONE
            }
        }

        submitPaymentStatus.setOnClickListener {
            updatePaymentStatusStatus()
        }

        rgPayDetails.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rbOnline -> payMode = "2"
                R.id.rbCash -> payMode = "1"
            }
        }
    }

    private fun getDetailsBooking(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<BookingTypeMaster>> {
                getBookingDetails(bookedDetailsJsonRequest())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateViews(response.body()?.data!![0])
            }else{
                activity!!.toast {
                    message = "Some details are not available!"
                    duration = Toast.LENGTH_SHORT
                }
            }
            showProgress(false)
        }
    }

    private fun populateViews(bookingTypeObj: BookingTypeTrans){

        when(bookingType){
            "1" -> {
                populatePackageView(bookingTypeObj)
            }
            "2" -> {
                populateOfferView(bookingTypeObj)
            }
            "3" -> {
                populateServiceView(bookingTypeObj)
            }
        }
    }

    private fun populatePackageView(bookingTypeObj: BookingTypeTrans){
        packageLayout.visibility = View.VISIBLE
        offerLayout.visibility = View.GONE
        serviceLayout.visibility = View.GONE
        framelayout.visibility = View.VISIBLE

        Glide.with(activity!!).load(BuildConfig.BANNER_URL_ENDPOINT + bookingTypeObj.image)
            .error(R.drawable.road_mate_plain).error(R.drawable.road_mate_plain)
            .into(detail_image)

        pTitle.text = bookingTypeObj.title
        pDesc.text = bookingTypeObj.description
        pFor.text = "Package for:" + bookingTypeObj.package_for
        item_price.text = resources.getString(R.string.Rs) + bookingTypeObj.offer_amount
        item_strikeprice.text = resources.getString(R.string.Rs) + bookingTypeObj.amount
        packageVehType.text = bookingTypeObj.veh_type
        packageVehBrand.text = bookingTypeObj.brand_model
    }

    private fun populateOfferView(bookingTypeObj: BookingTypeTrans){
        packageLayout.visibility = View.GONE
        offerLayout.visibility = View.VISIBLE
        serviceLayout.visibility = View.GONE
        framelayout.visibility = View.VISIBLE

        Glide.with(activity!!).load(BuildConfig.BANNER_URL_ENDPOINT + bookingTypeObj.pic)
            .error(R.drawable.road_mate_plain).error(R.drawable.road_mate_plain)
            .into(detail_image)

        oTitle.text = bookingTypeObj.title
        oDesc.text = bookingTypeObj.small_desc
        oEndDate.text = "Offer valid up to: " + CommonUtils.formatDate_yyyyMMdd(bookingTypeObj.offer_end_date)
        offer_price.text = resources.getString(R.string.Rs) + bookingTypeObj.offer_amount
        offer_strikeprice.text = resources.getString(R.string.Rs) + bookingTypeObj.normal_amount
        offerVehType.text = bookingTypeObj.veh_type
        offerVehBrand.text = bookingTypeObj.brand_model
    }

    private fun populateServiceView(bookingTypeObj: BookingTypeTrans){
        packageLayout.visibility = View.GONE
        offerLayout.visibility = View.GONE
        framelayout.visibility = View.GONE
        serviceLayout.visibility = View.VISIBLE

        serviceVehType.text = bookingTypeObj.veh_type
        serviceVehBrand.text = bookingTypeObj.brand_model
    }

    private fun bookedDetailsJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("bookid", bookingId)
            json.put("booktype", bookingType)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun completionStatusJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("booktimemasterid", bookingMasterId)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun paymentStatusJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("booktimemasterid", bookingMasterId)
            json.put("pay_type", payMode)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun updateCompletionStatus(){
        statusRepo.visibility = View.VISIBLE
        statusLay.visibility = View.GONE
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                workCompleteStatusUpdate(completionStatusJsonRequest())
            }
            if (response.isSuccessful && response.body()?.message.equals("success", true)){
                activity!!.toast {
                    message = "Work completion updated"
                    duration = Toast.LENGTH_SHORT
                }
            }else{
                activity!!.toast {
                    message = "OOPS! Something went wrong"
                    duration = Toast.LENGTH_SHORT
                }
                cbCompletion.isChecked = false
            }

            statusRepo.visibility = View.GONE
            statusLay.visibility = View.VISIBLE
        }
    }

    private fun updatePaymentStatusStatus(){
        statusRepo.visibility = View.VISIBLE
        statusLay.visibility = View.GONE
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                paymentStatusUpdate(paymentStatusJsonRequest())
            }
            if (response.isSuccessful && response.body()?.message.equals("success", true)){
                activity!!.toast {
                    message = "Payment status updated"
                    duration = Toast.LENGTH_SHORT
                }
            }else{
                activity!!.toast {
                    message = "OOPS! Something went wrong"
                    duration = Toast.LENGTH_SHORT
                }
                cbPayment.isChecked = false
            }

            statusRepo.visibility = View.GONE
            statusLay.visibility = View.VISIBLE
        }
    }

    private fun showProgress(show: Boolean){
        show.doIfTrue {
            loadingFrame.visibility = View.VISIBLE
            bottomLay.visibility = View.GONE
        }elseDo {
            loadingFrame.visibility = View.GONE
            bottomLay.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_booking_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bookingMasterId = arguments!!["bookmasterid"].toString()
        bookingId = arguments!!["bookid"].toString()
        bookingType = arguments!!["booktype"].toString()
        paymentStatus = arguments!!["pay_status"].toString()
        workstatus = arguments!!["work_status"].toString()

        cbCompletion.isChecked = workstatus == "1"
        cbPayment.isChecked = false

        setListeners()

        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getDetailsBooking()
        }elseDo {
            activity!!.toast {
                message = "No internet connectivity!"
                duration = Toast.LENGTH_SHORT
            }
        }
    }
}