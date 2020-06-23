package com.pozyx.nfctool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PinEnter extends AppCompatActivity {
    EditText pin;
    Button submit_pin;
    String correct_pin = "1234";
    TextView invalid_password_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_enter);

        pin = (EditText) findViewById(R.id.pinenter);
        submit_pin = (Button) findViewById(R.id.submit_pin_button);
        invalid_password_text = (TextView) findViewById(R.id.invalidtext);
        submit_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pintext = pin.getText().toString();
                if(pintext.equals(correct_pin)){
                    Intent settingsPage = new Intent(PinEnter.this,OptionsPage.class);
                    startActivity(settingsPage);
                }
                else {
                    invalid_password_text.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}