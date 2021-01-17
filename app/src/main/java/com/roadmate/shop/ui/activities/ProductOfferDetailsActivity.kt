package com.roadmate.shop.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.roadmate.shop.R
import com.roadmate.shop.constants.FragmentConstants
import com.roadmate.shop.rmapp.AppSession
import com.roadmate.shop.ui.fragments.ProductOfferDetailsFragment

class ProductOfferDetailsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Offer Details"

        setFragment(ProductOfferDetailsFragment(), FragmentConstants.PRODUCT_OFFER_DETAILS_FRAGMENT, intent.extras, false)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("geo:0,0?q=${AppSession.selectedShopLocation}")
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            R.id.menu_call -> {

                val uri = "tel:" + AppSession.selectedShopPhone
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse(uri)
                startActivity(intent)
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}