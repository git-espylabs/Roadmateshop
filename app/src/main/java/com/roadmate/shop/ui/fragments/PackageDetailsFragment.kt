package com.roadmate.shop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.roadmate.shop.BuildConfig
import com.roadmate.shop.R
import kotlinx.android.synthetic.main.fragment_pakage_details.*
import java.util.HashMap

class PackageDetailsFragment: Fragment() {

    private fun showProgress(visible:Boolean){
        if (visible){
            loadingFrame.visibility = View.VISIBLE
        }else{
            loadingFrame.visibility = View.GONE
        }
    }

    private fun populateData(){
        var mMap: HashMap<String, String> = this.arguments!!.getSerializable("packageMap") as HashMap<String, String>

        val image = mMap["image"]!!
        val pName = mMap["pName"]!!
        val pDesc = mMap["pDesc"]!!
        val pAmount = mMap["pAmount"]!!
        val pOfferAmount = mMap["pOfferAmount"]!!
        val pFor = mMap["pFor"]!!
        val pType = mMap["pType"]!!
        val pVehType = mMap["pVehType"]!!
        val pVehModel = mMap["pVehModel"]!!
        val pVehFuel = mMap["pVehFuel"]!!


        Glide.with(activity!!).load(BuildConfig.BANNER_URL_ENDPOINT + image)
            .error(R.drawable.road_mate_plain).error(R.drawable.road_mate_plain)
            .into(package_image)

        package_name.text = pName
        package_details.text = pDesc
        item_price.text = resources.getString(R.string.Rs) + pAmount
        item_strikeprice.text = resources.getString(R.string.Rs) + pOfferAmount
        packageFor.text = "Package for: $pFor"
        packageVehType.text = pVehType
        packageVehBrand.text = pVehModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pakage_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        populateData()
    }
}