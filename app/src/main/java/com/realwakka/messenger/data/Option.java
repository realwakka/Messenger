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


    private boolean sound;
    private boolean vibration;
    private boolean alarm_on_screen_off;


    private static final String PREF_KEY="OPTION";

    public Option(String name, String regid, boolean sound, boolean vibration, boolean alarm_on_screen_off) {
        this.name = name;
        this.regid = regid;
        this.sound = sound;
        this.vibration = vibration;
        this.alarm_on_screen_off = alarm_on_screen_off;
    }


    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public boolean isVibration() {
        return vibration;
    }

    public void setVibration(boolean vibration) {
        this.vibration = vibration;
    }

    public boolean isAlarm_on_screen_off() {
        return alarm_on_screen_off;
    }

    public void setAlarm_on_screen_off(boolean alarm_on_screen_off) {
        this.alarm_on_screen_off = alarm_on_screen_off;
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
