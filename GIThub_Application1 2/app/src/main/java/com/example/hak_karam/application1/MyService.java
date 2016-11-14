package com.example.hak_karam.application1;

import android.app.Service;
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
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;

//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.conn.ssl.SSLContexts;
//import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
/**
 * Created by hak_karam on 10/06/16.
 */
public class MyService extends Service implements SensorEventListener {
    private LocalBroadcastManager broadcaster;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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
    static final public String COPA_RESULT = "com.controlj.copame.backend.COPAService.REQUEST_PROCESSED";

    static final public String COPA_MESSAGE = "com.controlj.copame.backend.COPAService.COPA_MSG";


    protected void onHandleIntent(Intent intent) {
        Timer servicenewtimer = new Timer();
        servicenewtimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // do your thing here, such as execute AsyncTask or send data to server
                showToast("Service Running in timer ");
            }
        }, 1000, 10000);
    }

    public void sendResult(String message) {
        Intent intent = new Intent(COPA_RESULT);
        if(message != null)
            intent.putExtra(COPA_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.

        showToast("Service started ");
        broadcaster = LocalBroadcastManager.getInstance(this);
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
                    sensorManager.registerListener(MyService.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
                                //mylocman.removeUpdates((LocationListener myloclist);

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


//        Timer servicenewtimer = new Timer();
//        servicenewtimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                // do your thing here, such as execute AsyncTask or send data to server
//                String request = "";
//                try {
//                    sendNavRequest();
//                    //request = makJsonObject().toString();
//                    //showToast(request);
//                } catch (Exception e) {
//                    //showToast("JSON obj error ");
//                    showToast("JSON obj error "+ e.getMessage());
//                }
//            }
//        }, 31000, 30000); // starts your code after 30000 milliseconds, then repeat it every 30 seconds


        return START_STICKY;
    }



    public String makStringObject() {

        //String obj = null;
        String finalobject = "";
        StringBuilder obj = new StringBuilder("");


        //TODO : CAll DB here
        mydb.open();
        showToast("mydb is open now ");
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
        //showToast("sending final obj");
        return finalobject;

    }


    public  void  sendNavRequest () throws ClientProtocolException, IOException, NoSuchAlgorithmException, KeyManagementException,JSONException {


        //3rd trial
try {
    URL url = new URL("https://50.112.196.222:8445/AmazonTracker/rest/PostFleetMgmtLiveTrackingDetails/LiveTrackingData");
    HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
    con.setDoInput(true);
    con.setDoOutput(true);
    con.setRequestMethod("POST");
    con.setRequestProperty("Content-Type", "application/json");
    con.setFollowRedirects(true);

    String query = makStringObject();
    byte[] postData = query.getBytes();
    OutputStream out = con.getOutputStream();

    out.write(postData);
    out.close();

    String temp = ((HttpsURLConnection) con).getResponseMessage();
    showToast("Response " + temp);


}
catch (Exception E)
{
    showToast("Exception " + E.getMessage());
}




    }







    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        deltaX = sensorEvent.values[0];
        deltaY = sensorEvent.values[1];
        deltaZ = sensorEvent.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    class MylocListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            mylocman.removeUpdates(this);
            //showToast("location changed");
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
