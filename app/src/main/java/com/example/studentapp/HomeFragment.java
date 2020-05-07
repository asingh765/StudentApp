package com.example.studentapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class HomeFragment extends Fragment {

    CardView rtnbkcard, abtappcard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View HomeView = inflater.inflate(R.layout.fragment_home, container, false);
        rtnbkcard = HomeView.findViewById(R.id.returnbookcard);
        abtappcard = HomeView.findViewById(R.id.aboutappcard);

        rtnbkcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RtnBkintent = new Intent(getActivity(), ReturnBookActivity.class);
                startActivity(RtnBkintent);
            }
        });

        abtappcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AbtAppintent = new Intent(getActivity(), AboutAppActivity.class);
                startActivity(AbtAppintent);
            }
        });



        return HomeView;
    }

    private void rtnbkclk(View view){

    }

}