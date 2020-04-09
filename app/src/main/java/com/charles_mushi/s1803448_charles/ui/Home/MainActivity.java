//
// Student name: Charles Mushi
//  Student ID: S1803448
//

package com.charles_mushi.s1803448_charles.ui.Home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.charles_mushi.s1803448_charles.data.network.XMLPullParser;
import com.charles_mushi.s1803448_charles.Model.LinkedModel;
import com.charles_mushi.s1803448_charles.Model.DataBindingModel;
import com.charles_mushi.s1803448_charles.R;
import com.charles_mushi.s1803448_charles.ui.Activities.DetailsActivity;
import com.charles_mushi.s1803448_charles.ui.Activities.SearchActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * View model Class for the main activity, shows the main interface of the class
 */


public class MainActivity extends AppCompatActivity {


    private ImageView dates;

    private Intent intent1;
    private int id = 0;
    private DataBindingModel.ParcebleModel parse;
    private Intent IntentParse;
    private mynetwork send;
    private network net;
    private IntentFilter get;
    private LinearLayout container;
    private IntentFilter inten;
    private static LinkedModel load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dates=findViewById(R.id.datess);

        container = findViewById(R.id.layoutmain);
        intent1 = getIntent();
        parse = intent1.getParcelableExtra("data");
        load = parse.getData();
        id = 0;

        dates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XMLPullParser.Threadint xml = new XMLPullParser.Threadint();
                LinkedModel load = xml.getChannelComp();
                parse = new DataBindingModel.ParcebleModel(load);
                Intent range= new Intent(MainActivity.this, SearchActivity.class);
                range.putExtra("data", parse);
                startActivity(range);
            }
        });
        for(DataBindingModel i: load.getItems()) {
            Button button = new Button(this);
            button.setText(i.getDescription());
            LinearLayout.LayoutParams aspects = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            aspects.setMargins(7,5,6,2);
            button.setBackgroundResource(R.color.blackcol);
            button.setLayoutParams(aspects);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            button.setId(id);

            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    XMLPullParser.Threadint xml = new XMLPullParser.Threadint();
                    load = xml.getChannelComp();
                    parse = new DataBindingModel.ParcebleModel(load);
                    intent1.putExtra("id",v.getId());
                    intent1.putExtra("data", parse);
                    startActivity(intent1);
                }
            });
            registerForContextMenu(button);
            container.addView(button);
            id++;
        }

        if(container.getChildCount() == 0) {
        }




        intent1 = new Intent(this, DetailsActivity.class);



        IntentParse = new Intent(this, XMLPullParser.class);
        send = new mynetwork();
        net = new network();
        get = new IntentFilter("get");
        inten = new IntentFilter("int");

        registerReceiver(send, get);
        registerReceiver(net, inten);

        if(!XMLPullParser.isInstanceCreated()) {
            Thread t = new Thread() {
                public void run() {
                    startService(IntentParse);
                }
            };
            t.start();
            Toast.makeText(MainActivity.this,"Loading...",Toast.LENGTH_SHORT).show();
        }
    }


    private void updateData(Intent intent) {
        parse = intent.getParcelableExtra("data");
        load = parse.getData();
        container.removeAllViews();
        id = 0;
        for(DataBindingModel i: load.getItems()) {
            Button button = new Button(this);
            button.setText(i.getDescription());
            LinearLayout.LayoutParams aspects = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            aspects.setMargins(7,5,6,2);
            button.setBackgroundResource(R.color.blackcol);
            button.setLayoutParams(aspects);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            button.setId(id);
           // button.setBackground(getDrawable(R.drawable.button));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent1.putExtra("id",v.getId());
                    intent1.putExtra("data", parse);
                    startActivity(intent1);
                }
            });
            registerForContextMenu(button);
            container.addView(button);
            id++;
        }

        if(container.getChildCount() == 0) {
            Button button = new Button(this);
            button.setText("No Data found, check connection and click here to force update.");
            LinearLayout.LayoutParams aspects = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            aspects.setMargins(7,5,6,2);
            button.setLayoutParams(aspects);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            button.setBackgroundResource(R.color.blackcol);

            button.setId(id);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XMLPullParser.Threadint dl = new XMLPullParser.Threadint();
                    load = dl.getChannelComp();
                    parse = new DataBindingModel.ParcebleModel(load);
                    intent1.putExtra("data", parse);
                    updateData(intent1);
                }
            });
            container.addView(button);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(send, get);
        registerReceiver(net, inten);
    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(send);
        unregisterReceiver(net);
    }
    private Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat("E, dd MMM yyyy").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LinkedModel getLoad() {
        return load;
    }

    public class mynetwork extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this,"Refreshing...",Toast.LENGTH_SHORT).show();

            updateData(intent);
        }
    }

    public class network extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String runn = intent.getStringExtra("des");
            int runnnum = 0;
            try {
                runnnum = Integer.parseInt(runn);
            } catch (NumberFormatException e) {
                Log.e("MyTag","Do Nothing");
            }
        }
    }

}