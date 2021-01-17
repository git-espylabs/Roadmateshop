package com.roadmate.shop.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionUtils {
    companion object{
        const val EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE = 1
        const val EXTERNAL_STORAGE_READ_PERMISSION_REQUEST_CODE = 2
        const val CAMERA_PERMISSION_REQUEST_CODE = 3
        const val ACCESS_FINE_LOCATION = 4
        const val READ_PHONE_STATE = 5


        /**
         * method to check if permission is granted
         *
         * @param context
         * @param permission
         * @return booelan status
         */
        fun checkPermission(
            context: Context?,
            permission: String?
        ): Boolean {
            return ContextCompat.checkSelfPermission(context!!, permission!!) === PackageManager.PERMISSION_GRANTED
        }

        /**
         * method to show permission request if not accepted
         *
         * @param activity
         * @param permission
         * @return boolean status
         */
        fun isPermissionRationale(
            activity: Activity?,
            permission: String?
        ): Boolean {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission!!)
        }

        /**
         * method to request permission
         *
         * @param fragment
         * @param permission
         * @param requestCode
         */
        fun requestPermission(
            fragment: Fragment,
            permission: String,
            requestCode: Int
        ) {
            fragment.requestPermissions(
                arrayOf(permission),
                requestCode
            )
        }

        /**
         * method to request permission from Activity
         *
         * @param activity
         * @param permission
         * @param requestCode
         */
        fun requestPermissionFromActivity(
            activity: Activity?,
            permission: String,
            requestCode: Int
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(permission),
                requestCode
            )
        }

        /**
         * method to request permission
         *
         * @param fragment
         * @param permission
         * @param requestCode
         */
        fun requestMultiplePermission(
            fragment: Fragment,
            permission: Array<String?>?,
            requestCode: Int
        ) {
            fragment.requestPermissions(
                permission!!,
                requestCode
            )
        }
    }
}