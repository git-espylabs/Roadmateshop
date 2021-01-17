package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.BuildConfig
import com.roadmate.shop.R
import com.roadmate.shop.api.response.MyStoreProductTrans

import com.squareup.picasso.Picasso

class MyStoreProductAdapter  internal constructor(private val context: Context, private val mData: ArrayList<MyStoreProductTrans>, val isOwnedProducts: Boolean, val clickHandler: (pData: MyStoreProductTrans?, isSoldOut: Boolean) -> Unit) : RecyclerView.Adapter<MyStoreProductAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var product_image: ImageView = itemView.findViewById(R.id.product_image)
        internal var product_name: TextView = itemView.findViewById(R.id.product_name)
        internal var product_sold_by: TextView = itemView.findViewById(R.id.product_sold_by)
        internal var product_sold_contact: TextView = itemView.findViewById(R.id.product_sold_contact)
        internal var product_price: TextView = itemView.findViewById(R.id.product_price)
        internal var call_layout: TextView = itemView.findViewById(R.id.call)
        internal var soldout: Button = itemView.findViewById(R.id.soldout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.my_store_product_list_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]

        holder.product_name.text = data.pname
        if (data.user_type == "1") {
            holder.product_sold_by.text = "Sold By: " + data.username
            holder.product_sold_contact.text = "Phone: " + data.phone
        } else {
            holder.product_sold_by.text = "Sold By: " + data.shopname
            holder.product_sold_contact.text = "Phone: " + data.shopnumber
        }
        holder.product_price.text = "Price: " + context.resources.getString(R.string.Rs) + data.pprice

        if (isOwnedProducts){
            holder.product_sold_by.visibility = View.GONE
            holder.product_sold_contact.visibility = View.GONE
            holder.soldout.visibility = View.VISIBLE
            holder.soldout.setOnClickListener {
                clickHandler(data, true)
            }
        }else{
            holder.product_sold_by.visibility = View.VISIBLE
            holder.product_sold_contact.visibility = View.VISIBLE
            holder.soldout.visibility = View.GONE
        }

        var pImg = ""
        when{
            data.pimg1 != null && data.pimg1.isNotEmpty() -> pImg = data.pimg1
            data.pimg2 != null && data.pimg2.isNotEmpty() -> pImg = data.pimg2
            data.pimg3 != null && data.pimg3.isNotEmpty() -> pImg = data.pimg3
        }
        Picasso.with(context).load(BuildConfig.BANNER_URL_ENDPOINT + pImg).into(holder.product_image)

        holder.call_layout.setOnClickListener(View.OnClickListener {
            clickHandler(data,false)
        })
    }
}