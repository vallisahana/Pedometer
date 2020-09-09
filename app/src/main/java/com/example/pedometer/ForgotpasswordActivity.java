package com.example.pedometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotpasswordActivity extends AppCompatActivity {

    EditText email;
    Button confirm;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

   // DatabaseUser databaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        email=findViewById(R.id.textInputEditTextEmail);
        confirm=findViewById(R.id.appCompatButtonConfirm);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

       // databaseUser = new DatabaseUser(this);


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Please Wait...!");
                progressDialog.setCancelable(false);
                progressDialog.show();

                firebaseAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ForgotpasswordActivity.this, "Password sent to your Email", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(ForgotpasswordActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(ForgotpasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //verifyFromSQLite();
            }
        });
    }

  /*  private void verifyFromSQLite(){

        if (email.getText().toString().isEmpty()){
            Toast.makeText(this, "Please fill your email", Toast.LENGTH_SHORT).show();
            return;
        }


        if (databaseUser.checkUser(email.getText().toString().trim())) {
            Intent accountsIntent = new Intent(this, ResetpasswordActivity.class);
            accountsIntent.putExtra("EMAIL", email.getText().toString().trim());
            emptyInputEditText();
            startActivity(accountsIntent);
        } else {
            Toast.makeText(this, "Invaild Email", Toast.LENGTH_SHORT).show();
        }
    }

    private void emptyInputEditText(){
        email.setText("");
    }*/
}
