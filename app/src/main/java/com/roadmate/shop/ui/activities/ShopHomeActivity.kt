package com.roadmate.shop.ui.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.roadmate.app.api.response.AppVersionMaster
import com.roadmate.shop.BuildConfig
import com.roadmate.shop.R
import com.roadmate.shop.api.manager.APIManager
import com.roadmate.shop.api.service.ApiServices
import com.roadmate.shop.constants.FragmentConstants
import com.roadmate.shop.extensions.doIfTrue
import com.roadmate.shop.extensions.elseDo
import com.roadmate.shop.extensions.startActivity
import com.roadmate.shop.location.AppLocation
import com.roadmate.shop.location.AppLocationListener
import com.roadmate.shop.log.AppLogger
import com.roadmate.shop.preference.ShopDetails
import com.roadmate.shop.rmapp.AppSession
import com.roadmate.shop.ui.fragments.MyStoreFragment
import com.roadmate.shop.ui.fragments.ShopPackageMainFragment
import com.roadmate.shop.ui.fragments.ShopServicesFragment
import com.roadmate.shop.ui.fragments.ShopsHomeFragment
import com.roadmate.shop.utils.PermissionUtils
import com.roadmate.shop.utils.PermissionUtils.Companion.ACCESS_FINE_LOCATION
import com.roadmate.shop.utils.PermissionUtils.Companion.EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE
import kotlinx.android.synthetic.main.shop_home_activity.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


class ShopHomeActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    AppLocationListener {

    var fragment: Fragment? = null
    var fragmentTag = ""

    private fun exitByBackKey() {
        AlertDialog.Builder(this)
            .setMessage("Do you want to exit from Roadmateshop?")
            .setPositiveButton("Yes") { _, _ ->
                finish()
            }
            .setNegativeButton(
                "No"
            ) { _, _ -> }
            .show()
    }

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
                PermissionUtils.requestPermissionFromActivity(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE)
            }else{
                PermissionUtils.requestPermissionFromActivity(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE)
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

    private fun hideItem(){
        if (ShopDetails().isMechanic) {
            val nav_Menu: Menu = bottomNavigation.menu
            nav_Menu.findItem(R.id.navigation_search).isVisible = true
        } else {
            val nav_Menu: Menu = bottomNavigation.menu
            nav_Menu.findItem(R.id.navigation_search).isVisible = true
        }

        if (AppSession.shopPackageCount > 0){
            val nav_Menu: Menu = bottomNavigation.menu
            nav_Menu.findItem(R.id.navigation_notification).isVisible = true
        }else{
            val nav_Menu: Menu = bottomNavigation.menu
            nav_Menu.findItem(R.id.navigation_notification).isVisible = false
        }
    }

    private fun promptUpdate(newVersion: String, oldversion: String) {
        AlertDialog.Builder(this)
            .setTitle("Update available!")
            .setCancelable(false)
            .setMessage("You are using an out dated version(v$oldversion) of RoadMate! An updated version(v$newVersion)available in Google Play Store.")
            .setPositiveButton("Update") { _, _ ->
                val appPackageName = packageName

                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")
                        )
                    )
                } catch (anfe: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
            }
            /*.setNegativeButton(
                "Dismiss"
            ) { _, _ -> }*/
            .show()
    }

    private fun getAppVersionName(): String{
        var versionName = "0";
        try {
            val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            versionName = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return  versionName;
    }

    private fun checkAppVersion(){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<AppVersionMaster>> {
                getAppVersionFromServer(appVersionRequestJSON())
            }
            if (response.isSuccessful && !response.body()?.data!!.isNullOrEmpty()){
                val serverAppVersion = response.body()?.data!![0];
                if (serverAppVersion.version_code.toInt() > BuildConfig.VERSION_CODE && serverAppVersion.version_name != getAppVersionName()){
                    promptUpdate(serverAppVersion.version_name, getAppVersionName())
                }
            }
        }
    }

    private fun appVersionRequestJSON() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("apptype", "2")
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shop_home_activity)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setLogo(R.mipmap.ic_launcher_round)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.elevation = 0F;

        bottomNavigation.setOnNavigationItemSelectedListener(this)


        hideItem()

        askAppLocationPermission()

        setFragment(ShopsHomeFragment(), FragmentConstants.SHOP_HOME_FRAGMENT, null, false, R.id.content_home)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_search -> {
                this.startActivity<SearchSuggestionActivity>()
            }
            R.id.menu_notification -> {
                this.startActivity<NotificationsActivity>()
            }
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            //home
            R.id.navigation_home ->{
                fragment = ShopsHomeFragment()
                fragmentTag = FragmentConstants.SHOP_HOME_FRAGMENT
            }

            R.id.navigation_search ->{
                fragment = ShopServicesFragment()
                fragmentTag = FragmentConstants.SHOP_SERVICE_FRAGMENT
            }

            R.id.navigation_notification ->{
                fragment = ShopPackageMainFragment()
                fragmentTag = FragmentConstants.SHOP_PACKAGE_MAIN_FRAGMENT
            }

            R.id.navigation_store ->{
                fragment = MyStoreFragment()
                fragmentTag = FragmentConstants.MY_STORE_FRAGMENT
            }

            R.id.navigation_account ->{
                this.startActivity<ShopAccountActivity>()
                this.finish()
            }
        }

        if (fragment != null) {
            setFragment(fragment!!, fragmentTag, null, false, R.id.content_home)
        }

        return true
    }

    override fun onBackPressed() {
        val seletedItemId: Int = bottomNavigation.selectedItemId
        if (R.id.navigation_home != seletedItemId) {
            bottomNavigation.selectedItemId = R.id.navigation_home;
        } else {
            exitByBackKey()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            ACCESS_FINE_LOCATION -> {
                askAppStoragePermission()
            }
            EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE -> {
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
        (location != null).doIfTrue {
            AppSession.appLatitude = ""+location.latitude
            AppSession.appLongitude = ""+location.longitude
        }elseDo {
            AppLogger.error("Location not received!")
        }
    }

    override fun onLocationSettingsSatisfied() {
        AppLocation().getAppLocation(this, this)
        checkAppVersion()
    }
}