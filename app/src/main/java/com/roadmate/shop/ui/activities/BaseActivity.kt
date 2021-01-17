package com.roadmate.shop.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.roadmate.shop.R
import kotlinx.android.synthetic.main.toolbar_main.*

open class BaseActivity : AppCompatActivity() {

    fun setFragment(targetFragment: Fragment, fragmentTag: String?, bundle: Bundle?, addToBackStack: Boolean) {
        val transaction =
            supportFragmentManager.beginTransaction()
        if (bundle != null) {
            targetFragment.arguments = bundle
        }
        transaction.replace(R.id.container, targetFragment, fragmentTag)
        if (addToBackStack) {
            transaction.addToBackStack(fragmentTag)
        } else {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    fun setFragment(targetFragment: Fragment, fragmentTag: String?, bundle: Bundle?, addToBackStack: Boolean, containerId: Int) {
        val transaction =
            supportFragmentManager.beginTransaction()
        if (bundle != null) {
            targetFragment.arguments = bundle
        }
        transaction.replace(containerId, targetFragment, fragmentTag)
        if (addToBackStack) {
            transaction.addToBackStack(fragmentTag)
        } else {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    protected fun setToolbar(isVisible: Boolean, titleMain: String?, titleSub: String?, isMain : Boolean, isCartView: Boolean) {
        if (isVisible) {
            if (isMain) {
                tbMain.visibility = View.VISIBLE
                tbSub1.visibility = View.GONE
                if (titleMain!!.isNotEmpty()) {
                    toolbar_title.text = titleMain
                } else {
                    toolbar_title.text = ""
                }
                if (titleSub!!.isNotEmpty()) {
                    toolbar_title_sub.text = titleSub
                } else {
                    toolbar_title.text = ""
                }

            }else{
                tbMain.visibility = View.GONE
                tbSub1.visibility = View.VISIBLE
                if (titleMain!!.isNotEmpty()) {
                    toolbar_title2.text = titleMain
                } else {
                    toolbar_title.text = "Thrissur Fish"
                }
                if (titleSub!!.isNotEmpty()) {
                    toolbar_title_sub2.text = titleSub
                } else {
                    toolbar_title_sub2.visibility = View.GONE
                }
                if (isCartView){
                    cust_icon.visibility = View.VISIBLE
                }else{
                    cust_icon.visibility = View.GONE
                }
            }
        } else {
            tbCommon.visibility = View.GONE
        }
    }

    protected fun setCustomTheme(themeId: Int){
        setTheme(themeId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
}