package com.roadmate.shop.ui.activities

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.razorpay.PaymentResultListener
import com.roadmate.shop.constants.FragmentConstants
import com.roadmate.shop.log.AppLogger
import com.roadmate.shop.ui.fragments.NotificationFragment
import com.roadmate.shop.ui.fragments.PaymentFragment

class PaymentActivity : BaseActivity(), PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Payment"

        setFragment(PaymentFragment(), FragmentConstants.PAYMENT_FRAGMENT, null, false)
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

    override fun onPaymentError(p0: Int, p1: String?) {
        AppLogger.error("OnPaymentError", "Exception in onPaymentError - $p1")
        var fragment: PaymentFragment? = supportFragmentManager.findFragmentByTag(FragmentConstants.PAYMENT_FRAGMENT) as PaymentFragment
        fragment?.showAlertPaymentError(p1!!)
    }

    override fun onPaymentSuccess(p0: String?) {
        AppLogger.info("onPaymentSuccess", "Message:- $p0")
        var fragment: PaymentFragment? = supportFragmentManager.findFragmentByTag(FragmentConstants.PAYMENT_FRAGMENT) as PaymentFragment
        fragment?.updatePaymentStatus(p0!!)
    }
}