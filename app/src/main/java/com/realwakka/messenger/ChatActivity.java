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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class ChatActivity extends Activity {
    ListView mChatView;
    ChatAdapter mAdapter;
    ArrayList<Chat> mChatList;
    EditText mEditText;
    SendChatTask mCurrentSendTask;
    String regid;
    String PROJECT_NUMBER;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatView = (ListView)findViewById(R.id.chat_list);

        mChatList = new ArrayList<Chat>();

        mAdapter = new ChatAdapter(mChatList);
        mEditText = (EditText)findViewById(R.id.chat_chat);

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
            try{

                // 1. URL
                URL url = new URL("https://android.googleapis.com/gcm/send");

                // 2. Open connection
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // 3. Specify POST method
                conn.setRequestMethod("POST");

                // 4. Set the headers
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "key="+apiKey);

                conn.setDoOutput(true);

                // 5. Add JSON data into POST request body

                //`5.1 Use Jackson object mapper to convert Contnet object into JSON
                ObjectMapper mapper = new ObjectMapper();

                // 5.2 Get connection output stream
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

                // 5.3 Copy Content "JSON" into

                mapper.writeValue(wr, content);

                // 5.4 Send the request
                wr.flush();

                // 5.5 close
                wr.close();

                // 6. Get the response
                int responseCode = conn.getResponseCode();
                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 7. Print result
                System.out.println(response.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
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
