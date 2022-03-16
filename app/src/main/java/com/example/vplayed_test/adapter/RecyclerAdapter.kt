package com.example.vplayed_test.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.vplayed_test.R
import com.example.vplayed_test.data.DataItem
import com.example.vplayed_test.postApiDataclass.Data

class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.myViewHolder>() {
    private var viewpager: ViewPager2? =null

    private var dataList:MutableList<Data>? = null

    fun setDataList(usersListData:MutableList<Data>?){
        this.dataList = usersListData
    }
    inner class myViewHolder(v: View):RecyclerView.ViewHolder(v){
        val image1=v.findViewById<ImageView>(R.id.poster_image)

        val image=v.findViewById<ImageView>(R.id.poster_image)
        val albumtitle=v.findViewById<TextView>(R.id.tv1)
        val artistname=v.findViewById<TextView>(R.id.tv2)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val v=inflater.inflate(R.layout.row_item,parent,false)
        return myViewHolder(v)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val listimage= dataList?.get(position)


        if (listimage != null) {
            holder.albumtitle.text=listimage.album_name
            holder.artistname.text=listimage.artist_name
            if (listimage?.album_thumbnail.isNullOrEmpty()){
                holder.image.setImageResource(R.drawable.union_1)
            }else
            Glide.with(holder.image
                .context).load(listimage?.album_thumbnail).into(holder.image)
        }
        if (position== dataList?.size!! -2){
            viewpager?.post(run)
        }


    }

    override fun getItemCount(): Int {
        if(dataList==null) return 0
        else return dataList?.size!!
    }
    private val run = object : Runnable {
        override fun run() {
            dataList!!.addAll(dataList!!)
            notifyDataSetChanged()
        }
    }
}