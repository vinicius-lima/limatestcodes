package com.example.vinicius.joinstring;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment implements View.OnClickListener {
    Button btn;
    EditText first_number;
    EditText second_number;
    TextView textView;


    public FirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_first, container, false);

        btn = (Button) rootView.findViewById(R.id.add_button);
        btn.setOnClickListener(this);

        first_number = (EditText) rootView.findViewById(R.id.first_number);
        second_number = (EditText) rootView.findViewById(R.id.second_number);
        textView = (TextView) rootView.findViewById(R.id.result);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int a = Integer.parseInt(first_number.getText().toString());
        int b = Integer.parseInt(second_number.getText().toString());
        int result = a + b;
        textView.setText(result);
    }

}
