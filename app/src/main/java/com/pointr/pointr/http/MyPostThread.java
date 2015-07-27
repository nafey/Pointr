package com.pointr.pointr.http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

public class MyPostThread extends Thread {
    private static final String TAG = "LOG__MyPostThread";

    private HashMap<String, String> map;
    private String url;

    public MyPostThread(HashMap<String, String> map, String url) {
        this.map = map;
        this.url = url;
    }

    public void run() {
        Log.d(TAG, "run()");
        HttpURLConnection connection = null;

        try {
            String data = "";

            Iterator<String> iter = this.map.keySet().iterator();
            while(iter.hasNext()) {
                String key = iter.next();
                data += URLEncoder.encode(key, "UTF-8") + "=" +
                        URLEncoder.encode(this.map.get(key), "UTF-8");
                if (iter.hasNext()) data += "&";
            }

            Log.d(TAG, "Data is " + data);
            Log.d(TAG, "URL is " + url);

            URL myUrl = new URL(url);
            connection = (HttpURLConnection) myUrl.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(data);
            wr.flush();
            wr.close();

            Log.d(TAG, "Posted data, response is ");

            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((line = reader.readLine()) != null) {
                Log.d(TAG, line);
            }


            reader.close();
        }
        catch(MalformedURLException ex) {
            Log.d(TAG, "Invalid URL");
        }
        catch (IOException ex) {
            Log.d(TAG, "Connection Error");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}