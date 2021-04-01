package com.example.project2.Community.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.project2.Community.DB.uploadData;
import com.example.project2.R;
import com.example.project2.Community.listView.listViewAdapter;
import com.example.project2.Community.listView.listViewClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.List;

import javax.security.auth.callback.Callback;

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
    private final static listViewAdapter adapter = new listViewAdapter();
    private String mParam1;
    private String mParam2;
    private DocumentReference communityDB;
    private DocumentReference usersDB;
    private View view;

    public interface Callback {
        void run();
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
        final ListView lv = view.findViewById(R.id.listViewItemContainer);
        final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.cm_main_container_refresh);
        final Button sndBtn = view.findViewById(R.id.sendButton);
        final TextView tField = view.findViewById(R.id.naeyongField);
        final FloatingActionButton floatBtn = view.findViewById(R.id.cm_main_btn_floating);

        //이름 가져오기
        final String[] userName = new String[1];

        //onComplete 리스너 없이 통신하면 통신상태가 느릴 경우 null이 출력될 수도 있음
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                userName[0] = task.getResult().getString("name");
            }
        });
        ;
        //lv.setDivider(null); <-이거로 글과 글 사이에 나누는 선 설정가능.
        lv.setAdapter(updateList(user));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetail(position);
            }
        });

        //리스트 최상단에 올라와 있을 때만 새로고침 가능하게 설정
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem != 0) {
                    refreshLayout.setEnabled(false);
                } else {
                    refreshLayout.setEnabled(true);
                }
            }
        });

        //만약 글 작성, 글 보기 후 나왔다면
        getParentFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
                public void onBackStackChanged() {
                updateList(user);
            }
        });

        //플로팅 버튼 작동
        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getParentFragmentManager();
                CommunityDetailWrite cdw = new CommunityDetailWrite();
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_bottom,R.anim.slide_out_bottom,R.anim.slide_in_top,R.anim.slide_out_top)
                        .add(R.id.container, cdw)
                        .addToBackStack("frag_communityWrite")
                        .commit();
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lv.setAdapter(updateList(user));
            }
        });

        return view;
    }

    public listViewAdapter updateList(FirebaseUser user) {
        //유저 UID 기반으로 각 컬렉션에 접근
        communityDB = db.collection("community").document(user.getUid());
        usersDB = db.collection("users").document(user.getUid());

        //이 함수가 호출된 뒤 새로고침 중단 위해 참조
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.cm_main_container_refresh);

        //글이 중복으로 들어가면 안되기 때문에 우선 모든 글 제거 후 시작
        adapter.removeAllItem();

        //DB로부터 이름 가져온 후 여기로 꺼내옴
        final String[] myUserName = new String[1];
        final String[] myDogName = new String[1];

        //내 이름 추출
        usersDB.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        myUserName[0] = task.getResult().getString("name");
                        myDogName[0] = task.getResult().getString("petName");
                    }
                });

        //내 글 가져오기
        communityDB.collection("article").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot result : task.getResult()) {
                                adapter.addItem("&" + myDogName[0] , myUserName[0], result.getString("content"), "", result.getTimestamp("uptime"));
                                Log.wtf(result.getId(), "가져오기 성공");
                            }
                        }
                    }
                });

        //친구 글 불러오기
        communityDB.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            //친구 목록
                            List<String> friendList = (List<String>) task.getResult().get("friend");
                            //친구 목록이 비었는지 체크하고 진행
                            try {
                                if (!friendList.isEmpty()) {
                                    //친구 이름 추출되서 임시 저장될 변수
                                    final String[] friendUserName = new String[1];
                                    final String[] friendDogName = new String[1];

                                    for (String friendUID : friendList) {
                                        //친구들 이름 추출
                                        usersDB = db.collection("users").document(friendUID);
                                        usersDB.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                friendUserName[0] = task.getResult().getString("name");
                                                friendDogName[0] = task.getResult().getString("petName");
                                            }
                                        });
                                        Log.wtf("친구정보",friendUserName[0]+friendDogName[0]);

                                        //친구들 글 가져오기
                                        communityDB = db.collection("community").document(friendUID);
                                        communityDB.collection("article").get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot d : task.getResult()) {
                                                                adapter.addItem("&" + friendDogName[0], friendUserName[0], d.getString("content"), "", d.getTimestamp("uptime"));
                                                            }
                                                            adapter.sortItems();
                                                            adapter.setReverse(true);
                                                            adapter.notifyDataSetChanged();
                                                            chkContentCount();
                                                        }
                                                    }
                                                });
                                    }
                                    friendUserName[0] = null;
                                    friendDogName[0] = null;
                                    System.gc();
                                }
                                //친구목록 비었을 시 처리
                            } catch (NullPointerException e) {
                                adapter.sortItems();
                                adapter.setReverse(true);
                                adapter.notifyDataSetChanged();
                                chkContentCount();
                            }
                        }
                    }
                });

        refreshLayout.setRefreshing(false);

        //adapter를 돌려보내서 ListView에 탑재할 수 있도록 함
        return adapter;
    }

    public void chkContentCount() {
        //글이 하나도 없을 경우
        TextView noContent = view.findViewById(R.id.cm_main_txt_noContent);
        Log.wtf("어댑터", adapter.getCount() + "");
        if (adapter.getCount() <= 0) {
            noContent.setVisibility(View.VISIBLE);
        } else {
            noContent.setVisibility(View.GONE);
        }
    }

    public void showDetail(int position) {
        listViewClass obj = (listViewClass) adapter.getItem(position);
        FragmentManager fm = getParentFragmentManager();
        Fragment currentFragment = fm.findFragmentById(R.id.container);
        //다음 프래그먼트로 넘길 값 지정
        Bundle bundle = new Bundle();
        bundle.putString("id", obj.getId());
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