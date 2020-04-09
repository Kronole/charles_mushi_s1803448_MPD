//
// Student name: Charles Mushi
//  Student ID: S1803448
//

package com.charles_mushi.s1803448_charles.ui.Activities;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.charles_mushi.s1803448_charles.data.network.XMLPullParser;
import com.charles_mushi.s1803448_charles.Model.LinkedModel;
import com.charles_mushi.s1803448_charles.Model.DataBindingModel;
import com.charles_mushi.s1803448_charles.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;


/**
 * View model Class for showing the search range of the deepest, shallowest, easternly, westernly, nothernly, southernly earthquakes that have occures
 */

public class SearchActivity extends AppCompatActivity {



    private int nid;
    private int sid;
    private int wid;
    private int eid;
    private int lid;
    private int did;
    private int shid;
    private int id;
    private TextView northernly, southernly,westernly,easternly,shallow;
    private DatePickerDialog.OnDateSetListener dateFrom, dateTo;
    private TextView deep, largemag;
    DataBindingModel.ParcebleModel p, sear;
    private Intent globalIntent;
    private network uec;
    private IntentFilter ifi, ufi;
    private Calendar calender;
    private static LinkedModel c;
    private Intent intent;
    private ImageView searchic;
    private EditText fromDate, toDate;
    private ProgressBar load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        intent = getIntent();
      DataBindingModel.ParcebleModel p = intent.getParcelableExtra("data");
        c = p.getData();


        id = 0;
        load = findViewById(R.id.progress);
        toDate = findViewById(R.id.endDate);
        searchic= findViewById(R.id.searchic);





        calender = Calendar.getInstance();
        dateFrom = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calender.set(Calendar.YEAR, year);
                calender.set(Calendar.MONTH, month);
                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calender.set(Calendar.HOUR,0);
                calender.set(Calendar.MINUTE,0);
                calender.set(Calendar.SECOND,0);
                updateLabelFrom();
            }
        };

        dateTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calender.set(Calendar.YEAR, year);
                calender.set(Calendar.MONTH, month);
                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calender.set(Calendar.HOUR,23);
                calender.set(Calendar.MINUTE,59);
                calender.set(Calendar.SECOND,59);
                updateLabelTo();
            }
        };

        fromDate = findViewById(R.id.startDate);
        fromDate.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                DatePickerDialog dp = new DatePickerDialog(SearchActivity.this,dateFrom,calender.get(Calendar.YEAR),calender.get(Calendar.MONTH),calender.get(Calendar.DAY_OF_MONTH));
                dp.show();
            }
        });


        toDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DatePickerDialog dp = new DatePickerDialog(SearchActivity.this,dateTo,calender.get(Calendar.YEAR),calender.get(Calendar.MONTH),calender.get(Calendar.DAY_OF_MONTH));
                dp.show();
            }
        });


        intent = new Intent(this, DetailsActivity.class);


        searchic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                srchimg("click");
            }
        });

        northernly = findViewById(R.id.northernly);
        southernly = findViewById(R.id.southernly);
        westernly = findViewById(R.id.westernly);
        easternly = findViewById(R.id.easternly);
        deep = findViewById(R.id.greatDepth);
        shallow = findViewById(R.id.minDepth);
        largemag = findViewById(R.id.greatMag);

        globalIntent = new Intent(this, XMLPullParser.class);
        uec = new network();
        ifi = new IntentFilter("");
        ufi = new IntentFilter("");
        registerReceiver(uec,ufi);
        if(!XMLPullParser.isInstanceCreated()) {
        Thread t = new Thread() {
                public void run() {
                    startService(globalIntent);
                }
            };
            t.start();
            Toast.makeText(SearchActivity.this,"Loading...",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
       srchimg( "continue");

    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private void updateLabelFrom() {
        String myFormat = "E, dd MMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        Log.e("Date",calender.getTime().toString());
        fromDate.setText(sdf.format(calender.getTime()));
    }

    private void updateLabelTo() {
        String myFormat = "E, dd MMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        Log.e("Date",calender.getTime().toString());
        toDate.setText(sdf.format(calender.getTime()));
    }

    private void srchimg(String string) {

        if((fromDate.getText().length() == 0 || toDate.getText().length() == 0) && string.equals("click")) {

            return;
        }


        if((fromDate.getText().length() == 0 || toDate.getText().length() == 0) && string.equals("continue")) {

            return;
        }


        double toplat = 0.0;
        double toplon = 0.0;
        double minlat = 0.0;
        double minlon = 0.0;
        double greatmag = 0.0;
        int shallowest = 0;
        int count = 0;
        int deepest = 0;
        LinkedList<DataBindingModel> loaded = new LinkedList<>();

        for(DataBindingModel i : c.getItems()) {
            if((i.getPubDate().after(parseDate(fromDate.getText().toString()))) && (i.getPubDate().before(parseDate(toDate.getText().toString())))) {
                loaded.add(i);
            }
        }

        if(loaded.size() == 0) {

            Toast.makeText(SearchActivity.this,getResources().getString(R.string.nodata),Toast.LENGTH_SHORT).show();

            return;
        }
        for(DataBindingModel i : loaded) {

            if(count == 0) {
                toplat = i.getLat();
                greatmag = i.getMagnitude();
                toplon = i.getLon();
                minlat = i.getLat();
                minlon = i.getLon();
                deepest = i.getDepth();
                shallowest = i.getDepth();
            }
            if(i.getLon() > toplon) {
                eid = count;
                toplon = i.getLon();
            }
            if(i.getLon() < minlon) {
                wid = count;
                minlon = i.getLon();
            }
            if(greatmag < i.getMagnitude()) {
                lid = count;
                greatmag = i.getMagnitude();
            }
            if(deepest < i.getDepth()) {
                did = count;
                deepest = i.getDepth();
            }
            if(i.getLat() > toplat) {
                nid = count;
                toplat = i.getLat();
            }
            if(shallowest > i.getDepth()) {
                shid = count;
                shallowest = i.getDepth();
            }
            if(i.getLat() < minlat) {
                sid = count;
                minlat = i.getLat();
            }
            count++;
        }
        westernly.setText(getResources().getString(R.string.wester,toplon, loaded.get(wid).getTitle()));
        northernly.setText(getResources().getString(R.string.norther,toplat,loaded.get(nid).getTitle()));
        largemag.setText(getResources().getString(R.string.largem, greatmag,loaded.get(lid).getTitle()));
        southernly.setText(getResources().getString(R.string.souther, minlat, loaded.get(sid).getTitle()));
        easternly.setText(getResources().getString(R.string.easter,minlon,loaded.get(eid).getTitle()));
        deep.setText(getResources().getString(R.string.deepestearth, deepest , loaded.get(did).getTitle()));
        shallow.setText(getResources().getString(R.string.shallowearth,shallowest,loaded.get(shid).getTitle()));

    }

    private Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat("E, dd MMM yyyy").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public class network extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra("method");
            int tempnum = 0;
            try {
                tempnum = Integer.parseInt(temp);
            } catch (NumberFormatException e) {
                Log.e("MyTag","DoNothing");
            }
            if(temp.equals("start")) {
                load.setProgress(0);
                load.setVisibility(View.VISIBLE);
            } else {
                load.setProgress(Integer.parseInt(temp));
            }
            if(tempnum == 100) {
                load.setVisibility(View.GONE);
            }
        }
    }

}
