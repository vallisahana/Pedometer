package com.example.pedometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText editTextemail, editTextpass;
    Button buttonuserlogin;
    TextView textView;
    CheckBox chk;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    //DatabaseUser databaseUser= new DatabaseUser(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        editTextemail = findViewById(R.id.editemail);
        editTextpass = findViewById(R.id.editpassword);
        buttonuserlogin = findViewById(R.id.buttonlogin);
        textView = findViewById(R.id.textlogin);
        chk = (CheckBox) findViewById(R.id.checklogin);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                startActivity(intent);
            }
        });

        chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextpass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                } else {
                    editTextpass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });


        buttonuserlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = editTextemail.getText().toString();
                String Password = editTextpass.getText().toString();


                progressDialog.setMessage("Please Wait...!");
                progressDialog.setCancelable(false);
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(LoginActivity.this, WelcomepageActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();

                        } else {
                            Snackbar.make(buttonuserlogin, task.getException().getMessage(), Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            startActivity(new Intent(LoginActivity.this,WelcomepageActivity.class));
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            finish();
        }
    }

    public void forgot(View view) {

        Intent intent=new Intent(LoginActivity.this,ForgotpasswordActivity.class);
        startActivity(intent);
    }
}