package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.api.response.BookedCustomersTrans
import com.roadmate.shop.api.response.ShopTimeSlotTrans
import com.roadmate.shop.utils.CommonUtils

class ShopTimeSlotListAdapter  internal constructor(private val context: Context, private val mData: ArrayList<ShopTimeSlotTrans>, val clickHandler: (obj: ShopTimeSlotTrans?) -> Unit) : RecyclerView.Adapter<ShopTimeSlotListAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var tvTimeSlot: TextView = itemView.findViewById(R.id.name)
        internal var remove: TextView = itemView.findViewById(R.id.remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_shop_time_slot_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]

        holder.tvTimeSlot.text = CommonUtils.formatTime_Hmmss(data.timeslots)

        holder.remove.setOnClickListener {
            clickHandler(data)
            notifyDataSetChanged()
        }
    }
}