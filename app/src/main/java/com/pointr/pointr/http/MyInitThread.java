package com.pointr.pointr.http;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.pointr.pointr.ui.MainActivity;
import com.pointr.pointr.ui.SplashActivity;
import com.pointr.pointr.util.MyContactsProvider;
import com.pointr.pointr.util.MyLocationProvider;

public class MyInitThread extends Thread {
    private SplashActivity launcher;
    private Handler handler;

    public MyInitThread(SplashActivity launcher, Handler handler){
        this.launcher = launcher;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {

            this.sendMessage("Loading Your Location");

            while ((MyLocationProvider.get().getStatus() != MyLocationProvider.STATUS_FAILED) &&
                    (MyLocationProvider.get().getStatus() != MyLocationProvider.STATUS_READY)) {
                sleep(500);
            }

            this.sendMessage("Loading Contacts");

            while ((MyContactsProvider.get().getStatus() != MyContactsProvider.STATUS_FAILED) &&
                    (MyContactsProvider.get().getStatus() != MyContactsProvider.STATUS_READY)) {

                sleep(500);
            }

            Intent intent = new Intent(this.launcher, MainActivity.class);
            this.launcher.startActivity(intent);
            this.launcher.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        Bundle msgBundle = new Bundle();
        msgBundle.putString("result", msg);

        Message message = new Message();
        message.setData(msgBundle);

        this.handler.sendMessage(message);
    }
}