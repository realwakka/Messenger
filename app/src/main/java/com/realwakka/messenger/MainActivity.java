package com.realwakka.messenger;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
    String[] bar_titles;

    ViewPager.SimpleOnPageChangeListener PageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
        int currentPosition = 0;

        @Override
        public void onPageSelected(int newPosition) {

            FragmentLifecycle fragmentToShow = (FragmentLifecycle)mDemoCollectionPagerAdapter.getItem(newPosition);
            fragmentToShow.onResumeFragment();

            FragmentLifecycle fragmentToHide = (FragmentLifecycle)mDemoCollectionPagerAdapter.getItem(currentPosition);
            fragmentToHide.onPauseFragment();

            currentPosition = newPosition;
            getActionBar().setSelectedNavigationItem(newPosition);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity","onCreate");
        bar_titles = getResources().getStringArray(R.array.tab_title);

        mDemoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.main_pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);


        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        Option option = Option.load(this);
        if(option==null){
            Intent intent = new Intent(this,RegisterActivity.class);
            startActivityForResult(intent,REGISTER_REQUEST);
        }

        for (int i = 0; i < bar_titles.length; i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mDemoCollectionPagerAdapter.getPageTitle(i))
                            .setTabListener(new CustomTabListener()));
        }

        mViewPager.setOnPageChangeListener(PageChangeListener);
        actionBar.setIcon(R.drawable.ic_launcher);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity","onResume");
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

    class CustomTabListener implements ActionBar.TabListener{
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            mViewPager.setCurrentItem(tab.getPosition());

        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {


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
                    fragment = new NFCFragment();
                    break;
                case 2:
                    fragment = new OptionFragment();
                    break;

            }

            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return bar_titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles = getResources().getStringArray(R.array.tab_title);
            return titles[position];
        }
    }



}