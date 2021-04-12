package com.example.project2.Main.Search;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project2.Main.MainActivity;
import com.example.project2.R;

public class SearchFragment extends Fragment{

    MainActivity activity;

    static final String[] LIST_MENU = {"LIST1", "LIST2", "LIST3", "LIST4", "LIST5", "LIST6", "LIST7", "LIST8", "LIST9"} ; // 리스트뷰 데이터

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //이 메소드가 호출될떄는 프래그먼트가 엑티비티위에 올라와있는거니깐 getActivity메소드로 엑티비티참조가능
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //이제 더이상 엑티비티 참초가안됨
        activity = null;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //프래그먼트 메인을 인플레이트해주고 컨테이너에 붙여달라는 뜻임

        View view = inflater.inflate(R.layout.activity_sreach_fragment, null) ;

        ArrayAdapter Adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, LIST_MENU);

        ListView listview = (ListView) view.findViewById(R.id.list_sreach) ;
        listview.setAdapter(Adapter) ;

        return view;

    }
}