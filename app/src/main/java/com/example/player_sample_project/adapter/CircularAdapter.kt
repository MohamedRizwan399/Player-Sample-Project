package com.example.player_sample_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.player_sample_project.R
import com.example.player_sample_project.data_mvvm.Data

class CircularAdapter(private var onclickListener: OnclickListener):RecyclerView.Adapter<CircularAdapter.MyViewHolder>() {

    private var dataList:MutableList<Data>? = null

    fun setDataList(usersListData:MutableList<Data>?) {
        this.dataList = usersListData
    }

    inner class MyViewHolder(v: View):RecyclerView.ViewHolder(v){
        val image: ImageView = v.findViewById(R.id.image_shape)
        val albumTitle: TextView = v.findViewById(R.id.textview1)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircularAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.row_item1, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: CircularAdapter.MyViewHolder, position: Int) {
        val listImage= dataList?.get(position)
        if (listImage != null) {
            holder.albumTitle.text = listImage.name
            //item clicked
            holder.itemView.setOnClickListener {
                onclickListener.onclick(position)
            }
            if (listImage.img_url.isNullOrEmpty()) {
                holder.image.setImageResource(R.drawable.union_1)
            } else {
                Glide.with(holder.image.context)
                    .load(listImage.img_url)
                    .placeholder(R.drawable.union_1)
                    .into(holder.image)
            }
        }
    }

    override fun getItemCount(): Int {
        if(dataList==null) return 0
        else return dataList?.size!!
    }

}