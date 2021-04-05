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
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

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

    public ArrayList<recyclerClass> getItemList() {
        return itemList;
    }

    public void setOnItemClickListener(recyclerOnItemClick listener){
        onItemClickListener = listener;
    }

    public void addItem(recyclerClass item){
        itemList.add(item);
        for(recyclerClass i : itemList)
        Log.wtf("목록",i.getContext());
    }

    public recyclerClass getItem(int position){
        return itemList.get(position);
    }

    public void removeAll(){
        itemList.clear();
    }

    public void sortItems(){
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
            SimpleDateFormat transTimeToAll = new SimpleDateFormat("yy년 MM월 dd일 HH시 mm분");
            SimpleDateFormat transTimeToDay = new SimpleDateFormat("dd일 전");
            SimpleDateFormat transTimeToHour = new SimpleDateFormat("HH시간 전");
            SimpleDateFormat transTimeToMinute = new SimpleDateFormat("mm분 전");
            SimpleDateFormat transTimeToSecond = new SimpleDateFormat("ss초 전");
            try {
                long nowTime = TimeUnit.NANOSECONDS.toSeconds(Timestamp.now().getNanoseconds());
                long uploadTime = TimeUnit.NANOSECONDS.toSeconds(item.getUpTime().getNanoseconds());
                long allFormat = TimeUnit.NANOSECONDS.toSeconds(4000 * 60 * 60 * 24 * 7);
                long dayFormat = TimeUnit.NANOSECONDS.toSeconds(4000 * 60 * 60 * 24);
                long hourFormat = TimeUnit.NANOSECONDS.toSeconds(4000 * 60 * 60);
                long minFormat = TimeUnit.NANOSECONDS.toSeconds(4000 * 60);
                long secFormat = TimeUnit.NANOSECONDS.toSeconds(4000);
                if (nowTime - uploadTime > allFormat) {
                    uptimeTxt.setText(transTimeToAll.format(item.getUpTime().toDate()));
                } else if (nowTime - uploadTime > dayFormat) {
                    uptimeTxt.setText(transTimeToDay.format(item.getUpTime().toDate()));
                } else if (nowTime - uploadTime > hourFormat) {
                    uptimeTxt.setText(transTimeToHour.format(item.getUpTime().toDate()));
                } else if (nowTime - uploadTime > minFormat) {
                    uptimeTxt.setText(transTimeToMinute.format(item.getUpTime().toDate()));
                } else {
                    uptimeTxt.setText(transTimeToSecond.format(item.getUpTime().toDate()));
                }
                Log.wtf("?", Timestamp.now().getNanoseconds() + "," + TimeUnit.NANOSECONDS.toSeconds(Timestamp.now().getNanoseconds()) + "");
            } catch (NullPointerException e){
                Log.wtf("e", item.toString());
            }
        }
    }
}
