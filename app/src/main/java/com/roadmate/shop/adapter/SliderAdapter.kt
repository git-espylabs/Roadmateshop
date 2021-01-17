package com.roadmate.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.roadmate.shop.BuildConfig
import com.roadmate.shop.R

class SliderAdapter internal constructor(private val context: Context, private val bannerName: ArrayList<String>): PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return view == `object`
    }

    override fun getCount(): Int {
       return bannerName.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val myImageLayout: View = LayoutInflater.from(context).inflate(R.layout.slide, container, false)
        val title: PhotoView = myImageLayout.findViewById(R.id.image)
        try {
            if (!bannerName[position].equals("", ignoreCase = true)) {
                Glide.with(context).load(BuildConfig.BANNER_URL_ENDPOINT + bannerName[position]).into(title)
            }
        } catch (e: Exception) {
        }

        container.addView(myImageLayout, 0)
        return myImageLayout
    }
}