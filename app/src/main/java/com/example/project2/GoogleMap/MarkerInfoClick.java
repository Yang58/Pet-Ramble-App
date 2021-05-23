package com.example.project2.GoogleMap;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.project2.R;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MarkerInfoClick#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarkerInfoClick extends DialogFragment implements OnStreetViewPanoramaReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static LatLng location;
    private static View view;

    public MarkerInfoClick() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MarkerInfoClick.
     */
    // TODO: Rename and change types and number of parameters
    public static MarkerInfoClick newInstance(String param1, String param2) {
        MarkerInfoClick fragment = new MarkerInfoClick();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_marker_info_click, container, false);
        Bundle bundle = getArguments();
        location = bundle.getParcelable("latlng");
        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment) getChildFragmentManager().findFragmentById(R.id.marker_info_fragment_view);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
        new getResult().execute();
        return view;
    }

    public class getResult extends AsyncTask {

        @Override
        protected Void doInBackground(Object... objects) {
            String getFromApi = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?radius=20&types=point_of_interest&sensor=false&language=ko&key=AIzaSyCqOFbgT-WPnY3yWj3xzOERy8cpj_TQfEM"
                    +"&location="+location.latitude+","+location.longitude;

            URL url = null;
            try {
                url = new URL(getFromApi);
            } catch(MalformedURLException e1) {
                e1.printStackTrace();
            }

            InputStream in = null;
            try {
                Log.wtf("실행","되는중");
                StringBuffer result = new StringBuffer();

                InputStream is = url.openConnection().getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);

                String str ;
                while((str=br.readLine()) != null){

                    result.append(str + "\r\n") ;

                }

                JSONObject jsonObj1 = new JSONObject(result.toString());
                String _results = jsonObj1.getString("results");
                JSONArray jsonArr1 = new JSONArray(_results);
                TextView tv = view.findViewById(R.id.marker_info_txt_placeName);
                tv.setText("주소를 불러올 수 없는 장소에요.");
                String finalName = null;
                int cnt = 0;
                for(int i = 0; i<jsonArr1.length(); i++){
                    JSONObject jsonObj2 = jsonArr1.getJSONObject(i);
                    //이름 추출
                    String _name = jsonObj2.getString("name");
                    String _vicinity = jsonObj2.getString("vicinity");
                    //위치 추출
                    String _geometry = jsonObj2.getString("geometry");
                    JSONObject jsonObj3 = new JSONObject(_geometry);
                    String _location = jsonObj3.getString("location");
                    JSONObject jsonObj4 = new JSONObject(_location);
                    String _latitude = jsonObj4.getString("lat");
                    String _longitude = jsonObj4.getString("lng");
                    LatLng latLng = new LatLng(Double.valueOf(_latitude),Double.valueOf(_longitude));
                    float[] distance= new float[1];
                    Location.distanceBetween(latLng.latitude,latLng.longitude,location.latitude,location.longitude,distance);
                    if(distance[0]<10){
                        cnt++;
                        if(_name.equals(null)){
                            tv.setText(_vicinity);
                        }else {
                            tv.setText(_name);
                        }
                        Log.wtf("asdf2",i +","+ cnt);
                    }else{
                        Log.wtf("asdf",i +","+ cnt);
                        if(i==jsonArr1.length()-1 && cnt == 0){
                            tv = view.findViewById(R.id.marker_info_txt_placeName);
                            if(_name.equals(null)){
                                tv.setText(_vicinity);
                            }else {
                                tv.setText(_name);
                            }
                        }
                    }
                }

            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onStreetViewPanoramaReady(@NonNull StreetViewPanorama streetViewPanorama) {
        LatLng sanFrancisco = location;
        streetViewPanorama.setPosition(sanFrancisco);
        streetViewPanorama.setStreetNamesEnabled(true);
    }
}