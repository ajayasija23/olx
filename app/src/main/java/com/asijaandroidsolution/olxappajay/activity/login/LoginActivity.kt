package com.asijaandroidsolution.olxappajay.activity.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.asijaandroidsolution.olxappajay.activity.BaseActivity
import com.asijaandroidsolution.olxappajay.R
import com.asijaandroidsolution.olxappajay.activity.main.MainActivity
import com.asijaandroidsolution.olxappajay.databinding.ActivityLoginBinding
import com.asijaandroidsolution.olxappajay.utils.Constants
import com.asijaandroidsolution.olxappajay.utils.SharedPref
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

class LoginActivity: BaseActivity(), View.OnClickListener {
    private var user: FirebaseUser? = null
    private var callbackManager: CallbackManager? = null
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN: Int=123;
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        FacebookSdk.sdkInitialize(this);
        //creating the request
        createRequest()

        loginWithFb()

        binding.btnFbLogin.setOnClickListener(this)
        binding.btnGoogleLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnGoogleLogin->{
                loginWithGoogle()
            }
            R.id.btnFbLogin->{
                binding.buttonFacebookLogin.performClick()
            }
        }
    }

    private fun loginWithGoogle() {

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        user = auth.currentUser
        if(user!=null) {
            sendUserToHomeScreen(user);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {

            }
        }else {
            callbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    user = auth.currentUser
                    if (user!=null) {
                        sendUserToHomeScreen(user!!)
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun sendUserToHomeScreen(user: FirebaseUser?) {
        if(user?.email!=null)
            SharedPref(this).setString(Constants.EMAIL,user.email!!)
        if(user?.uid!=null)
            SharedPref(this).setString(Constants.USER_ID,user.uid!!)
        if(user?.displayName!=null)
            SharedPref(this).setString(Constants.USER_NAME,user.displayName!!)
        if(user?.photoUrl!=null)
            SharedPref(this).setString(Constants.PHOTO, user.photoUrl.toString()!!)
        val intent =Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    private fun createRequest() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }



    private fun loginWithFb() {
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()

        binding.buttonFacebookLogin.setReadPermissions(Arrays.asList("email"))
        binding.buttonFacebookLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("TAG", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException) {

            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("TAG", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    user = auth.currentUser
                    sendUserToHomeScreen(user!!)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }


}