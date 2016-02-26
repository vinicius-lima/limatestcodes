package com.example.vinicius.tcptest;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class Server extends ActionBarActivity {
    private ServerSocket socket;
    Thread serverThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        serverThread = new Thread(new ServerThread());
        serverThread.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_server, menu);
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

    class ServerThread implements Runnable{

        @Override
        public void run() {
            TextView message = (TextView)findViewById(R.id.message_view);
            String clientSentence;
            String capitalizedSentence;

            try {
                socket = new ServerSocket(5577);
            }
            catch (Exception e){

            }

            while (!Thread.currentThread().isInterrupted()){
                try{
                    Socket clientSocket = socket.accept();

                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());

                    clientSentence = inFromClient.readLine();
                    message.setText("Received: " + clientSentence);
                    capitalizedSentence = clientSentence.toUpperCase() + "\n";
                    outToClient.writeBytes(capitalizedSentence);
                }
                catch (Exception e){
                    message.setText("ERROR:" + e.getMessage() + "\n");
                }
            }
        }
    }
}
