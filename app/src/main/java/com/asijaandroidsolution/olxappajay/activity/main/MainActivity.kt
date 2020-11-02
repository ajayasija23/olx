package com.asijaandroidsolution.olxappajay.activity.main

import android.R.attr
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.asijaandroidsolution.olxappajay.R
import com.asijaandroidsolution.olxappajay.activity.BaseActivity
import com.asijaandroidsolution.olxappajay.utils.Constants
import com.asijaandroidsolution.olxappajay.utils.OnImageSelectedListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.alhazmy13.mediapicker.Image.ImagePicker


class MainActivity : BaseActivity() {

    lateinit var imageListener:OnImageSelectedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.nav_sell,R.id.nav_my_ads,
                R.id.nav_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount>1)
            supportFragmentManager.popBackStackImmediate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
        when(item.groupId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
    }

    fun getOnImageSelected(imageListener:OnImageSelectedListener){
        this.imageListener=imageListener
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_OK) {
            val mPaths=data?.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH)
            val bundle=Bundle()
            bundle.putStringArrayList(Constants.IMAGE_PATH,mPaths)
            imageListener.onImageSelected(bundle)
        }
    }
}