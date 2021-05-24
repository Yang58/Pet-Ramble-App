package com.example.project2.Friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2.FirebaseDB.WalkingDB;
import com.example.project2.R;

import java.util.ArrayList;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.CustomViewHolder> {

    private ArrayList<WalkingDB> arrayList;
    private Context context;

    //어댑터에서 액티비티 액션을 가져올 때 context가 필요한데 어댑터에는 context가 없다.
    //선택한 액티비티에 대한 context를 가져올 때 필요하다.

    public RankingAdapter(ArrayList<WalkingDB> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    //실제 리스트뷰가 어댑터에 연결된 다음에 뷰 홀더를 최초로 만들어낸다.
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_list, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.nickname.setText(arrayList.get(position).getUser_nick());
        holder.tv_walk_count.setText(arrayList.get(position).getWalking_Count() + "회");
        holder.tv_walk_distance.setText(arrayList.get(position).getWalking_Distance() + "km");
        holder.tv_walk_time_h.setText(arrayList.get(position).getWalking_Time_h() + "H");
        holder.tv_walk_time_m.setText(arrayList.get(position).getWalking_Time_m() + "m");
    }
    // 아이템 총 개수 반환
    @Override
    public int getItemCount() {
        // 삼항 연산자 arrayList가 null이 아니면 arrayList.size()를 가져오고 아니면 0을 가져오라는 의미.
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tv_walk_count;
        TextView tv_walk_distance;
        TextView tv_walk_time_h;
        TextView tv_walk_time_m;
        TextView nickname;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nickname = itemView.findViewById(R.id.tv_Nickname);
            this.tv_walk_count = itemView.findViewById(R.id.tv_count);
            this.tv_walk_distance = itemView.findViewById(R.id.tv_distance);
            this.tv_walk_time_h = itemView.findViewById(R.id.tv_time_h);
            this.tv_walk_time_m = itemView.findViewById(R.id.tv_time_m);
        }
    }
}