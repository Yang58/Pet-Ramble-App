package com.example.project2.Community.ui.main;

import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;

import javax.security.auth.callback.Callback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityMain#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityMain extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mParam1;
    private String mParam2;
    private listViewAdapter adapter;
    private ViewGroup vg;

    public interface Callback{
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
        final View v = inflater.inflate(R.layout.fragment_community_main, container, false);
        vg = container;
        final Context c = vg.getContext();
        Callback callback;

        //새로고침 레이아웃
        SwipeRefreshLayout refreshLayout =(SwipeRefreshLayout) v.findViewById(R.id.cm_main_container_refresh);

        //유저설정
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //레이아웃 뷰 가져오기
        TextView dpId = v.findViewById(R.id.cm_main_txt_id);
        TextView dpName = v.findViewById(R.id.cm_main_txt_name);
        ImageView dpCover = v.findViewById(R.id.cm_main_img_cover_picture);
        //이름 가져오기
        final String[] userName = {"a"};
        //onComplete 리스너 없이 통신하면 통신상태가 느릴 경우 null이 출력될 수도 있음
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                userName[0] = task.getResult().getString("name");
                dpName.setText(userName[0]);
                dpId.setText("@"+user.getUid());
            }
        });


        adapter = new listViewAdapter();
        final ListView lv = v.findViewById(R.id.listViewItemContainer);
        //lv.setDivider(null); <-이거로 글과 글 사이에 나누는 선 설정가능.
        lv.setAdapter(adapter);
        updateList(v,adapter,user);

        chkContentCount(v);

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
                if(firstVisibleItem!=0){
                    refreshLayout.setEnabled(false);
                }else{
                    refreshLayout.setEnabled(true);
                }
            }
        });

        Button sndBtn = v.findViewById(R.id.sendButton);
        sndBtn.setOnClickListener(new View.OnClickListener(){
        TextView tField = v.findViewById(R.id.naeyongField);
            @Override
            public void onClick(View vv) {
                if(tField.getText().toString().length()!=0) {
                                uploadData upData = new uploadData(tField.getText().toString(),new Timestamp(System.currentTimeMillis()));
                                uploadContent(user,upData);
                                updateList(v,adapter,user);
                                adapter.notifyDataSetChanged();
                                tField.setText("");
                                lv.requestFocusFromTouch();
                                lv.clearFocus();
                }else{
                    Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                    tField.startAnimation(shake);
                    Toast.makeText(c,"적어도 한글자 이상은 적어야해요!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList(v,adapter,user);
                chkContentCount(v);
            }
        });

        return v;
    }

    public void updateList(View v, listViewAdapter adapter, FirebaseUser user){
        ListView lv = v.findViewById(R.id.listViewItemContainer);
        lv.setAdapter(adapter);
        adapter.removeAllItem();
        //이름 가져오기
        final String[] userName = {"a"};
        //onComplete 리스너 없이 통신하면 통신상태가 느릴 경우 null이 출력될 수도 있음
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                userName[0] = task.getResult().getString("name");
            }
        });
        db.collection("community").document(user.getUid()).collection("article").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot d : task.getResult()){
                                Log.wtf(d.getId(),"가져오기 성공");
                                adapter.addItem("@"+user.getUid(),userName[0],d.getString("content"),"",d.getTimestamp("uptime"));
                            }
                            SwipeRefreshLayout refreshLayout = v.findViewById(R.id.cm_main_container_refresh);
                            refreshLayout.setRefreshing(false);
                            adapter.sortItems();
                            adapter.setReverse(true);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public void uploadContent(FirebaseUser user, uploadData upData){
        CollectionReference addDB = db.collection("community").document(user.getUid()).collection("article");
        addDB.add(upData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.wtf("경고", "정상처리됨");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.wtf("경고",e.getMessage());
                    }
                });
    }

    public void chkContentCount(View v){
        //글이 하나도 없을 경우
        TextView noContent = v.findViewById(R.id.cm_main_txt_noContent);
        Log.wtf("어댑터",adapter.getCount()+"");
        if(adapter.getCount()<=0){
            noContent.setVisibility(View.VISIBLE);
        }else{
            noContent.setVisibility(View.GONE);
        }
    }

    public void showDetail(int position) {
        listViewClass obj = (listViewClass)adapter.getItem(position);
        FragmentManager fm = getParentFragmentManager();
        Fragment currentFragment = fm.findFragmentById(R.id.container);
        Bundle bundle = new Bundle();
        bundle.putString("id", obj.getId());
        bundle.putString("name", obj.getName());
        bundle.putString("context", obj.getContext());
        bundle.putString("profile_img", obj.getProfile_img());
        CommunityDetailView cdv = new CommunityDetailView();
        cdv.setArguments(bundle);
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container, cdv)
                .addToBackStack("tag1")
                .commit();
    }
}