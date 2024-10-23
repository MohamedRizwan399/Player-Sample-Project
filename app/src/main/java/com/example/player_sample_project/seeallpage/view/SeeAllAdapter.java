package com.example.player_sample_project.seeallpage.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.player_sample_project.R;
import com.example.player_sample_project.seeallpage.modelclassPoJo.NewResponseData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.List;

public class SeeAllAdapter extends RecyclerView.Adapter<SeeAllAdapter.MyViewHolder> {
    private static final Log log = LogFactory.getLog(SeeAllAdapter.class);
    private final List<NewResponseData> dataList;
    private final Context context;

    public SeeAllAdapter(List<NewResponseData> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvTitle;
        public MyViewHolder(View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.img_view);
            tvTitle = itemView.findViewById(R.id.see_all_title);
        }
    }

    @NonNull
    @Override
    public SeeAllAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.see_all_rowitem,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeeAllAdapter.MyViewHolder holder, int position) {
        if (dataList.get(position).getUrlToImage() != null) {
            holder.tvTitle.setText(dataList.get(position).getTitle());
            Glide.with(context)
                    .load(dataList.get(position).getUrlToImage())
                    .placeholder(R.drawable.union_1)
                    .into(holder.ivThumb);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
