package com.example.project2.Community.listView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2.R;

import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

public class listViewAdapter extends BaseAdapter {
    private ImageView profile_img = null;
    private TextView name_txt, id_txt, context_txt = null;
    private ArrayList<listViewClass> listViewItemList = new ArrayList<listViewClass>();
    private Toast toast = null;
    private boolean isReversed = false;

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        if(convertView==null){
            LayoutInflater inflater =  (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_list_view_item,parent,false);
        }
        profile_img = (ImageView) convertView.findViewById(R.id.profile_img);
        name_txt = (TextView) convertView.findViewById(R.id.name_txt);
        id_txt = (TextView) convertView.findViewById(R.id.id_txt);
        context_txt = (TextView) convertView.findViewById(R.id.context_txt);

        listViewClass listViewItem = listViewItemList.get(position);
        //profile_img.setImageBitmap();
        //new DrawUrlImageTask((ImageView) convertView.findViewById(R.id.profile_img))
        //        .execute("");
        name_txt.setText(listViewItem.getName());
        id_txt.setText(listViewItem.getId());
        context_txt.setText(listViewItem.getContext());

        profile_img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                toast = Toast.makeText(context, "test", Toast.LENGTH_LONG);
                //toast.show();
                return true;
            }
        });
        return convertView;
    }

    public void addItem(String id, String name, String context, String profile_img, Timestamp uptime){
        listViewClass listViewItem = new listViewClass();

        listViewItem.setId(id);
        listViewItem.setName(name);
        listViewItem.setContext(context);
        listViewItem.setProfile_img(profile_img);
        listViewItem.setTimestamp(uptime);
        listViewItemList.add(listViewItem);
        /*
        if(isReversed){
            setReverse(false);
            listViewItemList.add(listViewItem);
            setReverse(true);
        }else{
            listViewItemList.add(listViewItem);
        }
        */
    }
/*
    public void addItem(int num, String id, String name, String context, String profile_img){
        listViewClass listViewItem = new listViewClass();

        listViewItem.setId(id);
        listViewItem.setName(name);
        listViewItem.setContext(context);
        listViewItem.setProfile_img(profile_img);

        listViewItemList.add(num, listViewItem);
    }
*/
    public void sortItems(){
        Log.wtf("정보","호출됨");
        Collections.sort(listViewItemList,new sortTimestamp());
    }

    public void removeAllItem(){
        listViewItemList.clear();
    }

    public void setReverse(boolean b){
        if(b) {
            isReversed = true;
            Collections.reverse(listViewItemList);
        }else{
            isReversed = false;
            Collections.reverse(listViewItemList);
        }
    }
}
