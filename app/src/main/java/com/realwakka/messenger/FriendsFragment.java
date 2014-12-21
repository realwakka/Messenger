package com.realwakka.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.realwakka.messenger.data.Friend;
import com.realwakka.messenger.sqlite.FriendsDataSource;

import java.util.ArrayList;
import java.util.zip.Inflater;


public class FriendsFragment extends Fragment {

    ListView mListView;
    ArrayList<Friend> mFriendList;
    FriendsAdapter mAdapter;
    FriendsDataSource mDataSource;
    BroadcastReceiver mNewFriendReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshFriendsList();
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }



    private void refreshFriendsList(){
        mFriendList = mDataSource.getFriendsList();

        mAdapter = new FriendsAdapter(getActivity(),mFriendList);
        mListView.setAdapter(mAdapter);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        mListView = (ListView)v.findViewById(R.id.friends_list);

        mDataSource = new FriendsDataSource(getActivity());
        mListView.setOnItemClickListener(new FriendClickListener());


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDataSource.open();

        refreshFriendsList();
        IntentFilter intentFilter = new IntentFilter("com.realwakka.messenger.RefreshFriends");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mNewFriendReceiver,intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mDataSource.close();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mNewFriendReceiver);
    }

    class FriendClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Friend friend = mFriendList.get(position);
            Intent intent = new Intent(getActivity(),ChatActivity.class);
            Log.d("FriendsFragment",friend.toJSON());
            intent.putExtra("FRIEND",friend.toJSON());

            startActivity(intent);

        }
    }

    class FriendsAdapter extends BaseAdapter{
        Context context;
        ArrayList<Friend> list;

        FriendsAdapter(Context context, ArrayList<Friend> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.item_friend, viewGroup, false);

            Friend friend = list.get(position);
            TextView mName = (TextView)v.findViewById(R.id.friend_name);
            mName.setText(friend.getName());


            return v;
        }
    }


}
