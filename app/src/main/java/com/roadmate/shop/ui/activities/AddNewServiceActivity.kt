package com.roadmate.shop.ui.activities

import android.R
import android.R.id.message
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.roadmate.shop.constants.FragmentConstants
import com.roadmate.shop.models.ServiceInsertModel
import com.roadmate.shop.ui.fragments.AddNewServiceFragment


class AddNewServiceActivity : BaseActivity() {

    fun exitWithResult(list: ArrayList<ServiceInsertModel>){
        val intent = Intent()
        intent.putExtra("datalist", list)
        setResult(25, intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Add Service"

        setFragment(
            AddNewServiceFragment(),
            FragmentConstants.ADD_NEW_SERVICE_FRAGMENT,
            null,
            false
        )
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

    companion object{
        const val EXTRA_TITLE = "title"
        const val EXTRA_SUBMIT_BTN_NAME = "submit_btn_name"
        var isFrom = "Add Service"
    }
}