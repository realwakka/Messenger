package com.realwakka.messenger.data;

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

    public Friend(int id, String name, byte[] pubicKey, String regid) {
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

    public static Friend fromJSON(String json) throws Exception{
        JSONObject obj = new JSONObject(json);
        int id = obj.getInt(ID_JSON_KEY);
        String name = obj.getString(NAME_JSON_KEY);
        byte[] pub = obj.getString(PUB_JSON_KEY).getBytes();
        String regid = obj.getString(REGID_JSON_KEY);
        return new Friend(id,name,pub,regid);
    }

    public static String toJSON(Friend friend) throws Exception{
        String json=null;

        JSONObject obj = new JSONObject();

        obj.put(ID_JSON_KEY,friend.getId());
        obj.put(NAME_JSON_KEY,friend.getName());
        obj.put(PUB_JSON_KEY,friend.getPubicKey());
        obj.put(REGID_JSON_KEY,friend.getRegid());

        json = obj.toString();

        return json;
    }



}
