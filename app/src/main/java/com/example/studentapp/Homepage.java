package com.example.studentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Homepage extends AppCompatActivity {

    Toolbar toolbar;
    private FragmentManager fragmentManager;
    CardView Homertnbkcard, Homeabtappcard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        toolbar = findViewById(R.id.toolbar);

        BottomNavigationView bottomNav  = findViewById(R.id.bottom_navigation_homepage);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        Homertnbkcard = findViewById(R.id.Homebookcard);
        Homeabtappcard = findViewById(R.id.Homeaboutappcard);

        Homertnbkcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RtnBkintent = new Intent(Homepage.this , ReturnBookActivity.class);
                startActivity(RtnBkintent);
            }
        });

        Homeabtappcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AbtAppintent = new Intent(Homepage.this , AboutAppActivity.class);
                startActivity(AbtAppintent);
            }
        });


    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_profile:
                    selectedFragment = new ProfileFragment();
                    break;
                case R.id.nav_search_book:
                    selectedFragment = new SearchFragment();

                    setSupportActionBar(toolbar);

                    break;
                case R.id.nav_issue_book:
                    Intent intent=new Intent(Homepage.this,ScanQRCodeActivity.class);

                    startActivity(intent);
                    selectedFragment = new IssueFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            return true;
        }
    };

    @Override
    public void onBackPressed() {
    }
    
}
