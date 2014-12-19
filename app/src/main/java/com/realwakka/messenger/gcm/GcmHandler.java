package com.realwakka.messenger.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.net.URLDecoder;

/**
 * Created by UCLAB_T60 on 2014-12-06.
 */
public class GcmHandler extends IntentService {
    private Handler handler;
    private String msg;
    private String TAG="GcmHandler";



    public GcmHandler(){
        super("GcmHandler");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("GcmHandler","onCreate");
        handler = new Handler();

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            Bundle extras = intent.getExtras();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String messageType = gcm.getMessageType(intent);

            if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

                if (GoogleCloudMessaging.
                        MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {

                    sendNotification("Send error: " + extras.toString());
                } else if (GoogleCloudMessaging.
                        MESSAGE_TYPE_DELETED.equals(messageType)) {
                    sendNotification("Deleted messages on server: " +
                            extras.toString());
                    // If it's a regular GCM message, do some work.
                } else if (GoogleCloudMessaging.
                        MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                    processTypeMessage(extras);

                    Log.i(TAG, "Received: " + extras.toString());
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void processTypeMessage(Bundle extras) throws Exception{
        String encoded = extras.getString("text");
        String decoded_msg = URLDecoder.decode(encoded, "UTF-8");
        String sender = extras.getString("from_reg");

        Intent intent = new Intent(sender);
        intent.putExtra("message", decoded_msg);

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        boolean result = manager.sendBroadcast(intent);

        sendNotification(decoded_msg);

        Log.d("GcmHandler","sendBroadcast : "+result);
    }
    private void sendNotification(String msg) {
        this.msg = msg;
        handler.post(new Runnable() {
            public void run() {

                Toast.makeText(getApplicationContext(),GcmHandler.this.msg , Toast.LENGTH_LONG).show();
            }
        });
    }

}
