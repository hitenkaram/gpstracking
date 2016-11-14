package com.example.hak_karam.application1;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.io.IOUtils;

//import org.apache.http.client.HttpClient;

/**
 * Created by hak_karam on 29/04/16.
 */
public class NavigationService extends IntentService implements SensorEventListener {


    TelephonyManager tel;
    SensorManager sensorManager;
    Sensor accelerometer;

    float deltaX = 0;
    float deltaY = 0;
    float deltaZ = 0;

    LocationManager mylocman;
    LocationListener myloclist;
    DatabaseHelper mydb;
    boolean isGPSEnabled = false;
    Location location = null;
    String latitude = "";
    String longitude = "";
    String speed = "";

    public NavigationService() {
        super(NavigationService.class.getName());

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        showToast("Service started ");
        tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mydb = new DatabaseHelper(getApplicationContext());
        final SimpleDateFormat writeFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");


        mylocman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = mylocman.isProviderEnabled(LocationManager.GPS_PROVIDER);
        myloclist = new MylocListener();
        if (!isGPSEnabled) {
            showToast("Please Enable GPS");
        } else {
            if (location == null) {
                try {
                    mylocman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myloclist);
                    if (mylocman != null) {
                        location = mylocman.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }catch (Exception e)
                {
                    showToast(e.getMessage());
                }
                                }
        }


            Timer gpsnewtimer = new Timer();
            gpsnewtimer.schedule(new TimerTask() {
            @Override
            public void run() {

                // Accelerometer logic starts here
                sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                    // success! we have an accelerometer
                    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    sensorManager.registerListener(NavigationService.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                } else {  showToast("Accelerometer Data Problem "); }

                // getting GPS status
                mylocman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean isGPSEnabled = mylocman.isProviderEnabled(LocationManager.GPS_PROVIDER);
                myloclist = new MylocListener();
                if (!isGPSEnabled) {
                    showToast("Please Enable GPS");
                } else {
                       try {
                            mylocman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                                @Override
                                public void onStatusChanged(String provider, int status, Bundle extras) {
                                }
                                @Override
                                public void onProviderEnabled(String provider) {
                                }
                                @Override
                                public void onProviderDisabled(String provider) {
                                }
                                @Override
                                public void onLocationChanged(final Location location) {
                                }
                            },Looper.getMainLooper());
                            if (mylocman != null) {
                                location = mylocman.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = String.valueOf(location.getLatitude());
                                longitude = String.valueOf(location.getLongitude());
                                speed = String.valueOf((int) (location.getSpeed()*3600)/1000);
                                showToast("Imei :  " + tel.getDeviceId() + "\r\n" + "X : " + Float.toString(deltaX) + "\r\n" + "Y : " + Float.toString(deltaY) + "\r\n" + "Z : " + Float.toString(deltaZ) + "\r\n"
                                        + "Time : " + writeFormat.format((Calendar.getInstance().getTime())) + "\r\n"
                                        + "Lat : " + latitude + "\r\n" + "Long : " + longitude + "Speed " + speed);

                                mydb.open();
//                                boolean result =
                                mydb.insertNavData(tel.getDeviceId().toString(),longitude,latitude,speed,writeFormat.format((Calendar.getInstance().getTime())).toString(),Float.toString(deltaX),Float.toString(deltaY),Float.toString(deltaZ));

                                }
                                else
                            {
                                latitude = "";
                                longitude = "";
                                speed = "";
                                mydb.open();
//                                boolean result =
                                mydb.insertNavData(tel.getDeviceId().toString(),longitude,latitude,speed,writeFormat.format((Calendar.getInstance().getTime())).toString(),Float.toString(deltaX),Float.toString(deltaY),Float.toString(deltaZ));


                                showToast("Imei :  " + tel.getDeviceId() + "\r\n" + "X : " + Float.toString(deltaX) + "\r\n" + "Y : " + Float.toString(deltaY) + "\r\n" + "Z : " + Float.toString(deltaZ) + "\r\n"
                                        + "Time : " + writeFormat.format((Calendar.getInstance().getTime())) + "\r\n"
                                        + "GPS is null ");


                            }

                            }
                        }catch (Exception e)
                        {
                            showToast("Exception "+ e.getMessage());
                        }

                }
                }


        },1000, 5000);


        Timer servicenewtimer = new Timer();
        servicenewtimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // do your thing here, such as execute AsyncTask or send data to server
                String request = "";
                try {
                    sendNavRequest();
                    //request = makJsonObject().toString();
                    //showToast(request);
                } catch (Exception e) {
                    //showToast("JSON obj error ");
                    showToast("JSON obj error "+ e.getMessage());
                }
            }
        }, 31000, 30000); // starts your code after 30000 milliseconds, then repeat it every 30 seconds




    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showToast("onstartCommandfunc");
        super.onStartCommand(intent,flags,startId);
        return START_STICKY;
    }


    public JSONObject makJsonObject() throws JSONException {

        JSONObject obj = null;
        JSONObject finalobject = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        //TODO : CAll DB here
        mydb.open();
        Cursor cursor = mydb.getAllNavRows();
        //showToast("count = " + cursor.getCount());
        if (cursor .moveToFirst()) {

            while (cursor.isAfterLast() == false) {

                obj = new JSONObject();

                obj.put("DEVICEID",cursor.getString(cursor.getColumnIndex("DEVICE_ID")));
                obj.put("LONGITUDE",cursor.getString(cursor.getColumnIndex("LONGITUDE")));
                obj.put("LATITUDE",cursor.getString(cursor.getColumnIndex("LATITUDE")));
                obj.put("SPEED",cursor.getString(cursor.getColumnIndex("SPEED")));
                obj.put("TIMESTAMP",cursor.getString(cursor.getColumnIndex("TIMESTAMP")));
                obj.put("xAxisValue",cursor.getString(cursor.getColumnIndex("xAxisValue")));
                obj.put("yAxisValue",cursor.getString(cursor.getColumnIndex("yAxisValue")));
                obj.put("zAxisValue",cursor.getString(cursor.getColumnIndex("zAxisValue")));

                //showToast(name);

                //list.add(name);
                jsonArray.put(obj);
                cursor.moveToNext();
            }


            finalobject.put("Request", jsonArray);
        }
        else
        {
            showToast("EMpty DB");
        }
        showToast("Records fetched from DB " + cursor.getCount());
        return finalobject;

    }
    public String makStringObject() {

        //String obj = null;
        String finalobject = "";
        StringBuilder obj = new StringBuilder("");


        //TODO : CAll DB here
        mydb.open();
        Cursor cursor = mydb.getAllNavRows();
        showToast("count = " + cursor.getCount());
        if (cursor .moveToFirst()) {

            while (cursor.isAfterLast() == false) {


                obj.append(cursor.getString(cursor.getColumnIndex("DEVICE_ID")) + ",");
                obj.append(cursor.getString(cursor.getColumnIndex("LONGITUDE")) + ",");
                obj.append(cursor.getString(cursor.getColumnIndex("LATITUDE"))+ ",");
                obj.append(cursor.getString(cursor.getColumnIndex("SPEED"))+ ",");
                obj.append(cursor.getString(cursor.getColumnIndex("TIMESTAMP"))+ ",");
                obj.append(cursor.getString(cursor.getColumnIndex("xAxisValue"))+ ",");
                obj.append(cursor.getString(cursor.getColumnIndex("yAxisValue"))+ ",");
                obj.append(cursor.getString(cursor.getColumnIndex("zAxisValue"))+ ",");
                obj.append("1" + ",");
                obj.append("1" + ",");
                obj.append("1" + ",");
                obj.append("1" + ";");

                cursor.moveToNext();
            }


            finalobject = obj.toString();
        }
        else
        {
            showToast("EMpty DB");
        }
        showToast("sending final obj");
        return finalobject;

    }

    public  void  sendNavRequest () throws JSONException {

        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;
        showToast("Post : " + "inside sendNav");
        String json = makStringObject();

        String URL = "http://xxxx.yy";
        showToast("Post : " + json);
        try {
            //showToast("Inside try");
            HttpPost post = new HttpPost(URL);
            StringEntity se = new StringEntity(json);
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(se);
            //showToast("ready to post");
            response = client.execute(post);

                    /*Checking response */
            if(response!=null){
                InputStream in = response.getEntity().getContent(); //Get the data in the entity
                //StringWriter writer = new StringWriter();
                //IOUtils.copy(inputStream, writer, encoding);
                //String theString = writer.toString();
                String myString = IOUtils.toString(in, "UTF-8");
                showToast("Service response " + myString);
                //db.delete(TABLE_NAME, null, null);
                //db.delete(TABLE_NAME, 1, null); //returns no of rows deleated
                if(myString.contains("Success"))
                {
                    mydb.deleteRows();
                }
                else
                {
                    showToast("Webservice didn't gave the expected result so records not deleted");
                }
            }

        } catch(Exception e) {
            //e.printStackTrace();
            showToast("Cannot Estabilish Connection");
        }

    }


    public void showToast(String message) {
        final String msg = message;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {



        deltaX = sensorEvent.values[0];
        deltaY = sensorEvent.values[1];
        deltaZ = sensorEvent.values[2];
        //displayCurrentValues();



    }

    public void displayCurrentValues() {
        showToast("X -> " + Float.toString(deltaX));
        showToast("Y -> " + Float.toString(deltaY));
        showToast("Z -> " + Float.toString(deltaZ));
    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

     class MylocListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            showToast("location changed");
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
    }


}
