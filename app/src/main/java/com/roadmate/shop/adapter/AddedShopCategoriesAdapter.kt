package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.api.response.MoreServicesTrans
import com.roadmate.shop.api.response.PackageTrans

class AddedShopCategoriesAdapter  internal constructor(private val context: Context, private val mData: ArrayList<MoreServicesTrans>, val clickHandler: (position: Int, obj: MoreServicesTrans) -> Unit) : RecyclerView.Adapter<AddedShopCategoriesAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var itemName: TextView = itemView.findViewById(R.id.itemName)
        internal var tvremove: TextView = itemView.findViewById(R.id.tvremove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_added_categories_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = mData[position]

        holder.itemName.text = data.serviceName

        holder.tvremove.setOnClickListener {
            clickHandler(position, data)
            notifyDataSetChanged()
        }
    }
}