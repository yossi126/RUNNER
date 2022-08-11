package com.example.runner.Adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runner.R;
import com.example.runner.data.User;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ClubAdapterRecyclerView  extends RecyclerView.Adapter <ClubAdapterRecyclerView.ViewHolder> {

    private ArrayList<User> userArrayList;
    private final OnContactClickListener contactClickListener;

    public ClubAdapterRecyclerView(ArrayList<User> userArrayList, OnContactClickListener onContactClickListener) {
        this.userArrayList = userArrayList;
        this.contactClickListener = onContactClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view, contactClickListener);
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
            holder.letsRunBtn.setEnabled(false);
            holder.letsRunBtn.setBackgroundColor(Color.parseColor("#e0f2f1"));
        }
    }


    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    // listener to pass uid to the club activity
    public interface OnContactClickListener {
        void onContactClick(String uid);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textView;
        public ImageView imageView;
        public Button letsRunBtn;
        OnContactClickListener onContactClickListener;

        public ViewHolder(@NonNull View itemView,OnContactClickListener onContactClickListener) {
            super(itemView);
            textView = itemView.findViewById(R.id.userName);
            imageView = itemView.findViewById(R.id.statusImage);
            letsRunBtn = itemView.findViewById(R.id.letsRunBtn);
            letsRunBtn.setOnClickListener(this);
            this.onContactClickListener = onContactClickListener;
        }

        @Override
        public void onClick(View view) {
            //pass the uid on the clicked view holder (user) to the club activity
            onContactClickListener.onContactClick(userArrayList.get(getAdapterPosition()).getUid());
        }
    }
}
