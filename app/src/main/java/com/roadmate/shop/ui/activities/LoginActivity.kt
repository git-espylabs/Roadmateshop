package com.roadmate.shop.ui.activities

import android.R
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import com.roadmate.shop.constants.FragmentConstants
import com.roadmate.shop.ui.fragments.LoginFragment

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar!!.hide()

        setFragment(LoginFragment(), FragmentConstants.LOGIN_FRAGMENT, null, false)
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