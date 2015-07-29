package com.pointr.pointr.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.pointr.pointr.R;

public class MyRegIntentService extends IntentService {
    private static final String TAG = "MyRegIntentService";

    public MyRegIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent()");
        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_senderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.d(TAG, "GCM Registration Token: " + token);
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }
}
