package com.roadmate.shop.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.adapter.BookedCustomersListAdapter
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.BookedCustomersMaster
import com.roadmate.shop.api.response.BookedCustomersTrans
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.toast
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.ui.activities.BookingDetailsActivity
import com.roadmate.shop.ui.activities.OfferDetailsActivity
import com.roadmate.shop.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_booked_customers_list.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class BookedCustomersListFragment: BaseFragment(), AdapterView.OnItemSelectedListener {


    private fun populateBookingTypes(){
        val category = resources.getStringArray(R.array.book_type_category)
        val arrayAdapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_dropdown_item,
            category
        )
        bookTypeSpn.adapter = arrayAdapter
        bookTypeSpn.onItemSelectedListener = this
    }

    private fun bookedCustomersListJsonRequest(bookType: String) : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shop", ShopDetails().shopId)
            json.put("book_type", bookType)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun getBookedCustomersList(bookType: String){
        showProgress(true)

        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<BookedCustomersMaster>> {
                getBookedCustomersTimeList(bookedCustomersListJsonRequest(bookType))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateCustomersList(response.body()?.data!!)
            }else{
                empty_caution.visibility = View.VISIBLE
                customersList.visibility = View.GONE
            }
            showProgress(false)
        }
    }

    private fun populateCustomersList(list: ArrayList<BookedCustomersTrans>){
        empty_caution.visibility = View.GONE
        customersList.visibility = View.VISIBLE
        customersList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        val adapter = BookedCustomersListAdapter(activity!!, list){ obj ->

            val intent = Intent(context, BookingDetailsActivity::class.java)
            intent.putExtra("work_status", obj!!.work_status)
            intent.putExtra("pay_status", obj!!.pay_status)
            intent.putExtra("bookid", obj!!.book_id)
            intent.putExtra("bookmasterid", obj!!.id)
            intent.putExtra("booktype", obj!!.book_type)
            startActivity(intent)
        }
        customersList.adapter = adapter
    }

    private fun showProgress(show: Boolean){
        show.doIfTrue {
            progress.visibility = View.VISIBLE
        }elseDo {
            progress.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_booked_customers_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        populateBookingTypes()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getBookedCustomersList("1")
        }elseDo {
            activity!!.toast {
                message = "No Internet Connectivity"
                duration = Toast.LENGTH_LONG
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getBookedCustomersList((position + 1).toString())
    }elseDo {
        activity!!.toast {
            message = "No Internet Connectivity"
            duration = Toast.LENGTH_LONG
        }
    }
    }
}