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

class SliderAdapter( private var onClickListener: OnclickListener ):RecyclerView.Adapter<SliderAdapter.SliderViewHolder>()
{

    private var viewpager: ViewPager2? =null
    private var imgList:MutableList<DataItem>? = null

    fun setDataList(usersListData:MutableList<DataItem>?){
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
//        holder.image.setImageResource(listimage.posterImage)
        Glide.with(holder.image
            .context).load(listimage?.img_url).into(holder.image)
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


