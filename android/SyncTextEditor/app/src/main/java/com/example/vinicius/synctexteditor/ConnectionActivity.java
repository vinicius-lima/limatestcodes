package com.example.vinicius.synctexteditor;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class ConnectionActivity extends ActionBarActivity {

    public final static String REMOTE_IP = "com.example.vinicius.synctexteditor.REMOTE_IP";
    public final static String REMOTE_PORT = "com.example.vinicius.synctexteditor.REMOTE_PORT";

    private EditText local_port_text;
    private EditText remote_ip_text;
    private EditText remote_port_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        local_port_text = (EditText)findViewById(R.id.local_port);
        local_port_text.setText("7755");
        remote_ip_text = (EditText)findViewById(R.id.remote_ip);
        remote_ip_text.setText("10.16.1.33");
        remote_port_text = (EditText)findViewById(R.id.remote_port);
        remote_port_text.setText("5577");
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

    public void connect(View view) {
        Intent intent = new Intent(this, EditionActivity.class);
        String remote_ip = remote_ip_text.getText().toString();
        String remote_port = remote_port_text.getText().toString();
        intent.putExtra(REMOTE_IP, remote_ip);
        intent.putExtra(REMOTE_PORT, remote_port);

        Thread t = new Thread(new ReceiveThread());
        t.start();

        startActivity(intent);
    }

    public void offline(View view) {
        Intent intent = new Intent(this, EditionActivity.class);
        startActivity(intent);
    }

    private class ReceiveThread implements Runnable {

        @Override
        public void run() {
            try {
                ServerSocket sock = new ServerSocket(Integer.parseInt(local_port_text.getText().toString()));
                while (true){
                    receive(sock);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void receive(ServerSocket sock) {
            try {
                Socket source = sock.accept();
                DataInputStream in = new DataInputStream(source.getInputStream());
                String text = in.readUTF();
                System.out.println(text);
                //char[] aux = text.toCharArray();
                //System.out.println("Text received!!!\n\n" + text);
                //if(text != null){
                    EditText user_text = (EditText) findViewById(R.id.user_text);
                    //user_text.setText(new String(aux));
                    user_text.setText(text);
                //}
                source.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
