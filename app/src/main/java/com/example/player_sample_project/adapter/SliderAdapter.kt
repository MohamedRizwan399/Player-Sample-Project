package com.example.player_sample_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.player_sample_project.R
import com.example.player_sample_project.data_mvvm.Data

class SliderAdapter( private var onClickListener: OnclickListener ):RecyclerView.Adapter<SliderAdapter.SliderViewHolder>()
{

    private var viewpager: ViewPager2? =null
    private var imgList:MutableList<Data>? = null

    fun setDataList(usersListData:MutableList<Data>?) {
        this.imgList = usersListData
    }

    fun clearDataList() {
        imgList?.clear()
    }

    inner class SliderViewHolder(v:View):RecyclerView.ViewHolder(v) {
        val image: ImageView = v.findViewById(R.id.imageslider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val v=inflater.inflate(R.layout.slider_items,parent,false)
        return SliderViewHolder(v)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val listImage= imgList?.get(position)
        holder.itemView.setOnClickListener{
            onClickListener.onclick(position)
        }
        if (listImage?.img_url.isNullOrEmpty()) {
            holder.image.setImageResource(R.drawable.union_1)
        } else
            Glide.with(holder.image.context)
                .load(listImage?.img_url)
                .placeholder(R.drawable.union_1)
                .into(holder.image)

        if (position == imgList?.size!! -2){
            viewpager?.post(run)
        }
    }

    override fun getItemCount(): Int {
        if (imgList == null) return 0
        else return imgList?.size!!
    }

    private val run = object : Runnable {
        override fun run() {
            imgList!!.addAll(imgList!!)
            notifyDataSetChanged()
        }

    }
}


