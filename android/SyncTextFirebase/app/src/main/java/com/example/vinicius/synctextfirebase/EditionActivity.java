package com.example.vinicius.synctextfirebase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;


public class EditionActivity extends ActionBarActivity {

    private EditText user_text;
    private String current_file_name;

    private boolean online;
    private String user_name;

    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private ValueEventListener mListener;

    private Firebase ackFirebaseRef;
    //private ValueEventListener ackListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edition);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        user_name = intent.getStringExtra(ConnectionActivity.USER_NAME);
        online = intent.getBooleanExtra(ConnectionActivity.ONLINE, false);

        user_text = (EditText) findViewById(R.id.user_text);
        current_file_name = "blank.txt";

        Firebase.setAndroidContext(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (online)
            connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (online) {
            mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
            mFirebaseRef.removeEventListener(mListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;

            case R.id.action_new:
                newFile();
                return true;

            case R.id.action_save:
                saveFile();
                return true;

            case R.id.action_open:
                chooseFile();
                return true;

            case R.id.action_transfer:
                transfer();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void connect() {
        // Connect to Firebase service.
        mFirebaseRef = new Firebase(ConnectionActivity.FIREBASE_URL).child("synctext/" + user_name);
        ackFirebaseRef = new Firebase(ConnectionActivity.FIREBASE_URL).child("synctext/ack");

        // Check if our app is connected.
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(EditionActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditionActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });

        // Set up retrieving data options.
        mListener = mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataDescription dataDescription = dataSnapshot.getValue(DataDescription.class);
                if (dataDescription != null) {
                    EditText user_text = (EditText) findViewById(R.id.user_text);
                    String text = new String(Base64.decode(dataDescription.getData64Encoded(), Base64.DEFAULT));
                    user_text.setText(text);
                    ackFirebaseRef.setValue(System.currentTimeMillis());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(EditionActivity.this, "Could not retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void newFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Do you want to create a new file?")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user_text.setText("");
                        current_file_name = "blank.txt";
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }

    private void saveFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view_dialog = inflater.inflate(R.layout.dialog_save, null);
        final EditText file_name_text = (EditText) view_dialog.findViewById(R.id.new_file_name);

        builder.setTitle("Save file")
                .setView(view_dialog)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String file_name = file_name_text.getText().toString();
                            File file = new File("/mnt/sdcard/SyncTextEditor/" + file_name);
                            PrintWriter out = new PrintWriter(new FileWriter(file));
                            out.write(user_text.getText().toString());
                            out.close();
                            current_file_name = file_name;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }

    private void chooseFile() {
        new FileChooser(this).setFileListener(new FileChooser.FileSelectedListener() {
            @Override
            public void fileSelected(final File file) {
                try{
                    Scanner in = new Scanner(file);
                    String str = "";
                    while(in.hasNextLine())
                        str += in.nextLine() + "\n";
                    EditText userText = (EditText)findViewById(R.id.user_text);
                    userText.setText(str);
                    current_file_name = file.getName();
                }
                catch (Exception e){

                }
            }
        }).showDialog();
    }

    private void transfer() {
        byte[] data = user_text.getText().toString().getBytes();
        DataDescription dataDescription = new DataDescription(current_file_name, data.length, Base64.encodeToString(data, Base64.DEFAULT));
        mFirebaseRef.setValue(dataDescription);
    }
}
