//
// Student name: Charles Mushi
//  Student ID: S1803448
//

package com.charles_mushi.s1803448_charles.ui.Home;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.charles_mushi.s1803448_charles.Model.DataBindingModel;
import com.charles_mushi.s1803448_charles.Model.LinkedModel;
import com.charles_mushi.s1803448_charles.R;
import com.charles_mushi.s1803448_charles.data.network.XMLPullParser;
import com.charles_mushi.s1803448_charles.ui.Activities.DetailsActivity;

/**
 * View model Class That initializes the app
 */


public class LaunchActivity extends AppCompatActivity
{
    DataBindingModel.ParcebleModel st;
    Intent IntentParse, details;
    private Button exit;


    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        Button start= findViewById(R.id.start);
        exit= findViewById(R.id.end);


        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                final AlertDialog.Builder builder = new AlertDialog.Builder(LaunchActivity.this);
                builder.setTitle("Exit");
                builder.setMessage("Do you want to exit ??");

                builder.setPositiveButton("Yes. Exit now!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }


            });
	    builder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i){

                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            launch();
            }
        });
        details = new Intent(this, DetailsActivity.class);



        IntentParse = new Intent(this, XMLPullParser.class);

        if(!XMLPullParser.isInstanceCreated()) {
            Thread t = new Thread() {
                public void run() {
                    startService(IntentParse);
                }
            };
            t.start();
            Toast.makeText(LaunchActivity.this,"Initializing...",Toast.LENGTH_SHORT).show();
        }

    }


    public void launch(){
        XMLPullParser.Threadint dl = new XMLPullParser.Threadint();
        LinkedModel channel = dl.getChannelComp();
        st = new DataBindingModel.ParcebleModel(channel);
    Intent launch= new Intent(LaunchActivity.this, MainActivity.class);
        launch.putExtra("data", st);
        startActivity(launch);
}
}





