package com.roadmate.shop.ui.activities

import android.Manifest
import android.R
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import com.roadmate.shop.constants.FragmentConstants
import com.roadmate.shop.location.AppLocation
import com.roadmate.shop.location.AppLocationListener
import com.roadmate.shop.ui.fragments.ShopEditFragmentSecond
import com.roadmate.shop.ui.fragments.SignUpFragmentFirst
import com.roadmate.shop.ui.fragments.SignUpFragmentSecond
import com.roadmate.shop.utils.PermissionUtils

class SignUpActivity : BaseActivity(),
    AppLocationListener {

    private fun askAppLocationPermission(){
        if (PermissionUtils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            askAppStoragePermission()
        }else{
            if (PermissionUtils.isPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                PermissionUtils.requestPermissionFromActivity(this, Manifest.permission.ACCESS_FINE_LOCATION, PermissionUtils.ACCESS_FINE_LOCATION)
            }else{
                PermissionUtils.requestPermissionFromActivity(this, Manifest.permission.ACCESS_FINE_LOCATION, PermissionUtils.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun askAppStoragePermission(){
        if (!PermissionUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (PermissionUtils.isPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                PermissionUtils.requestPermissionFromActivity(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    PermissionUtils.EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE
                )
            }else{
                PermissionUtils.requestPermissionFromActivity(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    PermissionUtils.EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE
                )
            }
        }else{
            AppLocation().checkLocationSettings(this, this)
        }
    }

    private fun showLocationSettingsWarning(){
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(this).create()
        alertDialog.setTitle("Location settings warning!")
        alertDialog.setMessage("Your Location settings are not set to High accuracy. You may not be able to get precise location at times.")
        alertDialog.setButton(
            androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "Check Settings"
        ) { dialog, _ -> AppLocation().checkLocationSettings(this, this)
            dialog.dismiss()}
        alertDialog.setButton(
            androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, "Dismiss"
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar!!.hide()

        askAppLocationPermission()

        setFragment(SignUpFragmentFirst(), FragmentConstants.SIGNUP_FRAGMENT_FIRST, null, false)
    }

    override fun onBackPressed() {
        var fragment: SignUpFragmentSecond? = supportFragmentManager.findFragmentByTag(FragmentConstants.SIGNUP_FRAGMENT_SECOND) as? SignUpFragmentSecond
        if (fragment is SignUpFragmentSecond){
            supportFragmentManager.popBackStack()
        }else{
            finish()
        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PermissionUtils.ACCESS_FINE_LOCATION -> {
                askAppStoragePermission()
            }
            PermissionUtils.EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE -> {
                AppLocation().checkLocationSettings(this, this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1024 && resultCode != Activity.RESULT_OK){
            showLocationSettingsWarning()
        }
    }

    override fun onLocationReceived(location: Location) {
    }

    override fun onLocationSettingsSatisfied() {
    }
}