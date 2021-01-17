package com.roadmate.shop.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.roadmate.shop.BuildConfig
import com.roadmate.shop.R
import com.roadmate.shop.api.response.MoreServicesTrans
import com.roadmate.shop.api.response.PackageTrans

class PackageListAdapter  internal constructor(private val context: Context, private val mData: ArrayList<PackageTrans>,  val clickHandler: (position: Int, obj: PackageTrans) -> Unit) : RecyclerView.Adapter<PackageListAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var package_name: TextView = itemView.findViewById(R.id.package_name)
        internal var package_amount: TextView = itemView.findViewById(R.id.package_amount)
        internal var package_shopamount: TextView = itemView.findViewById(R.id.package_shopamount)
        internal  var remove_packge :Button=itemView.findViewById(R.id.removePackage)
        internal var package_for: TextView = itemView.findViewById(R.id.package_for)
        internal var package_details: TextView = itemView.findViewById(R.id.package_details)
        internal var package_image: ImageView = itemView.findViewById(R.id.package_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_packe_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]

        holder.package_name.text = data.title
        holder.package_amount.text = context.resources.getString(R.string.Rs) + data.amount
        holder.package_for.text = data.package_for
        holder.package_details.text = data.description
        holder.remove_packge.setOnClickListener {
            mData.remove(mData[position])
            clickHandler(position, data)

            notifyDataSetChanged()

        }


        Glide.with(context).load(BuildConfig.BANNER_URL_ENDPOINT + data.image)
            .error(R.drawable.road_mate_plain).error(R.drawable.road_mate_plain)
            .into(holder.package_image)
    }
}