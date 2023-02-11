package com.example.playernk;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ItemViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Favorite> items;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNumberDate;
        private TextView tvDescription;
        public ItemViewHolder(View itemView) {
            super(itemView);

            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Favorite item = items.get(getLayoutPosition());
                    onItemClickListener.onItemClick(item);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Favorite item = items.get(getLayoutPosition());
                    onItemLongClickListener.onLongItemClick(item);
                    return true;
                }
            });
        }

    }

    public FavoritesAdapter(Context context, ArrayList<Favorite> songs) {
        this.items = songs;
        this.inflater = LayoutInflater.from(context);
    }

    public interface OnItemClickListener {
        void onItemClick(Favorite item);
    }

    public interface OnItemLongClickListener {
        void onLongItemClick(Favorite item);
    }

    public void setOnItemClickListener(OnItemClickListener onDocumentItemClickListener) {
        this.onItemClickListener = onDocumentItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onDocumentItemLongClickListener) {
        this.onItemLongClickListener = onDocumentItemLongClickListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.style_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Favorite item = items.get(position);

        holder.tvDescription.setText(item.name);

//        if (item.selected){
//            holder.tvDescription.setBackgroundColor(Color.parseColor("#FF018786"));
//        } else {
//            holder.tvDescription.setBackgroundColor(Color.parseColor("#FFFFFF"));
//        }




    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}

