package com.example.vinicius.joinstring;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_search:
                //openSearch();
                return true;
            case R.id.action_settings:
                //openSettings();
                return true;
            case R.id.action_fragments:
                Intent intent = new Intent(this, TwoFragActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Called when the user clicks the Join button */
    public void joinString(View view) {
        EditText editText = (EditText) findViewById(R.id.first_part);
        String first_part = editText.getText().toString();
        editText = (EditText) findViewById(R.id.second_part);
        String second_part = editText.getText().toString();
        String result = first_part + " " + second_part;
        TextView textView = (TextView) findViewById(R.id.result);
        textView.setText(result);
    }

    /** Called when the user clicks on a Text Field */
    public void resetText(View view) {
        EditText editText = (EditText) view;
        editText.setText("");
    }
}
