package com.asijaandroidsolution.olxappajay.fragments.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.asijaandroidsolution.olxappajay.R
import com.asijaandroidsolution.olxappajay.databinding.FragmentHomeBinding
import com.asijaandroidsolution.olxappajay.fragments.BaseFragment
import com.asijaandroidsolution.olxappajay.fragments.home.adapter.RecyclerAdapterCategories
import com.asijaandroidsolution.olxappajay.model.Categories
import com.asijaandroidsolution.olxappajay.utils.Constants
import com.asijaandroidsolution.olxappajay.utils.SharedPref
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : BaseFragment(),RecyclerAdapterCategories.ItemClickListener {

    private lateinit var adpater: RecyclerAdapterCategories
    private lateinit var binding: FragmentHomeBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var categoriesList:MutableList<Categories>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvLocation.text=SharedPref(requireActivity()).getString(Constants.CITY_NAME)
        getCategories()
        textListenter()
    }

    private fun textListenter() {
        binding.edSearch.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                
            }

            override fun afterTextChanged(s: Editable?) {
                filterList(s.toString())
            }

        })
    }

    private fun filterList(text: String) {
        val temp: MutableList<Categories> = ArrayList()
        for (d in categoriesList){
            if (d.category_name.contains(text.capitalize())||d.category_name.contains(text)){
                temp.add(d)
            }
            adpater.updateList(temp)
        }
    }

    private fun getCategories() {
        showProgressbar()
        db.collection("Categories")
            .get()
            .addOnSuccessListener { Result->
                hideProgressbar()
                categoriesList=Result.toObjects(Categories::class.java)
                setAdapter()
            }
    }

    private fun setAdapter() {
        binding.rvCategories.layoutManager= GridLayoutManager(context,3)!!
        adpater=RecyclerAdapterCategories(categoriesList,this)
        binding.rvCategories.adapter=adpater
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onItemClick(position: Int) {
        var bundle = Bundle()
        bundle.putString(Constants.CATEGORIE_NAME, categoriesList.get(position).category_name)
        Log.v("Category Clicked:",categoriesList.get(position).category_name)
        findNavController().navigate(R.id.action_home_to_browse_category, bundle)
    }
}