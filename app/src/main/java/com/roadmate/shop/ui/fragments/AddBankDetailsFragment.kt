package com.roadmate.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.roadmate.shop.R
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.RoadmateApiResponse
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.preference.ShopDetails
import kotlinx.android.synthetic.main.fragment_add_bank_details.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


class AddBankDetailsFragment: BaseFragment() {

    private fun submitDetails(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                submitBankDetails(bookedCustomersListJsonRequest())
            }
            if (response.isSuccessful && response.body()!!.message!!.equals("success", true)){
                activity!!.toast {
                    message = "Account Details Submitted successfully"
                    duration = Toast.LENGTH_SHORT
                }
                activity!!.finish()
            }else{
                activity!!.toast {
                    message = "OOPS! Something went wrong."
                    duration = Toast.LENGTH_SHORT
                }
            }
            showProgress(false)
        }
    }

    private fun bookedCustomersListJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shop_id", ShopDetails().shopId)
            json.put("bankaccount", beneficiarAccNo.text.toString())
            json.put("ifsc", beneficiarIfsc.text.toString())
            json.put("account_holdername", beneficiaryName.text.toString())
            json.put("bank", beneficiarBank.text.toString())
            json.put("branch", beneficiarBrnch.text.toString())
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun isAccountIFSCvalid(): Boolean {
        return when {
            beneficiarAccNo.text.toString().isEmpty() -> {
                activity!!.toast {
                    message = "Please enter your Account number"
                    duration = Toast.LENGTH_SHORT
                }
                false
            }
            beneficiarIfsc.text.toString().isEmpty() -> {
                activity!!.toast {
                    message = "Please enter you branch IFSC"
                    duration = Toast.LENGTH_SHORT
                }
                false
            }
            else -> {
                true
            }
        }
    }

    private fun showProgress(show:Boolean){
        if (show){
            spin_kit.visibility = View.VISIBLE
            saveButton.visibility = View.GONE
        }else{
            spin_kit.visibility = View.GONE
            saveButton.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_bank_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveButton.setOnClickListener {
            isAccountIFSCvalid().doIfTrue {
                submitDetails()
            }
        }
    }
}