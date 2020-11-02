package com.asijaandroidsolution.olxappajay.fragments.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.asijaandroidsolution.olxappajay.R
import com.asijaandroidsolution.olxappajay.fragments.BaseFragment
import com.asijaandroidsolution.olxappajay.utils.Constants
import com.asijaandroidsolution.olxappajay.utils.SharedPref
import kotlinx.android.synthetic.main.fragment_settings.*

class FragmentSetting : BaseFragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        edFullName.setText(SharedPref(requireActivity()).getString(Constants.USER_NAME))
        edEmail.setText(SharedPref(requireActivity()).getString(Constants.EMAIL))
        edPhone.setText(SharedPref(requireActivity()).getString(Constants.PHONE!!))
        edAddress.setText(SharedPref(requireActivity()).getString(Constants.ADDRESS!!))

        listener()
    }
    private fun listener() {
        textViewSave.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.textViewSave -> {
                validateData()
            }
        }
    }

    private fun validateData() {
        if (edFullName.text?.isEmpty()!!) {
            edFullName.setError(getString(R.string.enter_full_name))
        } else if (edEmail.text.toString().isEmpty()) {
            edEmail.setError(getString(R.string.enter_email))
        } else if (edPhone.text.toString().isEmpty()) {
            edPhone.setError(getString(R.string.enter_phone))
        } else {
            SharedPref(requireActivity()).setString(Constants.USER_NAME, edFullName.text.toString())
            SharedPref(requireActivity()).setString(Constants.EMAIL, edEmail.text.toString())
            SharedPref(requireActivity()).setString(Constants.PHONE!!, edPhone.text.toString())
            SharedPref(requireActivity()).setString(Constants.ADDRESS!!, edAddress.text.toString())
            Toast.makeText(requireActivity(), "Saved Successfully", Toast.LENGTH_LONG).show()
            fragmentManager?.popBackStack()
        }
    }

}