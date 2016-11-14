package com.example.hak_karam.application1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by hak_karam on 09/06/16.
 */
public class StartMyServiceAtBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            //from blog

            Intent serviceIntent = new Intent(context, MyService.class);
            context.startService(serviceIntent);

//            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, NavigationService.class);
//            context.startService();
        }
    }
}
