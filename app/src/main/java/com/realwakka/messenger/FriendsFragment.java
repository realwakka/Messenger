package com.realwakka.messenger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.realwakka.messenger.data.Friend;
import com.realwakka.messenger.sqlite.FriendsDataSource;

import java.util.ArrayList;
import java.util.zip.Inflater;


public class FriendsFragment extends Fragment implements FragmentLifecycle{

    ListView mListView;
    ArrayList<Friend> mFriendList;
    FriendsAdapter mAdapter;
    BroadcastReceiver mNewFriendReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context,"New Friend Added",Toast.LENGTH_LONG).show();
            refreshFriendsList();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FriendsFragment","onCreate");

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFriendsList();
    }

    @Override
    public void onPauseFragment() {

    }

    @Override
    public void onResumeFragment() {

    }

    private void refreshFriendsList(){
        FriendsDataSource source = new FriendsDataSource(getActivity());
        source.open();
        mFriendList = source.getFriendsList();
        source.close();

        mAdapter = new FriendsAdapter(getActivity(),mFriendList);
        mListView.setAdapter(mAdapter);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        mListView = (ListView)v.findViewById(R.id.friends_list);

        mListView.setOnItemClickListener(new FriendClickListener());
        mListView.setOnItemLongClickListener(new FriendLongClickListener());
        IntentFilter intentFilter = new IntentFilter(getString(R.string.action_new_friend));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mNewFriendReceiver,intentFilter);

        refreshFriendsList();

        Log.d("FriendsFragment","onCreateView");
        return v;
    }

    private void openDeleteDialog(final Friend friend){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to delete "+friend.getName())
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteFriend(friend);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.show();
    }
    private void deleteFriend(Friend friend){
        FriendsDataSource source = new FriendsDataSource(getActivity());
        source.open();
        source.deleteFriendByRegid(friend.getRegid());
        source.close();
        refreshFriendsList();

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

    class FriendLongClickListener implements AdapterView.OnItemLongClickListener{
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            openDeleteDialog(mFriendList.get(position));
            return true;
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
