package com.realwakka.messenger;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

import com.realwakka.messenger.data.Option;

public class OptionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    Switch mSound;
    Switch mVibration;
    Switch mWakeUp;

    EditText mEditName;

    Option mOption;

    public OptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OptionFragment","onCreate");





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("OptionFragment","onCreateView");
        View v = inflater.inflate(R.layout.fragment_option, container, false);
        mEditName = (EditText)v.findViewById(R.id.option_name);
        mVibration = (Switch)v.findViewById(R.id.option_sound);
        mSound = (Switch) v.findViewById(R.id.option_sound);
        mWakeUp = (Switch)v.findViewById(R.id.option_screen_off);

        mOption = Option.load(getActivity());

        mEditName.setText(mOption.getName());
        mVibration.setChecked(mOption.isVibration());
        mSound.setChecked(mOption.isSound());
        mWakeUp.setChecked(mOption.isAlarm_on_screen_off());


        return v;
    }


}
