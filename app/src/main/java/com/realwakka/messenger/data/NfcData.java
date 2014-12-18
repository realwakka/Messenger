package com.realwakka.messenger.data;

import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by realwakka on 12/14/14.
 */
public class NfcData {
    private String name;
    private String regid;


    private static final String REGID_KEY="REGID";

    public NfcData(String regid, String name) {
        this.regid = regid;
        this.name = name;
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

    public String toJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static NfcData fromJSON(String json){
        Gson gson = new Gson();
        return gson.fromJson(json,NfcData.class);

    }

}
