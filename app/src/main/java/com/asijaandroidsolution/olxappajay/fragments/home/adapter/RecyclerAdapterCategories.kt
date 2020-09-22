package com.asijaandroidsolution.olxappajay.fragments.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asijaandroidsolution.olxappajay.R
import com.asijaandroidsolution.olxappajay.model.Categories
import com.bumptech.glide.Glide

class RecyclerAdapterCategories(
    var categoriesList: MutableList<Categories>,
    var mClickListener: ItemClickListener
) :    RecyclerView.Adapter<RecyclerAdapterCategories.MyViewHolder>() {

    private lateinit var context: Context

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val imageView=view.findViewById<ImageView>(R.id.ivCategory)
        val textView=view.findViewById<TextView>(R.id.tvName)
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecyclerAdapterCategories.MyViewHolder {

       context=parent.context
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_categories, parent, false)

        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.textView.text =categoriesList.get(position).category_name
        Glide.with(context)
            .load(categoriesList.get(position).image)
            .into(holder.imageView)
        holder.imageView.setOnClickListener(){
            mClickListener.onItemClick(position)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount():Int{
        return categoriesList.size
    }

    fun updateList(temp: MutableList<Categories>) {
        categoriesList=temp
        notifyDataSetChanged()
    }


    interface ItemClickListener{
        fun onItemClick(position: Int)
    }
}
