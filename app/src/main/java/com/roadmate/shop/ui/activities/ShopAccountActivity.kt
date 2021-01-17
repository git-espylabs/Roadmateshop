package com.roadmate.shop.ui.activities

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.FragmentManager
import com.roadmate.shop.constants.AppConstants.Companion.FEEDBACK_RESULT
import com.roadmate.shop.constants.FragmentConstants
import com.roadmate.shop.extensions.startActivity
import com.roadmate.shop.ui.fragments.MyShopProfileFragment

class ShopAccountActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.title = "Account"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        setFragment(MyShopProfileFragment(), FragmentConstants.MY_SHOP_PROFILE_FRAGMENT, null, false)
    }

    override fun onBackPressed() {
        this.startActivity<ShopHomeActivity>()
        this.finish()
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