package com.pointr.pointr.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.pointr.pointr.activity.MyApplication;
import com.pointr.pointr.component.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MyContactsProvider {
    private static final String TAG = "LOG_MyContactsProvider";

    private static MyContactsProvider instance;
    private

    HashMap<String, Contact> contacts;


    public static final int STATUS_NEW = 0;
    public static final int STATUS_READY = 1;
    public static final int STATUS_FAILED = 2;

    private int status;

    private MyContactsProvider() {


        this.status = MyContactsProvider.STATUS_NEW;

        ContentResolver cr = MyApplication.getAppContext().getContentResolver();

        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        try {
            contacts = new HashMap<>();

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {

                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String keyPhoneNo = MyHelper.formatNumber(phoneNo);
                            contacts.put(keyPhoneNo, new Contact(name, phoneNo));
                        }
                        pCur.close();
                    }
                }
            }

            cur.close();

            this.status = MyContactsProvider.STATUS_READY;
        } catch (Exception ex) {
            this.status = MyContactsProvider.STATUS_FAILED;

            Log.d(TAG, "Error while fetching contacts");
        }

    }

    public static MyContactsProvider get() {
        if (instance == null) {
            instance = new MyContactsProvider();
        }

        return instance;
    }

    public ArrayList<Contact> getContacts() {
        ArrayList<Contact> contactsList;
        contactsList = new ArrayList<>();

        for(String key : contacts.keySet()) {
            contactsList.add(contacts.get(key));
        }

        Collections.sort(contactsList);

        return  contactsList;
    }

    public String getName(String num) {
        return this.contacts.get(MyHelper.formatNumber(num)).getName();
    }

    public int getStatus() {
        return  status;
    }
}