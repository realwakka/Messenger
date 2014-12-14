package com.realwakka.messenger.data;

import org.json.JSONObject;

/**
 * Created by realwakka on 12/14/14.
 */
public class NfcData {

    private String regid;

    private static final String REGID_KEY="REGID";


    public NfcData(String regid) {
        this.regid = regid;

    }

    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }

    public static String toJSON(NfcData data) throws Exception{
        JSONObject obj = new JSONObject();
        obj.put(REGID_KEY,data.getRegid());
        return obj.toString();
    }

    public static NfcData fromJSON(String json) throws Exception{
        JSONObject obj = new JSONObject(json);
        return new NfcData(obj.getString(REGID_KEY));
    }

}
