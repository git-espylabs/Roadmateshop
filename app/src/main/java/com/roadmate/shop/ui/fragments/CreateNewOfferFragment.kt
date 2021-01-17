package com.roadmate.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.roadmate.shop.R
import com.roadmate.shop.adapter.TabAdapter
import kotlinx.android.synthetic.main.fragment_create_new_offer.*

class CreateNewOfferFragment: BaseFragment() {

    lateinit var tabAdapter: TabAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_new_offer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabAdapter = TabAdapter(activity!!.supportFragmentManager)
        tabAdapter.addFragment(CreateServiceOfferFragmentTwo(), "Service Offer")
        tabAdapter.addFragment(CreateProductOfferFragment(), "Product Offer")
        viewPager.adapter = tabAdapter
        tabs.setupWithViewPager(viewPager)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}