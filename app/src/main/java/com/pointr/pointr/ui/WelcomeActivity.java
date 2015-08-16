package com.pointr.pointr.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.pointr.pointr.R;
import com.pointr.pointr.util.MyHelper;


public class WelcomeActivity extends Activity {
    private static final String TAG = "LOG__WelcomeActivity";
    private EditText txtPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        this.txtPhone = (EditText) findViewById(R.id.txtPhone);
    }

    public void doneClick(View v) {
        Log.d(TAG, "doneClick()");

        String input = this.txtPhone.getText().toString();
        if (!MyHelper.isCorrectNumberFormat(input)) {
            MyHelper.showStandardToast("Incorrect phone number");
            return;
        }

        input = MyHelper.formatNumber(input);

        Bundle b = new Bundle();
        b.putString("result", input);
        Intent intent = getIntent();
        intent.putExtras(b);

        this.setResult(Activity.RESULT_OK, intent);
        this.finish();
    }

}