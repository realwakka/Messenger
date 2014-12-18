package com.realwakka.messenger.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.realwakka.messenger.R;

/**
 * Created by realwakka on 12/14/14.
 */
public class Option {
    private String name;
    private String regid;
    private boolean alarm;

    private static final String PREF_KEY="OPTION";

    public Option(String name, String regid, boolean alarm) {
        this.name = name;
        this.regid = regid;
        this.alarm = alarm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public void save(Context context){
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(PREF_KEY,toJSON());
        editor.commit();

    }
    public static Option load(Context context) {
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String json = pref.getString(PREF_KEY, null);

        if (json == null) {
            return null;
        } else{

            return fromJSON(json);
        }
    }



    public String toJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Option fromJSON(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, Option.class);
    }
}
