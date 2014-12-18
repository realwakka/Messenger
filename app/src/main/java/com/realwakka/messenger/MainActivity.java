package com.realwakka.messenger;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.realwakka.messenger.data.Option;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;


public class MainActivity extends FragmentActivity {
    ListView mListView;
    ViewPager mViewPager;
    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    private final int REGISTER_REQUEST=120;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mDemoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.main_pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);


        final ActionBar actionBar = getActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        Option option = Option.load(this);
        if(option==null){
            Intent intent = new Intent(this,RegisterActivity.class);
            startActivityForResult(intent,REGISTER_REQUEST);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REGISTER_REQUEST:
                if(resultCode==RESULT_CANCELED){
                    finish();
                }
                break;
        }
    }

    class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            Fragment fragment = null;

            switch(i){
                case 0:
                    fragment = new FriendsFragment();
                    break;
                case 1:
                    fragment = new FriendsFragment();
                    break;
                case 2:
                    fragment = new NFCFragment();
                    break;
            }

            Bundle args = new Bundle();

            fragment.setArguments(args);


            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }



}



