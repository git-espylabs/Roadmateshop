package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.api.response.MoreServicesTrans
import com.roadmate.shop.api.response.PackageTrans

class ShopTypeSelectionAdapter  internal constructor(private val context: Context, private val mData: ArrayList<MoreServicesTrans>, val clickHandler: (obj: MoreServicesTrans?, isSelected: Boolean) -> Unit) : RecyclerView.Adapter<ShopTypeSelectionAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var shop_types: TextView = itemView.findViewById(R.id.shop_types)
        internal var chkBox1: CheckBox = itemView.findViewById(R.id.chkBox1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_add_shop_category, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = mData[position]

        holder.shop_types.text = data.serviceName
        holder.chkBox1.isChecked = data.isSelected

        holder.chkBox1.setOnClickListener {
            data.isSelected = holder.chkBox1.isChecked
            clickHandler(data, holder.chkBox1.isChecked)
        }
    }
}