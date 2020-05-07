package com.example.studentapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private TextView profileEmail;
    private TextView profileName;
    private TextView profilePhone;
    private TextView profileDob;
    //private TextView profileHeaderName;
    private TextView profileAddress;
    private Button logoutBtn;
    AlertDialog.Builder builder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        profileEmail = getView().findViewById(R.id.profile_email);
        profileDob = getView().findViewById(R.id.profile_dob);
        profileName = getView().findViewById(R.id.profile_name);
        profilePhone = getView().findViewById(R.id.profile_mobile);
        profileAddress = getView().findViewById(R.id.profile_address);
        logoutBtn = getView().findViewById(R.id.LogoutBtn);
        //profileHeaderName = (TextView) getView().findViewById(R.id.profile_Header_name);


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(getActivity());

                builder.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //finish();
                                dialog.cancel();
                                mAuth.signOut();
                                //Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                                Intent logoutIntent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(logoutIntent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Logout");
                alert.show();
            }
        });


        if(profileEmail.getText().toString().equals("") || profileName.getText().toString().equals("") || profilePhone.getText().toString().equals("") || profileDob.getText().toString().equals("")){
            Log.i("ProfileFragment.java","Getting Profile Info");
            getProfileInfo();
        }

        super.onViewCreated(view, savedInstanceState);
    }

    private void getProfileInfo(){
        final String CurrentUserEmail = mAuth.getCurrentUser().getEmail();

        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ProfileUser profileuser = snapshot.getValue(ProfileUser.class);
                            System.out.println(profileuser.email);
                            System.out.println("Google Signed In User" + CurrentUserEmail);
                            if(profileuser.email.equals(CurrentUserEmail)){
                                profileEmail.setText(profileuser.getEmail());
                                System.out.println("EMAIL ADDRESS" + profileuser.getEmail());
                                profileDob.setText(profileuser.getDob());
                                profileName.setText(profileuser.getName());
                                profilePhone.setText(profileuser.getPhone());
                                profileAddress.setText(profileuser.getAddress());
                                //profileHeaderName.setText(profileuser.getName().toUpperCase());
                                return ;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void logout(View view){

    }
}
