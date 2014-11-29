package com.realwakka.messenger.data;

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
}
