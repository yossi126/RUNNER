package com.example.runner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runner.R;
import com.example.runner.data.Splits;

import java.util.List;

public class SplitsRvAdapter extends RecyclerView.Adapter<SplitsRvAdapter.ViewHolder>{

    private Context context;
    private List<Splits> splitsList;

    public SplitsRvAdapter(Context context, List<Splits> splitsList){
        this.context = context;
        this.splitsList = splitsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.split_item_table, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Splits splits = splitsList.get(position);
        holder.kmNumber.setText(String.valueOf(splits.getKm()));
        holder.splitTime.setText(splits.getTime());
    }

    @Override
    public int getItemCount() {
        return splitsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView kmNumber,splitTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            kmNumber = itemView.findViewById(R.id.kmNumber);
            splitTime = itemView.findViewById(R.id.splitTime);
        }
    }
}
