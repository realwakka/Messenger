package com.realwakka.messenger;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;


public class LockActivity extends Activity {
    Button[] buttons;
    EditText[] edits;
    ArrayList<String> mPasswords;

    View.OnClickListener mButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Button button = (Button)v;
            String text = button.getText().toString();
            if(text.equals("DEL")){
                mPasswords.remove(mPasswords.size()-1);

            }else{
                if(mPasswords.size() < edits.length){

                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        mPasswords = new ArrayList<String>();
        buttons = new Button[11];

        for(int i=0; i<buttons.length; i++) {

            String buttonID = "lock_button" + i;

            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[i] = ((Button) findViewById(resID));
            buttons[i].setOnClickListener(mButtonListener);

        }

        edits = new EditText[4];

        for(int i=0; i<edits.length; i++) {

            String editID = "lock_edit" + i;
            int resID = getResources().getIdentifier(editID, "id", getPackageName());
            edits[i] = ((EditText) findViewById(resID));

        }

    }


}
