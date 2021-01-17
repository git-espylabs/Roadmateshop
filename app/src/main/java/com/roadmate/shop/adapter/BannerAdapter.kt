package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.roadmate.shop.BuildConfig
import com.roadmate.shop.R
import com.roadmate.shop.api.response.AppBannerTrans
import com.squareup.picasso.Picasso
import java.util.*

class BannerAdapter(
    var context: Context,
    var bannerimage: ArrayList<AppBannerTrans>
) :
    PagerAdapter() {
    var inflater: LayoutInflater? = null
    override fun getCount(): Int {
        return bannerimage.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View =
            inflater!!.inflate(R.layout.view_pager_custom_layout, container, false)
        val imageView =
            view.findViewById<View>(R.id.viewPagerImageView) as ImageView
        Picasso.with(context).load(BuildConfig.BANNER_URL_ENDPOINT + bannerimage[position].bannerImage).into(imageView)
        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }

    override fun isViewFromObject(
        view: View,
        `object`: Any
    ): Boolean {
        return view == `object`
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }

}