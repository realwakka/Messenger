package com.realwakka.messenger;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.realwakka.messenger.data.Option;
import com.realwakka.messenger.encryption.Translator;

import java.io.IOException;


public class RegisterActivity extends Activity {
    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mEditText = (EditText)findViewById(R.id.register_name);

    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.register_ok:
                new GetRegIdTask().execute();
                break;
        }
    }


    public void registerOption(String regid){
        Option option = new Option(mEditText.getText().toString(),regid,true);
        option.save(this);
    }
    private class GetRegIdTask extends AsyncTask<Void,Void,String> {
        GoogleCloudMessaging gcm;

        @Override
        protected String doInBackground(Void... params) {
            String regid = null;
            try {
                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                regid = gcm.register(getString(R.string.project_number));
            } catch (IOException ex) {
                ex.printStackTrace();

            }
            return regid;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (s == null) {
                    setResult(RESULT_CANCELED);
                } else {
                    setResult(RESULT_OK);
                    registerOption(s);
                    if(!Translator.areKeysPresent())
                        Translator.generateKey();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            finish();
        }
    }

}
