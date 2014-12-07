package com.realwakka.messenger.data;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by realwakka on 14. 11. 29.
 */
public class Chat {

    private String text;
    private Date date;

    public Chat(String text, Date date) {
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

    public String toJSON() throws Exception{
        JSONObject obj = new JSONObject();
        obj.put("text",text);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String date_str = format.format(date);

        obj.put("date",date_str);
        return obj.toString();
    }

    public static Chat fromJSON(String json) throws Exception{
        JSONObject obj = new JSONObject(json);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = format.parse(obj.getString("date"));
        return new Chat(obj.getString("text"),d);
    }
}
