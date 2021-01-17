package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.shop.R
import com.roadmate.shop.api.response.MoreServicesTrans
import com.roadmate.shop.api.response.ReviewTrans

class ReviewListAdapter  internal constructor(private val context: Context, private val mData: ArrayList<ReviewTrans>) : RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var username: TextView = itemView.findViewById(R.id.username)
        internal var usercomment: TextView = itemView.findViewById(R.id.usercomment)
        internal var ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_review_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = mData[position]

        holder.username.text = data.name
        holder.usercomment.text = data.comment

        val ratingValue: Float = data.review_count.toFloat()
        holder.ratingBar.rating = ratingValue
    }
}