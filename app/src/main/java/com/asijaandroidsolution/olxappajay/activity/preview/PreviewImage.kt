package com.asijaandroidsolution.olxappajay.activity.preview

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.Window
import android.view.WindowManager
import com.asijaandroidsolution.olxappajay.R
import com.asijaandroidsolution.olxappajay.databinding.ActivityLoginBinding
import com.asijaandroidsolution.olxappajay.databinding.ActivityPreviewImageBinding
import com.bumptech.glide.Glide

class PreviewImage : AppCompatActivity() {
    private lateinit var binding: ActivityPreviewImageBinding

    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var mScaleFactor = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewImageBinding.inflate(layoutInflater)
        val view = binding.root
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(view)

        val extras = getIntent().getExtras()
        if (extras?.containsKey("imageuri")!!) {
            val imageUri = extras.getString("imageuri")
            val myBitmap = BitmapFactory.decodeFile(imageUri)
            binding.previewImage!!.setImageBitmap(myBitmap)
        } else if (extras.containsKey("imageurl")) {
            val imageUrl = extras.getString("imageurl")
            Glide.with(this)
                .load(imageUrl)
                .into(binding.previewImage)

        }



        binding.previewImage!!.scaleX = 1.0f
        binding.previewImage!!.scaleY = 1.0f
        binding.btnClosePreview!!.setOnClickListener { finish() }


        mScaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        mScaleGestureDetector!!.onTouchEvent(motionEvent)
        return true
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = Math.max(
                0.1f,
                Math.min(mScaleFactor, 10.0f)
            )
            binding.previewImage!!.scaleX = mScaleFactor
            binding.previewImage!!.scaleY = mScaleFactor
            return true
        }
    }
}