package com.example.project2.Community.listView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.*;

import com.example.project2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class recyclerAdapter extends Adapter<recyclerAdapter.viewHolder> implements recyclerOnItemClick {

    private ArrayList<recyclerClass> itemList = new ArrayList<recyclerClass>();
    private recyclerOnItemClick onItemClickListener;

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_view_item,parent,false);
        return new viewHolder(view,this);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.onHold(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onClick(int position) {
        if(onItemClickListener!=null) {
            onItemClickListener.onClick(position);
        }
    }

    public void setOnItemClickListener(recyclerOnItemClick listener){
        onItemClickListener = listener;
    }

    public void addItem(recyclerClass item){
        itemList.add(item);
    }

    public recyclerClass getItem(int position){
        return itemList.get(position);
    }

    public void removeAll(){
        itemList.clear();
    }

    public void sortItems(){
        Log.wtf("정보","호출됨");
        Collections.sort(itemList ,new sortTimestamp());
    }

    public void reverseAll(){
        Collections.reverse(itemList);
    }

    public class viewHolder extends ViewHolder{
        private ImageView coverImg;
        private TextView nameTxt;
        private TextView dogNameTxt;
        private TextView contextTxt;
        private TextView uptimeTxt;

        public viewHolder(@NonNull View itemView, recyclerOnItemClick listener) {
            super(itemView);

            //참조할 객체들
            coverImg = (ImageView) itemView.findViewById(R.id.profile_img);
            nameTxt = (TextView) itemView.findViewById(R.id.name_txt);
            dogNameTxt = (TextView) itemView.findViewById(R.id.id_txt);
            contextTxt = (TextView) itemView.findViewById(R.id.context_txt);
            uptimeTxt = (TextView) itemView.findViewById(R.id.uptime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener!=null){
                        listener.onClick(position);
                    }
                }
            });
        }

        public void onHold(recyclerClass item){
            //coverImg
            nameTxt.setText(item.getMyName());
            dogNameTxt.setText(item.getDogName());
            contextTxt.setText(item.getContext());
            SimpleDateFormat transTime = new SimpleDateFormat("yy-MM-dd HH:mm");
            uptimeTxt.setText(transTime.format(item.getUpTime().toDate()));
        }
    }
}
