package com.example.vplayed_test.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.vplayed_test.data.DataItem
import com.example.vplayed_test.R
import com.example.vplayed_test.postApiDataclass.Data

class SliderAdapter( private var onClickListener: OnclickListener ):RecyclerView.Adapter<SliderAdapter.SliderViewHolder>()
{

    private var viewpager: ViewPager2? =null
    private var imgList:MutableList<Data>? = null

    fun setDataList(usersListData:MutableList<Data>?){
        this.imgList = usersListData
    }
    inner class SliderViewHolder(v:View):RecyclerView.ViewHolder(v){
        val image=v.findViewById<ImageView>(R.id.imageslider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val v=inflater.inflate(R.layout.slider_items,parent,false)
        return SliderViewHolder(v)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val listimage= imgList?.get(position)
        holder.itemView.setOnClickListener{
            onClickListener.onclick(position)
        }
        if (listimage?.album_thumbnail.isNullOrEmpty()){
            holder.image.setImageResource(R.drawable.union_1)
        }else
            Glide.with(holder.image
                .context).load(listimage?.album_thumbnail).into(holder.image)

//        holder.image.setImageResource(listimage.posterImage)

    if (position== imgList?.size!! -2){
        viewpager?.post(run)
    }
    }

    override fun getItemCount(): Int {
        if(imgList==null) return 0
        else return imgList?.size!!
    }
    private val run = object : Runnable {
        override fun run() {
            imgList!!.addAll(imgList!!)
            notifyDataSetChanged()
        }

    }
}


