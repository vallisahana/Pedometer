package com.example.pedometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");




    EditText editTextname,editTextemail,editTextpassword,editTextphone;
    Button buttonuseregister;
    TextView textViewuserlogin;
    CheckBox checkpass;

    //DatabaseUser DH = new DatabaseUser(this);

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        editTextname=findViewById(R.id.editname);
        editTextemail=findViewById(R.id.editemail);
        editTextpassword=findViewById(R.id.editpassword);
        editTextphone=findViewById(R.id.editphone);
        buttonuseregister=findViewById(R.id.buttonregister);
        textViewuserlogin=findViewById(R.id.textsign);
        checkpass=findViewById(R.id.checkregister);


        textViewuserlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(in);
            }
        });

        checkpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                } else {
                    editTextpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        buttonuseregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullname = editTextname.getText().toString().trim();
                final String Email = editTextemail.getText().toString().trim();
                final String Pass = editTextpassword.getText().toString().trim();
                String Phone = editTextphone.getText().toString().trim();

                if(TextUtils.isEmpty(fullname)){
                    editTextname.setError( "Name is Required" );
                    editTextname.requestFocus() ;
                    return;
                }
                if(TextUtils.isEmpty(Email)){
                    editTextemail.setError( "Email is Required" );
                    editTextemail.requestFocus() ;
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(Email ).matches()){
                    editTextemail.setError( "please enter the vaild email" );
                    editTextemail.requestFocus(  );
                    return;
                }
                if(TextUtils.isEmpty(Pass)){
                    editTextpassword.setError( "password is Required" );
                    editTextpassword.requestFocus() ;
                    return;
                }

                if(!PASSWORD_PATTERN.matcher(Pass).matches()){
                    editTextpassword.setError( "please enter 1 uppercase,1 lowercase,1 digit,1 special character " );
                    editTextpassword.requestFocus(  );
                    return;
                }

                if(Pass.length()<6){
                    editTextpassword.setError( "minimum length of password should be 6" );
                    editTextpassword.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(Phone)){
                    editTextphone.setError( "Number is Required" );
                    editTextphone.requestFocus() ;
                    return;
                }

                if(!isValidPhone(editTextphone.getText().toString())){
                    editTextphone.setError( "length of phone number should be 10" );
                    editTextphone.requestFocus();
                    return;
                }

                progressDialog.setMessage("Registering...!");
                progressDialog.setCancelable(false);
                progressDialog.show();

                firebaseAuth.createUserWithEmailAndPassword(Email, Pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                User us=new User(fullname,Email,Pass);
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    firebaseUser=firebaseAuth.getCurrentUser();
                                    databaseReference= FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child(firebaseUser.getUid()).setValue(us);
                                    Snackbar.make(buttonuseregister, "Your SignUp is successful", Snackbar.LENGTH_SHORT)
                                            .setAction("Action", null).show();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                                    finish();
                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Snackbar.make(buttonuseregister, "Already you have an account", Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();
                                    } else {
                                        Snackbar.make(buttonuseregister,task.getException().getMessage(), Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();
                                    }
                                }
                            }
                        });

                /*boolean res = DH.User_Data(editTextname.getText().toString(),
                        editTextemail.getText().toString(),
                        editTextpassword.getText().toString(), editTextphone.getText().toString());
                if (res) {

                    Toast.makeText(RegisterActivity.this, "Registered Successfully",
                            Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Try Again",
                            Toast.LENGTH_LONG).show();
                }*/

            }
        });



    }

    public boolean isValidPhone(String phone) {

        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", phone))
        {
            if(phone.length() <=9 || phone.length() > 10)
            {
                check = false;

            }
            else
            {
                check = true;

            }
        }
        else
        {
            check=false;
        }
        return check;
    }
}
