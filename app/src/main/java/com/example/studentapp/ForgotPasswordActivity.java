package com.example.studentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailET;
    Button ResetPwBtn;
    RelativeLayout relLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailET = findViewById(R.id.resetemailET);
        ResetPwBtn = findViewById(R.id.resetpwBtn);
        relLayout = findViewById(R.id.frgtPwRelativeLayout);

    }

    public void sendPwResetMail(View view){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailaddress = emailET.getText().toString();
        if(emailaddress.isEmpty()){
            Snackbar snackbar = Snackbar
                    .make(relLayout, "Email Address field is empty. Please fill the field.", Snackbar.LENGTH_LONG);
            snackbar.show();
            //Toast.makeText(this, "Email Address field is empty. Please fill the field.", Toast.LENGTH_SHORT).show();
        }else {
            try {
                auth.sendPasswordResetEmail(emailaddress).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar snackbar = Snackbar
                                .make(relLayout, "Password Reset Link Sent To The Registered Email Address Successfully.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        //Toast.makeText(ForgotPasswordActivity.this, "Password Reset Link Sent Successfully To The Registered Email Addressfsnac. Kindly use that link to reset your password.", Toast.LENGTH_SHORT).show();
                        emailET.setText("");
                        //startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar snackbar = Snackbar
                                .make(relLayout, "This Email Address Is Not Registerd With Us, Kindly Enter A Registered Email Address.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        //Toast.makeText(ForgotPasswordActivity.this, "This Email Address Is Not Registerd, Kindly Enter A Registered Email Address", Toast.LENGTH_SHORT).show();
                    }
                });

            }catch(Exception e){
                    Toast.makeText(ForgotPasswordActivity.this, "Some error occured, please try again later.", Toast.LENGTH_SHORT).show();

                    }
        }
    }
}
