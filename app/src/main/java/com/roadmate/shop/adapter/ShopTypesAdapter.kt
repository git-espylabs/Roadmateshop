package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.api.response.MoreServicesTrans

class ShopTypesAdapter  internal constructor(private val context: Context, private val mData: ArrayList<MoreServicesTrans>, val clickHandler: (shopId: String?, shopName: String?) -> Unit) : RecyclerView.Adapter<ShopTypesAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var shop_types: TextView = itemView.findViewById(R.id.shop_types)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.shoptypes_recycler_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val service: MoreServicesTrans = mData[position]

        holder.shop_types.text = service.serviceName

        //Picasso.with(context).load(BuildConfig.BANNER_URL_ENDPOINT + service.serviceImage).into(holder.moreimage)

        holder.itemView.setOnClickListener {
            clickHandler(service.serviceId, service.serviceName)
        }
    }
}