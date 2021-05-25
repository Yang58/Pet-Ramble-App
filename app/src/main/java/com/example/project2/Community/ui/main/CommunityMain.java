package com.example.project2.Community.ui.main;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.project2.Community.functions.loadImage;
import com.example.project2.Community.listView.recyclerAdapter;
import com.example.project2.Community.listView.recyclerClass;
import com.example.project2.Community.listView.recyclerOnItemClick;
import com.example.project2.R;
import com.example.project2.tensorflowTest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityMain#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityMain extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //전역변수 추가하는 공간
    private final static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private recyclerAdapter adt = new recyclerAdapter();
    private String mParam1;
    private String mParam2;
    private DocumentReference communityDB;
    private DocumentReference usersDB;
    private FirebaseStorage storageDB;
    private View view;

    //리스트의 값을 외부로 뽑기 위해서 사용
    public interface getCallback {
        void getRecyclerClass(recyclerClass getItem);
    }

    //어떤 작업이 완료되기까지 기다리기 위해 사용
    public interface completeCallback {
        void onComplete();
    }

    public CommunityMain() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommunityMain.
     */
    // TODO: Rename and change types and number of parameters
    public static CommunityMain newInstance(String param1, String param2) {
        CommunityMain fragment = new CommunityMain();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_community_main, container, false);

        //레이아웃 뷰 가져오기
        final Context c = container.getContext();
        final TextView dpId = view.findViewById(R.id.cm_main_txt_id);
        final TextView dpName = view.findViewById(R.id.cm_main_txt_name);
        final ImageView dpCover = view.findViewById(R.id.cm_main_img_cover_picture);
        final RecyclerView listView = view.findViewById(R.id.cm_main_list_recyclerContainer);
        final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.cm_main_container_refresh);
        final Button sndBtn = view.findViewById(R.id.sendButton);
        final TextView tField = view.findViewById(R.id.naeyongField);
        final ExtendedFloatingActionButton floatBtn = view.findViewById(R.id.cm_main_btn_floating);
        final ExtendedFloatingActionButton floatBtn2 = view.findViewById(R.id.cm_main_btn_floating2);

        //이름 가져오기
        final String[] userName = new String[1];

        //onComplete 리스너 없이 통신하면 통신상태가 느릴 경우 null이 출력될 수도 있음
        db.collection("Login_user").document(user.getUid()).collection("Info").document("UserInfo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                userName[0] = task.getResult().getString("user_name");
            }
        });

        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(adt);
        updateList();

        adt.setOnItemClickListener(new recyclerOnItemClick() {
            @Override
            public void onClick(int position) {
                recyclerClass item = adt.getItem(position);
                showDetail(item);
            }
        });

        //만약 글 작성, 글 보기 후 나왔다면
        getParentFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                //updateList();
            }
        });

        //플로팅 버튼 작동
        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getParentFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putInt("mode",0);
                CommunityDetailWrite cdw = new CommunityDetailWrite();
                cdw.setArguments(bundle);
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_bottom,R.anim.slide_out_bottom,R.anim.slide_in_top,R.anim.slide_out_top)
                        .add(R.id.container, cdw)
                        .addToBackStack("frag_communityWrite")
                        .commit();
            }
        });

        floatBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
            }
        });

        return view;
    }

    public void listViewFadeIn(){
        RecyclerView listView = view.findViewById(R.id.cm_main_list_recyclerContainer);
        listView.setAlpha(0f);
        listView.setVisibility(View.VISIBLE);

        int shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        listView.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);
    }

    public void updateList(){
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.cm_main_container_refresh);
        RecyclerView listView = view.findViewById(R.id.cm_main_list_recyclerContainer);

        //새로고침 되는동안 잠시 가림
        listView.setVisibility(View.GONE);

        //새로고침 하면서 글이 중복으로 추가되는 것을 막기 위해서
        //기존 리스트를 삭제
        removeList(new completeCallback() {
            @Override
            public void onComplete() {
                //삭제가 완료되면 새 리스트를 가져옴
                Log.i("정보","리스트 제거 완료");
                getList(user.getUid(), addList(), new completeCallback() {
                    @Override
                    public void onComplete() {
                        //글이 하나도 없는지 체크
                        chkContentCount();
                        //어댑터 업데이트
                        adt.notifyDataSetChanged();
                        Log.i("정보","내 글 불러오기 완료");
                    }
                });
                //친구 글 목록 가져오기
                communityDB.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        try {
                            ArrayList<String> result = (ArrayList<String>) task.getResult().get("friend");
                            for (int i = 0; i < result.size(); i++) {
                                final ArrayList<Integer> innerAI = new ArrayList<>();
                                innerAI.add(i);
                                getList(result.get(i), addList(), new completeCallback() {
                                    @Override
                                    public void onComplete() {
                                        if (innerAI.get(0) +1 == result.size()) {
                                            //가림막 해제
                                            listViewFadeIn();
                                            //글이 하나도 없는지 체크
                                            chkContentCount();
                                            //어댑터 업데이트
                                            adt.notifyDataSetChanged();
                                            //새로고침 해제
                                            refreshLayout.setRefreshing(false);
                                            Log.wtf("진행도", (innerAI.get(0) +1) + "/" + result.size());
                                        }


                                    }
                                });
                            }
                        }catch(NullPointerException e){
                            //가림막 해제
                            listViewFadeIn();
                            //새로고침 해제
                            refreshLayout.setRefreshing(false);
                            Log.wtf("경고",e.getMessage());
                        }
                    }
                });
            }
        });
    }

    public void removeList(completeCallback inCall){
        adt.removeAll();
        inCall.onComplete();
    }

    public getCallback addList(){
        getCallback callback = new getCallback() {
            @Override
            public void getRecyclerClass(recyclerClass getItem) {
                adt.addItem(getItem);
                adt.sortItems();
                adt.reverseAll();
            }
        };
        return callback;
    }

    public void getList(String uid, getCallback inCall, completeCallback outCall) {
        //유저 UID 기반으로 각 컬렉션에 접근
        communityDB = db.collection("community").document(uid);
        DocumentReference usersDB = db.collection("Login_user").document(user.getUid()).collection("Info").document("UserInfo");
        DocumentReference petDB = db.collection("Login_user").document(user.getUid()).collection("Info").document("PetInfo");
        storageDB = FirebaseStorage.getInstance();
        StorageReference storageRef = storageDB.getReference();

        final String[] dogName=new String[1];
        petDB.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                dogName[0] = task.getResult().getString("petName");
            }
        });
        //이름 추출 이후 글 주출
        usersDB.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String userName = task.getResult().getString("user_nickname");
                        String profileImage = task.getResult().getString("user_profile");

                        //글 가져오기
                        //이름 로딩보다 글 로딩이 먼저 될 경우를 대비해서 여기 배치
                        communityDB.collection("article").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot result : task.getResult()) {
                                                final recyclerClass tmpItem = new recyclerClass();
                                                tmpItem.setProfileImage(profileImage);
                                                tmpItem.setContext(result.getString("content"));
                                                tmpItem.setUpTime(result.getTimestamp("uptime"));
                                                tmpItem.setMyName(userName);
                                                tmpItem.setDogName("&" + dogName[0]);
                                                tmpItem.setUserUid(uid);
                                                tmpItem.setArticleUid(result.getId());
                                                tmpItem.setLikeNum(result.getLong("likeNum").intValue());
                                                tmpItem.setPhotoAddr((ArrayList<String>) result.get("photoAddr"));
                                                tmpItem.setContentImage(new ArrayList<>());

                                                if(!tmpItem.getPhotoAddr().isEmpty()) {
                                                    for (String i : tmpItem.getPhotoAddr()) {
                                                        //이미지 파일 캐싱
                                                        String fileName = String.valueOf(i.hashCode());
                                                        String fileType = null;
                                                        if (i.contains(".jpg")) {
                                                            fileType = ".JPG";
                                                        } else if (i.contains(".png")) {
                                                            fileType = ".PNG";
                                                        } else if (i.contains(".gif")) {
                                                            fileType = ".GIF";
                                                        }
                                                        File imageCache = new File(getContext().getCacheDir(), fileName+fileType);

                                                        if (!imageCache.exists()) {
                                                            //캐싱된 이미지가 아직 존재하지 않을 경우
                                                            String innerFileType = fileType;
                                                            storageRef.child(i).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    loadImage loadImage = new loadImage(getContext().getCacheDir(), uri.toString(), fileName, innerFileType);
                                                                    loadImage.execute();
                                                                    tmpItem.addContentImage(getContext().getCacheDir().toString()+"/"+fileName+innerFileType);
                                                                }
                                                            });
                                                        } else {
                                                            File imageCacheList = new File(getContext().getCacheDir().toString());
                                                            for (File j : imageCacheList.listFiles()) {
                                                                if(j.getName().equals(fileName+fileType)) {
                                                                    tmpItem.addContentImage(j.getPath());

                                                                    Log.wtf("이미지 캐싱", j.getPath());
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
                                                //여기서 addList함수의 getRecyclerClass가 실행됨
                                                inCall.getRecyclerClass(tmpItem);
                                                Log.wtf("상태", "완전완료");
                                            }
                                            outCall.onComplete();
                                        }
                                    }
                                });
                    }
                });



        File file = new File(getContext().getCacheDir().toString());
        File[] files = file.listFiles();

        for (File tempFile : files) {
            Log.wtf("MyTag", tempFile.getName());
        }

        //refreshLayout.setRefreshing(false);
    }

    public void chkContentCount() {
        //글이 하나도 없을 경우
        TextView noContent = view.findViewById(R.id.cm_main_txt_noContent);
        Log.wtf("어댑터", adt.getItemCount() + "");
        if (adt.getItemCount() <= 0) {
            noContent.setVisibility(View.VISIBLE);
        } else {
            noContent.setVisibility(View.GONE);
        }
    }

    public void showDetail(recyclerClass item) {
        FragmentManager fm = getParentFragmentManager();
        Fragment currentFragment = fm.findFragmentById(R.id.container);
        //다음 프래그먼트로 넘길 값 지정
        Bundle bundle = new Bundle();
        bundle.putString("profileImage", item.getProfileImage());
        bundle.putString("dogName", item.getDogName());
        bundle.putString("userName", item.getMyName());
        bundle.putString("context", item.getContext());
        bundle.putString("uptime", item.getUpTime().toString());
        bundle.putString("userUid", item.getUserUid());
        bundle.putString("articleUid", item.getArticleUid());
        bundle.putStringArrayList("images", item.getContentImage());
        bundle.putInt("likeNum", item.getLikeNum());
        CommunityDetailView cdv = new CommunityDetailView();
        //다음 프래그먼트에 값 붙이기
        cdv.setArguments(bundle);
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.container, cdv)
                .addToBackStack("frag_communityMain")
                .commit();
    }
}