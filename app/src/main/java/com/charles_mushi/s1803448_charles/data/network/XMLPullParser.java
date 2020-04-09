//
// Student name: Charles Mushi
//  Student ID: S1803448
//
package com.charles_mushi.s1803448_charles.data.network;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import com.charles_mushi.s1803448_charles.Model.DataBindingModel;
import com.charles_mushi.s1803448_charles.Model.LinkedModel;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;


/**
 * Class for parsing XML data into Channel  that is Image, and Item objects.
 */

public class XMLPullParser extends Service {


    private static XMLPullParser inst = null;

    private String result;
    private String urlSource="http://quakes.bgs.ac.uk/feeds/WorldSeismology.xml";
    public Intent IntentClose;
    public static final String STRT = "start";
    public static final String RSTART = "finnish";
    public Intent IntentParse, IntentRefresh;

    public static boolean isInstanceCreated() {
        return inst != null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        inst = this;
        IntentParse = new Intent();
        IntentRefresh = new Intent();
        IntentClose = new Intent();
        IntentClose.setAction(RSTART);
        IntentRefresh.setAction(RSTART);
        IntentParse.setAction(STRT);

    }

    @Override
    public void onDestroy() {
        inst = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Task().execute();
        return super.onStartCommand(intent, flags, startId);
    }

    private class Task extends AsyncTask<String, Integer, Void> {

        final Object lock = new Object();

        @Override
        public Void doInBackground(String... strings) {
            while (true) {
                IntentClose.removeExtra("method");
                IntentClose.putExtra("method", "start");
                sendBroadcast(IntentClose);
                URL aurl;
                URLConnection yc;
                BufferedReader in = null;
                String inputLine = "";
                result = "";
                Log.e("MyTag", "in run");
                try {
                    Log.e("MyTag", "in try");
                    aurl = new URL(urlSource);
                    yc = aurl.openConnection();
                    in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                    in.readLine();
                    while ((inputLine = in.readLine()) != null) {
                        result = result + inputLine + "\n";
                        Log.e("MyTag", inputLine);
                    }
                    in.close();
                } catch (IOException ae) {
                    Log.e("MyTag", "ioexception");
                    ae.printStackTrace();
                }

                DataBindingModel item = null;
                LinkedModel myChannel = null;
                DataBindingModel.ImageTitleLink myImage = null;
                myChannel = new LinkedModel();
                myChannel.setItems(new LinkedList<DataBindingModel>());

                try {

                    XmlPullParserFactory myFact = XmlPullParserFactory.newInstance();
                    myFact.setNamespaceAware(true);
                    XmlPullParser parser = myFact.newPullParser();
                    parser.setInput(new StringReader(result));
                    int event = parser.getEventType();
                    String currentType = null;
                    int currentLine = 0;
                    int countOfLines = result.split("\n").length;
                    while (event != XmlPullParser.END_DOCUMENT) {
                        if (event == XmlPullParser.START_TAG) {
                            if (parser.getName().equalsIgnoreCase("channel")) {
                                myChannel = new LinkedModel();
                                currentType = "channel";
                            } else if (parser.getName().equalsIgnoreCase("title") && currentType == "channel") {
                                myChannel.setTitle(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("link") && currentType == "channel") {
                                myChannel.setLink(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("description") && currentType == "channel") {
                                myChannel.setDescription(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("language")) {
                                myChannel.setLanguage(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("lastBuildDate")) {
                                myChannel.setLastBuildDate(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("image")) {
                                myImage = new DataBindingModel.ImageTitleLink();
                                currentType = "image";
                            } else if (parser.getName().equalsIgnoreCase("title") && currentType == "image") {
                                myImage.setTitle(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("url") && currentType == "image") {
                                myImage.setURL(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("link") && currentType == "image") {
                                myImage.setLink(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("item")) {
                                item = new DataBindingModel();
                                currentType = "item";
                            } else if (parser.getName().equalsIgnoreCase("title") && currentType == "item") {
                                item.setTitle(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("description") && currentType == "item") {
                                item.setDescription(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("link") && currentType == "item") {
                                item.setLink(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("pubDate")) {
                                item.setPubDate(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("category")) {
                                item.setCategory(parser.nextText());
                            } else if (parser.getPrefix() != null) {
                                if (parser.getName().equalsIgnoreCase("lat")) {
                                    item.setLat(Double.parseDouble(parser.nextText()));
                                } else {
                                    item.setLon(Double.parseDouble(parser.nextText()));

                                }
                            }
                        } else if (event == XmlPullParser.END_TAG) {
                            if (parser.getName().equalsIgnoreCase("image")) {
                                myChannel.setImage(myImage);
                            } else if (parser.getName().equalsIgnoreCase("item")) {
                                myChannel.addItem(item);
                            } else if (parser.getName().equalsIgnoreCase("channel")) {
                            }
                        }
                        publishProgress((int) ((currentLine / (float) countOfLines) * 100));
                        currentLine++;
                        event = parser.next();
                    }
                } catch (XmlPullParserException xme) {
                    Log.e("myTag", xme.toString());
                } catch (IOException ioe) {
                    Log.e("myTag", ioe.toString());
                }
              DataBindingModel.ParcebleModel p = new DataBindingModel.ParcebleModel(myChannel);
                IntentParse.putExtra("data", p);
                sendBroadcast(IntentParse);
                synchronized (this) {
                    try {
                        this.wait(300000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onProgressUpdate(Integer... progress) {
            IntentRefresh.putExtra("method", progress[0].toString());
            sendBroadcast(IntentRefresh);
        }

        @Override
        protected void onPostExecute(Void v) {
        }

        @Override
        protected void onPreExecute() {
        }
    }

    public static class Threadint {



        private String urlSource="http://quakes.bgs.ac.uk/feeds/WorldSeismology.xml";
        private String p;
        private LinkedModel instance;

        public Threadint() {
            returnThread();
        }

        public Thread returnThread() {
            return new Thread(new Task(urlSource));
        }

        public LinkedModel getChannelComp() {

            Thread t = new Thread(new Task(urlSource));
            t.start();
            while(instance == null) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
            return instance;
        }

        private class Task implements Runnable
        {
            private String url;

            public Task(String aurl)
            {
                url = aurl;
            }

            @Override
            public void run()
            {

                URL aurl;
                URLConnection yc;
                BufferedReader in = null;
                String inputLine = "";
                p = "";


                Log.e("MyTag","in run");

                try
                {
                    Log.e("MyTag","in try");
                    aurl = new URL(url);
                    yc = aurl.openConnection();
                    in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                    in.readLine();
                    while ((inputLine = in.readLine()) != null)
                    {
                        p = p + inputLine;
                        Log.e("MyTag",inputLine);
                    }
                    in.close();
                }
                catch (IOException ae)
                {
                    Log.e("MyTag", "ioexception");
                    ae.printStackTrace();
                }

                instance = parseXML(p);

            }

            public LinkedModel parseXML(String data) {

                DataBindingModel item = null;
                LinkedModel myChan;
                DataBindingModel.ImageTitleLink myImage = null;
                myChan = new LinkedModel();
                myChan.setItems(new LinkedList<DataBindingModel>());

                try {

                    XmlPullParserFactory myFact = XmlPullParserFactory.newInstance();
                    myFact.setNamespaceAware(true);
                    XmlPullParser parser = myFact.newPullParser();
                    parser.setInput(new StringReader(data));
                    int event = parser.getEventType();
                    String currentType = null;

                    while (event != XmlPullParser.END_DOCUMENT) {
                        if (event == XmlPullParser.START_TAG) {
                            if (parser.getName().equalsIgnoreCase("channel")) {
                                myChan = new LinkedModel();
                                currentType = "channel";
                            } else if (parser.getName().equalsIgnoreCase("title") && currentType == "channel") {
                                myChan.setTitle(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("link") && currentType == "channel") {
                                myChan.setLink(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("description") && currentType == "channel") {
                                myChan.setDescription(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("language")) {
                                myChan.setLanguage(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("lastBuildDate")) {
                                myChan.setLastBuildDate(parser.nextText());
                            }  else if (parser.getName().equalsIgnoreCase("image")) {
                                myImage = new DataBindingModel.ImageTitleLink();
                        currentType = "image";
                            } else if (parser.getName().equalsIgnoreCase("title") && currentType == "image") {
                                myImage.setTitle(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("url") && currentType == "image") {
                                myImage.setURL(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("link") && currentType == "image") {
                                myImage.setLink(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("item")) {
                                item = new DataBindingModel();
                                currentType = "item";
                            } else if (parser.getName().equalsIgnoreCase("title") && currentType == "item") {
                                item.setTitle(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("description") && currentType == "item") {
                                item.setDescription(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("link") && currentType == "item") {
                                item.setLink(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("pubDate")) {
                                item.setPubDate(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("category")) {
                                item.setCategory(parser.nextText());
                            } else if (parser.getPrefix() != null) {
                                if (parser.getName().equalsIgnoreCase("lat")) {
                                    item.setLat(Double.parseDouble(parser.nextText()));
                                } else {
                                    item.setLon(Double.parseDouble(parser.nextText()));

                                }
                            }
                        } else if (event == XmlPullParser.END_TAG) {
                            if (parser.getName().equalsIgnoreCase("image")) {
                                myChan.setImage(myImage);
                            } else if (parser.getName().equalsIgnoreCase("item")) {
                                myChan.addItem(item);
                            } else if (parser.getName().equalsIgnoreCase("channel")) {
                            }
                        }
                        event = parser.next();
                    }
                } catch (XmlPullParserException xme) {
                    Log.e("myTag",xme.toString());
                } catch (IOException ioe) {
                    Log.e("myTag", ioe.toString());
                }
                return myChan;
            }
        }
    }
}