package com.example.studentapp;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jeevandeshmukh.glidetoastlib.GlideToast;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    EditText email,password;
    private FirebaseAuth mAuth;
    TextView frgtpwET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_in_left);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        frgtpwET = findViewById(R.id.frgtpwET);
        setFrgtPwFunc();
    }

    private void setFrgtPwFunc() {

        frgtpwET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navtofrgtpg = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(navtofrgtpg);
            }
        });



    }

    public void PerformLogin(View view){

        String emailtext = email.getText().toString();
        String passwordtext = password.getText().toString();

        mAuth.signInWithEmailAndPassword(emailtext, passwordtext)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Email Autentication","signInWithEmail:success");
                            //new GlideToast.makeToast(LoginActivity.this,"Login Successful", GlideToast.LENGTHLONG, GlideToast.SUCCESSTOAST).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent routeMainActivity = new Intent(LoginActivity.this, Homepage.class);
                            startActivity(routeMainActivity);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("Email authentication", "signInWithEmail:failure");
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onSignupClick(View view){
        Intent routeSignup = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(routeSignup);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Intent routeMainActivity = new Intent(LoginActivity.this, Homepage.class);
            startActivity(routeMainActivity);
        }
    }

    @Override
    public void onBackPressed() {
    }

}
