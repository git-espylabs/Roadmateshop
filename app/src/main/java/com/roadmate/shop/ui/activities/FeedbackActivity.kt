package com.roadmate.shop.ui.activities

import android.R
import android.R.id.message
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.roadmate.shop.constants.AppConstants.Companion.FEEDBACK_RESULT
import com.roadmate.shop.constants.FragmentConstants
import com.roadmate.shop.ui.fragments.FeedbackFragment


class FeedbackActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Suggestions & Complaints"

        setFragment(FeedbackFragment(), FragmentConstants.FEEDBACK_FRAGMENT, null, false)
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