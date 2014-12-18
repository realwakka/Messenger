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
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realwakka.messenger.data.Friend;
import com.realwakka.messenger.data.NfcData;
import com.realwakka.messenger.sqlite.FriendsDataSource;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;


public class AcceptActivity extends Activity {
    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mNFCTechLists;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.activity_accept);
        mTextView = (TextView)findViewById(R.id.accept_text);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter != null) {
            mTextView.setText("Read an NFC tag");
        } else {
            mTextView.setText("This phone is not NFC enabled.");
        }

        // create an intent with tag data and deliver to this activity
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // set an intent filter for all MIME data
        IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefIntent.addDataType("*/*");
            mIntentFilters = new IntentFilter[] { ndefIntent };
        } catch (Exception e) {
            Log.e("TagDispatch", e.toString());
        }

        mNFCTechLists = new String[][] { new String[] { NfcF.class.getName() } };

        loadData(getIntent());
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
        if (data != null) {
            Log.d("AcceptActivity","data not null");
            try {
                for (int i = 0; i < data.length; i++) {
                    NdefRecord [] recs = ((NdefMessage)data[i]).getRecords();
                    for (NdefRecord ndefRecord : recs) {
                        s = s+readText(ndefRecord);
                    }
                }
            } catch (Exception e) {
                Log.e("TagDispatch", e.toString());
            }
        }else{
            Log.d("AcceptActivity","data null");
        }

        mTextView.setText(s);

        NfcData nfcData = NfcData.fromJSON(s);

        FriendsDataSource dataSource = new FriendsDataSource(this);
        dataSource.open();
        byte[] b = new byte[]{};

        dataSource.addFriend(new Friend(0,nfcData.getName(),new byte[]{},nfcData.getRegid()));

        dataSource.close();
    }
    @Override
    public void onNewIntent(Intent intent) {
        Log.d("AcceptActivity","onNewIntent");
        loadData(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mNFCTechLists);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

}
