package com.example.project2.GoogleMap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.project2.Community.functions.loadImage;
import com.example.project2.FirebaseDB.WalkingDB;
import com.example.project2.R;
import com.google.android.gms.common.api.ApiException;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.Timestamp;
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
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MapsFragment extends Fragment implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMarkerClickListener {

    private FragmentActivity mContext;
    private View v;

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
    private static final int UPDATE_INTERVAL_MS = 1000 * 60 * 1;        // 1분 단위 시간 갱신
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000 * 30;    // 30초 단위로 화면 갱신

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    //파이어베이스 인스턴스
    FirebaseAuth user = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();  //DB
    FirebaseDatabase database = FirebaseDatabase.getInstance();     //리얼타임 DB

    //위치 마커 비트맵
    private static Bitmap iconBitmap;
    private static View marker_root_view;
    private static ImageView marker_imageView;
    private static TextView marker_textView;
    private static String photoPath;

    //내 위치 관련
    private LatLng startPoint = null;
    private LatLng endPoint = null;
    private static float walkDistanceResult[] = new float[1];
    private static int walkDistance = 0;
    private ArrayList<PolylineOptions> myLinesSaved = new ArrayList<>();
    private static long cameraCooldown;

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
        v = inflater.inflate(R.layout.activity_maps_fragment, container, false);

        final ExtendedFloatingActionButton st = (ExtendedFloatingActionButton) v.findViewById(R.id.btn_start); //시작
        final ExtendedFloatingActionButton fi = (ExtendedFloatingActionButton) v.findViewById(R.id.btn_finish); //종료

        mChr = (Chronometer) v.findViewById(R.id.chronometer);

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

        mapView = (MapView) v.findViewById(R.id.mapview);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        mapView.getMapAsync(this);

        return v;
    }

    //타 사용자 위치 표시
    public void showOtherLocation() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("mapData");
        MarkerOptions marker = new MarkerOptions();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    //다른 사람의 UID
                    String otherUID = d.getKey();
                    //나를 제외한 다른 사람의 위치 변경이 감지되었을 경우
                    if (otherUID.equals(user.getUid())) continue;
                    try {
                        //데이터를 가져온다
                        HashMap<String, HashMap<String, Object>> value = (HashMap<String, HashMap<String, Object>>) d.getValue();
                        //도착 좌표
                        LatLng startPos = new LatLng(
                                Float.valueOf(value.get("start").get("latitude").toString()),
                                Float.valueOf(value.get("start").get("longitude").toString())
                        );
                        //시작 위치 좌표
                        LatLng endPos = new LatLng(
                                Float.valueOf(value.get("end").get("latitude").toString()),
                                Float.valueOf(value.get("end").get("longitude").toString())
                        );
                        //마지막 이동 시간
                        int time = Integer.valueOf(value.get("lastTime").get("seconds").toString());
                        int passedTime = (int) Timestamp.now().getSeconds()-time;

                        //만일 접속종료 마지막 위치가 현재 위치와 같다면( = 산책중이 아니라면)패스
                        if (startPos.equals(endPos) && passedTime>10) {
                            otherMarker.get(otherUID).remove();
                            continue;
                        } else {
                            //마커 셋팅
                            cashingImage(otherUID);
                            Bitmap b = BitmapFactory.decodeFile(photoPath);
                            marker_imageView.setImageBitmap(b);
                            firestore.collection("users").document(otherUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    marker_textView.setText(task.getResult().getString("name"));
                                }
                            });
                            iconBitmap = Bitmap.createScaledBitmap(createDrawableFromView(mContext, marker_root_view), 155, 180, false);
                            marker.position(startPos);
                            marker.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap));

                            //마커 추가
                            otherMarker.get(otherUID).remove();
                            otherMarker.put(otherUID, mMap.addMarker(marker));
                            otherMarker.get(otherUID).setTitle(otherUID);
                        }
                    } catch (Exception e) {
                        marker.position(new LatLng(0,0));
                        otherMarker.put(otherUID, mMap.addMarker(marker));
                        otherMarker.get(otherUID).setTitle(otherUID);
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
        @SuppressLint("MissingPermission")
        @Override
        public void onLocationChanged(Location location) {
            DatabaseReference startRef = database.getReference().child("mapData").child(user.getUid()).child("start");
            DatabaseReference endRef = database.getReference().child("mapData").child(user.getUid()).child("end");
            DatabaseReference lastTimeRef = database.getReference().child("mapData").child(user.getUid()).child("lastTime");
            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            //현재 위치, startPoint에 지속적으로 위치가 갱신됨
            startPoint = new LatLng(location.getLatitude(), location.getLongitude());
            if (endPoint == null)
                endPoint = new LatLng(location.getLatitude(), location.getLongitude());

            //걸은 거리 구하기 미터 단위
            Location.distanceBetween(startPoint.latitude, startPoint.longitude, endPoint.latitude, endPoint.longitude, walkDistanceResult);
            int tmpDistance = walkDistance;
            walkDistance += (int) walkDistanceResult[0];

            //선 그리기
            myLinesSaved.add(new PolylineOptions().add(startPoint, endPoint).clickable(true).color(Color.GREEN).width(20));
            mMap.addPolyline(myLinesSaved.get(myLinesSaved.size() - 1));

            //움직이는동안 마커 일단 지우기
            currentMarker.remove();

            //카메라 부드럽게 중앙으로 이동
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 18));

            //리얼타임 데이터베이스에 실시간으로 노드 전송
            Log.wtf("거리", String.valueOf(walkDistance == tmpDistance));
            //1미터도 안움직였으면 멈춘걸로 처리
            if (walkDistance == tmpDistance) {
                startRef.setValue(startPoint); //멈춤
                endRef.setValue(startPoint);
            } else {
                startRef.setValue(startPoint); //움직이는 중
                endRef.setValue(endPoint);
                lastTimeRef.setValue(Timestamp.now());
            }

            endPoint = startPoint;
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            try {
                //임시로 걸은 거리 칼로리 칸에 표시
                TextView tv = mContext.findViewById(R.id.map_txt_calorie);
                tv.setText(String.valueOf(walkDistance));
            } catch (NullPointerException e) {
            }
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

    //최초로 산책 실행 여부가 결정되는 곳
    public void setWalkState(boolean b) {
        isWalkStart = b;

        if (isWalkStart) { // 시작
            startProcess();
        } else {  // 종료
            endProcess();
        }
    }

    //위치 권한 얻기
    private void getMapLocationPermission() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }

    //산책버튼 나타내기,숨기기
    //b가 true일시 산책 시작, false일시 산책 종료
    private void setWalkButton(boolean b) {
        final CardView ct = v.findViewById(R.id.cardtest);
        final ExtendedFloatingActionButton st = (ExtendedFloatingActionButton) v.findViewById(R.id.btn_start); //시작
        final ExtendedFloatingActionButton fi = (ExtendedFloatingActionButton) v.findViewById(R.id.btn_finish); //종료
        if (b) {
            ct.setVisibility(View.VISIBLE); // 보이기
            st.setVisibility(View.GONE); // 시작 버튼 클릭시 숨기고
            fi.setVisibility(View.VISIBLE); //종료 버튼 활성화
        } else {
            ct.setVisibility(View.GONE); // 안보이기
            st.setVisibility(View.VISIBLE); // 종료 버튼 클릭시 숨기고
            fi.setVisibility(View.GONE); //시작 버튼 활성화
        }
    }

    //버튼 눌러서산책시작시에 실행되는 과정
    @SuppressLint("MissingPermission")
    private void startProcess() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //권한 얻기
        getMapLocationPermission();
        setWalkButton(true);

        mChr.setBase(SystemClock.elapsedRealtime()); // 시간 초기화
        mChr.start();
        //최초 위치 갱신
        lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //위치정보 업데이트 시작
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, loListener);
        //백그라운드 서비스 시작
        Intent bgService = new Intent(mContext, LocationBackground.class);
//            mContext.startService(bgService);
        //지도 모든 선 지우기
        mMap.clear();
        //걸은 미터수 초기화
        walkDistance = 0;
    }

    //버튼 눌러서 산책 종료시 실행되는 과정
    @SuppressLint("MissingPermission")
    private void endProcess() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        final int count = 1;
        long WalkTimeSum = (SystemClock.elapsedRealtime() - mChr.getBase()) / 1000;

        int min = (int) (WalkTimeSum / 60);
        int hour = (min / 60);
        int sec = (int) (WalkTimeSum % 60);
        min = min % 60;

        //다른사람 마커 다 지우기
        List<String> keySet = new ArrayList<>(otherMarker.keySet());
        for(String i : keySet){
            otherMarker.get(i).remove();
        }

        DocumentReference db = firestore.collection("Login_user").document(user.getUid()).collection("Info").document("Walk");

        int finalMin = min;
        db.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if ((document != null) && document.exists()) {
                    db.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) { //문서가 있을 때 실행
                            Log.e(TAG, "Walk Data Upload");

                            String h = documentSnapshot.getString("walking_Time_h");
                            String m = documentSnapshot.getString("walking_Time_m");
                            String c = documentSnapshot.getString("walking_Count");
                            String d = documentSnapshot.getString("walking_Distance");

                            int h1 = Integer.parseInt(h);
                            int m1 = Integer.parseInt(m);
                            int c1 = Integer.parseInt(c);
                            int d1 = Integer.parseInt(d);

                            int sum_h = hour + h1;
                            int sum_m = finalMin + m1;
                            int sum_c = count + c1;
                            int sum_d = walkDistance + d1;

                            if (finalMin >= 5) {// 5분 이상 산책 했을 때
                                WalkingDB walkingDB = new WalkingDB(String.valueOf(sum_h), String.valueOf(sum_m), String.valueOf(sum_c), String.valueOf(sum_d));
                                WalkingUploader(walkingDB);
                                Log.e(TAG,"Walk Data Null");

                                Intent intent = new Intent(getContext().getApplicationContext(), WalkFinishPopup.class);
                                intent.putExtra("sec", String.valueOf(sec));
                                intent.putExtra("min", String.valueOf(finalMin));
                                intent.putExtra("hour", String.valueOf(hour));
                                startActivityForResult(intent, 1);

                                //위치정보 업데이트 중단
                                lm.removeUpdates(loListener);
                                setWalkButton(false);
                                mChr.stop();
                            } else {// 5분 미만만산책 했을 때
                                Log.e(TAG,"test 2. Walk Data Null");
                                Intent intent = new Intent(getContext().getApplicationContext(), WalkFinishPopup2.class);
                                startActivity(intent);

                                //위치정보 업데이트 중단
                                lm.removeUpdates(loListener);
                                setWalkButton(false);
                                mChr.stop();
                            }
                        }
                    });
                } else { // 문서가 없을때 실행
                    Log.e(TAG, "test 1. Walk Data Null");

                    if (finalMin >= 5) { // 5분 이상 산책 했을 때
                        Log.e(TAG, "Walk Data Null");
                        WalkingDB walkingDB = new WalkingDB(String.valueOf(hour), String.valueOf(finalMin), String.valueOf(count), String.valueOf(walkDistance));
                        WalkingUploader(walkingDB);

                        Intent intent = new Intent(getContext().getApplicationContext(), WalkFinishPopup.class);
                        intent.putExtra("sec", String.valueOf(sec));
                        intent.putExtra("min", String.valueOf(finalMin));
                        intent.putExtra("hour", String.valueOf(hour));
                        startActivityForResult(intent, 1);

                        //위치정보 업데이트 중단
                        lm.removeUpdates(loListener);
                        setWalkButton(false);
                        mChr.stop();
                    } else {// 5분 미만만산책 했을 때
                        Log.e(TAG, "test 2. Walk Data Null");

                        Intent intent = new Intent(getContext().getApplicationContext(), WalkFinishPopup2.class);
                        startActivity(intent);

                        //위치정보 업데이트 중단
                        lm.removeUpdates(loListener);
                        setWalkButton(false);
                        mChr.stop();
                    }
                }
            }
        });
    }

    private void WalkingUploader(WalkingDB walkDB) {
        FirebaseAuth user = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Login_user").document(user.getUid()).collection("Info").document("Walk").set(walkDB)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "walk Upload Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error", e);
                    }
                });
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
        //다른사람 마커 다 지우기
        List<String> keySet = new ArrayList<>(otherMarker.keySet());
        for(String i : keySet){
            otherMarker.get(i).remove();
        }
        //마커 준비
        setCustomMarkerView();

        //타유저 위치 갱신
        showOtherLocation();

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
                                + "    견종 : " + value.getString("petKind") + "    나 이 : " + value.getString("petAge") + " 살 ";
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
        String UID = marker.getTitle();
        FirebaseFirestore fbRef = FirebaseFirestore.getInstance();
        DocumentReference dbRef = fbRef.collection("users").document(UID);
        dbRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String profileImg = value.getString("photoUrl");
                String petName = value.getString("petName");
                String name = value.getString("name");
                String perKind = value.getString("petKind");
                String petAge = value.getString("petAge");

                if(name==null)return;

                FragmentManager fm = getParentFragmentManager();
                MarkerClickPopup popup = new MarkerClickPopup();
                Bundle bundle = new Bundle();
                bundle.putString("photoUrl", profileImg);
                bundle.putString("name", name);
                bundle.putString("petName", petName);
                bundle.putString("petKind", perKind);
                bundle.putString("petAge", petAge);
                popup.setArguments(bundle);
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_in_top, R.anim.slide_out_top)
                        .add(popup, "userInfo")
                        .commit();
            }
        });

        return true;
    }

    private void setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(mContext).inflate(R.layout.fragment_map_icon, null);
        marker_imageView = marker_root_view.findViewById(R.id.map_icon_container);
        marker_textView = marker_root_view.findViewById(R.id.map_icon_name);
    }

    // View를 Bitmap으로 변환
    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public void cashingImage(String UID) {
        FirebaseFirestore dbRef = FirebaseFirestore.getInstance();
        FirebaseStorage storageRef = FirebaseStorage.getInstance();

        //이미지 파일 캐싱
        String fileName = String.valueOf(UID.hashCode());
        String fileType = ".JPG";
        File imageCache = new File(getContext().getCacheDir(), fileName + fileType);

        if (!imageCache.exists()) {
            //캐싱된 이미지가 아직 존재하지 않을 경우
            String innerFileType = fileType;
            dbRef.collection("users").document(UID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    loadImage loadImage = new loadImage(getContext().getCacheDir(), value.getString("photoUrl"), fileName, innerFileType);
                    loadImage.execute();
                    photoPath = mContext.getCacheDir().toString() + "/" + fileName + innerFileType;
                    Log.wtf("캐싱", photoPath);
                }
            });
        } else {
            File imageCacheList = new File(getContext().getCacheDir().toString());
            for (File j : imageCacheList.listFiles()) {
                if (j.getName().equals(fileName + fileType)) {
                    photoPath = j.getPath();
                    Log.wtf("이미지 캐싱", j.getPath());
                }
            }
        }
    }
}

