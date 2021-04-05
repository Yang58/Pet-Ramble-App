package com.example.project2.Community.ui.main;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.project2.Community.DB.uploadData;
import com.example.project2.R;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommunityDetailWrite#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommunityDetailWrite extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //전역변수 추가하는 공간
    private final static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference communityDB;
    private DocumentReference usersDB;
    private View view;

    private String mParam1;
    private String mParam2;

    public interface getCallback {
        public void get(DocumentReference d);
    }

    public CommunityDetailWrite() {
        // Required empty public constructor
    }

    public static CommunityDetailWrite newInstance(String param1, String param2) {
        CommunityDetailWrite fragment = new CommunityDetailWrite();
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
        view = inflater.inflate(R.layout.fragment_community_detail_write, container, false);

        //레이아웃 뷰 가져오기
        final Context c = container.getContext();
        final TextView dpId = view.findViewById(R.id.cm_main_txt_id);
        final TextView dpName = view.findViewById(R.id.cm_main_txt_name);
        final ImageView dpCover = view.findViewById(R.id.cm_main_img_cover_picture);
        final FloatingActionButton sndBtn = view.findViewById(R.id.sendButton);
        final TextView tField = view.findViewById(R.id.naeyongField);
        final LinearLayout layout = view.findViewById(R.id.cm_detail_write_container_layout);
        final InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        usersDB = db.collection("users").document(user.getUid());
        communityDB = db.collection("community").document(user.getUid());

        //빈칸을 클릭하면 키보드 내려지게 하기
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputManager.hideSoftInputFromWindow(tField.getWindowToken(),0);
            }
        });

        usersDB.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                dpName.setText(task.getResult().getString("name"));
                dpId.setText("&" + task.getResult().getString("petName"));
            }
        });

        //글 전송
        sndBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vv) {
                if (tField.getText().toString().length() != 0) {
                    uploadData upData = new uploadData(tField.getText().toString(), new Timestamp(System.currentTimeMillis()));
                    //mode가 1이면 댓글 모드, 0이면 일반 글 작성 모드
                    if (getArguments().getInt("mode") == 1) {
                        //부모 게시글의 유저id와 게시글id
                        String userUid = getArguments().getString("userUid");
                        String articleUid = getArguments().getString("articleUid");

                        //댓글이 달릴 메인 게시글 주소
                        DocumentReference relatedDB = db.collection("community").document(userUid).collection("article").document(articleUid);
                        //댓글 게시글의 부모 게시글 주소를 지정해줌
                        upData.setRelatedID(relatedDB);

                        uploadContent(user, upData, new getCallback() {
                            @Override
                            public void get(DocumentReference d) {
                                //글이 업로드 된 이후
                                //부모 게시글에 댓글 주소를 추가해줌
                                relatedDB.update("relatedList", FieldValue.arrayUnion(d));
                                //글쓰기 창 초기화 이후 목록으로 돌아가기
                            }
                        });
                    }else{
                        uploadContent(user, upData, new getCallback() {
                            @Override
                            public void get(DocumentReference d) {
                                //글이 업로드 된 이후
                                Log.i("글쓰기 완료",d.toString());
                            }
                        });
                    }
                    //글쓰기 창 초기화 이후 목록으로 돌아가기
                    tField.setText("");
                    FragmentManager fm = getParentFragmentManager();
                    fm.popBackStack();
                    //키보드 내리기
                    inputManager.hideSoftInputFromWindow(tField.getWindowToken(),0);
                } else {
                    Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                    tField.startAnimation(shake);
                    Toast.makeText(c, "적어도 한글자 이상은 적어야해요!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public void uploadContent(FirebaseUser user, uploadData upData, getCallback inCall) {
        CollectionReference addDB = communityDB.collection("article");
        addDB.add(upData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        inCall.get(documentReference);
                        Log.wtf("정보", "글이 정상적으로 올라감.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.wtf("경고", e.getMessage());
                    }
                });
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}