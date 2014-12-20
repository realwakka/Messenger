package com.realwakka.messenger.gcm;

import android.util.Log;

import com.realwakka.messenger.R;
import com.realwakka.messenger.data.Chat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by realwakka on 12/20/14.
 */
public class GcmUtils {
    public static String sendChat(Chat chat,String apiKey) throws Exception{

        HttpClient httpclient = new DefaultHttpClient();
        URL url = new URL("https://android.googleapis.com/gcm/send");
        HttpPost post = new HttpPost("https://android.googleapis.com/gcm/send");
        post.addHeader("Content-Type", "application/json; charset=UTF-8");
        post.addHeader("Authorization", "key=" + apiKey);

        JSONObject obj = new JSONObject();

        JSONArray reg_ids = new JSONArray();
        reg_ids.put(chat.getTo_reg());
        obj.put("registration_ids",reg_ids);
        obj.put("data",chat.toJSONObject());
        StringEntity stringEntity = new StringEntity(obj.toString());

        post.setEntity(stringEntity);

        HttpResponse response = httpclient.execute(post);
        HttpEntity entity = response.getEntity();

        String responseAsString = EntityUtils.toString(entity);

        return responseAsString;
    }
}
