package com.realwakka.messenger;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.realwakka.messenger.data.Chat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ChatActivity extends Activity {
    ListView mChatView;
    ChatAdapter mAdapter;
    ArrayList<Chat> mChatList;
    EditText mEditText;
    SendChatTask mCurrentSendTask;
    String regid="APA91bGdUMaKQr9DGeot4iARvs9b4X4gMp6CrfQlMltiwOWrzmjaI1-8SV9RsC3-5iXieNyShvZYyWqZnkjCqGplfn5oawD2L7XksVlAOqno5I8bpn7E2gWQ63fH3nxvf5tia8IA2Lq9SDPeiAZJ2FgDYJUmyuPrcA";
    String PROJECT_NUMBER = "61823441123";
    String apiKey = "AIzaSyDdYYZyScZoQ7REysA85CDYMNWu7hmhjG4";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatView = (ListView)findViewById(R.id.chat_list);

        mChatList = new ArrayList<Chat>();

        mAdapter = new ChatAdapter(mChatList);
        mEditText = (EditText)findViewById(R.id.chat_chat);
        new GetRegIdTask().execute();
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.chat_send:
                String msg = mEditText.getText().toString();
                new SendChatTask().execute(msg);
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
            TextView textView = (TextView)v.findViewById(R.id.chat_chat);

            Chat chat = list.get(position);

            textView.setText(chat.getText());

            return v;
        }
    }

    private class SendChatTask extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            try {
                // 1. URL
                HttpClient httpclient = new DefaultHttpClient();
                URL url = new URL("https://android.googleapis.com/gcm/send");
                HttpPost post = new HttpPost("https://android.googleapis.com/gcm/send");
                post.addHeader("Content-Type", "application/json");
                post.addHeader("Authorization", "key=" + apiKey);

                Chat chat = new Chat(params[0], new Date());

                List<NameValuePair> post_params = new ArrayList<NameValuePair>(2);
                post_params.add(new BasicNameValuePair("registration_ids", chat.toJSON()));
                post_params.add(new BasicNameValuePair("data", chat.toJSON()));
                post.setEntity(new UrlEncodedFormEntity(post_params, "UTF-8"));
                HttpResponse response = httpclient.execute(post);
                HttpEntity entity = response.getEntity();

                String responseAsString = EntityUtils.toString(entity);
                Log.d("ChatActivity",responseAsString);
            }

            catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

    }

    private class GetRegIdTask extends AsyncTask<Void,Void,String>{
        GoogleCloudMessaging gcm;
        @Override
        protected String doInBackground(Void... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                }
                regid = gcm.register(PROJECT_NUMBER);
                msg = "Device registered, registration ID=" + regid;
                Log.i("GCM", msg);

            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();

            }
            return msg;
        }
    }

}
