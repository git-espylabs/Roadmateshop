package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.api.response.NotificationMaster
import com.roadmate.shop.api.response.NotificationTrans
import com.roadmate.shop.api.response.ShopTimeSlotTrans
import com.roadmate.shop.utils.CommonUtils

class NotificationListAdapter  internal constructor(private val context: Context, private val mData: ArrayList<NotificationTrans>, val clickHandler: (obj: NotificationTrans?) -> Unit) : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var title: TextView = itemView.findViewById(R.id.title)
        internal var message: TextView = itemView.findViewById(R.id.message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_notification_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]

        holder.title.text = data.notification_title
        holder.message.text = data.notification_message
    }
}