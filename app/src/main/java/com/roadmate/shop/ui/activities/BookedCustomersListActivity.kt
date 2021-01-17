package com.roadmate.shop.ui.activities

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.roadmate.shop.constants.FragmentConstants
import com.roadmate.shop.ui.fragments.BookedCustomersListFragment
import com.roadmate.shop.ui.fragments.ShopOffersFragment

class BookedCustomersListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Booked Customers Details"

        setFragment(BookedCustomersListFragment(), FragmentConstants.BOOKED_CUSTOMERS_LIST_FRAGMENT, null, false)
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