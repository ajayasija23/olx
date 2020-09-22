package com.asijaandroidsolution.olxappajay.activity.splash

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityCompat
import com.asijaandroidsolution.olxappajay.activity.BaseActivity
import com.asijaandroidsolution.olxappajay.databinding.ActivitySplashBinding
import com.asijaandroidsolution.olxappajay.activity.login.LoginActivity
import com.asijaandroidsolution.olxappajay.utils.Constants
import com.asijaandroidsolution.olxappajay.utils.SharedPref
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.util.*

class SplashActivity : BaseActivity() {


    private var mDelayHandler: Handler?=null
    private val RC_LOC_PERMISSION=111
    private var locationRequest: LocationRequest? = null
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_CHECK_SETTINGS: Int=321
    private lateinit var binding: ActivitySplashBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mDelayHandler= Handler()
        fusedLocationClient=LocationServices.getFusedLocationProviderClient(this)
        locationsCallback()

        //permissions
        if(EasyPermissions.hasPermissions(this,Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)){
            //user already has permissions
            checkForSetting()
        }
        else{
            EasyPermissions.requestPermissions(this,"Give Location Permission to continue",
                        RC_LOC_PERMISSION,Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private fun locationsCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                if (locationResult.locations.isNotEmpty()){
                    //get the latest location
                    val location=locationResult.lastLocation
                    SharedPref(applicationContext).setString(
                        Constants.CITY_NAME,
                        getCityName(location)!!
                    )

                    mDelayHandler!!.postDelayed(
                        mRunnable,
                        3000
                    )

                }
            }
        }
    }

    private fun getCityName(location: Location): String? {
        var cityName=""
        val geocoder=Geocoder(this,Locale.getDefault())
        try {
            val address=geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
            cityName=address[0].locality
        }catch (e: IOException){
            when{
                e.message=="grpc failed"->{ }
                else-> throw e
            }
        }
        return cityName
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    @AfterPermissionGranted(111)
    private fun checkForSetting() {
        //permission granted
        createLocationRequest();
        //location setting request object
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            //setting are satisfied
            requestLocationUpdates()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(this,
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    fun createLocationRequest() {
         locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==REQUEST_CHECK_SETTINGS){
            if (resultCode== RESULT_OK){
                requestLocationUpdates()
            }
            else if (resultCode== RESULT_CANCELED){
                finish()
            }
        }
    }

    //Creating Runnable
    internal val mRunnable: Runnable= Runnable {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}