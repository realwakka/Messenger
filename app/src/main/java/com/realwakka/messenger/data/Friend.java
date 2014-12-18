package com.realwakka.messenger.data;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by realwakka on 14. 11. 28.
 */
public class Friend {
    private long id;
    private String name;
    private byte[] PubicKey;
    private String regid;

    private static final String ID_JSON_KEY="ID";
    private static final String NAME_JSON_KEY="NAME";
    private static final String PUB_JSON_KEY="PUB";
    private static final String REGID_JSON_KEY="REGID";


    public Friend() {

    }

    public Friend(long id, String name, byte[] pubicKey, String regid) {
        this.id = id;
        this.name = name;
        PubicKey = pubicKey;
        this.regid = regid;
    }

    public String getRegid() {

        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getPubicKey() {
        return PubicKey;
    }

    public void setPubicKey(byte[] pubicKey) {
        PubicKey = pubicKey;
    }

    public static Friend fromJSON(String json){
        Gson gson = new Gson();
        Friend friend = gson.fromJson(json,Friend.class);
        return friend;
    }

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }



}
