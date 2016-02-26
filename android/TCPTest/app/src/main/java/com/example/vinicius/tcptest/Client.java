package com.example.vinicius.tcptest;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;


public class Client extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client, menu);
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

    /** Called when the user clicks the Send button. Thread only version. */
    public void sendMessage(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TextView responseView = (TextView)findViewById(R.id.response_view);

                EditText editText = (EditText)findViewById(R.id.server_ip_address);
                String serverIP = editText.getText().toString();

                editText = (EditText) findViewById(R.id.edit_message);
                String message = editText.getText().toString() + "\n";
                String modifiedSentence;

                try{
                    Socket socket = new Socket(serverIP, 5577);
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());

                    outToServer.writeBytes(message);
                    modifiedSentence = inFromServer.readLine();
                    responseView.setText("Server IP = " + serverIP + "\nFROM SERVER: " + modifiedSentence);
                    socket.close();
                }
                catch (Exception e){
                    responseView.setText("Server IP = " + serverIP + "\nERROR: " + e.getMessage() + "\n");
                }
            }
        }).start();
    }

    /** Called when the user clicks the Send button. AsyncTask version. */
    public void sendMessageV2(View view) {
        EditText editText = (EditText)findViewById(R.id.server_ip_address);
        String serverIP = editText.getText().toString();

        editText = (EditText)findViewById(R.id.server_port);
        String serverPort = editText.getText().toString();

        editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString() + "\n";

        new SendTask().execute(serverIP, serverPort, message);
    }

    private class SendTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... args){
            String modifiedSentence;

            try{
                Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());

                outToServer.writeBytes(args[2]);
                modifiedSentence = inFromServer.readLine();
                modifiedSentence = "Server IP = " + args[0] + "\nFROM SERVER: " + modifiedSentence;
                socket.close();
            }
            catch (Exception e){
                modifiedSentence = "Server IP = " + args[0] + "\nERROR: " + e.getMessage() + "\n";
            }

            return modifiedSentence;
        }

        protected void onPostExecute(String modifiedSentence){
            TextView responseView = (TextView)findViewById(R.id.response_view);
            responseView.setText(modifiedSentence);
        }
    }
}
