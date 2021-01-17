package com.roadmate.shop.ui.activities

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.roadmate.shop.constants.FragmentConstants
import com.roadmate.shop.ui.fragments.MyStoreDetailFragment

class MyStoreDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (intent.hasExtra("p_name")) {
            supportActionBar!!.title = intent.getStringExtra("p_name")
        } else {
            supportActionBar!!.title = ""
        }

        var bundle =Bundle()
        bundle.putString("p_id",intent.getStringExtra("p_id"))

        setFragment(MyStoreDetailFragment(), FragmentConstants.MY_STORE_DETAILS_FRAGMENT, bundle, false)
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