package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.api.response.ShopOffersTrans
import com.roadmate.shop.api.response.VehicleModelNewTrans
import com.roadmate.shop.api.response.VehicleModelTrans
import com.roadmate.shop.models.ServiceInsertModel
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.ui.fragments.AddNewServiceFragment

class NewServiceBrandModelListAdapter  internal constructor(private val context: Context, private val mData: ArrayList<VehicleModelNewTrans>, val clickHandler: (obj: ServiceInsertModel?, isSelected: Boolean) -> Unit) : RecyclerView.Adapter<NewServiceBrandModelListAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var model_name: TextView = itemView.findViewById(R.id.model_name)
        internal var check: CheckBox = itemView.findViewById(R.id.check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_new_service_brand_model_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]

        holder.model_name.text = data.vehicleModel

        holder.check.isChecked = data.isSelected

        holder.check.setOnClickListener {
            data.isSelected = holder.check.isChecked
            var mObj = ServiceInsertModel(ShopDetails().shopId, ShopDetails().shopType, AddNewServiceFragment.selectedVehicleTypeId, data.vehicleModelId, data.brandId)
            clickHandler(mObj, holder.check.isChecked)
            notifyDataSetChanged()
        }
    }
}