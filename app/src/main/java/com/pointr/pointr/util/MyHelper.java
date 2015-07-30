package com.pointr.pointr.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.pointr.pointr.R;
import com.pointr.pointr.activity.MyApplication;

public class MyHelper {
    private static final String TAG = "LOG__MyHelper";
    private static final String PREFERENCES_FILE_NAME = "POINTR_PREFERENCES";
    private static final String MY_NUM_PREF_NAME = "MY_NUM_PREF";

    private static SharedPreferences sharedPrefInstance;

    private static String myPhoneNumber;

    public static void showStandardToast(Activity activity, String message) {
        Context c = activity.getApplicationContext();
        Toast toast = Toast.makeText(c, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showStandardToast(String text) {
        Toast toast = Toast.makeText(MyApplication.getAppContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static double getBearing(LatLng l1, LatLng l2) {
        double y = Math.sin(l2.longitude - l1.longitude) * Math.cos(l2.latitude);
        double x = Math.cos(l1.latitude) * Math.sin(l2.latitude) -
                Math.sin(l1.latitude) * Math.cos(l2.latitude) * Math.cos(l2.longitude - l1.longitude);

        return Math.toDegrees(Math.atan2(y, x));
    }

    public static boolean isCorrectNumberFormat(Long l) {
        Log.d(TAG, "isCorrectNumberFormat()");
        return l > 999999999L && l <= 9999999999L;
    }

    public static boolean isCorrectNumberFormat(String str) {
        Log.d(TAG, "isCorrectNumberFormat()");

        if (null == str) return false;
        if ("".equals(str)) return false;

        str = formatNumber(str);

        try {
            Long l = Long.valueOf(str);
            return isCorrectNumberFormat(l);
        } catch (NumberFormatException ex) {
            Log.d(TAG, ex.getMessage());
            return false;
        }
    }

    public static String formatNumber(String str) {
        if (str.length() >= 10) return str.substring(str.length() - 10, str.length());
        else return str;
    }

    public static int getIdFromNumber(Long num) {
        return (int)(num % 100000);
    }

    public static String getMyPreference(String name, String default_) {
        if (null == sharedPrefInstance) {
            sharedPrefInstance = MyApplication.getAppContext().getSharedPreferences(PREFERENCES_FILE_NAME,
                    Context.MODE_PRIVATE);
        }

        return sharedPrefInstance.getString(name, default_);
    }

    public static void setMyPreference(String name, String value) {
        SharedPreferences.Editor prefsEditor = sharedPrefInstance.edit();

        prefsEditor.putString(name, value);
        prefsEditor.apply();
    }

    public static String getMyPhoneNum() {
        //If fetching for the first time get from stored preferences
        if (!MyHelper.isCorrectNumberFormat(myPhoneNumber)) {
            myPhoneNumber = getMyPreference(MY_NUM_PREF_NAME,
                    MyApplication.getAppContext().getResources().getString(R.string.default_phone_number));

            if (!MyHelper.isCorrectNumberFormat(myPhoneNumber)) {
                setMyPhoneNum(MyApplication.getAppContext().getResources().getString(R.string.default_phone_number));
            }
        }

        return myPhoneNumber;
    }

    public static void setMyPhoneNum(String num) {
        if (MyHelper.isCorrectNumberFormat(num)) {
            MyHelper.setMyPreference(MY_NUM_PREF_NAME, num);
            myPhoneNumber = num;
        }
    }
}