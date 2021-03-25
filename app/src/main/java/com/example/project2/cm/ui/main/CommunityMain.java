package com.example.project2.cm.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.project2.R;
import com.example.project2.cm.listView.listViewAdapter;
import com.example.project2.cm.listView.listViewClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        final String UID="test";

        adapter = new listViewAdapter();
        final ListView lv = v.findViewById(R.id.listViewItemContainer);
        //lv.setDivider(null); <-이거로 글과 글 사이에 나누는 선 설정가능.
        lv.setAdapter(adapter);
        adapter.addItem("@test_account", "아무개", "1가나다라마바사아자차카타파하","");
        //adapter.addItem("@recipes4dev", "recipes4dev", "일반적으로 안드로이드 앱에 사용하는 ListView는 단순히 문자열만 표시하기보단 이미지나 버튼 또는 크기가 다른 문자열 등으로 구성하는 경우가 더 많습니다." +
        //        " 단순 문자열만이 아닌 여러 종류의 위젯을 하나의 아이템으로 구성한 ListView를 Custom ListView라고 하며, 안드로이드 앱을 만들 때 가장 많이 사용하는 컴포넌트 중 하나입니다.","");
        adapter.addItem("@test_account", "아무개", "3가나다라마바사아자차카타파하", "");
        adapter.addItem("@test_account", "아무개", "4가나다라마바사아자차카타파하", "");
        adapter.addItem("@test_account", "아무개", "5가나다라마바사아자차카타파하", "");
        adapter.addItem("@test_account", "아무개", "6가나다라마바사아자차카타파하", "");
        adapter.addItem("@test_account", "아무개", "7가나다라마바사아자차카타파하", "");
        adapter.addItem("@test_account", "아무개", "8가나다라마바사아자차카타파하","");
        adapter.addItem("@test_account", "아무개", "9가나다라마바사아자차카타파하", "");
        adapter.addItem("@test_account", "아무개", "0가나다라마바사아자차카타파하", "");
        adapter.addItem("@test_account", "아무개", "가나다라마바사아자차카타파하", "");
        adapter.setReverse(true);
        adapter.notifyDataSetChanged();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetail(position);
            }
        });

        Button sndBtn = v.findViewById(R.id.sendButton);
        sndBtn.setOnClickListener(new View.OnClickListener(){
        TextView tField = v.findViewById(R.id.naeyongField);
            @Override
            public void onClick(View v) {
                if(tField.getText().toString().length()!=0) {
                    //FirebaseStorage storage = FirebaseStorage.getInstance();
                    //StorageReference storageRef = storage.getReference();
                    //StorageReference sr = storageRef.child("KakaoTalk_20181002_210657248.png");
                    DocumentReference db_access = db.collection("user").document(UID);
                    //int cover_picture = getResources().getIdentifier(userInfo.getString("cover_photo"),"drawable", c.getPackageName());

                    db_access.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot d = task.getResult();
                            if(task.isSuccessful()&&d!=null){
                                adapter.addItem(0,"@"+d.getString("id"), d.getString("name"), tField.getText().toString(), "");
                                adapter.notifyDataSetChanged();
                                tField.setText("");
                                lv.requestFocusFromTouch();
                                lv.clearFocus();
                            }
                        }
                    });
                    //adapter.addItem(0,userInfo.getString("id"), userInfo.getString("name"), tField.getText().toString(), sr.toString());
                }else{
                    Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                    tField.startAnimation(shake);
                    Toast.makeText(c,"적어도 한글자 이상은 적어야해요!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }

    public void showDetail(int position) {
        listViewClass obj = (listViewClass)adapter.getItem(position);
        FragmentManager fm = getParentFragmentManager();
        Fragment currentFragment = fm.findFragmentById(R.id.fragment_container);
        Bundle bundle = new Bundle();
        bundle.putString("id", obj.getId());
        bundle.putString("name", obj.getName());
        bundle.putString("context", obj.getContext());
        bundle.putString("profile_img", obj.getProfile_img());
        CommunityDetailView cdv = new CommunityDetailView();
        cdv.setArguments(bundle);
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_container, cdv)
                .addToBackStack("tag1")
                .commit();
    }
}