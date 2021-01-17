package com.roadmate.shop.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.roadmate.shop.R
import com.roadmate.shop.adapter.BannerAdapter
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.response.AppBannerMaster
import com.roadmate.shop.api.response.AppBannerTrans
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.extensions.startActivity
import com.roadmate.shop.rmapp.AppSession
import com.roadmate.shop.ui.activities.AddBankDetailsActivity
import com.roadmate.shop.ui.activities.AddPackageActivity
import com.roadmate.shop.ui.activities.PackageListingActivity
import com.roadmate.shop.ui.activities.ShopOffersActivity
import kotlinx.android.synthetic.main.fragment_shop_packge_main.*
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*

class ShopPackageMainFragment:BaseFragment(), View.OnClickListener {

    var bannerHandler = Handler()
    var bannerSwipeTimer = Timer()
    private var currentPage = 0
    private var NUM_PAGES = 0
    private var isBannerTimerRunning = false


    private fun setListeners(){
        addpackage.setOnClickListener(this)
        packageList.setOnClickListener(this)
        btnAddBankDetails.setOnClickListener(this)
    }

    private fun processAppBanner(){
        lifecycleScope.launch{
            val response = APIManager.call<ApiServices, Response<AppBannerMaster>> {
                getPackageBanners()
            }
            if (response.isSuccessful && response.body()?.message =="Success"){
                AppSession.packageBannerList = response.body()?.data!!

                populateAppBanner(response.body()?.data!!)
            }
        }
    }

    private fun populateAppBanner(bannerList: ArrayList<AppBannerTrans>){
        NUM_PAGES = bannerList.size
        val viewPagerAdapter = BannerAdapter(activity!!, bannerList)
        viewPager.adapter = viewPagerAdapter

        bannerSwipeTimer.schedule(object : TimerTask() {
            override fun run() {
                isBannerTimerRunning = true
                bannerHandler.post {
                    if (currentPage == NUM_PAGES) {
                        currentPage = 0
                    }
                    viewPager.setCurrentItem(currentPage++, true)
                }
            }
        }, 0, 3000)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (activity != null) {
            activity!!.window
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        }
        return inflater.inflate(R.layout.fragment_shop_packge_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()
        processAppBanner()
    }

    override fun onStop() {
        super.onStop()
        bannerHandler.removeCallbacks(null)
        bannerSwipeTimer.cancel()
        isBannerTimerRunning = false
    }

    override fun onResume() {
        super.onResume()
        if (AppSession.packageBannerList.isNotEmpty() && !isBannerTimerRunning){
            bannerHandler= Handler()
            bannerSwipeTimer = Timer()
            populateAppBanner(AppSession.packageBannerList)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.packageList -> {
                activity?.startActivity<PackageListingActivity>()
            }
            R.id.addpackage -> {
                activity?.startActivity<AddPackageActivity>()
            }
            R.id.btnAddBankDetails -> {
                activity?.startActivity<AddBankDetailsActivity>()
            }
        }
    }

}