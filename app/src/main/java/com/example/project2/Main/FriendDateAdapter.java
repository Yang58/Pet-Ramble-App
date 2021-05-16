package com.example.project2.Main;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;


import com.example.project2.Friend.FriendListItem;

import com.example.project2.R;

import java.util.ArrayList;

public class FriendDateAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<FriendDateItem> listViewItemFriendDate = new ArrayList<FriendDateItem>() ;

    // ListViewAdapter의 생성자
    public FriendDateAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemFriendDate.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_frienddate, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득

        TextView friendTextView = (TextView) convertView.findViewById(R.id.friend_who);
        //TextView dateTextView = (TextView) convertView.findViewById(R.id.friend_date);
        TextView timeTextView = (TextView) convertView.findViewById(R.id.friend_time);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.friend_content);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        FriendDateItem friendDateItem = listViewItemFriendDate.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        /*
        iconImageView.setImageResource(R.drawable.images);
         */
        friendTextView.setText(friendDateItem.getWho());
        timeTextView.setText(friendDateItem.getTime());
        //dateTextView.setText(friendDateItem.getDate());
        titleTextView.setText(friendDateItem.getContent());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemFriendDate.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String who, String time, String content) {
        FriendDateItem item = new FriendDateItem();
        item.setWho(who);

        item.setTime(time);
        item.setContent(content);

        listViewItemFriendDate.add(item);
    }
}
