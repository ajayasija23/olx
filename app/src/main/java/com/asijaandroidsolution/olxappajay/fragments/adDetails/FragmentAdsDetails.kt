package com.asijaandroidsolution.olxappajay.fragments.adDetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.asijaandroidsolution.olxappajay.R
import com.asijaandroidsolution.olxappajay.activity.preview.PreviewImage
import com.asijaandroidsolution.olxappajay.databinding.FragmentUploadPhotoBinding
import com.asijaandroidsolution.olxappajay.fragments.BaseFragment
import com.asijaandroidsolution.olxappajay.fragments.adDetails.adapter.ImagesPagerAdapter
import com.asijaandroidsolution.olxappajay.model.DataItemModel
import com.asijaandroidsolution.olxappajay.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_ad_details.*
import java.text.SimpleDateFormat
import java.util.*

class FragmentAdsDetails: BaseFragment(), ImagesPagerAdapter.ItemClickListener {
    private lateinit var dataItemModel: DataItemModel
    val db=FirebaseFirestore.getInstance()
    internal var count: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ad_details, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getItemDetails()
        clickListener()
    }

    private fun clickListener() {
     btnCall.setOnClickListener(View.OnClickListener {
         var intent= Intent(Intent.ACTION_DIAL)
         intent.data= Uri.parse("tel:${dataItemModel.PHONE}")
         startActivity(intent)
     })
    }

    private fun getItemDetails() {
        showProgressbar()
        val s=arguments?.getString(Constants.Id)


        db.collection(arguments?.getString(Constants.CATEGORIE_NAME)!!)
            .document(arguments?.getString(Constants.Id)!!)
            .get()
            .addOnSuccessListener {
                hideProgressbar()
                dataItemModel=it.toObject(DataItemModel::class.java)!!
                setData()
                setPagerAdapter()
            }

    }

    private fun setPagerAdapter() {
        val detailImagesAdapter = ImagesPagerAdapter(requireContext(), dataItemModel.images, this)
        viewpager.setAdapter(detailImagesAdapter)
        circleIndicator.setViewPager(viewpager)
        viewpager.setOffscreenPageLimit(1)
        viewpager.setCurrentItem(count)
    }

    private fun setData() {

        tvPriceDetails.text = Constants.CURRENCY_SYMBOL + dataItemModel.PRICE
        tvBrandDetails.text = dataItemModel.BRAND
        tvAddressDetails.text = dataItemModel.ADDRESS
        tvDescDetails.text = dataItemModel.DESCRIPTION

        tvPhoneNo.text = dataItemModel.PHONE
        tvPostedDate.text = dataItemModel.CREATED_Date

    }

    override fun onItemClick(position: Int) {

        startActivity(
            Intent(activity, PreviewImage::class.java).putExtra(
                "imageurl",
                dataItemModel.images.get(position)
            )
        )

    }

}