package com.asijaandroidsolution.olxappajay.fragments.include_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.asijaandroidsolution.olxappajay.R
import com.asijaandroidsolution.olxappajay.databinding.FragmentIncludeDetailsBinding
import com.asijaandroidsolution.olxappajay.fragments.BaseFragment
import com.asijaandroidsolution.olxappajay.utils.Constants
import kotlinx.android.synthetic.main.fragment_include_details.*

class IncludeDetails: BaseFragment() {
    private lateinit var binding: FragmentIncludeDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIncludeDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener()
    }

    private fun listener() {
        binding.btnNext.setOnClickListener(View.OnClickListener {
            sendData()
        })
    }

    private fun sendData() {
        if(validateFields()){
            //all fields are correct
            val bundle = Bundle()
            bundle.putString(Constants.BRAND,binding.edBrand.text.toString())
            bundle.putString(Constants.YEAR,binding.edYear.text.toString())
            bundle.putString(Constants.TITLE,binding.edTitle.text.toString())
            bundle.putString(Constants.DESCRIPTION,binding.edDescription.text.toString())
            bundle.putString(Constants.ADDRESS,edAddress.text.toString())
            bundle.putString(Constants.PHONE,binding.edPhoneNumber.text.toString())
            bundle.putString(Constants.PRICE,binding.edPrice.text.toString())
            bundle.putString(Constants.CATEGORIE_NAME,arguments?.getString(Constants.CATEGORIE_NAME))
            findNavController().navigate(R.id.action_include_details_to_upload_photo,bundle)

        }
        else{
            Toast.makeText(activity,"one or more fields are empty",Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateFields(): Boolean {
        if (binding.edBrand.text?.isEmpty()!!){
            binding.edBrand.setError(getString(R.string.enter_brand))
            return false
        }else if (binding.edYear.text.toString().isEmpty()){
            binding.edYear.setError(getString(R.string.enter_year))
            return false
        }else if (binding.edTitle.text.toString().isEmpty()){
            binding.edTitle.setError(getString(R.string.enter_ad_title))
            return false
        }else if (binding.edPrice.text.toString().isEmpty()){
            binding.edPrice.setError(getString(R.string.enter_price))
            return false
        }else if (binding.edAddress.text.toString().isEmpty()){
            binding.edAddress.setError(getString(R.string.enter_address))
            return false
        }else if (binding.edPhoneNumber.text.toString().isEmpty()){
            binding.edPhoneNumber.setError(getString(R.string.enter_phone))
            return false
        }
        return true
    }
}


