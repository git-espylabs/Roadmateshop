package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.roadmate.shop.BuildConfig
import com.roadmate.shop.R
import com.roadmate.shop.api.response.AppBannerTrans
import com.roadmate.shop.api.response.NotificationTrans

class ShopStoreProductOfferAdapter  internal constructor(private val context: Context, private val mData: ArrayList<AppBannerTrans>, val clickHandler: (obj: AppBannerTrans?) -> Unit) : RecyclerView.Adapter<ShopStoreProductOfferAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var img: ImageView = itemView.findViewById(R.id.package_image)
        internal var package_name: TextView = itemView.findViewById(R.id.package_name)
        internal var package_offeramount: TextView = itemView.findViewById(R.id.package_offeramount)
        internal var package_amount: TextView = itemView.findViewById(R.id.package_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_shop_product_offer, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]

        Glide.with(context).load(BuildConfig.BANNER_URL_ENDPOINT + data.bannerImage)
            .error(R.drawable.road_mate_plain).error(R.drawable.road_mate_plain)
            .into(holder.img)

        holder.package_name.text = data.title
        holder.package_offeramount.text = context.resources.getString(R.string.Rs) + data.discount_amount
        holder.package_amount.text = context.resources.getString(R.string.Rs) + data.normal_amount

        holder.img.setOnClickListener {
            clickHandler(data)
        }
    }
}