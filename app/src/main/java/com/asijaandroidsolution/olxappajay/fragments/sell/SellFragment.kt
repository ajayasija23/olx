package com.asijaandroidsolution.olxappajay.fragments.sell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.asijaandroidsolution.olxappajay.R
import com.asijaandroidsolution.olxappajay.databinding.FragmentSellBinding
import com.asijaandroidsolution.olxappajay.fragments.BaseFragment
import com.asijaandroidsolution.olxappajay.fragments.sell.adapter.SellAdapter
import com.asijaandroidsolution.olxappajay.model.Categories
import com.asijaandroidsolution.olxappajay.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore

class SellFragment : BaseFragment(), SellAdapter.ItemClickListener {

    private lateinit var binding: FragmentSellBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: SellAdapter
    private lateinit var categoriesModel: MutableList<Categories>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSellBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getCategoryList()
    }

    private fun getCategoryList() {
        showProgressbar()
        db.collection("Categories")
            .get().addOnSuccessListener { result ->
                hideProgressbar()
                categoriesModel = result.toObjects(Categories::class.java)
                setAdapter()
            }
            .addOnFailureListener{
            }
    }

    private fun setAdapter() {
        binding.rvSellCategories.layoutManager = GridLayoutManager(context, 3)
        adapter = SellAdapter(categoriesModel, this)
        binding.rvSellCategories.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        var bundle =Bundle()
        bundle.putString(Constants.CATEGORIE_NAME,categoriesModel.get(position).category_name)
        findNavController().navigate(R.id.action_sell_fragment_to_inclue,bundle)
    }

}