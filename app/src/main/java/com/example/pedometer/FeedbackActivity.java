package com.example.pedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FeedbackActivity extends AppCompatActivity {


    EditText e1,e2,e3;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        e1=(EditText)findViewById(R.id.editText6);
        e2=(EditText)findViewById(R.id.editText7);
        e3=(EditText)findViewById(R.id.editText8);
        b1=(Button) findViewById(R.id.button7);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(Intent.ACTION_SEND);
                i.setType("message/html");
                i.putExtra(Intent.EXTRA_EMAIL,new String("sahana0413@gmail.com"));

                i.putExtra(Intent.EXTRA_SUBJECT,new String("Feedback From App"));

                i.putExtra(Intent.EXTRA_TEXT,"Name:"+e1.getText()+"\n message:"+e3.getText());
                try {
                    startActivity(Intent.createChooser(i, "please select email"));
                }
                catch (android.content.ActivityNotFoundException ex){
                    Toast.makeText(FeedbackActivity.this,"There are no email cilents",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
