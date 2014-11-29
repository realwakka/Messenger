package com.realwakka.messenger.data;

/**
 * Created by realwakka on 14. 11. 28.
 */
public class Friend {
    private int id;
    private String name;
    private byte[] PubicKey;

    public Friend() {

    }

    public Friend(int id, String name, byte[] pubicKey) {
        this.id = id;
        this.name = name;
        PubicKey = pubicKey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
}
