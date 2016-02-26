package com.example.vinicius.synctexteditor;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class ConnectionActivity extends ActionBarActivity {

    public static final String FIREBASE_URL = "https://fiery-inferno-5459.firebaseio.com";
    public static final String USER_NAME = "com.example.vinicius.synctextfirebase.USER_NAME";
    public static final String ONLINE = "com.example.vinicius.synctextfirebase.ONLINE";

    private EditText user_name;
    private boolean online;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        user_name = (EditText)findViewById(R.id.user_name);
        user_name.setText("SyncTextUser");

        online = false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void online(View view) {
        online = true;

        // Start edtion activity.
        Intent intent = new Intent(this, EditionActivity.class);
        intent.putExtra(USER_NAME, user_name.getText().toString());
        intent.putExtra(ONLINE, online);

        startActivity(intent);
    }

    public void offline(View view) {
        Intent intent = new Intent(this, EditionActivity.class);
        startActivity(intent);
    }

    /*public void sendData(String data) {
        DataDescription dataDescription = new DataDescription("sync.txt", data);
        mFirebaseRef.setValue(dataDescription);
    }*/

}
