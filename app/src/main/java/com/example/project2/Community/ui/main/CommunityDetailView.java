package com.example.project2.Community.ui.main;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2.Community.listView.recyclerAdapter;
import com.example.project2.Community.listView.recyclerClass;
import com.example.project2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    public interface getListCallback{
        public void get(Map<String, ArrayList<String>> list);
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

        String dogName = getArguments().getString("dogName");
        String userName = getArguments().getString("userName");
        String context_ = getArguments().getString("context");
        String profile_img = getArguments().getString("profile_img");

        idV.setText(dogName);
        nameV.setText(userName);
        contextV.setText(context_);
        //profile_imgV.setImageResource(profile_img);

        userUid = getArguments().getString("userUid");
        articleUid = getArguments().getString("articleUid");

        listView.setNestedScrollingEnabled(false); //false로 해줘야 scrollview 안에서 recyclerview의 스크롤이 정상동작함
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(adt);
        updateList();

        LinearLayout.LayoutParams layoutScale = new LinearLayout.LayoutParams(
                500, 500);
        /*
        ImageView im = new ImageView(v.getContext());
        im.setImageResource(R.drawable.dog);
        im.setVisibility(View.VISIBLE);
        im.setScaleType(ImageView.ScaleType.FIT_XY);
        im.setLayoutParams(layoutScale);
        ir.addView(im);
        */

        View gView = inflater.inflate(R.layout.fragment_community_detail_gallary2x2,null);
        gallary.addView(gView);


        return view;
    }

    public void getRelatedList(String userUid, String articleUid, getListCallback inCall){
        //유저 UID 기반으로 각 컬렉션에 접근
        communityDB = db.collection("community").document(userUid);
        usersDB = db.collection("users").document(userUid);

        communityDB.collection("article").document(articleUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, ArrayList<String>> tmpList = (Map<String, ArrayList<String>>) task.getResult().get("relatedList");
                inCall.get(tmpList);
            }
        });
    }

    public void updateList(){
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
                Log.i("정보","리스트 제거 완료");

                    getRelatedList(userUid, articleUid, new getListCallback() {
                        @Override
                        public void get(Map<String, ArrayList<String>> list) {
                            try {
                                for (String i : list.keySet()) {
                                    for (String j : list.get(i)) {
                                        getList(i, j.substring(1, j.length() - 1), addList(), new CommunityMain.completeCallback() {
                                            @Override
                                            public void onComplete() {
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                }
                            } catch (NullPointerException e) {
                                progressBar.setVisibility(View.GONE);
                                Log.wtf("경고", e.getMessage());
                            }
                        }
                    });
            }
        });
    }

    public void removeList(CommunityMain.completeCallback inCall){
        adt.removeAll();
        inCall.onComplete();
    }

    public CommunityMain.getCallback addList(){
        CommunityMain.getCallback callback = new CommunityMain.getCallback() {
            @Override
            public void getRecyclerClass(recyclerClass getItem) {
                adt.addItem(getItem);
                adt.sortItems();
                adt.reverseAll();
                adt.notifyDataSetChanged();
            }
        };
        return callback;
    }

    public void getList(String uid, String aid, CommunityMain.getCallback inCall, CommunityMain.completeCallback outCall) {
        //유저 UID 기반으로 각 컬렉션에 접근
        communityDB = db.collection("community").document(uid);
        usersDB = db.collection("users").document(uid);

        //글이 중복으로 들어가면 안되기 때문에 우선 모든 글 제거 후 시작
        recyclerAdapter tmpAdapter = new recyclerAdapter();

        //이름 추출 이후 글 주출
        usersDB.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String userName = task.getResult().getString("name");
                        String dogName = task.getResult().getString("petName");

                        //글 가져오기
                        //이름 로딩보다 글 로딩이 먼저 될 경우를 대비해서 여기 배치
                        communityDB.collection("article").document(aid).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                                DocumentSnapshot result = task.getResult();
                                                recyclerClass tmpItem = new recyclerClass();
                                                tmpItem.setContext(result.getString("content"));
                                                tmpItem.setUpTime(result.getTimestamp("uptime"));
                                                tmpItem.setMyName(userName);
                                                tmpItem.setDogName("&" + dogName);

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