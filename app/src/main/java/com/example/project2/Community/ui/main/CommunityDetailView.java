package com.example.project2.Community.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.project2.R;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CommunityDetailView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment community_detail_view.
     */
    // TODO: Rename and change types and number of parameters
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
        View v = inflater.inflate(R.layout.fragment_community_detail_view, container, false);

        TextView idV = v.findViewById(R.id.cm_detail_txt_id);
        TextView contextV = v.findViewById(R.id.cm_detail_txt_context);
        TextView nameV = v.findViewById(R.id.cm_detail_txt_name);
        ImageView profile_imgV = v.findViewById(R.id.cm_detail_img_coverPhoto);

        String id = getArguments().getString("id");
        String name = getArguments().getString("name");
        String context_ = getArguments().getString("context");
        int profile_img = getArguments().getInt("profile_img");

        ListView commentList = v.findViewById(R.id.cm_detail_container_listView);
        com.example.project2.Community.listView.listViewAdapter commentAdapter = new com.example.project2.Community.listView.listViewAdapter();
        commentList.setAdapter(commentAdapter);
        commentAdapter.addItem("@something","이름","Color-coded labels help you categorize and filter your issues (just like labels in email)","");
        commentAdapter.addItem("@effe","강아지","One assignee is responsible for working on the issue at any given time","");
        commentAdapter.setReverse(true);
        commentAdapter.notifyDataSetChanged();

        idV.setText(id);
        nameV.setText(name);
        contextV.setText(context_);
        profile_imgV.setImageResource(profile_img);

        LinearLayout.LayoutParams layoutScale = new LinearLayout.LayoutParams(
                500, 500);
        LinearLayout ir = v.findViewById(R.id.cm_detail_container_view);
        /*
        ImageView im = new ImageView(v.getContext());
        im.setImageResource(R.drawable.dog);
        im.setVisibility(View.VISIBLE);
        im.setScaleType(ImageView.ScaleType.FIT_XY);
        im.setLayoutParams(layoutScale);
        ir.addView(im);
        */
        FrameLayout gallary = v.findViewById(R.id.cm_detail_container_gallary);
        View gView = inflater.inflate(R.layout.fragment_community_detail_gallary2x2,null);
        gallary.addView(gView);

        return v;
    }
}