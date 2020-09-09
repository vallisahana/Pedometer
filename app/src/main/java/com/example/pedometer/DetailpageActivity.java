package com.example.pedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DetailpageActivity extends AppCompatActivity {

    Button button;

    EditText name,age,phone,weight,heigh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailpage);

        button=findViewById(R.id.buttonext);
        name=findViewById(R.id.editTextname);
        age=findViewById(R.id.editTextage);
        phone=findViewById(R.id.editTextphone);
        weight=findViewById(R.id.spinnerweight);
        heigh=findViewById(R.id.editTextheight);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(name.getText().toString())){

                }


            }
        });
    }
}
