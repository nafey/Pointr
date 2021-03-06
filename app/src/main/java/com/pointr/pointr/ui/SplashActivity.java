package com.pointr.pointr.ui;

import android.app.Activity;
import android.os.Message;
import android.os.Bundle;
import android.widget.TextView;

import com.pointr.pointr.R;
import com.pointr.pointr.http.Handled;
import com.pointr.pointr.http.MyHandler;
import com.pointr.pointr.http.MyInitThread;

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
