package com.example.player_sample_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.player_sample_project.R
import com.example.player_sample_project.data_mvvm.Data

class RecyclerAdapter(private var onclickListener: OnclickListener): RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {
    private var viewpager: ViewPager2? =null
    private var dataList:MutableList<Data>? = null

    fun setDataList(usersListData:MutableList<Data>?) {
        this.dataList = usersListData
    }

    inner class MyViewHolder(v: View):RecyclerView.ViewHolder(v) {
        val image: ImageView = v.findViewById(R.id.poster_image)
        val albumTitle: TextView = v.findViewById(R.id.tv1)
        val artistName: TextView = v.findViewById(R.id.tv2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val v=inflater.inflate(R.layout.row_item,parent,false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val listImage= dataList?.get(position)

        if (listImage != null) {
            holder.albumTitle.text = listImage.name
            holder.artistName.text = listImage.name
            //item clicked
            holder.itemView.setOnClickListener {
                onclickListener.onclick(position)
            }
            if (listImage.img_url.isEmpty()) {
                holder.image.setImageResource(R.drawable.union_1)
            } else {
                Glide.with(holder.image.context)
                    .load(listImage.img_url)
                    .placeholder(R.drawable.union_1)
                    .into(holder.image)
            }
        }
        if (position == dataList?.size!! -2) {
            viewpager?.post(run)
        }


    }

    override fun getItemCount(): Int {
        if (dataList == null) return 0
        else return dataList?.size!!
    }

    private val run = object : Runnable {
        override fun run() {
            dataList!!.addAll(dataList!!)
            notifyDataSetChanged()
        }
    }
}