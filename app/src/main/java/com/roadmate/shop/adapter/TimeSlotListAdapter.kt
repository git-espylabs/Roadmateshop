package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.api.response.MyStoreProductTrans
import com.roadmate.shop.api.response.ReviewTrans
import com.roadmate.shop.api.response.TimeSlotTrans

class TimeSlotListAdapter  internal constructor(private val context: Context, private var mData: ArrayList<TimeSlotTrans>, val clickHandler: (obj: TimeSlotTrans?) -> Unit) : RecyclerView.Adapter<TimeSlotListAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var name: TextView = itemView.findViewById(R.id.name)
        internal var remove: TextView = itemView.findViewById(R.id.remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_time_slot_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = mData[position]

        holder.name.text = data.name

        holder.remove.setOnClickListener {
            clickHandler(data)
        }
    }

    fun update(mData: ArrayList<TimeSlotTrans>){
        this.mData.clear();
        this.mData.addAll(mData);
        this.notifyDataSetChanged();
    }
}