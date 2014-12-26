package com.realwakka.messenger;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.realwakka.messenger.encryption.Translator;

import java.nio.charset.Charset;

public class NFCFragment extends Fragment implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback ,FragmentLifecycle{
    NfcAdapter mNfcAdapter;

    private static final int MESSAGE_SENT = 1;
    TextView mTextView;
    public NFCFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPauseFragment() {
        Log.d("NFCFragment","onPauseFragment");
    }

    @Override
    public void onResumeFragment() {
        Log.d("NFCFragment","onResumeFragment");

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

//        mNfcAdapter.setNdefPushMessage(getNdefMessage(),getActivity());

        String str = getString(R.string.nfc_instruction1);

        mTextView.setText(str);

        Log.d("NFCFragment","NFC READY");
        return v;
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        NdefMessage msg = null;
        try{
            Option option = Option.load(getActivity());
            byte[] pub_key = Translator.getPublicKey(getActivity()).getEncoded();
            NfcData nfcData = new NfcData(option.getName(),option.getRegid(),pub_key);

            String text = nfcData.toJSON();
            msg = new NdefMessage(
                    new NdefRecord[] { createMimeRecord(
                            "application/vnd.com.example.android.beam", text.getBytes())
                    });

            String str = getString(R.string.nfc_instruction2);
//            mTextView.setText(str);
        }catch(Exception e){
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
        String str = getString(R.string.nfc_instruction1);
//        mTextView.setText(str);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getActivity(), "Invitation send", Toast.LENGTH_LONG).show();
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
}
