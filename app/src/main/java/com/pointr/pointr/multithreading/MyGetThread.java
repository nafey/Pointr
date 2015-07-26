package com.pointr.pointr.multithreading;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

public class MyGetThread extends Thread {
    private static final String TAG = "LOG__MyGetThread";
    private Handler uiHandler;
    private String url;
    private HashMap<String, String> map;

    public MyGetThread(HashMap<String, String> map, String url, Handler handler) {
        super();
        this.map = map;
        this.url = url;
        this.uiHandler = handler;
    }

    @Override
    public void run() {
        Log.d(TAG, "run()");
        HttpURLConnection connection = null;

        try {
            Log.d(TAG, "Entering get data");

            Iterator<String> iter = this.map.keySet().iterator();
            if (iter.hasNext()) url += "?";
            while(iter.hasNext()) {
                String key = iter.next();
                url += URLEncoder.encode(key, "UTF-8") + "=" +
                        URLEncoder.encode(this.map.get(key), "UTF-8");
                if (iter.hasNext()) url += "&";
            }

            Log.d(TAG, url);

            URL myUrl = new URL(url);
            connection = (HttpURLConnection) myUrl.openConnection();

            connection.setRequestMethod("GET");


            Log.d(TAG, "Getting data");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = bufferedReader.readLine();
            String content = "" + line;

            while (line != null) {
                line = bufferedReader.readLine();
                content += line;
            }

            if (content.contains("]")) content = content.substring(0, content.indexOf("]") + 1);

            Bundle msgBundle = new Bundle();
            msgBundle.putString("result", content);

            Message message = new Message();
            message.setData(msgBundle);

            this.uiHandler.sendMessage(message);
        }
        catch(MalformedURLException ex) {
            Log.e(TAG, "Invalid URL");
        }
        catch (IOException ex) {
            Log.e(TAG, "Connection Error");
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}