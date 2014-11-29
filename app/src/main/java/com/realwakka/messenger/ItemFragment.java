package com.realwakka.messenger;

import android.app.Activity;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.TextView;


public class ItemFragment extends Fragment {

    public static final String ARG_OBJECT = "object";

    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);

        Bundle args = getArguments();

        TextView textView = (TextView)rootView.findViewById(R.id.item_text);
        mListView = (ListView)rootView.findViewById(R.id.friends_list);

        textView.setText("aaaaa");




        return rootView;
    }



}
