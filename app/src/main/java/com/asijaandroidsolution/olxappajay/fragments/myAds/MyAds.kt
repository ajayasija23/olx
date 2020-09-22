package com.asijaandroidsolution.olxappajay.fragments.myAds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.asijaandroidsolution.olxappajay.R
import com.asijaandroidsolution.olxappajay.databinding.FragmentMyAdsBinding
import com.asijaandroidsolution.olxappajay.fragments.BaseFragment
import com.asijaandroidsolution.olxappajay.fragments.myAds.adapter.BrowseCategoryAdapter
import com.asijaandroidsolution.olxappajay.fragments.myAds.adapter.MyAdsAdapter
import com.asijaandroidsolution.olxappajay.model.DataItemModel
import com.asijaandroidsolution.olxappajay.utils.Constants
import com.asijaandroidsolution.olxappajay.utils.SharedPref
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_my_ads.*


class MyAds : BaseFragment(), MyAdsAdapter.ItemClickListener {

    private lateinit var myAdsAdapter: MyAdsAdapter
    private var documentIdList: MutableList<DataItemModel> = ArrayList()
    private lateinit var dataItemModel: MutableList<DataItemModel>
    private lateinit var binding: FragmentMyAdsBinding
    private val db=FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_my_ads, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getMyAds()
        rvMyAds.layoutManager=LinearLayoutManager(context)
    }

    private fun getMyAds() {
        showProgressbar()
        db.collection(Constants.CATEGORIES)
            .get()
            .addOnSuccessListener {
                for (i in it.documents){
                    getDataFromKey(i.getString(Constants.CATEGORIE_NAME)!!)
                }
            }
    }

    private fun getDataFromKey(key: String) {
        db.collection(key)
            .whereEqualTo("USER_ID",SharedPref(requireActivity()).getString(Constants.USER_ID))
            .get()
            .addOnSuccessListener {
                hideProgressbar()
                dataItemModel=it.toObjects(DataItemModel::class.java)
                documentIdList.addAll(dataItemModel)
                setAdapter()
            }
    }

    private fun setAdapter() {
        myAdsAdapter =
            MyAdsAdapter(documentIdList, this)
        if (rvMyAds != null)
            rvMyAds.adapter = myAdsAdapter
    }

    override fun onItemClick(position: Int) {

        var bundle=Bundle()
        bundle.putString(Constants.CATEGORIE_NAME,documentIdList[position].TYPE)
        bundle.putString(Constants.Id,documentIdList.get(position).Id)
        findNavController().navigate(R.id.action_nav_my_ads_to_nav_ads_details,bundle)
    }
}