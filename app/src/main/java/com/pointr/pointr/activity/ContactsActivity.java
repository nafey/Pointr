package com.pointr.pointr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pointr.pointr.R;
import com.pointr.pointr.component.Contact;
import com.pointr.pointr.util.MyContactsProvider;

import java.util.ArrayList;
import java.util.Collections;

public class ContactsActivity extends Activity {
    private static final String TAG = "LOG__ContactsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        ListView listView = (ListView) findViewById(R.id.listView);

        ArrayList<Contact> contactsList = MyContactsProvider.get().getContacts();

        String[] values = new String[contactsList.size()];
        for(int i = 0; i < contactsList.size(); i++) {
            values[i] = contactsList.get(i).toString();
        }

        final ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, values);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact c = MyContactsProvider.get().getContacts().get(position);

                Intent intent = ContactsActivity.this.getIntent();
                intent.putExtra("result", c.getNum());
                ContactsActivity.this.setResult(Activity.RESULT_OK, intent);
                ContactsActivity.this.finish();
            }
        });
    }
}
