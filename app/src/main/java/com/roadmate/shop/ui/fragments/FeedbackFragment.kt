package com.roadmate.shop.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.roadmate.shop.R
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.RoadmateApiResponse
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.ToastInfo
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.ui.activities.FeedbackActivity
import kotlinx.android.synthetic.main.fragment_feedback.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class FeedbackFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnSubmit.setOnClickListener {
            val suggestion = etSuggestion.text.trim().toString()
            val complaint = etComplaint.text.trim().toString()

            var jsonData = ""
            var json: JSONObject? = null
            try {
                json = JSONObject()
                json.put("shopid", ShopDetails().shopId)
                json.put("suggestion", suggestion)
                json.put("complaint", complaint)
                jsonData = json.toString()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            spin_kit.visibility = View.VISIBLE
            btnSubmit.visibility = View.GONE

            lifecycleScope.launch {
                val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                    insertFeedback(jsonData.toRequestBody())
                }
                if (response.isSuccessful && response.body()?.message == "Success"){
                    activity!!.toast {
                        message = "Your response added. Thanks!"
                        duration = Toast.LENGTH_LONG
                    }
                    (activity as FeedbackActivity).onBackPressed()
                }else{
                    activity!!.toast {
                        message = "OOPS!! Something went wrong."
                        duration = Toast.LENGTH_LONG
                    }
                }

                spin_kit.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
            }
        }
    }
}