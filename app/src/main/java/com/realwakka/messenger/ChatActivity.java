package com.realwakka.messenger;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.realwakka.messenger.data.Chat;

import java.util.ArrayList;


public class ChatActivity extends Activity {
    ListView mChatView;
    ChatAdapter mAdapter;
    ArrayList<Chat> mChatList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatView = (ListView)findViewById(R.id.chat_list);

        mChatList = new ArrayList<Chat>();

        mAdapter = new ChatAdapter(mChatList);


    }

    private class ChatAdapter extends BaseAdapter{
        ArrayList<Chat> list;

        public ChatAdapter(ArrayList<Chat> list){
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
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.item_chat,null);
            TextView textView = (TextView)v.findViewById(R.id.chat_chat);

            Chat chat = list.get(position);

            textView.setText(chat.getText());

            return v;
        }
    }

}
