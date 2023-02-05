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

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ItemViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Song> items;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNumberDate;
        private TextView tvDescription;
        private TextView tvStyle;

        public ItemViewHolder(View itemView) {
            super(itemView);

            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            tvStyle = (TextView) itemView.findViewById(R.id.tvStyle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Song item = items.get(getLayoutPosition());
                    onItemClickListener.onItemClick(item);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Song item = items.get(getLayoutPosition());
                    onItemLongClickListener.onLongItemClick(item);
                    return true;
                }
            });
        }

    }

    public SongsAdapter(Context context, ArrayList<Song> songs) {
        this.items = songs;
        this.inflater = LayoutInflater.from(context);
    }

    public interface OnItemClickListener {
        void onItemClick(Song item);
    }

    public interface OnItemLongClickListener {
        void onLongItemClick(Song item);
    }

    public void setOnItemClickListener(OnItemClickListener onDocumentItemClickListener) {
        this.onItemClickListener = onDocumentItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onDocumentItemLongClickListener) {
        this.onItemLongClickListener = onDocumentItemLongClickListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.song_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Song song = items.get(position);

        holder.tvDescription.setText(song.name + "." + song.ext);
        holder.tvStyle.setText(song.style);



    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}

