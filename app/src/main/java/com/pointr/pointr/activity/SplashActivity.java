package com.pointr.pointr.activity;

import android.app.Activity;
import android.os.Message;
import android.os.Bundle;
import android.widget.TextView;

import com.pointr.pointr.R;
import com.pointr.pointr.multithreading.Handled;
import com.pointr.pointr.multithreading.MyHandler;
import com.pointr.pointr.multithreading.MyInitThread;

public class SplashActivity extends Activity implements Handled {
    private TextView txtLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        this.txtLoading = (TextView) findViewById(R.id.txtLoadingProgress);
        (new MyInitThread(this, new MyHandler(this))).start();
    }

    public void setText(String str) {
        this.txtLoading.setText(str);
    }

    public void handlerCallback(Message message) {
        this.setText(message.getData().getString("result"));
    }
}
