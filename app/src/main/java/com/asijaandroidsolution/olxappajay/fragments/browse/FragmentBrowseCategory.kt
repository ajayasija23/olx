package com.asijaandroidsolution.olxappajay.fragments.browse

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.asijaandroidsolution.olxappajay.R
import com.asijaandroidsolution.olxappajay.fragments.BaseFragment
import com.asijaandroidsolution.olxappajay.fragments.myAds.adapter.BrowseCategoryAdapter
import com.asijaandroidsolution.olxappajay.model.DataItemModel
import com.asijaandroidsolution.olxappajay.utils.Constants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_browse_category.*

class FragmentBrowseCategory : BaseFragment(), BrowseCategoryAdapter.ItemClickListener {

    private lateinit var categoriesAdapter: BrowseCategoryAdapter
    private lateinit var documentIdList: MutableList<DocumentSnapshot>
    private lateinit var dataItemModel: MutableList<DataItemModel>
    private val db= FirebaseFirestore.getInstance()
    private lateinit var category_name: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse_category, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category_name = arguments?.getString(Constants.CATEGORIE_NAME)!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getAdsList()
        textListener()
    }

    private fun textListener() {
        edSearchBrowse.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                // filter your list from your input
                filterList(s.toString())
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        })
    }

    private fun filterList(text: String) {
        val temp: MutableList<DataItemModel> = ArrayList()
        for (d in dataItemModel) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.BRAND.contains(text.capitalize()) || d.BRAND.contains(text) )
            {
                temp.add(d)
            }
        }
        //update recyclerview
        categoriesAdapter.updateList(temp)
    }

    private fun getAdsList() {
        showProgressbar()
        db.collection(category_name)
            .get().addOnSuccessListener { result ->
                hideProgressbar()
                dataItemModel = result.toObjects(DataItemModel::class.java)
                documentIdList = result.documents
                if (documentIdList.size > 0) {
                    setAdapter()
                }
                setAdapter()
            }
            .addOnFailureListener {
                Log.d("TAG", it.message)

            }
    }

    private fun setAdapter() {
        rvBrowse.layoutManager = LinearLayoutManager(context)
        categoriesAdapter =
            BrowseCategoryAdapter(dataItemModel, this)
        rvBrowse.adapter = categoriesAdapter
    }

    override fun onItemClick(position: Int) {
        var bundle = Bundle()
        bundle.putString(Constants.Id, documentIdList.get(position).id)
        bundle.putString(Constants.CATEGORIE_NAME, category_name)
        findNavController().navigate(R.id.action_browse_category_to_detail, bundle)

    }
}