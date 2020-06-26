package com.pozyx.nfctool;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AssociateCow extends AppCompatActivity {
    TextView cowid;
    Button associateCow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_associate_cow);


    }

    public void associateCow(View view){
       Toast.makeText(this,"sdfsdf",Toast.LENGTH_LONG);
    }

}