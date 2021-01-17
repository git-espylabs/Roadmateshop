package com.roadmate.shop.ui.fragments

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.roadmate.app.razorpay.PaymentUtils
import com.roadmate.shop.R
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.RoadmateApiResponse
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.startActivity
import com.roadmate.shop.log.AppLogger
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.ui.activities.ShopHomeActivity
import kotlinx.android.synthetic.main.fragment_payment.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


class PaymentFragment: BaseFragment(), View.OnClickListener {

    private var userPayableAmount = 0.0
    private var paymentMode = "1"
    private var transactionId = ""

    private fun moveToHome() {
        activity?.startActivity<ShopHomeActivity>()
        activity?.finish()
    }

    fun updatePaymentStatus(transId: String){
        transactionId = transId
        showPaymentProgress(true)
        lifecycleScope.launch {
            val  response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                insertPaymentToServer(paymentUpdateJsonRequest())
            }
            if (response.isSuccessful && response.body()?.message!! == "Success"){
                AppLogger.info("update to server", "success")
                ShopDetails().isActiveUser = true
            }
            showAlertPaymentSuccess()
            showPaymentProgress(false)
        }
    }

    private fun paymentUpdateJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shop", ShopDetails().shopId)
            json.put("transid", transactionId)
            json.put("paystatus", "1")
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    fun showAlertPaymentError(error: String){
        android.app.AlertDialog.Builder(activity!!)
            .setTitle("Payment Failed!")
            .setMessage("Error: $error")
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showAlertPaymentSuccess(){
        android.app.AlertDialog.Builder(activity!!)
            .setTitle("Payment Success")
            .setMessage("Your payment  was successful\nTransaction Id: $transactionId")
            .setPositiveButton("Close") { _, _ ->
                moveToHome()
            }
            .show()
    }

    private fun showPaymentProgress(show:Boolean){
        show.doIfTrue {
            spin_kit.visibility = View.VISIBLE
            btnPayNow.visibility = View.INVISIBLE
        }elseDo {
            spin_kit.visibility = View.GONE
            btnPayNow.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnSkip.setOnClickListener(this)
        btnPayNow.setOnClickListener(this)
        userPayableAmount = 500.0

        oginalprice.text = resources.getString(R.string.Rs) + "4999/-"
        offerprice.text = resources.getString(R.string.Rs) + "500/- "

        val mystring = "roadmate.in@gmail.com"
        val content = SpannableString(mystring)
        content.setSpan(UnderlineSpan(), 0, mystring.length, 0)
        rdmmail.setText(content)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnSkip ->{
                moveToHome()
            }
            R.id.btnPayNow ->{
                PaymentUtils(activity!!, userPayableAmount).proceedPayment()
            }
        }
    }
}