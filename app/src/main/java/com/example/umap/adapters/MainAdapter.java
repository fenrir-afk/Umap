package com.example.umap.adapters;


import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umap.R;
import com.example.umap.data.models.MainData;
import com.example.umap.data.room.RoomDB;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>{
    private List<MainData> dataList;
    private RoomDB database;
    private Activity context;
    public MainAdapter(Activity context, List<MainData> dataList) {
        this.context = context;
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_main,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        MainData data = dataList.get(position);
        database = RoomDB.getInstance(context);
        holder.longitude.setText(data.getLongitude().toString());
        holder.latitude.setText(data.getLatitude().toString());
        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainData d = dataList.get(holder.getAdapterPosition());
                database.userDao().delete(d);
                int position = holder.getAdapterPosition();
                dataList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,dataList.size());

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView latitude;
        TextView longitude;
        ImageView btEdit,btDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            latitude = itemView.findViewById(R.id.latitude);
            longitude = itemView.findViewById(R.id.Longitude);
            btEdit = itemView.findViewById(R.id.bt_edit);
            btDelete = itemView.findViewById(R.id.bt_delete);
        }
    }
}
