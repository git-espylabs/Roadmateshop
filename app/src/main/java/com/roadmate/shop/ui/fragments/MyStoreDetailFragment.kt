package com.roadmate.shop.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.roadmate.app.api.response.ProductDetailsMaster
import com.roadmate.app.api.response.ProductDetailsTrans
import com.roadmate.shop.R
import com.roadmate.shop.adapter.SliderAdapter
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.toast
import kotlinx.android.synthetic.main.fragment_mystore_detail.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.util.*

class MyStoreDetailFragment : Fragment(){
    private var NUMPAGES = 0
    private val currentpage = 0

    var p_id = ""
    var p_contact = ""

    var r_images: ArrayList<String>? = arrayListOf()

    private fun getProductDetails(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<ProductDetailsMaster>> {
                getProductDetails(productDetailsRequestJSON(p_id))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateDetails(response.body()?.data!![0])
            }

            showProgress(false)
        }
    }

    private fun populateDetails(data: ProductDetailsTrans){

        r_images?.add(data.pimg1)
        r_images?.add(data.pimg2)
        r_images?.add(data.pimg3)

        product_name.text = data.pname
        product_sold_by.text = data.username
        product_sold_contact.text = data.phone
        product_price.text = data.pprice

        p_contact = data.phone

        pager.adapter = SliderAdapter(activity!!, r_images!!)
        indicator.setViewPager(pager)
        NUMPAGES = r_images!!.size

        call.visibility = View.VISIBLE
    }

    private fun productDetailsRequestJSON(value: String) : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("productid", value)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun showProgress(visible:Boolean){
        if (visible){
            loadingFrame.visibility = View.VISIBLE
        }else{
            loadingFrame.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mystore_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        p_id = arguments!!.getString("p_id")!!

        call.visibility = View.GONE

        getProductDetails()

        call.setOnClickListener(View.OnClickListener {
            if (p_contact.isNotEmpty()) {
                val uri = "tel:+91$p_contact"
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse(uri)
                startActivity(intent)
            } else {
                activity!!.toast {
                    message = "No contact details available"
                    duration = Toast.LENGTH_LONG
                }
            }
        })

    }
}