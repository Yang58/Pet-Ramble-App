package com.example.project2.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project2.FirebaseDB.CalendarDB;
import com.example.project2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;

public class LocateActivity extends AppCompatActivity {
    //로그캣 사용 설정
    private static final String TAG = "LocateActivity";

    //객체 선언
    SupportMapFragment mapFragment;
    GoogleMap map;
    Button btnLocation, btnKor2Loc;
    EditText editText;
    Location savepoint;
    String datename;
    String timename;
    String content;
    Boolean Personal;

    MarkerOptions myMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);
        Intent intent = getIntent();
        datename = intent.getStringExtra("datename");
        timename = intent.getStringExtra("timename");
        content = intent.getStringExtra("Content");
        Log.d("Debug","datename=" + datename);
        Log.d("Debug","timename=" + timename);
        Personal = intent.getBooleanExtra("Personal",true);

        //권한 설정
        checkDangerousPermissions();

        getSupportActionBar().hide();
        //객체 초기화
        editText = findViewById(R.id.editText);
        btnLocation = findViewById(R.id.button1);
        btnKor2Loc = findViewById(R.id.button2);

        //지도 프래그먼트 설정
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "onMapReady: ");
                map = googleMap;
                if (ActivityCompat.checkSelfPermission(LocateActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocateActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                map.setMyLocationEnabled(true);
            }
        });
        MapsInitializer.initialize(this);

        //위치 확인 버튼 기능 추가
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateEditFragment fragment = new DateEditFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Latitude", String.valueOf(savepoint.getLatitude()));
                bundle.putString("Longitude",String.valueOf(savepoint.getLongitude()));
                fragment.setArguments(bundle);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                CalendarDB calendarDB = new CalendarDB(datename,timename,content,Personal,savepoint);
                db.collection("community").document(user.getUid()).collection("calendar").document(datename).set(calendarDB)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Debug", "성공");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Debug", "Error", e);
                            }
                        });


                finish();
                //requestMyLocation();
            }
        });

        btnKor2Loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().length() > 0) {
                    Location location = getLocationFromAddress(getApplicationContext(), editText.getText().toString());

                    showCurrentLocation(location);
                }
            }
        });

    }

    private Location getLocationFromAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses;
        Location resLocation = new Location("");
        try {
            addresses = geocoder.getFromLocationName(address, 5);
            if((addresses == null) || (addresses.size() == 0)) {
                return null;
            }
            Address addressLoc = addresses.get(0);

            resLocation.setLatitude(addressLoc.getLatitude());
            resLocation.setLongitude(addressLoc.getLongitude());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resLocation;
    }

    private void requestMyLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            long minTime = 1000;    //갱신 시간
            float minDistance = 0;  //갱신에 필요한 최소 거리

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    showCurrentLocation(location);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void showCurrentLocation(Location location) {
        try {
            LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
            if(curPoint ==null){
                Toast.makeText(this, "알 수 없는 위치입니다.", Toast.LENGTH_SHORT).show();
            } else{
                String msg = "Latitutde : " + curPoint.latitude
                        + "\nLongitude : " + curPoint.longitude;
                //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                //화면 확대, 숫자가 클수록 확대
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));

                //마커 찍기
            /*
            Location targetLocation = new Location("");
            targetLocation.setLatitude(37.4937);
            targetLocation.setLongitude(127.0643);

             */
                showMyMarker(location);
            }
        }catch(NullPointerException e){
            Toast.makeText(this, "알 수 없는 위치입니다.", Toast.LENGTH_SHORT).show();
        }

    }

    //------------------권한 설정 시작------------------------
    private void checkDangerousPermissions() {
        String[] permissions = {
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    //------------------권한 설정 끝------------------------

    private void showMyMarker(Location location) {
            myMarker   = new MarkerOptions();

            myMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
            myMarker.title("검색위치\n");
            myMarker.snippet("일정 위치를 이곳으로.");
            //myMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_close_black_48));
            map.addMarker(myMarker);
            savepoint = location;

            Log.d("Debug", "Location= " + String.valueOf(savepoint.getLatitude()) + String.valueOf(savepoint.getLongitude()));

    }

}
