package com.example.project2.Community.listView;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.*;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class recyclerAdapter extends Adapter<recyclerAdapter.viewHolder> implements recyclerOnItemClick {

    private ArrayList<recyclerClass> itemList = new ArrayList<recyclerClass>();
    private recyclerOnItemClick onItemClickListener;
    private final static FirebaseStorage db = FirebaseStorage.getInstance();
    private final static StorageReference dbRef = db.getReference();

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
        private ImageView profileImage;
        private TextView nameTxt;
        private TextView dogNameTxt;
        private TextView contextTxt;
        private TextView uptimeTxt;
        private FrameLayout gallary;

        public viewHolder(@NonNull View itemView, recyclerOnItemClick listener) {
            super(itemView);

            //참조할 객체들
            profileImage = (ImageView) itemView.findViewById(R.id.profile_img);
            nameTxt = (TextView) itemView.findViewById(R.id.name_txt);
            dogNameTxt = (TextView) itemView.findViewById(R.id.id_txt);
            contextTxt = (TextView) itemView.findViewById(R.id.context_txt);
            uptimeTxt = (TextView) itemView.findViewById(R.id.uptime);
            gallary = (FrameLayout) itemView.findViewById(R.id.item_gallary_container);

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

        public void onHold(recyclerClass item) {
            LayoutInflater inflater = (LayoutInflater) itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            nameTxt.setText(item.getMyName());
            dogNameTxt.setText(item.getDogName());
            contextTxt.setText(item.getContext());
            Glide.with(itemView.getContext().getApplicationContext()).load(item.getProfileImage()).into(profileImage);
            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialogbuider = new AlertDialog.Builder(itemView.getContext());
                    View dialogView = inflater.inflate(R.layout.fragment_community_popup_image, null);
                    dialogbuider.setView(dialogView);
                    ImageView im = dialogView.findViewById(R.id.cm_dialog_img_popup);
                    Glide.with(itemView.getContext().getApplicationContext()).load(item.getProfileImage()).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(im);
                    AlertDialog dialog = dialogbuider.create();
                    dialog.show();
                }
            });

            try {
                ArrayList<ImageView> imageViews = new ArrayList<>();
                int photoNum = item.getPhotoAddrSize();
                View gView = null;
                gallary.setVisibility(View.GONE);
                if(photoNum==1){
                    gView = inflater.inflate(R.layout.fragment_community_detail_gallary1x1, null);
                    imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_1x1_1));

                    if(!item.getContentImage().isEmpty()) {
                        imageViews.get(0).setImageBitmap(BitmapFactory.decodeFile(item.getContentImage().get(0)));
                        //이미지 클릭시 확대되는 부분
                        imageViews.get(0).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //이미지가 보여줄 다이얼로그를 구축하는 부분
                                AlertDialog.Builder dialogbuider = new AlertDialog.Builder(itemView.getContext());
                                View dialogView = inflater.inflate(R.layout.fragment_community_popup_image, null);
                                dialogbuider.setView(dialogView);
                                //이미지가 보여지는 부분
                                ImageView im = dialogView.findViewById(R.id.cm_dialog_img_popup);
                                im.setImageBitmap(BitmapFactory.decodeFile(item.getContentImage().get(0)));
                                AlertDialog dialog = dialogbuider.create();
                                dialog.show();
                            }
                        });
                    }else {
                        dbRef.child(item.getPhotoAddr(0)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //이미지가 보여지는 부분
                                Glide.with(itemView.getContext().getApplicationContext())
                                        .load(uri)
                                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                        .thumbnail(0.1f)
                                        .placeholder(new ColorDrawable(Color.parseColor("#D1D1D1")))
                                        .into(imageViews.get(0));
                                //이미지 클릭시 확대되는 부분
                                imageViews.get(0).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //이미지가 보여줄 다이얼로그를 구축하는 부분
                                        AlertDialog.Builder dialogbuider = new AlertDialog.Builder(itemView.getContext());
                                        View dialogView = inflater.inflate(R.layout.fragment_community_popup_image, null);
                                        dialogbuider.setView(dialogView);
                                        //이미지가 보여지는 부분
                                        ImageView im = dialogView.findViewById(R.id.cm_dialog_img_popup);
                                        Glide.with(itemView.getContext().getApplicationContext())
                                                .load(uri)
                                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                                .into(im);
                                        AlertDialog dialog = dialogbuider.create();
                                        dialog.show();
                                    }
                                });
                            }
                        });
                    }
                    gallary.addView(gView);
                    gallary.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,600));
                    gallary.setVisibility(View.VISIBLE);
                }else {
                    if (photoNum == 2) {
                        gView = inflater.inflate(R.layout.fragment_community_detail_gallary1x2, null);
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_1x2_1));
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_1x2_2));
                        gallary.addView(gView);
                        gallary.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600));
                    } else if (photoNum == 3) {
                        gView = inflater.inflate(R.layout.fragment_community_detail_gallary2x1, null);
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_2x1_1));
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_2x1_2));
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_2x1_3));
                        gallary.addView(gView);
                        //gallary.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600));
                    } else if (photoNum == 4) {
                        gView = inflater.inflate(R.layout.fragment_community_detail_gallary2x2, null);
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_2x2_1));
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_2x2_2));
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_2x2_3));
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_2x2_4));
                        gallary.addView(gView);
                        gallary.setVisibility(View.VISIBLE);
                        //gallary.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600));
                    }

                    for (int i = 0; i < photoNum; i++) {
                        int innerAI = i;
                        if(!item.getContentImage().isEmpty()) {
                            imageViews.get(i).setImageBitmap(BitmapFactory.decodeFile(item.getContentImage().get(i)));
                            //이미지 클릭시 확대되는 부분
                            imageViews.get(i).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //이미지가 보여줄 다이얼로그를 구축하는 부분
                                    AlertDialog.Builder dialogbuider = new AlertDialog.Builder(itemView.getContext());
                                    View dialogView = inflater.inflate(R.layout.fragment_community_popup_image, null);
                                    dialogbuider.setView(dialogView);
                                    //이미지가 보여지는 부분
                                    ImageView im = dialogView.findViewById(R.id.cm_dialog_img_popup);
                                    im.setImageBitmap(BitmapFactory.decodeFile(item.getContentImage().get(innerAI)));
                                    AlertDialog dialog = dialogbuider.create();
                                    dialog.show();
                                }
                            });
                        }else {
                            dbRef.child(item.getPhotoAddr(i)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //이미지가 보여지는 부분
                                    Glide.with(itemView.getContext().getApplicationContext())
                                            .load(uri)
                                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                            .thumbnail(0.1f)
                                            .placeholder(new ColorDrawable(Color.parseColor("#D1D1D1")))
                                            .into(imageViews.get(innerAI));
                                    //이미지를 확대하는 부분
                                    imageViews.get(innerAI).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //이미지가 보여질 다이얼로그를 구축하는 부분
                                            AlertDialog.Builder dialogbuider = new AlertDialog.Builder(itemView.getContext());
                                            View dialogView = inflater.inflate(R.layout.fragment_community_popup_image, null);
                                            dialogbuider.setView(dialogView);
                                            //이미지가 보여지는 부분
                                            ImageView im = dialogView.findViewById(R.id.cm_dialog_img_popup);
                                            Glide.with(itemView.getContext().getApplicationContext())
                                                    .load(uri)
                                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                                    .into(im);
                                            AlertDialog dialog = dialogbuider.create();
                                            dialog.show();
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            }catch (Exception e){
                gallary.setVisibility(View.GONE);
                e.printStackTrace();
            }

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
            } catch (NullPointerException e){
            }
        }
    }
}
