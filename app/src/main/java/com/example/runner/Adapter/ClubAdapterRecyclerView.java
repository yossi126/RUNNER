package com.example.runner.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runner.R;
import com.example.runner.data.User;

import java.util.ArrayList;

public class ClubAdapterRecyclerView  extends RecyclerView.Adapter <ClubAdapterRecyclerView.ViewHolder> {

    private ArrayList<User> userArrayList;

    public ClubAdapterRecyclerView(ArrayList<User> userArrayList) {
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.textView.setText(user.getName());
        if(user.getIsConnected() == true){
            holder.imageView.setImageResource(R.drawable.ic_baseline_online);
        }
        else
        {
            //MAKE "LETS RUN BUTTON" OPACITY AND UNCLICKABLE
            holder.button.setEnabled(false);
            holder.button.setBackgroundColor(Color.parseColor("#e0f2f1"));
        }
    }


    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageView imageView;
        public Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.userName);
            imageView = itemView.findViewById(R.id.statusImage);
            button = itemView.findViewById(R.id.sendBtn);
        }

    }
}
