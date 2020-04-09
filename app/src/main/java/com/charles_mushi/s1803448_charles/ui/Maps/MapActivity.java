//
// Student name: Charles Mushi
//  Student ID: S1803448
//

package com.charles_mushi.s1803448_charles.ui.Maps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.charles_mushi.s1803448_charles.ui.Activities.DetailsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.charles_mushi.s1803448_charles.Model.DataBindingModel;
import com.charles_mushi.s1803448_charles.R;


/**
 * View model Class to show the map location of a given earthquake location
 */


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Intent intent;
    private DataBindingModel.ParcebleModel parse;
    private int id;
    private DataBindingModel item;
    LatLng loc;
    LatLng locorig;
    SupportMapFragment mapFragment;
    private MyReceiver send;
    private IntentFilter get;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        intent = getIntent();
        Intent i = new Intent(this, DetailsActivity.class);
        parse = intent.getParcelableExtra("data");
        id = intent.getIntExtra("id",0);
        item = parse.getData().getItems().get(id);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        send = new MyReceiver();
        get = new IntentFilter("");
        registerReceiver(send, get);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locorig = new LatLng(item.getLat(),item.getLon());
        double radius;
        mMap.addMarker(new MarkerOptions().position(locorig).title("Earthquake" + item.getPubDate().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(Float.parseFloat("12.0")));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(locorig));
        for(DataBindingModel i: parse.getData().getItems()) {
            loc = new LatLng(i.getLat(),i.getLon());
            double mag = i.getMagnitude();
            radius = mag * 1200;
            if(radius < 0) {
                radius = radius * -1;
            }
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(send);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(send, get);
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(MapActivity.this,"Refreshing",Toast.LENGTH_SHORT).show();


        }
    }
}
