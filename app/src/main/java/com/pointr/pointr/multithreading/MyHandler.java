package com.pointr.pointr.multithreading;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class MyHandler extends Handler {
    private final WeakReference<Handled> handledActivityReference;

    public MyHandler(Handled handledActivity) {
        this.handledActivityReference = new WeakReference<>(handledActivity);
    }

    @Override
    public void handleMessage(Message message) {
        this.handledActivityReference.get().handlerCallback(message);
    }
}
