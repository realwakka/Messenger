package com.realwakka.messenger.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.util.EncodingUtils;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by realwakka on 14. 11. 29.
 */
public class Chat {
    private int type;
    private String from_reg;
    private String to_reg;
    private String text;
    private Date date;

    public static final int TYPE_ACCEPT=0;
    public static final int TYPE_MESSAGE=1;

    public Chat(int type, String from_reg, String to_reg, String text, Date date) {
        this.type = type;
        this.from_reg = from_reg;
        this.to_reg = to_reg;
        this.text = text;
        this.date = date;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String toJSON(){
        Gson gson = new GsonBuilder()
                .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
        String json = gson.toJson(this);


        return gson.toJson(this);


    }

    public static Chat fromJSON(String json) throws Exception{
        Gson gson = new GsonBuilder()
                .setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
        return gson.fromJson(json,Chat.class);
    }

    public JSONObject toJSONObject() throws Exception{

        JSONObject obj = new JSONObject(toJSON());

        return obj;

    }

}
