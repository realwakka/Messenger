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
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.realwakka.messenger.ChatActivity;
import com.realwakka.messenger.R;
import com.realwakka.messenger.data.Chat;
import com.realwakka.messenger.data.Friend;
import com.realwakka.messenger.data.NfcData;
import com.realwakka.messenger.encryption.Translator;
import com.realwakka.messenger.sqlite.ChatsDataSource;
import com.realwakka.messenger.sqlite.FriendsDataSource;

import java.net.URLDecoder;
import java.security.PrivateKey;
import java.util.Date;

/**
 * Created by UCLAB_T60 on 2014-12-06.
 */
public class GcmHandler extends IntentService {
    private Handler handler;
    private String TAG="GcmHandler";
    private Chat mReceivedChat;
    private Friend mFriend;
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


                } else if (GoogleCloudMessaging.
                        MESSAGE_TYPE_DELETED.equals(messageType)) {

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
        String decoded_json = URLDecoder.decode(encoded, "UTF-8");

        NfcData data =  NfcData.fromJSON(decoded_json);
        Log.d("GcmHandler","Received pub key length"+data.getPub_key().length);
        Friend friend = new Friend(0,data.getName(),data.getPub_key(),data.getRegid());
        FriendsDataSource source = new FriendsDataSource(this);
        source.open();
        source.addFriend(friend);
        source.close();

    }
    private void processTypeMessage(Bundle extras) throws Exception{


        String sender = extras.getString("from_reg");
        String receiver = extras.getString("to_reg");
        FriendsDataSource friendsDataSource = new FriendsDataSource(this);
        friendsDataSource.open();
        mFriend = friendsDataSource.getFriendByRegid(sender);
        friendsDataSource.close();

        if(mFriend==null) {
            Log.d("GcmHandler", "Someone send message");
            return;
        }else {
            String encrypted = extras.getString("text");
            PrivateKey privateKey = Translator.getPrivateKey(this);
            String decrypted = Translator.decryptStringBase64(encrypted, privateKey);
            String decoded_msg = URLDecoder.decode(decrypted, "UTF-8");


            Intent intent = new Intent(sender);
            intent.putExtra("message", decoded_msg);

            Chat chat = new Chat(0, sender, receiver, decoded_msg, new Date());

            ChatsDataSource source = new ChatsDataSource(this);
            source.open();
            source.addChat(chat);
            source.close();

            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);

            if (!manager.sendBroadcast(intent)) {
                sendNotification(chat);
            }
        }

    }
    private void sendNotification(Chat chat) {

        mReceivedChat = chat;

        handler.post(new Runnable() {
            public void run() {
                Context context = getApplicationContext();

                Chat chat = mReceivedChat;
                Friend friend = mFriend;

                String toastText = friend.getName() +":"+chat.getText();
                Toast.makeText(getApplicationContext(),toastText , Toast.LENGTH_LONG).show();

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(friend.getName())
                                .setContentText(chat.getText());

                Intent resultIntent = new Intent(getApplicationContext(), ChatActivity.class);
                resultIntent.putExtra("FRIEND",friend.toJSON());
                resultIntent.putExtra("FROM_NOTIFICATION",true);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());

                stackBuilder.addParentStack(ChatActivity.class);

                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(getResources().getInteger(R.integer.notification_id), mBuilder.build());
            }

        });
    }


}
