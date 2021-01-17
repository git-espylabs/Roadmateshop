package com.roadmate.shop.ui.activities

import android.R
import android.os.Bundle
import android.view.MenuItem
import com.roadmate.shop.constants.FragmentConstants
import com.roadmate.shop.ui.fragments.ShopTimeSlotsFragment

class ShopTimeSlotsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Time Slots"

        setFragment(ShopTimeSlotsFragment(), FragmentConstants.SHOP_TIME_SLOTS_FRAGMENT, null, false)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}