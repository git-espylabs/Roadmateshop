package com.roadmate.shop.ui.activities

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.roadmate.shop.constants.FragmentConstants
import com.roadmate.shop.ui.fragments.CreateNewOfferFragment

class CreateOfferActivity : BaseActivity() {

    public fun exitOfferCreation(){
        onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Create Offer"
        supportActionBar!!.elevation = 0F;

        setFragment(CreateNewOfferFragment(), FragmentConstants.CREATE_OFFERS_FRAGMENT, null, false)
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