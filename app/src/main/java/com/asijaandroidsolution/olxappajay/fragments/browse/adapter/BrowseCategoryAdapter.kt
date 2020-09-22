package com.asijaandroidsolution.olxappajay.fragments.myAds.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asijaandroidsolution.olxappajay.R
import com.asijaandroidsolution.olxappajay.model.DataItemModel
import com.asijaandroidsolution.olxappajay.utils.Constants
import com.bumptech.glide.Glide

class BrowseCategoryAdapter(
    var dataItemModel: MutableList<DataItemModel>,
    var mClickListener: ItemClickListener
):  RecyclerView.Adapter<BrowseCategoryAdapter.MyViewHolder>() {

    private lateinit var context: Context


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): BrowseCategoryAdapter.MyViewHolder {

        context=parent.context
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_my_ads, parent, false)

        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.price.setText(Constants.CURRENCY_SYMBOL + dataItemModel.get(position).PRICE)
        holder.city.setText(dataItemModel.get(position).ADDRESS)
        holder.brand.setText(dataItemModel.get(position).BRAND)
        Glide.with(context)
            .load(dataItemModel.get(position).images.get(0))
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.imageView)

        holder.date.setText(dataItemModel.get(position).CREATED_Date.toString())

        holder.itemView.setOnClickListener {
            mClickListener.onItemClick(position)
        }

    }

    override fun getItemCount(): Int {
        return dataItemModel.size
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val imageView=view.findViewById<ImageView>(R.id.ivAdsImage)
        val brand=view.findViewById<TextView>(R.id.tvBrand);
        val price=view.findViewById<TextView>(R.id.tvPrice);
        val city=view.findViewById<TextView>(R.id.tvCity);
        val date=view.findViewById<TextView>(R.id.tvDate);
    }



    fun updateList(temp: MutableList<DataItemModel>) {
        dataItemModel=temp
        notifyDataSetChanged()
    }


    interface ItemClickListener{
        fun onItemClick(position: Int)
    }

}


