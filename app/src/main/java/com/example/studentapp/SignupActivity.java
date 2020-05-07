package com.example.studentapp;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText name,email,password,phone, address, dob;
    DatabaseReference myRef;
    RelativeLayout relativeLayout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_in_left);

        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        phone = findViewById(R.id.editTextMobile);
        dob = findViewById(R.id.editTextDOB);
        address = findViewById(R.id.editTextAddress);
        relativeLayout = findViewById(R.id.SignupRelativeLayout);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
    }

    public void onRegisterClk(View view){
        String nametxt = name.getText().toString();
        String emailtxt = email.getText().toString();
        String passwordtxt = password.getText().toString();
        final String phonetxt = phone.getText().toString();
        String dobtxt = dob.getText().toString();
        String addresstxt = address.getText().toString();


        if(nametxt.equals("")){
            name.setError("Empty");
            //Toast.makeText(this, "NAME FIELD EMPTY", Toast.LENGTH_SHORT).show();
        }
        if(emailtxt.equals("")){
            email.setError("Empty");
            //Toast.makeText(this, "EMAIL FIELD EMPTY", Toast.LENGTH_SHORT).show();
        }
        if(passwordtxt.equals("")){
            password.setError("Empty");
            //Toast.makeText(this, "PASSWORD FIELD EMPTY", Toast.LENGTH_SHORT).show();
        }
        if(phonetxt.equals("")){
            phone.setError("Empty");
            //Toast.makeText(this, "PHONE FIELD EMPTY", Toast.LENGTH_SHORT).show();
        }
        if(dobtxt.equals("")){
            dob.setError("Empty");
            //Toast.makeText(this, "DOB FIELD EMPTY", Toast.LENGTH_SHORT).show();
        }
        if(addresstxt.equals("")){
            address.setError("Empty");
            //Toast.makeText(this, "ADDRESS FIELD EMPTY", Toast.LENGTH_SHORT).show();
        }

        if((!isEmailValid(emailtxt)) && emailtxt!=""){
            makesnackbar("Invalid Email-Id");
        }
        else if(nametxt!="" && passwordtxt!="" && emailtxt!="" && phonetxt!="") {
            final User user = new User(nametxt, emailtxt, passwordtxt, phonetxt, addresstxt, dobtxt);

            //signup user on firebase
            mAuth.createUserWithEmailAndPassword(emailtxt, passwordtxt)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("FIREBASE", "createUserWithEmail:success");
                                //FirebaseUser user = mAuth.getCurrentUser();


                                try {
                                    //add user to firebasedatabase table
                                    myRef.child(phonetxt).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("New User Created", "user created");
                                            Snackbar snackbar = Snackbar
                                                    .make(relativeLayout, "User Created Successfully", Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                            Intent routeLogin = new Intent(SignupActivity.this, LoginActivity.class);
                                            startActivity(routeLogin);
                                        }

                                    });
                                }catch (DatabaseException e){
                                    makesnackbar("User with same phone number already exists");
                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("FIREBASE", "createUserWithEmail:failure", task.getException());

                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    makesnackbar("Weak Password");
                                    password.setError("Choose a ");
                                } catch(FirebaseAuthUserCollisionException e) {
                                    makesnackbar("USER ALREADY EXISTS");
                                } catch(Exception e) {
                                    makesnackbar("Unknown exception occured. Authentication failed.");
                                }
                            }

                            // ...
                        }
                    });



        }

    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void makesnackbar(String message){
        Snackbar snkbr = Snackbar.make(relativeLayout,message,Snackbar.LENGTH_LONG);
        snkbr.show();
    }

    public void alreadyAccClk(View view){
        Intent backToLogin = new Intent(SignupActivity.this,LoginActivity.class);
        startActivity(backToLogin);
    }
}