package com.example.project2.Community.ui.main;

import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.project2.Community.functions.loadImage;
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

import java.io.File;
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

        //사용할 뷰 초기화
        TextView idV = view.findViewById(R.id.cm_detail_txt_id); //펫 이름
        TextView contextV = view.findViewById(R.id.cm_detail_txt_context); //글 내용
        TextView nameV = view.findViewById(R.id.cm_detail_txt_name); //사용자 이름
        ImageView profile_imgV = view.findViewById(R.id.cm_detail_img_coverPhoto); //프로필 사진
        FrameLayout gallary = view.findViewById(R.id.cm_detail_container_gallary); //게시글 사진
        RecyclerView listView = view.findViewById(R.id.cm_detail_write_container_recyclerView); //댓글 목록
        FloatingActionButton floatBtn = view.findViewById(R.id.cm_detail_view_btn_floating); //글 작성 버튼

        //이전 게시글에서 가져온 정보들 저장
        String dogName = getArguments().getString("dogName"); //펫 이름
        String userName = getArguments().getString("userName"); //사용자 이름
        String context_ = getArguments().getString("context"); //글 내용
        String profile_img = getArguments().getString("profileImage"); //프로필 사진 경로

        userUid = getArguments().getString("userUid"); //부모 게시글 유저 아이디
        articleUid = getArguments().getString("articleUid"); //부모 게시글 게시글 아이디

        //부모 게시글의 정보들을 초기화
        idV.setText(dogName); //펫 이름
        nameV.setText(userName); //사용자 이름
        contextV.setText(context_); //글 내용

        getPhotoes(gallary, inflater);

        //부모 게시글의 프로필 사진을 표시
        FirebaseStorage fbs = FirebaseStorage.getInstance();
        StorageReference fbsRef = fbs.getReference();
        Glide.with(getContext().getApplicationContext())
                .load(profile_img)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) //디스크에 캐시하도록 설정
                .thumbnail(0.1f) //실제 사진의 10%크기만
                .placeholder(new ColorDrawable(Color.parseColor("#D1D1D1"))) //로딩중일 때에 표시되는 임시 이미지
                .into(profile_imgV);

        //댓글 목록 초기화
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(adt);
        updateList();

        //글 작성 버튼 눌렀을시에 작동
        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //전달할 정보 초기화
                FragmentManager fm = getParentFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putInt("mode", 1); //1은 댓글모드, 0은 글작성모드
                bundle.putString("userUid", userUid); //부모 게시글의 유저 아이디
                bundle.putString("articleUid", articleUid); //부모 게시글의 게시글 아이디
                //글 작성 프래그먼트를 띄움
                CommunityDetailWrite cdw = new CommunityDetailWrite();
                cdw.setArguments(bundle); //위에서 지정한 정보들 글 작성 프래그먼트로 전달
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_in_top, R.anim.slide_out_top)
                        .add(R.id.container, cdw)
                        .addToBackStack("frag_communityWrite")
                        .commit();
            }
        });

        return view;
    }

    //게시글 사진을 DB로부터 가져와서 표시함
    //FrameLayout       gallary     : 이미지뷰를 포함한 프래그먼트가 담길 뷰
    //LayoutInflater    inflater    : gallary에 프래그먼트를 붙이기 위한 인플레이터터
    public void getPhotoes(FrameLayout gallary, LayoutInflater inflater) {
        //사용할 DB 초기화
        communityDB = db.collection("community").document(userUid).collection("article").document(articleUid);

        //#1 사진 경로를 구하기 위해 게시글 정보로 접근
        communityDB.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //접근이 성공하면 게시글 정보에서 사진 경로를 구함
                ArrayList<String> photoAddr = getArguments().getStringArrayList("images"); //사진 경로들이 담긴 배열
                ArrayList<ImageView> imageViews = new ArrayList<>(); //사진이 담길 이미지뷰 배열
                int photoNum = photoAddr.size(); //게시글의 사진 개수
                View gView = null; //이미지 뷰가 포함된 프래그먼트

                gallary.setVisibility(View.GONE);
                if (photoNum == 1) {
                    gView = inflater.inflate(R.layout.fragment_community_detail_gallary1x1, null);
                    imageViews.add(gView.findViewById(R.id.cm_detail_view_gallary_1x1_1));

                    Glide.with(getContext().getApplicationContext()).load(photoAddr.get(0)).dontTransform().into(imageViews.get(0));
                    //#2
                    imageViews.get(0).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder dialogbuider = new AlertDialog.Builder(getContext());
                            View dialogView = inflater.inflate(R.layout.fragment_community_popup_image, null);
                            dialogbuider.setView(dialogView);
                            ImageView im = dialogView.findViewById(R.id.cm_dialog_img_popup);
                            Glide.with(getContext().getApplicationContext()).load(photoAddr.get(0)).into(im);
                            AlertDialog dialog = dialogbuider.create();
                            dialog.show();
                        }
                    });
                    gallary.setVisibility(View.VISIBLE);
                    gallary.addView(gView);
                } else {
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
                        Glide.with(getContext().getApplicationContext()).load(photoAddr.get(i)).into(imageViews.get(i));
                        imageViews.get(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder dialogbuider = new AlertDialog.Builder(getContext());
                                View dialogView = inflater.inflate(R.layout.fragment_community_popup_image, null);
                                dialogbuider.setView(dialogView);
                                ImageView im = dialogView.findViewById(R.id.cm_dialog_img_popup);
                                Glide.with(getContext().getApplicationContext()).load(photoAddr.get(innerAI)).into(im);
                                AlertDialog dialog = dialogbuider.create();
                                dialog.show();
                            }
                        });
                        gallary.setVisibility(View.VISIBLE);
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
        FirebaseStorage storageDB = FirebaseStorage.getInstance();
        StorageReference storageRef = storageDB.getReference();

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
                                            Log.wtf("e", task.getResult().getString("content"));

                                            recyclerClass tmpItem = new recyclerClass();
                                            tmpItem.setProfileImage(profileImage);
                                            tmpItem.setContext(result.getString("content"));
                                            tmpItem.setUpTime(result.getTimestamp("uptime"));
                                            tmpItem.setMyName(userName);
                                            tmpItem.setDogName("&" + dogName);
                                            tmpItem.setPhotoAddr((ArrayList<String>) result.get("photoAddr"));
                                            tmpItem.setContentImage(new ArrayList<>());

                                            if (!tmpItem.getPhotoAddr().isEmpty()) {
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
                                                    File imageCache = new File(getContext().getCacheDir(), fileName + fileType);

                                                    if (!imageCache.exists()) {
                                                        //캐싱된 이미지가 아직 존재하지 않을 경우
                                                        String innerFileType = fileType;
                                                        storageRef.child(i).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                loadImage loadImage = new loadImage(getContext().getCacheDir(), uri.toString(), fileName, innerFileType);
                                                                loadImage.execute();
                                                            }
                                                        });
                                                    } else {
                                                        File imageCacheList = new File(getContext().getCacheDir().toString());
                                                        for (File j : imageCacheList.listFiles()) {
                                                            if (j.getName().equals(fileName + fileType)) {
                                                                tmpItem.addContentImage(j.getPath());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
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