package com.asijaandroidsolution.olxappajay.fragments.sell.adapter

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

class SellAdapter(
    var categoriesList: MutableList<Categories>,
    var mClickListener: ItemClickListener
)
    : RecyclerView.Adapter<SellAdapter.MyViewHolder>() {


    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_sell
            , parent, false
        )
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textViewTitle.text  =categoriesList.get(position).category_name
        Glide.with(context)
            .load(categoriesList.get(position).bw)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            mClickListener.onItemClick(position)
        }
        holder.imageView.setOnClickListener {
            mClickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    //view holder class

    class MyViewHolder(view:View) :RecyclerView.ViewHolder(view){
        val imageView = itemView.findViewById<ImageView>(R.id.iv_sell_icon)!!
        val textViewTitle = itemView.findViewById<TextView>(R.id.tv_icon_title)!!
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }
}