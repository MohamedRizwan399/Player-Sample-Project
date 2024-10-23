package com.example.player_sample_project.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.player_sample_project.R;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private final List<String> items;
    private final List<Integer> itemImages;
    private ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onItemClicked(String item);
    }
    public MenuAdapter(List<String> items, List<Integer> itemImages, ItemClickListener itemClickListener) {
        this.items = items;
        this.itemImages = itemImages;
        this.itemClickListener = itemClickListener;
    }


    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageIcon;
        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_menu);
            imageIcon = itemView.findViewById(R.id.icon_menu);
        }
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_items_row, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        String item = items.get(position);
        holder.textView.setText(item);
        // set the image
        holder.imageIcon.setImageResource(itemImages.get(position));

        // If item clicks
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("menu-", "menu getItem count--" + items.size());
        return items.size();
    }
}
