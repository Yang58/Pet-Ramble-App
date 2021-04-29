package com.example.project2.GoogleMap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.project2.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.nio.file.attribute.AclEntryPermission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMarkerClickListener {

    private FragmentActivity mContext;

    private static final String TAG = MapsFragment.class.getSimpleName();
    private GoogleMap mMap;
    private MapView mapView = null;
    private Marker currentMarker = null;
    private boolean isWalkStart = false;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient; // Deprecated된 FusedLocationApi를 대체
    private LocationRequest locationRequest;
    private Location mCurrentLocatiion;

    private final LatLng mDefaultLocation = new LatLng(37.56, 126.97);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000 * 60 * 1;  // 1분 단위 시간 갱신
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000 * 30; // 30초 단위로 화면 갱신

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private LatLng startPoint = null;
    private LatLng endPoint = null;

    //다른 사람 위치 표시
    private static HashMap<String, ArrayList<PolylineOptions>> otherLines = new HashMap<>();
    private static ArrayList<Polyline> otherLinesSaved = new ArrayList<>();
    private static HashMap<String, Marker> otherMarker = new HashMap<>();

    Chronometer mChr;

    public MapsFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        // Fragment 가 Activity에 attach 될 때 호출된다.
        mContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 초기화 해야 하는 리소스들을 여기서 초기화 해준다.
        // getActivity().getApplicationContext() * Fragment 에서 this *
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // Layout 을 inflate 하는 곳이다.
        if (savedInstanceState != null) {
            mCurrentLocatiion = savedInstanceState.getParcelable(KEY_LOCATION);
            CameraPosition mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        final View layout = inflater.inflate(R.layout.activity_maps_fragment, container, false);

        final ExtendedFloatingActionButton st = (ExtendedFloatingActionButton)layout.findViewById(R.id.btn_start); //시작
        final ExtendedFloatingActionButton fi = (ExtendedFloatingActionButton) layout.findViewById(R.id.btn_finish); //종료

        mChr = (Chronometer) layout.findViewById(R.id.chronometer);

        st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 산책 시작 ( 시간체크 칼로리 체크 )
                setWalkState(true);
            }
        });

        fi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 산책 종료 ( DB로 데이터 보낸 후 팝업창 출력 )
                setWalkState(false);
            }
        });

        mapView = (MapView) layout.findViewById(R.id.mapview);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        mapView.getMapAsync(this);

        return layout;
    }

    //타 사용자 위치 표시
    public void showOtherLocation(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = database.getReference().child("mapData");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    //다른 사람의 UID
                    String otherUID = d.getKey();
                    //나를 제외한 다른 사람의 위치 변경이 감지되었을 경우
                    if(otherUID.equals(user.getUid())) continue;
                    //데이터를 가져온다
                    HashMap<String, HashMap<String, Object>> value = (HashMap<String, HashMap<String, Object>>) d.getValue();
                    //시작 좌표
                    float startLat = Float.valueOf(value.get("start").get("latitude").toString());
                    float startLng = Float.valueOf(value.get("start").get("longitude").toString());
                    LatLng startPos = new LatLng(startLat, startLng);
                    //도착 좌표
                    float endLat = Float.valueOf(value.get("end").get("latitude").toString());
                    float endLng = Float.valueOf(value.get("end").get("longitude").toString());
                    LatLng endPos = new LatLng(endLat, endLng);

                    //선 추가
                    PolylineOptions line = new PolylineOptions().add(startPos,endPos).clickable(true).color(Color.RED).width(20);
                    try {
                        otherLines.get(otherUID).add(line);
                    }catch (NullPointerException e){
                        otherLines.put(otherUID, new ArrayList<>());
                        otherLines.get(otherUID).add(line);
                    }
                    otherLinesSaved.add(mMap.addPolyline(otherLines.get(otherUID).get(otherLines.get(otherUID).size()-1)));

                    //마커 추가
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(endPos);
                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.loading3));
                    try {
                        otherMarker.get(otherUID).remove();
                        otherMarker.put(otherUID, mMap.addMarker(marker));
                    }catch(NullPointerException e) {
                        otherMarker.put(otherUID, mMap.addMarker(marker));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //위치정보 업데이트 처리 객체
    public LocationListener loListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference startRef = database.getReference().child("mapData").child(user.getUid()).child("start");
            DatabaseReference endRef = database.getReference().child("mapData").child(user.getUid()).child("end");
//            DatabaseReference startRef = database.getReference().child("mapData").child("asaldfhulaw12u31312").child("start");
//            DatabaseReference endRef = database.getReference().child("mapData").child("asaldfhulaw12u31312").child("end");

            startPoint = new LatLng(location.getLatitude(), location.getLongitude());
            if (endPoint == null)
                endPoint = new LatLng(location.getLatitude(), location.getLongitude());
            //리얼타임 데이터베이스에 실시간으로 노드 전송
            startRef.setValue(startPoint);
            endRef.setValue(endPoint);
            //선 그리기
            PolylineOptions line = new PolylineOptions().add(startPoint, endPoint).clickable(true).color(Color.GREEN).width(20);
            //움직이는동안 마커 일단 지우기
            currentMarker.remove();
            //카메라 부드럽게 중앙으로 이동
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(endPoint, 18));
            mMap.addPolyline(line);
            endPoint = startPoint;
            //다른사람 위치 표시
            showOtherLocation();

            Log.wtf("위치", startPoint.toString() + "+" + endPoint.toString());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void setWalkState(boolean b) {
        final View view = getView();
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        final CardView ct = view.findViewById(R.id.cardtest);
        final ExtendedFloatingActionButton st = (ExtendedFloatingActionButton) view.findViewById(R.id.btn_start); //시작
        final ExtendedFloatingActionButton fi = (ExtendedFloatingActionButton) view.findViewById(R.id.btn_finish); //종료

        isWalkStart = b;

        if (isWalkStart) {
            //권한 얻기
            if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this.getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            ct.setVisibility(View.VISIBLE); // 보이기
            st.setVisibility(View.GONE); // 시작 버튼 클릭시 숨기고
            fi.setVisibility(View.VISIBLE); //종료 버튼 활성화

            mChr.setBase(SystemClock.elapsedRealtime()); // 시간 초기화
            mChr.start();
            //위치정보 업데이트 시작
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, loListener);
            //백그라운드 서비스 시작
            Intent bgService = new Intent(mContext, LocationBackground.class);
//            mContext.startService(bgService);
        } else {
            ct.setVisibility(View.GONE); // 안보이기
            st.setVisibility(View.VISIBLE); // 종료 버튼 클릭시 숨기고
            fi.setVisibility(View.GONE); //시작 버튼 활성화

            long WalkTimeSum = (SystemClock.elapsedRealtime() - mChr.getBase()) / 1000;

            int min = (int) (WalkTimeSum / 60);
            int hour = (min / 60);
            int sec = (int) (WalkTimeSum % 60);
            min = min % 60;

            mChr.stop();
            //위치정보 업데이트 중단
            lm.removeUpdates(loListener);
            Intent bgService = new Intent(mContext, LocationBackground.class);
            mContext.stopService(bgService);
            //다른 사람 선 다 지우기
            for(Polyline p : otherLinesSaved){
                p.remove();
            }

            Intent intent = new Intent(getContext().getApplicationContext(), WalkFinishPopup.class);
            intent.putExtra("sec", String.valueOf(sec));
            intent.putExtra("min", String.valueOf(min));
            intent.putExtra("hour", String.valueOf(hour));
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // Fragement에서의 OnCreateView를 마치고, Activity에서 onCreate()가 호출되고 나서 호출되는 메소드이다.
        // Activity와 Fragment의 뷰가 모두 생성된 상태로, View를 변경하는 작업이 가능한 단계다.
        super.onActivityCreated(savedInstanceState);

        //액티비티가 처음 생성될 때 실행되는 함수
        MapsInitializer.initialize(mContext);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // 정확도를 최우선적으로 고려
                .setInterval(UPDATE_INTERVAL_MS) // 위치가 Update 되는 주기
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS); // 위치 획득후 업데이트되는 주기

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        // FusedLocationProviderClient 객체 생성
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);

        setDefaultLocation(); // GPS를 찾지 못하는 장소에 있을 경우 지도의 초기 위치가 필요함.

        getLocationPermission();

        updateLocationUI();

        getDeviceLocation();

        //마커 정보 클릭시  팝업 으로 이동
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                MarkerClickPopup m = MarkerClickPopup.getInstance();
                m.show(getFragmentManager(), MarkerClickPopup.TAG_EVENT_DIALOG);

//                startActivityForResult(); 나중에 사용자 정보 데이터 전달
            }
        });

    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mCurrentLocatiion = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setDefaultLocation() {
        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mDefaultLocation);
        markerOptions.title("위치정보 가져올 수 없음");
        markerOptions.snippet("위치 퍼미션과 GPS 활성 여부 확인하세요");
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 15);
        mMap.moveCamera(cameraUpdate);
    }

    String getCurrentAddress(LatLng latlng) {
        // 위치 정보와 지역으로부터 주소 문자열을 구한다.
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        // 지오코더를 이용하여 주소 리스트를 구한다.
        try {
            addressList = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
        } catch (IOException e) {
            Toast.makeText(mContext, "위치로부터 주소를 인식할 수 없습니다. 네트워크가 연결되어 있는지 확인해 주세요.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return "주소 인식 불가";
        }

        if (addressList.size() < 1) { // 주소 리스트가 비어있는지 비어 있으면
            return "해당 위치에 주소 없음";
        }

        // 주소를 담는 문자열을 생성하고 리턴
        Address address = addressList.get(0);
        StringBuilder addressStringBuilder = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressStringBuilder.append(address.getAddressLine(i));
            if (i < address.getMaxAddressLineIndex())
                addressStringBuilder.append("\n");
        }

        return addressStringBuilder.toString();
    }


    // 마커 클릭시 보여지는 주소와 위도 경도 ( 후에 사용자 정보로 변경 )
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);

                LatLng currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore FBdb = FirebaseFirestore.getInstance();
                DocumentReference docRef = FBdb.collection("users").document(user.getUid());
                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        String markerSnippet = "애견 이름 : " + value.getString("petName")
                                + "    견종 : " + value.getString("petKind")+ "    나 이 : " + value.getString("petAge") + " 살 ";
                        String markerTitle = getCurrentAddress(currentPosition);

                        Log.d(TAG, "Time :" + CurrentTime() + " onLocationResult : " + markerSnippet);

                        //현재 위치에 마커 생성하고 이동
                        setCurrentLocation(location, markerTitle, markerSnippet);
                        mCurrentLocatiion = location;

                    }
                });

//                String markerSnippet = "위도 : " + String.valueOf(location.getLatitude())
//                        + " 경도:" + String.valueOf(location.getLongitude());


            }
        }

    };

    private String CurrentTime() {
        Date today = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
        return time.format(today);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(mContext,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onStart() { // 유저에게 Fragment가 보이도록 해준다.
        super.onStart();
        mapView.onStart();
        Log.d(TAG, "onStart ");
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        if (mFusedLocationProviderClient != null) {
            //Log.d(TAG, "onStop : removeLocationUpdates");
            //mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onResume() { // 유저에게 Fragment가 보여지고, 유저와 상호작용이 가능하게 되는 부분
        super.onResume();
        mapView.onResume();
        if (mLocationPermissionGranted) {
            Log.d(TAG, "onResume : requestLocationUpdates");
            if (ActivityCompat.checkSelfPermission(getContext().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            if (mMap != null)
                mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() { // 프래그먼트와 관련된 View 가 제거되는 단계
        super.onDestroyView();
        if (mFusedLocationProviderClient != null) {
            Log.d(TAG, "onDestroyView : removeLocationUpdates");
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onDestroy() {
        // Destroy 할 때는, 반대로 OnDestroyView에서 View를 제거하고, OnDestroy()를 호출한다.
        super.onDestroy();
        mapView.onDestroy();
        mChr.stop();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(getActivity().getApplicationContext(),"Marker Click",Toast.LENGTH_SHORT).show();

        return false;
    }
}

