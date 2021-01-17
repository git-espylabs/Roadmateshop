package com.roadmate.shop.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.api.response.PackageTrans
import com.roadmate.shop.api.response.VehicleBrandTrans

class PackageSelectAdapter  internal constructor(private val context: Context, private val mData: ArrayList<PackageTrans>, val clickHandler: (obj: PackageTrans?, isSelected: Boolean,isShowMore : Boolean) -> Unit) : RecyclerView.Adapter<PackageSelectAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    var fetureList: ArrayList<VehicleBrandTrans> = arrayListOf()

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var package_name: TextView = itemView.findViewById(R.id.package_name)
        internal var package_amount: TextView = itemView.findViewById(R.id.package_amount)
        internal var package_for: TextView = itemView.findViewById(R.id.package_for)
        internal var package_details: TextView = itemView.findViewById(R.id.package_details)
        internal var btnGetFeatureList:Button = itemView.findViewById((R.id.btnGetFeatureList))
        internal var check: CheckBox = itemView.findViewById(R.id.check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_package_select_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = mData[position]

        holder.package_name.text = data.title + " for "+ data.brand_model
        holder.package_amount.text = data.shop_amount
        holder.package_for.text = data.package_for
        holder.package_details.text = data.description
        holder.check.isChecked = data.isSelected
        holder.btnGetFeatureList.setOnClickListener {
            clickHandler(data, holder.check.isChecked,true)
        }
        holder.check.setOnClickListener {
            data.isSelected = holder.check.isChecked
            clickHandler(data, holder.check.isChecked,false)
        }
    }
}