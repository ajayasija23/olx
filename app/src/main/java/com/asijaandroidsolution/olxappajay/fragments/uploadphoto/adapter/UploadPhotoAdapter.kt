package com.asijaandroidsolution.olxappajay.fragments.uploadphoto.adapter

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.asijaandroidsolution.olxappajay.R
import com.asijaandroidsolution.olxappajay.model.Categories
import com.bumptech.glide.Glide

class UploadPhotoAdapter(
    internal var activity:Activity,
    internal var imagesArrayList: ArrayList<String>,
    internal val addImage:AddImage
) :    RecyclerView.Adapter<UploadPhotoAdapter.MyViewHolder>() {

    private lateinit var context: Context

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val imageView=view.findViewById<ImageView>(R.id.ivSelectedImage)
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): UploadPhotoAdapter.MyViewHolder {

        context=parent.context
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_upload_photo, parent, false)

        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

       if (position<imagesArrayList.size){
           val s=imagesArrayList[position]
           Glide.with(context).load(s).into(holder.imageView)
       }
        holder.imageView.setOnClickListener(View.OnClickListener {
            if (position==imagesArrayList.size){
                addImage.addImage()
            }
        })
        if (position==imagesArrayList.size&&imagesArrayList.size==3){
            holder.imageView.visibility=View.GONE
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount():Int{
        return imagesArrayList.size+1
    }
    fun customNotify(imagesArrayList: ArrayList<String>){
        this.imagesArrayList=imagesArrayList
        notifyDataSetChanged()
    }



    interface AddImage{
        fun addImage()
    }
}
