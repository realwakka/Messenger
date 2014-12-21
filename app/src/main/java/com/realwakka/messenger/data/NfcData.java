package com.realwakka.messenger.data;

import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by realwakka on 12/14/14.
 */
public class NfcData {
    private String name;
    private String regid;
    private byte[] pub_key;

    private static final String REGID_KEY="REGID";

    public NfcData(String name, String regid, byte[] pub_key) {
        this.name = name;
        this.regid = regid;
        this.pub_key = pub_key;
    }

    public byte[] getPub_key() {
        return pub_key;
    }

    public void setPub_key(byte[] pub_key) {
        this.pub_key = pub_key;
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
