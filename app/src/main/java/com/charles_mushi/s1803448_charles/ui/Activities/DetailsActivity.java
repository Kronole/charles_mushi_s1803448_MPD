//
// Student name: Charles Mushi
//  Student ID: S1803448
//

package com.charles_mushi.s1803448_charles.ui.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.charles_mushi.s1803448_charles.Model.LinkedModel;
import com.charles_mushi.s1803448_charles.Model.DataBindingModel;
import com.charles_mushi.s1803448_charles.R;
import com.charles_mushi.s1803448_charles.ui.Maps.MapActivity;

/**
 * View model Class for showing further detais about an earthquake
 */

public class DetailsActivity extends AppCompatActivity {

    private DataBindingModel.ParcebleModel parse;
    private LinkedModel c;
    private Intent intent;

    private DataBindingModel i;
    private Button mapView;
    private Intent mapIn;
    private network send;
    private IntentFilter get;
    private TextView link;
    private TextView pubDate;
    private TextView category;
    private TextView lat;
    private TextView lon;
    private TextView mag;
    private TextView dep;
    private TextView origDate;
    private TextView loc;
    private int sid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        link = findViewById(R.id.link);
        pubDate = findViewById(R.id.pubDate);
        category = findViewById(R.id.category);
        lat = findViewById(R.id.lat);
        lon = findViewById(R.id.lon);
        mapView = findViewById(R.id.mapView);
        mag = findViewById(R.id.mag);
        dep = findViewById(R.id.dep);
        origDate = findViewById(R.id.origDate);
        loc = findViewById(R.id.loc);

        intent = getIntent();
        parse = intent.getParcelableExtra("data");
        sid = intent.getIntExtra("ItemID",0);
        mapIn = new Intent(this, MapActivity.class);
        c = parse.getData();
        i = c.getItems().get(sid);
        link.setText(getResources().getString(R.string.link,i.getLink()));
        pubDate.setText(getResources().getString(R.string.publish, i.getPubDate().toString()));
        category.setText(getResources().getString(R.string.category, i.getCategory()));
        lat.setText(getResources().getString(R.string.lat, i.getLat()));
        lon.setText(getResources().getString(R.string.lon, i.getLon()));
        mag.setText(getResources().getString(R.string.magnitude, i.getMagnitude()));
        dep.setText(getResources().getString(R.string.depth, i.getDepth(), "km"));
        origDate.setText(getResources().getString(R.string.orig, i.getOriginDate().toString()));
        loc.setText(getResources().getString(R.string.location, i.getLocation()));


        mapView.setOnClickListener(new View.OnClickListener() {            //As above
            @Override
            public void onClick(View v) {
                mapIn.putExtra("data", parse);
                mapIn.putExtra("ItemID", sid);
                startActivity(mapIn);
            }
        });
        send = new network();
        get = new IntentFilter("");
        registerReceiver(send, get);
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
    public class network extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    }
}
