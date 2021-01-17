package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.api.response.ShopOffersTrans
import com.roadmate.shop.api.response.ShopServicesTrans

class ShopServicesListAdapter  internal constructor(private val context: Context, private val mData: ArrayList<ShopServicesTrans>) : RecyclerView.Adapter<ShopServicesListAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var vehType: TextView = itemView.findViewById(R.id.vehType)
        internal var vehBrand: TextView = itemView.findViewById(R.id.vehBrand)
        internal var vehModel: TextView = itemView.findViewById(R.id.vehModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_services_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]

        holder.vehType.text = data.veh_type
        holder.vehBrand.text = data.brand
        holder.vehModel.text = data.brand_model
    }
}