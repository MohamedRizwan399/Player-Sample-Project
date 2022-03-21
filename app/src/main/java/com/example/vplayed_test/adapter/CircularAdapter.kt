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
import com.example.vplayed_test.postApiDataclass.Data

class CircularAdapter:RecyclerView.Adapter<CircularAdapter.myViewHolder>() {
//    private var viewpager: ViewPager2? =null

    private var dataList:MutableList<Data>? = null

    fun setDataList(usersListData:MutableList<Data>?){
        this.dataList = usersListData
    }
    inner class myViewHolder(v: View):RecyclerView.ViewHolder(v){
        val image1=v.findViewById<ImageView>(R.id.image_shape)

        val image=v.findViewById<ImageView>(R.id.image_shape)
        val albumtitle=v.findViewById<TextView>(R.id.textview1)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircularAdapter.myViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.row_item1, parent, false)
        return myViewHolder(v)
    }

    override fun onBindViewHolder(holder: CircularAdapter.myViewHolder, position: Int) {
        val listimage= dataList?.get(position)


        if (listimage != null) {

            holder.albumtitle.text=listimage.album_name
            if (listimage?.album_thumbnail.isNullOrEmpty()){
                holder.image.setImageResource(R.drawable.union_1)
            }else
                Glide.with(holder.image
                    .context).load(listimage?.album_thumbnail).into(holder.image)

        }
//        if (position== dataList?.size!! -2){
//            viewpager?.post(run)
//        }
    }

    override fun getItemCount(): Int {
        if(dataList==null) return 0
        else return dataList?.size!!
    }
//    private val run = object : Runnable {
//        override fun run() {
//            dataList!!.addAll(dataList!!)
//            notifyDataSetChanged()
//        }
//    }
}