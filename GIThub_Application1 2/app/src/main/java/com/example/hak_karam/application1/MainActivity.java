package com.example.hak_karam.application1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import javax.net.ssl.HostnameVerifier;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Mail action in progress", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        AppCompatButton button = (AppCompatButton) findViewById(R.id.btn_login);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click


                //Snackbar.make(v, "Login action", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();

                EditText e1 = (EditText)findViewById(R.id.input_email);
                EditText e2 = (EditText)findViewById(R.id.input_password);
                String strInput = "";
                String strInput2 = "";
                strInput = e1.getText().toString();
                strInput2 = e2.getText().toString();

                if(strInput.equalsIgnoreCase(strInput2))
                    //Toast.makeText(getApplicationContext(),
                      //      "Authenticated", Toast.LENGTH_LONG).show();
                {
                    final Intent myIntent = new Intent(MainActivity.this, NewActivity.class);
                    startActivity(myIntent);

                }
                else
                    Toast.makeText(getApplicationContext(),
                            "Login Failed", Toast.LENGTH_LONG).show();


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
