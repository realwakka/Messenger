package com.realwakka.messenger;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realwakka.messenger.data.Chat;
import com.realwakka.messenger.data.Friend;
import com.realwakka.messenger.data.NfcData;
import com.realwakka.messenger.data.Option;
import com.realwakka.messenger.sqlite.FriendsDataSource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;


public class AcceptActivity extends Activity {
    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mNFCTechLists;

    private Option mOption;
    private NfcData mReceivedData;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_accept);

        mOption = Option.load(this);

        if(mOption ==null){
            finish();
        }

        mTextView = (TextView)findViewById(R.id.accept_text);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        loadData(getIntent());

        String str = "You've just received "+mReceivedData.getName()+"'s invitation. Do you want to accept this?";

        mTextView.setText(str);
    }
    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        Log.d("AcceptActivity","readText");
        byte[] payload = record.getPayload();

        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        int languageCodeLength = payload[0] & 0063;

        return new String(payload, textEncoding);
    }
    private void loadData(Intent intent){
        String action = intent.getAction();
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        String s = "";

        // parse through all NDEF messages and their records and pick text type only
        Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) data[0];
        try {
            s = readText(msg.getRecords()[0]);
        }catch(Exception e){
            e.printStackTrace();
        }
        s = new String(msg.getRecords()[0].getPayload());

        mTextView.setText(s);

        mReceivedData = NfcData.fromJSON(s);


    }

    public void saveToDB(NfcData data){
        FriendsDataSource dataSource = new FriendsDataSource(this);
        dataSource.open();
        byte[] b = new byte[]{};
        dataSource.addFriend(new Friend(0,data.getName(),new byte[]{},data.getRegid()));
        dataSource.close();
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.accept_ok:
                new SendRegidTask().execute();
                break;
        }
    }

    private class SendRegidTask extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String apiKey = getString(R.string.project_apikey);

                // 1. URL
                HttpClient httpclient = new DefaultHttpClient();
                URL url = new URL("https://android.googleapis.com/gcm/send");
                HttpPost post = new HttpPost("https://android.googleapis.com/gcm/send");
                post.addHeader("Content-Type", "application/json; charset=UTF-8");
                post.addHeader("Authorization", "key=" + apiKey);

                String encoded = URLEncoder.encode(mOption.getName(),"UTF-8");

                Chat chat = new Chat(Chat.TYPE_ACCEPT,mOption.getRegid(),mReceivedData.getRegid(),encoded, new Date());

                JSONObject obj = new JSONObject();

                JSONArray reg_ids = new JSONArray();
                reg_ids.put(mReceivedData.getRegid());

                obj.put("registration_ids",reg_ids);
                obj.put("data",chat.toJSONObject());
                Log.d("ChatActivity",obj.toString());
                StringEntity stringEntity = new StringEntity(obj.toString());

                post.setEntity(stringEntity);

                HttpResponse response = httpclient.execute(post);
                HttpEntity entity = response.getEntity();

                String responseAsString = EntityUtils.toString(entity);
                Log.d("ChatActivity", responseAsString);

                saveToDB(mReceivedData);
            }

            catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }
}
