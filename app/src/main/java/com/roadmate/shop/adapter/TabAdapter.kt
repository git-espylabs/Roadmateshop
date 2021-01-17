package com.roadmate.shop.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class TabAdapter(manager: FragmentManager): FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val mFragmentList: ArrayList<Fragment> = ArrayList()
    private val mFragmentTitleList: ArrayList<String> = ArrayList()

    fun addFragment(
        fragment: Fragment?,
        title: String?
    ) {
        mFragmentList.add(fragment!!)
        mFragmentTitleList.add(title!!)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position];
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position];
    }

    override fun getCount(): Int {
        return mFragmentList.size;
    }
}