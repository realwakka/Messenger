package com.realwakka.messenger;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.realwakka.messenger.data.NfcData;
import com.realwakka.messenger.data.Option;
import com.realwakka.messenger.encryption.Translator;

import java.nio.charset.Charset;


public class NfcActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback{
    NfcAdapter mNfcAdapter;
    TextView mTextView;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Toast.makeText(NfcActivity.this, "Invitation send", Toast.LENGTH_LONG).show();
            String str = getString(R.string.nfc_instruction1);
            mTextView.setText(str);
        }
    };

    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        NdefMessage msg = null;
        try{
            Option option = Option.load(this);
            byte[] pub_key = Translator.getPublicKey(this).getEncoded();
            NfcData nfcData = new NfcData(option.getName(),option.getRegid(),pub_key);

            String text = nfcData.toJSON();
            msg = new NdefMessage(
                    new NdefRecord[] { createMimeRecord(
                            getString(R.string.mime_type), text.getBytes())
                    });

        }catch(Exception e){
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        mHandler.obtainMessage().sendToTarget();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        mTextView = (TextView)findViewById(R.id.nfc_text);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNfcAdapter.setNdefPushMessageCallback(null, this);
        mNfcAdapter.setOnNdefPushCompleteCallback(null, this);
    }
}
