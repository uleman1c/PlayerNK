package com.example.playernk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ItemViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<History> items;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate;
        private TextView tvDescription;
        public ItemViewHolder(View itemView) {
            super(itemView);

            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    History item = items.get(getLayoutPosition());
                    onItemClickListener.onItemClick(item);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    History item = items.get(getLayoutPosition());
                    onItemLongClickListener.onLongItemClick(item);
                    return true;
                }
            });
        }

    }

    public HistoryAdapter(Context context, ArrayList<History> songs) {
        this.items = songs;
        this.inflater = LayoutInflater.from(context);
    }

    public interface OnItemClickListener {
        void onItemClick(History item);
    }

    public interface OnItemLongClickListener {
        void onLongItemClick(History item);
    }

    public void setOnItemClickListener(OnItemClickListener onDocumentItemClickListener) {
        this.onItemClickListener = onDocumentItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onDocumentItemLongClickListener) {
        this.onItemLongClickListener = onDocumentItemLongClickListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.history_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        History item = items.get(position);

        holder.tvDate.setText(item.date .isEmpty() ? "" : item.date.substring(6, 8) + "." + item.date.substring(4, 6) + "." + item.date.substring(0, 4)
                + " " + item.date.substring(8, 10) + ":" + item.date.substring(10, 12) + ":" + item.date.substring(12, 14));
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

