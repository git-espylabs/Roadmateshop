package com.roadmate.shop.ui.activities

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.roadmate.shop.constants.FragmentConstants
import com.roadmate.shop.ui.fragments.NotificationFragment
import com.roadmate.shop.ui.fragments.PackageListFragment

class PackageListingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Packages"

        setFragment(PackageListFragment(), FragmentConstants.PACKAGES_LISTING_FRAGMENT, null, false)
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