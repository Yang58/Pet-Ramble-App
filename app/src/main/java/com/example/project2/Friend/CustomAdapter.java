package com.example.project2.Friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.project2.Data.User;
import com.example.project2.R;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<User> arrayList;
    private Context context;

    public CustomAdapter(ArrayList<User> arrayList, Context context){

        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        Glide.with(holder.itemView.getContext())
                .load(arrayList.get(position).getPhotoUrl())
                .apply(new RequestOptions().circleCrop())
                .into(((CustomViewHolder)holder).friend_profile);

        holder.friend_Nickname.setText(arrayList.get(position).getName());
        holder.edit_pet.setText(arrayList.get(position).getPetKind());
        holder.edit_age.setText("( "+String.valueOf(arrayList.get(position).getPetAge()));
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView friend_profile;
        TextView friend_Nickname;
        TextView edit_pet;
        TextView edit_age;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            friend_profile = (ImageView)itemView.findViewById(R.id.friend_item_image);
            friend_Nickname = (TextView)itemView.findViewById(R.id.friend_item_text);
            edit_pet = (TextView)itemView.findViewById(R.id.item_pet);
            edit_age = (TextView)itemView.findViewById(R.id.item_age);
        }
    }
}
