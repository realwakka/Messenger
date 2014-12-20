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
import com.realwakka.messenger.data.Chat;
import com.realwakka.messenger.data.Friend;
import com.realwakka.messenger.sqlite.ChatsDataSource;
import com.realwakka.messenger.sqlite.FriendsDataSource;

import java.net.URLDecoder;
import java.util.Date;

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
                    switch(Integer.parseInt(extras.getString("type"))){
                        case Chat.TYPE_ACCEPT:
                            processTypeAccept(extras);
                            break;
                        case Chat.TYPE_MESSAGE:
                            processTypeMessage(extras);
                            break;
                        case Chat.TYPE_KEY_REQUEST:

                            break;
                        case Chat.TYPE_KEY_RESPONSE:

                            break;
                    }

                    Log.i(TAG, "Received: " + extras.toString());
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    private void processTypeAccept(Bundle extras) throws Exception{
        String encoded = extras.getString("text");
        String decoded_name = URLDecoder.decode(encoded, "UTF-8");
        String sender = extras.getString("from_reg");
        String receiver = extras.getString("to_reg");

        Friend friend = new Friend(0,decoded_name,new byte[]{},sender);
        FriendsDataSource source = new FriendsDataSource(this);
        source.open();
        source.addFriend(friend);
        source.close();

    }
    private void processTypeMessage(Bundle extras) throws Exception{
        String encoded = extras.getString("text");
        String decoded_msg = URLDecoder.decode(encoded, "UTF-8");
        String sender = extras.getString("from_reg");
        String receiver = extras.getString("to_reg");

        Intent intent = new Intent(sender);
        intent.putExtra("message", decoded_msg);

        Chat chat = new Chat(0,sender,receiver,decoded_msg,new Date());


        ChatsDataSource source = new ChatsDataSource(this);
        source.open();
        source.addChat(chat);
        source.close();

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
