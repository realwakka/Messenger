package com.realwakka.messenger;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.realwakka.messenger.data.Chat;
import com.realwakka.messenger.data.Friend;
import com.realwakka.messenger.data.Option;
import com.realwakka.messenger.encryption.Translator;
import com.realwakka.messenger.gcm.GcmUtils;
import com.realwakka.messenger.sqlite.ChatsDataSource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChatActivity extends Activity{
    public static String STATE="";

    ListView mChatView;
    ChatAdapter mAdapter;
    ArrayList<Chat> mChatList;
    EditText mEditText;
    SendChatTask mCurrentSendTask;
    Friend mFriend;

    Option mOption;

    ChatsDataSource mDataSource;

    String mApiKey;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshChatList();

        }
    };


    private void refreshChatList(){

        ArrayList<Chat> list = (ArrayList<Chat>)mDataSource.getAllChatsByRegid(mFriend.getRegid());
        mChatList.clear();
        mChatList.addAll(list);
        mAdapter.notifyDataSetChanged();
        mAdapter.notifyDataSetInvalidated();

        mChatView.setSelection(mChatList.size());

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,new IntentFilter(mFriend.getRegid()));
        mDataSource.open();
        STATE=mFriend.getRegid();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        mDataSource.close();
        STATE="PAUSED";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mApiKey = getString(R.string.project_apikey);

        mDataSource = new ChatsDataSource(this);
        mDataSource.open();

        String friend_json = getIntent().getStringExtra("FRIEND");
        mFriend = Friend.fromJSON(friend_json);
        mOption = Option.load(this);


        mChatView = (ListView)findViewById(R.id.chat_list);


        mChatList = (ArrayList<Chat>)mDataSource.getAllChatsByRegid(mFriend.getRegid());
        Log.d("ChatActivity","CHATLIST"+mChatList.size());

        mAdapter = new ChatAdapter(mChatList);
        mChatView.setAdapter(mAdapter);
        mEditText = (EditText)findViewById(R.id.chat_chat);




    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.chat_send:
                String msg = mEditText.getText().toString();
                mCurrentSendTask = new SendChatTask();
                mCurrentSendTask.execute(msg);
                break;
        }
    }



    private class ChatAdapter extends BaseAdapter{
        ArrayList<Chat> list;

        public ChatAdapter(ArrayList<Chat> list){
            this.list = list;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.item_chat,null);

            RelativeLayout layout = (RelativeLayout)v.findViewById(R.id.chat_item);

            TextView chatView = (TextView)v.findViewById(R.id.chat_chat);
            TextView nameView = (TextView)v.findViewById(R.id.chat_name);

            Chat chat = list.get(position);
            chatView.setText(chat.getText());

            if(chat.getFrom_reg().equals(mFriend.getRegid())){
                nameView.setText(mFriend.getName());
                layout.setGravity(Gravity.LEFT);

            }else{
                nameView.setText(mOption.getName());
                layout.setGravity(Gravity.RIGHT);
            }

            return v;
        }
    }

    private class RequestKeyTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                Chat chat = new Chat(Chat.TYPE_KEY_REQUEST,mOption.getRegid(),mFriend.getRegid(),"",new Date());
                String responseAsString = GcmUtils.sendChat(chat,mApiKey);
                Log.d("ChatActivity",responseAsString);
            }

            catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            refreshChatList();
            mEditText.setText("");
        }
    }

    private class SendChatTask extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            try {

                String encoded = URLEncoder.encode(params[0],"UTF-8");
                byte[] b_pub = mFriend.getPubicKey();
                Log.d("ChatActivity","PublicKey length"+b_pub.length);
                PublicKey pub_key = KeyFactory.getInstance(Translator.ALGORITHM).generatePublic(new X509EncodedKeySpec(b_pub));
                String encrypted = new String(Translator.encryptString(encoded,pub_key));

                Chat chat = new Chat(Chat.TYPE_MESSAGE,mOption.getRegid(),mFriend.getRegid(),encrypted, new Date());
                Chat decoded = new Chat(Chat.TYPE_MESSAGE,mOption.getRegid(),mFriend.getRegid(),params[0], new Date());

                String responseAsString = GcmUtils.sendChat(chat,mApiKey);
                Log.d("ChatActivity",responseAsString);

                mDataSource.addChat(decoded);

            }

            catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            refreshChatList();
            mEditText.setText("");
        }
    }



}
