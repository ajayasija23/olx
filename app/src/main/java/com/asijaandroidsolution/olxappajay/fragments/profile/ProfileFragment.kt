package com.asijaandroidsolution.olxappajay.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.asijaandroidsolution.olxappajay.R
import com.asijaandroidsolution.olxappajay.activity.login.LoginActivity
import com.asijaandroidsolution.olxappajay.fragments.BaseFragment
import com.asijaandroidsolution.olxappajay.utils.Constants
import com.asijaandroidsolution.olxappajay.utils.SharedPref
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listener()
        tvProfileName.text = SharedPref(requireActivity()).getString(Constants.USER_NAME)
        tvProfileEmail.text = SharedPref(requireActivity()).getString(Constants.EMAIL)
        Log.d("profileimagees",SharedPref(requireActivity()).getString(Constants.PHOTO))
        Glide.with(requireActivity()).
        load(SharedPref(requireActivity()).getString(Constants.PHOTO))
            .placeholder(R.drawable.avatar)
            .into(ivProfilePic)

    }

    private fun listener() {
        btnSetting.setOnClickListener(this)
        btnLogout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.btnSetting->{
                findNavController().navigate(R.id.action_profile_to_settings)
            }

            R.id.btnLogout->{
                showLogoutDialog()

            }
        }

    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(R.string.logout)
        //set message for alert dialog
        builder.setMessage(R.string.logout_message)
        builder.setIcon(R.drawable.ic_warning)


        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut();

            clearSession()

            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton("No"){dialogInterface, which ->
            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun clearSession() {
        SharedPref(requireActivity()).setString(Constants.USER_NAME,"")
        SharedPref(requireActivity()).setString(Constants.USER_ID,"")
        SharedPref(requireActivity()).setString(Constants.EMAIL,"")
        SharedPref(requireActivity()).setString(Constants.PHOTO,"")
        SharedPref(requireActivity()).setString(Constants.PHONE!!,"")
    }
}