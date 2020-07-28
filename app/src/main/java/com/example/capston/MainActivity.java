package com.example.capston;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ClusterManager<MyItem> clusterManager;
    ArrayList<Location> shelter_address;

    private GoogleMap mgoogleMap;
    Context context = this;
    ArrayList<Shelter> shelters;
    final String TAG = "LogMainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            shelters = (ArrayList<Shelter>)getIntent().getSerializableExtra("shelter");//대피소 정보 받기
            //shelter_address = (ArrayList<Location>)getIntent().getSerializableExtra("shelter_addr");//주소 받기
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
        }

        @Override
        public void onMapReady(final GoogleMap googleMap) {
            mgoogleMap = googleMap;
            clusterManager = new ClusterManager<>(this, mgoogleMap);

            mgoogleMap.setOnCameraIdleListener(clusterManager);
            mgoogleMap.setOnMarkerClickListener(clusterManager);

            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    Log.d(TAG, "Load");
                    LatLng latLng = new LatLng(35.178129, 128.557163); //첫 구글맵 로딩시 시작지점
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
                    mgoogleMap.animateCamera(cameraUpdate);


                    for(int i = 0 ; i < shelters.size(); i++) {
                 /*       Log.d(TAG,"create" + String.valueOf(i));
                        Location location = new LatLng(shelters.get(i), );
                        LatLng latLng = new LatLng(Shelter.get(i).getX(), Shelter.getY());*/


                        MyItem clinicItem = new MyItem(shelters.get(i).getY(), shelters.get(i).getX(),
                                shelters.get(i).getName());
                        clusterManager.addItem(clinicItem);
                        //MarkerOptions markerOptions = new MarkerOptions();
                        //markerOptions.position(new LatLng(shelters.get(i).getY(),shelters.get(i).getX()));
                        //mgoogleMap.addMarker(markerOptions);
                    } // 대피소 개수만큼 마커 추가



                  /*  for(int i = 0 ; i < shelters.size(); i++) {
                        MyItem clinicItem = new MyItem(shelter_address.get(i).getLatitude(), shelter_address.get(i).getLongitude(),
                                shelters.get(i).getName());
                        clusterManager.addItem(clinicItem);
                    } // 대피소 개수만큼 item 추가*/
                }
            });

            clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
                @Override
                public boolean onClusterClick(Cluster<MyItem> cluster) {
                    LatLng latLng = new LatLng(cluster.getPosition().latitude, cluster.getPosition().longitude);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                    mgoogleMap.moveCamera(cameraUpdate);
                    return false;
                }
            });
            mgoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    int marker_number=-1;

                    for (int i = 0; i < shelters.size(); i++) {// marker title로 shelter를 검색하여 number 반환받아옴
                            if (shelters.get(i).findIndex(marker.getTitle()) != -1){
                            marker_number = shelters.get(i).findIndex(marker.getTitle()); //뽑아옴
                            Log.d(TAG, "marker_number " + marker_number);}
                    }
                    final int marker_ID_number = marker_number;
                    Log.d(TAG, "marker number = " + String.valueOf(marker_ID_number));
                    Log.d(TAG, "marker shelter name = " + shelters.get(marker_ID_number).getName());
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("지진 옥외 대피소 정보");
                    builder.setMessage(
                            "이름 : " + shelters.get(marker_ID_number).getName() +
                                    "\n주소 : " + shelters.get(marker_ID_number).getAddress()
                    );
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });// 마커 클릭 시 Alert Dialog가 나오도록 설정
        }
}














