package com.example.vinicius.synctexteditor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class EditionActivity extends ActionBarActivity {

    private EditText user_text;
    private String remote_ip;
    private String remote_port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edition);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        remote_ip = intent.getStringExtra(ConnectionActivity.REMOTE_IP);
        remote_port = intent.getStringExtra(ConnectionActivity.REMOTE_PORT);

        user_text = (EditText) findViewById(R.id.user_text);
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

    private void newFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Do you want to create a new file?")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user_text.setText("");
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
                }
                catch (Exception e){

                }
            }
        }).showDialog();
    }

    private void transfer() {
        new TransferTask().execute(user_text.getText().toString());
    }

    private class TransferTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                Socket transfer = new Socket(remote_ip, Integer.parseInt(remote_port));
                DataOutputStream out = new DataOutputStream(transfer.getOutputStream());
                out.writeUTF(params[0]);
                out.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
