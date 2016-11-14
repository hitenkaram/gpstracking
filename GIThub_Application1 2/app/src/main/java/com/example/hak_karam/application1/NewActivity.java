package com.example.hak_karam.application1;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hak_karam on 26/04/16.
 */
public class NewActivity extends Activity {



    DatabaseHelper mydb;

    String lat = "";
    String log = "";
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.new_activity);

        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, NavigationService.class);
        //Intent intent = new Intent(Intent.ACTION_SYNC, null, this, MyService.class);
        startService(intent);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(MyService.COPA_MESSAGE);
//                final TextView mTextView = (TextView) findViewById(R.id.rate);
//                mTextView.setText("");
                // do something here.
            }
        };
        //unbindService((ServiceConnection) intent);


//        LocationManager mylocman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        // getting GPS status
//        boolean isGPSEnabled = mylocman.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
////
////        if(isGPSEnabled) {
////            LocationListener myloclist = new MylocListener();
////            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////                // TODO: Consider calling
////                //    ActivityCompat#requestPermissions
////                // here to request the missing permissions, and then overriding
////                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
////                //                                          int[] grantResults)
////                // to handle the case where the user grants the permission. See the documentation
////                // for ActivityCompat#requestPermissions for more details.
////                //return;
////            }
////            mylocman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myloclist);
////
////            // Get the view from new_activity.xml
////            setContentView(R.layout.new_activity);
////
////             mydb = new DatabaseHelper(this);
////        }
////        else
////        {
////
////            Toast.makeText(getApplicationContext(),
////                    "Please enable GPS on phone", Toast.LENGTH_LONG).show();
////        }

        //DatabaseHelper  dbobject = new DatabaseHelper(this);


//        AppCompatButton button = (AppCompatButton) findViewById(R.id.coordinates);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),
//                        "Coordinates", Toast.LENGTH_LONG).show();
//
//    }
//        });
    }




//    protected class MylocListener implements LocationListener {
//
//        @Override
//        public void onLocationChanged(Location loc) {
//            String text = " My location is  Latitude ="+loc.getLatitude() + " Longitude =" + loc.getLongitude();
//            lat=loc.getLatitude() + "";
//            log=loc.getLongitude()+"";
//            updateDatabase();
//
//        }
//
//        @Override
//        public void onStatusChanged(String s, int i, Bundle bundle) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String s) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String s) {
//
//        }
//
////        public  void updateDatabase(){
////            DatabaseHelper myDatabase=new DatabaseHelper(NewActivity.this);
////            //myDatabase.open();
////            myDatabase.insertData(lat.substring(0,4),log.substring(0,4));
////            //myDatabase.close();
////        }
//
//
//
//
//    }

}
