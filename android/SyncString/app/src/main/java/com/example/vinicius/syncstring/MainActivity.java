package com.example.vinicius.syncstring;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    @SuppressWarnings("resource")
                    ServerSocket sv = new ServerSocket(7755);
                    DrawString ds = (DrawString)findViewById(R.id.drawView);
                    while(true){
                        Socket ct = sv.accept();
                        DataInputStream in = new DataInputStream(ct.getInputStream());
                        if(in.readUTF().equals("SEND")){
                            byte[] inBuffer = new byte[28];
                            in.read(inBuffer);
                            ds.updateState(inBuffer);
                            ds.startRun();
                        }
                        else{ // A GET was received.
                            byte[] state = ds.getState();
                            DataOutputStream out = new DataOutputStream(ct.getOutputStream());
                            out.write(state);
                            ds.stopRun();
                        }
                        ct.close();
                    }
                } catch (Exception e) {
                    TextView textView = (TextView)findViewById(R.id.textView);
                    textView.setText(e.getMessage());
                }
            }
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private class SendTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... args){
            TextView textView = (TextView)findViewById(R.id.textView);
            try {
                Socket ct = new Socket(args[0], Integer.parseInt(args[1]));
                //Socket ct = new Socket("10.16.1.29", 5577);
                DataOutputStream out = new DataOutputStream(ct.getOutputStream());
                out.writeUTF("SEND");
                DrawString ds = (DrawString)findViewById(R.id.drawView);
                textView.setText("Sending state...");
                out.write(ds.getState());
                textView.setText("Closing socket!");
                ct.close();
            } catch (Exception e) {
                textView.setText(e.getMessage());
                //e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v){
            DrawString ds = (DrawString)findViewById(R.id.drawView);
            ds.stopRun();
        }
    }

    public void sendState(View view) {
        new SendTask().execute("10.16.1.29", "5577");
    }

    private class GetTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... args){
            TextView textView = (TextView)findViewById(R.id.textView);
            try {
                Socket ct = new Socket(args[0], Integer.parseInt(args[1]));
                DataOutputStream out = new DataOutputStream(ct.getOutputStream());
                out.writeUTF("GET");
                DataInputStream in = new DataInputStream(ct.getInputStream());
                byte[] state = new byte[28];
                in.read(state);
                DrawString ds = (DrawString)findViewById(R.id.drawView);
                textView.setText("Updating state...");
                ds.updateState(state);
                textView.setText("Closing socket!");
                ct.close();
            } catch (Exception e) {
                textView.setText(e.getMessage());
                //e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v){
            DrawString ds = (DrawString)findViewById(R.id.drawView);
            ds.startRun();
        }
    }

    public void getState(View view) {
        new GetTask().execute("10.16.1.29", "5577");
    }
}
