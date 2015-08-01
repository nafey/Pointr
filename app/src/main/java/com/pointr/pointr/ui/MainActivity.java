package com.pointr.pointr.ui;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.pointr.pointr.R;
import com.pointr.pointr.data.MyDatabaseHelper;
import com.pointr.pointr.gcm.MyRegIntentService;
import com.pointr.pointr.http.Handled;
import com.pointr.pointr.http.MyGetThread;
import com.pointr.pointr.http.MyHandler;
import com.pointr.pointr.http.MyPostThread;
import com.pointr.pointr.util.MyContactsProvider;
import com.pointr.pointr.util.MyHelper;
import com.pointr.pointr.util.MyLocationProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends Activity implements
        SensorEventListener, Handled {

    private static final String TAG = "LOG__MapActivity";
    private static final String URL_GET_SHARE = "http://www.pointr.me/getshare.php";
    private static final String URL_POST_LOC = "http://www.pointr.me/postloc.php";
    private static final String URL_POST_SHARE = "http://www.pointr.me/postshare.php";

    private static final int WELCOME_ACTIVITY_REQUEST_CODE = 1;
    private static final int CONTACTS_ACTIVITY_REQUEST_CODE = 2;

    //private GoogleMap googleMap;
    //private LatLng clickedLatLng;
    private HashMap<String, LatLng> fetchedLatLng;

    private TextView txtPhone;
    private LinearLayout layContacts;

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private float[] mGravity;
    private float[] mGeomagnetic;

    public void processMarkerData(String content) {
        Log.d(TAG, "processMarkerData()");
        String num;
        Float lat;
        Float lng;
        try{
            Log.d(TAG, content);

            this.fetchedLatLng.clear();

            JSONArray jsonArray = new JSONArray(content);
            JSONObject jsonObject;


            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = (JSONObject) jsonArray.get(i);
                num = (String) jsonObject.get("num");
                lat = Float.parseFloat((String) jsonObject.get("lat"));
                lng = Float.parseFloat((String) jsonObject.get("lng"));

                this.fetchedLatLng.put(num, new LatLng(lat, lng));
            }

            for(String key : this.fetchedLatLng.keySet()) {
                DoubleText doubleText = new DoubleText(this);
                doubleText.setId(MyHelper.getIdFromNumber(Long.valueOf(key)));

                doubleText.setNameText(MyContactsProvider.get().getName(key));

                this.layContacts.addView(doubleText);
            }
        }
        catch (JSONException ex) {
            Log.d(TAG, "JSON experienced an error");
            Log.d(TAG, ex.getMessage());
        }
    }

    public void getLocationFromOther(View view) {
        HashMap<String, String> get = new HashMap<>();
        get.put("toNum", MyHelper.getMyPhoneNum());
        new MyGetThread(get, MainActivity.URL_GET_SHARE, new MyHandler(this)).start();
    }

    public void sendLocationToOther(View view) {
        Intent intent = new Intent(this, ContactsActivity.class);
        this.startActivityForResult(intent, MainActivity.CONTACTS_ACTIVITY_REQUEST_CODE);
    }

    public void postLocation(View view) {
        Log.d(TAG, "postLocation()");

        if (MyLocationProvider.get().getStatus() != MyContactsProvider.STATUS_READY) {
            MyHelper.showStandardToast(this, "Unable to access your location. Please turn on GPS");
            return;
        }


        String phoneNumber = MyHelper.getMyPhoneNum();
        LatLng myLatLng = MyLocationProvider.get().getMyLocation();

        HashMap<String, String> map = new HashMap<>();

        map.put("num", phoneNumber);
        map.put("lat", String.valueOf(myLatLng.latitude));
        map.put("lng", String.valueOf(myLatLng.longitude));

        //Post data
        new MyPostThread(map, MainActivity.URL_POST_LOC).start();
    }

    public void handlerCallback(Message message) {
        Log.d(TAG, "handlerCallback()");
        this.processMarkerData(message.getData().getString("result"));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float azimuth;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            //SensorManager.getRotationMatrix()
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = orientation[0]; // orientation contains: azimuth, pitch and roll
                azimuth = azimuth * 180f / 3.14f;

                for (String key : this.fetchedLatLng.keySet()) {
                    DoubleText doubleText = (DoubleText) findViewById(MyHelper.getIdFromNumber(Long.valueOf(key)));
                    double deg = MyHelper.getBearing(MyLocationProvider.get().getMyLocation(), this.fetchedLatLng.get(key)) - azimuth;
                    //doubleText.setRightText(String.valueOf(deg));
                    doubleText.rotateImage((float) deg);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult()");
        if (requestCode == WELCOME_ACTIVITY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {
                String phone = data.getStringExtra("result");
                this.txtPhone.setText(phone);
                MyHelper.setMyPhoneNum(phone);

                this.getLocationFromOther(null);
            }
            else {
                Log.d(TAG, "The Activity finished unexpectedly");
            }
        } else if (requestCode == CONTACTS_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String toNum = data.getStringExtra("result");
                MyHelper.showStandardToast(this, "From " + MyHelper.getMyPhoneNum() + ", To " + toNum);

                HashMap<String, String> map = new HashMap<>();
                map.put("to", MyHelper.formatNumber(toNum));
                map.put("from", MyHelper.formatNumber(MyHelper.getMyPhoneNum()));
                String url = MainActivity.URL_POST_SHARE;

                new MyPostThread(map, url).start();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup variables
        this.txtPhone = (TextView) findViewById(R.id.txtPhone);
        this.layContacts = (LinearLayout) findViewById(R.id.layContact);

        this.fetchedLatLng = new HashMap<>();

        Log.d(TAG, "Initializing database");
        MyDatabaseHelper db = new MyDatabaseHelper(this);

        int x = db.getPointrCount();

        Log.d(TAG, "The number of records is " + x);


        Log.d(TAG, "invoking registration service");
        
        if (checkPlayServices()) {
            Log.d(TAG, "calling registration service");

            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, MyRegIntentService.class);
            startService(intent);
        }

        //Update your location on startup
        this.postLocation(null);

        //Set my phone number
        if (MyHelper.getMyPhoneNum().equals(this.getResources().getString(R.string.default_phone_number))) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            this.startActivityForResult(intent, MainActivity.WELCOME_ACTIVITY_REQUEST_CODE);
        } else {
            this.txtPhone.setText(MyHelper.getMyPhoneNum());
            this.getLocationFromOther(null);
        }

        //Set up sensor
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (MyLocationProvider.get().getStatus() != MyLocationProvider.STATUS_READY) return;
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (MyLocationProvider.get().getStatus() != MyLocationProvider.STATUS_READY) return;
        mSensorManager.unregisterListener(this);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, 9000).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
