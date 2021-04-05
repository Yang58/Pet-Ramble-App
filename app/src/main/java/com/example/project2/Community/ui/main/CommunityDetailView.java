package com.example.project2.Community.ui.main;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2.Community.listView.recyclerAdapter;
import com.example.project2.Community.listView.recyclerClass;
import com.example.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityDetailView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityDetailView extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // 전역변수 추가하는 공간
    private final static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private recyclerAdapter adt = new recyclerAdapter();
    private String mParam1;
    private String mParam2;
    private DocumentReference communityDB;
    private DocumentReference usersDB;
    private View view;
    private Map<String, ArrayList<String>> relatedList;
    private String userUid;
    private String articleUid;

    public interface getListCallback {
        public void get(ArrayList<DocumentReference> list);
    }

    public static CommunityDetailView newInstance(String param1, String param2) {
        CommunityDetailView fragment = new CommunityDetailView();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_community_detail_view, container, false);

        TextView idV = view.findViewById(R.id.cm_detail_txt_id);
        TextView contextV = view.findViewById(R.id.cm_detail_txt_context);
        TextView nameV = view.findViewById(R.id.cm_detail_txt_name);
        ImageView profile_imgV = view.findViewById(R.id.cm_detail_img_coverPhoto);
        FrameLayout gallary = view.findViewById(R.id.cm_detail_container_gallary);
        RecyclerView listView = view.findViewById(R.id.cm_detail_write_container_recyclerView);
        LinearLayout ir = view.findViewById(R.id.cm_detail_container_view);
        FloatingActionButton floatBtn = view.findViewById(R.id.cm_detail_view_btn_floating);

        String dogName = getArguments().getString("dogName");
        String userName = getArguments().getString("userName");
        String context_ = getArguments().getString("context");
        String profile_img = getArguments().getString("profileImage");

        idV.setText(dogName);
        nameV.setText(userName);
        contextV.setText(context_);

        FirebaseStorage fbs = FirebaseStorage.getInstance();
        StorageReference fbsRef = fbs.getReference();
        Glide.with(getContext().getApplicationContext()).load(profile_img).into(profile_imgV);

        userUid = getArguments().getString("userUid");
        articleUid = getArguments().getString("articleUid");

        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(adt);
        updateList();

        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getParentFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putInt("mode", 1);
                bundle.putString("userUid", userUid);
                bundle.putString("articleUid", articleUid);
                CommunityDetailWrite cdw = new CommunityDetailWrite();
                cdw.setArguments(bundle);
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_in_top, R.anim.slide_out_top)
                        .add(R.id.container, cdw)
                        .addToBackStack("frag_communityWrite")
                        .commit();
            }
        });

        /*
        LinearLayout.LayoutParams layoutScale = new LinearLayout.LayoutParams(
                500, 500);
        ImageView im = new ImageView(v.getContext());
        im.setImageResource(R.drawable.dog);
        im.setVisibility(View.VISIBLE);
        im.setScaleType(ImageView.ScaleType.FIT_XY);
        im.setLayoutParams(layoutScale);
        ir.addView(im);
        */

        getPhotoes(gallary,inflater);

        return view;
    }

    public void getPhotoes(FrameLayout gallary, LayoutInflater inflater){
        communityDB = db.collection("community").document(userUid).collection("article").document(articleUid);
        communityDB.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<String> photoAddr = (ArrayList<String>) task.getResult().get("photoAddr");
                ArrayList<ImageView> imageViews = new ArrayList<>();
                int photoNum = photoAddr.size();
                View gView = null;

                FirebaseStorage fbs = FirebaseStorage.getInstance();
                StorageReference fbsRef = fbs.getReference();

                gallary.setVisibility(View.GONE);
                if(photoNum==1){
                    gView = inflater.inflate(R.layout.fragment_community_detail_gallary1x1, null);
                    imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_1x1_1));

                    fbsRef.child(photoAddr.get(0)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getContext().getApplicationContext()).load(uri).into(imageViews.get(0));
                            imageViews.get(0).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder dialogbuider = new AlertDialog.Builder(getContext());
                                    View dialogView = inflater.inflate(R.layout.fragment_community_popup_image, null);
                                    dialogbuider.setView(dialogView);
                                    ImageView im = dialogView.findViewById(R.id.cm_dialog_img_popup);
                                    Glide.with(getContext().getApplicationContext()).load(uri).into(im);
                                    AlertDialog dialog = dialogbuider.create();
                                    dialog.show();
                                }
                            });
                            gallary.setVisibility(View.VISIBLE);
                        }
                    });
                    gallary.addView(gView);
                }else {
                    if (photoNum == 2) {
                        gView = inflater.inflate(R.layout.fragment_community_detail_gallary1x2, null);
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_1x2_1));
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_1x2_2));
                        gallary.addView(gView);
                    } else if (photoNum == 3) {
                        gView = inflater.inflate(R.layout.fragment_community_detail_gallary2x1, null);
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_2x1_1));
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_2x1_2));
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_2x1_3));
                        gallary.addView(gView);
                    } else if (photoNum == 4) {
                        gView = inflater.inflate(R.layout.fragment_community_detail_gallary2x2, null);
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_2x2_1));
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_2x2_2));
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_2x2_3));
                        imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_2x2_4));
                        gallary.addView(gView);
                    }

                    for (int i = 0; i < photoNum; i++) {
                        int innerAI = i;
                        fbsRef.child(photoAddr.get(i)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(getContext().getApplicationContext()).load(uri).into(imageViews.get(innerAI));
                                imageViews.get(innerAI).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog.Builder dialogbuider = new AlertDialog.Builder(getContext());
                                        View dialogView = inflater.inflate(R.layout.fragment_community_popup_image, null);
                                        dialogbuider.setView(dialogView);
                                        ImageView im = dialogView.findViewById(R.id.cm_dialog_img_popup);
                                        Glide.with(getContext().getApplicationContext()).load(uri).into(im);
                                        AlertDialog dialog = dialogbuider.create();
                                        dialog.show();
                                    }
                                });
                                gallary.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }
        });
    }

    public void getRelatedList(String userUid, String articleUid, getListCallback inCall) {
        //유저 UID 기반으로 각 컬렉션에 접근
        communityDB = db.collection("community").document(userUid);
        usersDB = db.collection("users").document(userUid);

        communityDB.collection("article").document(articleUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<DocumentReference> tmpList = (ArrayList<DocumentReference>) task.getResult().get("relatedList");
                inCall.get(tmpList);
            }
        });
    }

    public void updateList() {
        RecyclerView listView = view.findViewById(R.id.cm_detail_write_container_recyclerView);
        ProgressBar progressBar = view.findViewById(R.id.cm_detail_view_pg_circle);

        //새로고침 되는동안 잠시 가림
        //listView.setVisibility(View.GONE);

        //새로고침 하면서 글이 중복으로 추가되는 것을 막기 위해서
        //기존 리스트를 삭제
        removeList(new CommunityMain.completeCallback() {
            @Override
            public void onComplete() {
                //삭제가 완료되면 새 리스트를 가져옴
                Log.i("정보", "리스트 제거 완료");

                getRelatedList(userUid, articleUid, new getListCallback() {
                    @Override
                    public void get(ArrayList<DocumentReference> list) {
                        if (list.size() == 0) {
                            progressBar.setVisibility(View.GONE);
                        } else {
                            for (DocumentReference i : list) {
                                getList(i, addList(), new CommunityMain.completeCallback() {
                                    @Override
                                    public void onComplete() {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }

    public void removeList(CommunityMain.completeCallback inCall) {
        adt.removeAll();
        inCall.onComplete();
    }

    public CommunityMain.getCallback addList() {
        CommunityMain.getCallback callback = new CommunityMain.getCallback() {
            @Override
            public void getRecyclerClass(recyclerClass getItem) {
                adt.addItem(getItem);
                adt.sortItems();
                adt.notifyDataSetChanged();
            }
        };
        return callback;
    }

    public void getList(DocumentReference docRef, CommunityMain.getCallback inCall, CommunityMain.completeCallback outCall) {
        //유저 UID 기반으로 각 컬렉션에 접근
        //게시글ID(docRef)->article->유저ID
        userUid = docRef.getParent().getParent().getId();
        usersDB = db.collection("users").document(userUid);

        //글이 중복으로 들어가면 안되기 때문에 우선 모든 글 제거 후 시작
        recyclerAdapter tmpAdapter = new recyclerAdapter();

        //이름 추출 이후 글 주출
        usersDB.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String userName = task.getResult().getString("name");
                        String dogName = task.getResult().getString("petName");
                        String profileImage = task.getResult().getString("photoUrl");

                        //글 가져오기
                        //이름 로딩보다 글 로딩이 먼저 될 경우를 대비해서 여기 배치
                        docRef.get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot result = task.getResult();
                                            Log.wtf("e",task.getResult().getString("content"));

                                            recyclerClass tmpItem = new recyclerClass();
                                            tmpItem.setProfileImage(profileImage);
                                            tmpItem.setContext(result.getString("content"));
                                            tmpItem.setUpTime(result.getTimestamp("uptime"));
                                            tmpItem.setMyName(userName);
                                            tmpItem.setDogName("&" + dogName);
                                            tmpItem.setPhotoAddr((ArrayList<String>) result.get("photoAddr"));

                                            //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
                                            //여기서 addList함수의 getRecyclerClass가 실행됨
                                            inCall.getRecyclerClass(tmpItem);
                                            tmpItem = null;
                                        }
                                        outCall.onComplete();
                                    }
                                });
                    }
                });

        //refreshLayout.setRefreshing(false);
    }
}