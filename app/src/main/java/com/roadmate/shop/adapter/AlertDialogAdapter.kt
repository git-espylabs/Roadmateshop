package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R

class AlertDialogAdapter  internal constructor(private val context: Context, private val mData: ArrayList<String>, val clickHandler: (position: Int, itineraryName: String?) -> Unit) : RecyclerView.Adapter<AlertDialogAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var itineraryName: TextView = itemView.findViewById(R.id.alert_vehicle_brand_name_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertDialogAdapter.ViewHolder {
        val view = mInflater.inflate(R.layout.alert_dialog_single_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: AlertDialogAdapter.ViewHolder, position: Int) {
        holder.itineraryName.text = mData[position]

        holder.itineraryName.setOnClickListener(View.OnClickListener {
            clickHandler(position, mData[position])
        })
    }


}