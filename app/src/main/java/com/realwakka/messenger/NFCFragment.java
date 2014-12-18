package com.realwakka.messenger;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.realwakka.messenger.data.NfcData;
import com.realwakka.messenger.data.Option;

import java.nio.charset.Charset;

public class NFCFragment extends Fragment implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {
    NfcAdapter mNfcAdapter;
    private static final int MESSAGE_SENT = 1;
    TextView mTextView;
    public NFCFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_nfc, container, false);
        mTextView = (TextView)v.findViewById(R.id.nfc_text);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());

        mNfcAdapter.setNdefPushMessageCallback(this, getActivity());
        mNfcAdapter.setOnNdefPushCompleteCallback(this, getActivity());

        Log.d("NFCFragment","NFC READY");
        return v;
    }


    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        Option option = Option.load(getActivity());

        NfcData nfcData = new NfcData(option.getRegid(),option.getName());

        String text = nfcData.toJSON();
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMimeRecord(
                        "application/vnd.com.example.android.beam", text.getBytes())

                });
        return msg;
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();


    }
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getActivity(), "Message sent!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }

    private void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
    }

}
