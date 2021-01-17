package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.models.AddedOfferVehicle

class AddedOfferVehiclesListAdapter  internal constructor(private val context: Context, private val mData: ArrayList<AddedOfferVehicle>, val clickHandler: (position: Int?) -> Unit) : RecyclerView.Adapter<AddedOfferVehiclesListAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var name: TextView = itemView.findViewById(R.id.name)
        internal var remove: TextView = itemView.findViewById(R.id.remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_added_offer_vehicle_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]

        holder.name.text = data.brandName + " " + data.modelName + " - " + data.fuel_typeName

        holder.remove.setOnClickListener {
            clickHandler(position)
        }
    }
}