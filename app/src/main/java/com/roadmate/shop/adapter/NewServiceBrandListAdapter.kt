package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.api.response.VehicleBrandModelTrans
import com.roadmate.shop.api.response.VehicleBrandTrans
import com.roadmate.shop.api.response.VehicleModelNewTrans
import com.roadmate.shop.api.response.VehicleModelTrans
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.models.ServiceInsertModel
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.ui.fragments.AddNewServiceFragment

class NewServiceBrandListAdapter  internal constructor(private val context: Context, private val mData: ArrayList<VehicleBrandModelTrans>, val clickHandler: (customSelectionList: ArrayList<ServiceInsertModel>) -> Unit) : RecyclerView.Adapter<NewServiceBrandListAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var expandedPosition = -1
    var userCustomSelectionList: ArrayList<ServiceInsertModel> = arrayListOf()

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var model_name: TextView = itemView.findViewById(R.id.model_name)
        internal var check: CheckBox = itemView.findViewById(R.id.check)
        internal var model_recycler: RecyclerView = itemView.findViewById(R.id.model_recycler)
        internal var expand_brand: ImageView = itemView.findViewById(R.id.expand_brand)
        internal var collapse_brand: ImageView = itemView.findViewById(R.id.collapse_brand)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_new_service_brand_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mData[position]

        holder.model_name.text = data.vehicleBrand

        holder.check.isChecked = data.isSelected

        holder.model_recycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        var mSubData = data.models

        mSubData.isNotEmpty().doIfTrue {
            var isAllSelected = true
            setSubList(holder, mSubData, data)
            holder.expand_brand.visibility = View.VISIBLE
            holder.collapse_brand.visibility = View.VISIBLE

            for (item in mSubData){
                if (!item.isSelected){
                    isAllSelected = false
                    break
                }
            }
            holder.check.isChecked = isAllSelected
        }elseDo {
            holder.expand_brand.visibility = View.GONE
            holder.collapse_brand.visibility = View.GONE
            holder.check.isChecked = false
        }

        if (expandedPosition == position){
            holder.expand_brand.visibility = View.GONE
            holder.collapse_brand.visibility = View.VISIBLE
            holder.model_recycler.visibility = View.VISIBLE
        }else{
            if (mSubData.isNotEmpty()) {
                holder.expand_brand.visibility = View.VISIBLE
            }else{
                holder.expand_brand.visibility = View.GONE
            }
            holder.collapse_brand.visibility = View.GONE
            holder.model_recycler.visibility = View.GONE
        }

        holder.check.setOnClickListener {
            if (mSubData.isNotEmpty()) {
                data.isSelected = holder.check.isChecked

                if (holder.check.isChecked) {
                    for (item in data.models){
                        item.isSelected = holder.check.isChecked
                        var mObj = ServiceInsertModel(ShopDetails().shopId, ShopDetails().shopType, AddNewServiceFragment.selectedVehicleTypeId, item.vehicleModelId, item.brandId)
                        userCustomSelectionList.add(mObj)
                    }
                    expandedPosition = position
                }else{
                    val itr = userCustomSelectionList.iterator()
                    while (itr.hasNext()){
                        val itemMain = itr.next()
                        if (itemMain.brand == data.vehicleBrandId){
                            itr.remove()
                        }
                    }
                    for (item in data.models){
                        item.isSelected = holder.check.isChecked
                    }
                    expandedPosition = -1
                }
                clickHandler(userCustomSelectionList)
            }else{
                data.isSelected = false
            }
            notifyDataSetChanged()
        }

        holder.expand_brand.setOnClickListener {
            expandedPosition = position
            notifyDataSetChanged()
        }

        holder.collapse_brand.setOnClickListener {
            expandedPosition = -1
            notifyDataSetChanged()
        }
    }

    private fun setSubList(holder: ViewHolder, mSubLis: ArrayList<VehicleModelNewTrans>, selectedBrandObj: VehicleBrandModelTrans){
        val adapter = NewServiceBrandModelListAdapter(context, mSubLis){selObj, isSelected->
            for (item in userCustomSelectionList){
                if (item.model == selObj!!.model && item.brand == selObj!!.brand){
                    userCustomSelectionList.remove(item)
                    break
                }
            }
            if (isSelected) {
                userCustomSelectionList.add(selObj!!)
            }
            clickHandler(userCustomSelectionList)
            notifyDataSetChanged()
        }
        holder.model_recycler.adapter = adapter
    }
}