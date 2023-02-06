package com.example.playernk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StylesAdapter extends RecyclerView.Adapter<StylesAdapter.ItemViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Style> items;

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
                    Style item = items.get(getLayoutPosition());
                    onItemClickListener.onItemClick(item);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Style item = items.get(getLayoutPosition());
                    onItemLongClickListener.onLongItemClick(item);
                    return true;
                }
            });
        }

    }

    public StylesAdapter(Context context, ArrayList<Style> songs) {
        this.items = songs;
        this.inflater = LayoutInflater.from(context);
    }

    public interface OnItemClickListener {
        void onItemClick(Style item);
    }

    public interface OnItemLongClickListener {
        void onLongItemClick(Style item);
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
        Style song = items.get(position);

        holder.tvDescription.setText(song.name);



    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}

