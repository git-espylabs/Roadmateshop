package com.roadmate.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.roadmate.shop.BuildConfig
import com.roadmate.shop.R
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.RoadmateApiResponse
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.startActivity
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.rmapp.AppSession
import com.roadmate.shop.ui.activities.CheckOtpActivity
import com.roadmate.shop.ui.activities.SignUpActivity
import com.roadmate.shop.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class LoginFragment : Fragment(), View.OnClickListener {


    private fun loginJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("mobnum", phoneEditText.text.toString())
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun isInputsValid() : Boolean{
        if (phoneEditText.text.toString() == ""){
            phoneEditText.error = "Enter mobile number"
            return false
        }
        return true
    }

    private fun processLogin(){
        if (isInputsValid()){
            showProgress(true)
            lifecycleScope.launch{
                val response =  APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                    shopLogin(loginJsonRequest())
                }
                if (response.isSuccessful && response.body()?.message!! == "Success"){
                    AppSession.userMobile = phoneEditText.text.toString()
                    AppSession.otpTemp = response.body()?.data!!
                    activity?.startActivity<CheckOtpActivity>()
                    activity?.finish()
                }else{
                    phoneEditText.error = "Sign Up"
                    activity!!.toast {
                        message = "Mobile number not registered"
                        duration = Toast.LENGTH_SHORT
                    }
                }
                showProgress(false)
            }
        }
    }

    private fun moveToSignUp(){
        activity?.startActivity<SignUpActivity>()
    }

    private fun showProgress(visible:Boolean){
        if (visible){
            loginButton.visibility = View.GONE
            spin_kit.visibility = View.VISIBLE
        }else{
            loginButton.visibility = View.VISIBLE
            spin_kit.visibility = View.GONE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loginButton.setOnClickListener (this)
        signUpButton.setOnClickListener (this)
        BuildConfig.DEBUG.doIfTrue {
        }
    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.loginButton ->{
                NetworkUtils.isNetworkConnected(activity).doIfTrue { processLogin() }elseDo {activity!!.toast {
                    message = "No Internet Connectivity!"
                    duration = Toast.LENGTH_LONG }
                }
            }
            R.id.signUpButton ->{
                moveToSignUp()
            }
        }
    }
}