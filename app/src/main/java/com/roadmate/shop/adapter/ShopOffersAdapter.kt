package com.roadmate.shop.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.BuildConfig
import com.roadmate.shop.R
import com.roadmate.shop.api.response.ShopOffersTrans
import com.roadmate.shop.extensions.doIfTrue
import com.squareup.picasso.Picasso

class ShopOffersAdapter  internal constructor(private val context: Context, private val mData: ArrayList<ShopOffersTrans>, val clickHandler: (obj: ShopOffersTrans?) -> Unit) : RecyclerView.Adapter<ShopOffersAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var offer_pic: ImageView = itemView.findViewById(R.id.offer_pic)
        internal var offer_title: TextView = itemView.findViewById(R.id.offer_title)
        internal var offer_desc: TextView = itemView.findViewById(R.id.offer_desc)
        internal var normalPrice: TextView = itemView.findViewById(R.id.normalPrice)
        internal var offerPrice: TextView = itemView.findViewById(R.id.offerPrice)
        internal var btnDetails: TextView = itemView.findViewById(R.id.btnDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_shop_offers, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]

        var pImg = ""
        data.pic != null && data.pic.isNotEmpty().doIfTrue {
            pImg = data.pic
        }
        Picasso.with(context).load(BuildConfig.BANNER_URL_ENDPOINT + pImg)
            .error(R.drawable.road_mate_plain).error(R.drawable.road_mate_plain)
            .into(holder.offer_pic)

        holder.offer_title.text = data.title
        holder.offer_title.text = data.title
        holder.offer_desc.text = data.small_desc
        holder.normalPrice.text = context.resources.getString(R.string.Rs)  + " " + data.normal_amount

        holder.offerPrice.text = context.resources.getString(R.string.Rs)  + " " + data.offer_amount
        holder.offerPrice.paintFlags = holder.offerPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        holder.btnDetails.setOnClickListener {
            clickHandler(data)
        }
    }

}