package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.api.response.BookedCustomersTrans
import com.roadmate.shop.api.response.ShopOffersTrans
import com.roadmate.shop.utils.CommonUtils

class BookedCustomersListAdapter  internal constructor(private val context: Context, private val mData: ArrayList<BookedCustomersTrans>, val clickHandler: (obj: BookedCustomersTrans?) -> Unit) : RecyclerView.Adapter<BookedCustomersListAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvCustomerName: TextView = itemView.findViewById(R.id.tvCustomerName)
        internal var tvBookingDate: TextView = itemView.findViewById(R.id.tvBookingDate)
        internal var tvVehicle: TextView = itemView.findViewById(R.id.tvVehicle)
        internal var mainLay: ConstraintLayout = itemView.findViewById(R.id.mainLay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_booked_customer_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]

        holder.tvCustomerName.text = data.name
        holder.tvBookingDate.text = CommonUtils.formatDate_yyyyMMddInWords(data.bookingDate) + ", " + CommonUtils.formatTime_Hmmss(data.timeslot)
        holder.tvVehicle.text = "Vehicle: " + data.brand + " " + data.brand_model + " (" + data.fuel_type + ")"

        holder.mainLay.setOnClickListener {
            clickHandler(data)
        }
    }
}