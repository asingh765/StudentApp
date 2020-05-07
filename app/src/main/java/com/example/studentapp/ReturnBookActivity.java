package com.example.studentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LauncherActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReturnBookActivity extends AppCompatActivity {

    ListView listView;
    TextView textView;
    List<String> listItem;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    ArrayAdapter<String> adapter;
    AlertDialog.Builder builder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_book);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("issued-books");
        mAuth = FirebaseAuth.getInstance();

        listView= findViewById(R.id.listView);
        textView= findViewById(R.id.textView);
        listItem = new ArrayList<>();
        //listItem.add("a");

        TextView emptyTextView = findViewById(android.R.id.empty);
        listView.setEmptyView(emptyTextView);

        inflateListItem();

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final String RetbookName=adapter.getItem(position);

                final int selectedPosition = position;

                //logic to calculate dates between return date and actual return date and display a dialoguebox to return the book
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //get return date
                        Calendar RetDate = Calendar.getInstance();
                        String RetDateString = dataSnapshot.child(RetbookName.toLowerCase()).child("issueto").getValue().toString();
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

                        //String todayDateString = new  SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                        System.out.println("ISsue date" + RetDateString);

                        float DayDifference = -4321;
                        try {
                            RetDate.setTime(format.parse(RetDateString));

                            //today date - return date
                            long diff = Calendar.getInstance().getTimeInMillis() - RetDate.getTimeInMillis();
                            DayDifference = (float) diff / (24 * 60 * 60 * 1000);

                            System.out.println(DayDifference);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //logic to calculate fine based on DayDifference
                        if(DayDifference == -4321 ){
                            Toast.makeText(ReturnBookActivity.this, "Some Error Occured. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                        else if(DayDifference <= 0 ){
                            String dispmsg = "Are you sure you want to return the book '" + adapter.getItem(selectedPosition) + "' ?";
                            callDlgBox(dispmsg,adapter.getItem(selectedPosition), DayDifference);
                        }
                        else if( DayDifference > 0 && DayDifference <=4 ){

                            String dispmsg = "Book return date has passed by " + (int)DayDifference + " days. Please pay a fine of $5. Proceed with returning the book?";
                            callDlgBox(dispmsg,adapter.getItem(selectedPosition), DayDifference);
                        }
                        else if( DayDifference > 4 && DayDifference <= 30){
                            String dispmsg = "Book return date has passed by " + (int) DayDifference + " days. Please pay a fine of $20. Proceed with returning the book?";
                            callDlgBox(dispmsg,adapter.getItem(selectedPosition), DayDifference);
                        }
                        else if( DayDifference > 30){
                            String dispmsg = "Book return date has passed by " + (int) DayDifference + " days. Please pay a fine equal to the price of the book. Proceed with returning the book?";
                            callDlgBox(dispmsg,adapter.getItem(selectedPosition), DayDifference);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //callDlgBox(adapter.getItem(position));



            }
        });
    }

    private void inflateListItem() {

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    System.out.println(snapshot.child("issuedby").getValue().toString());
                    System.out.println(mAuth.getCurrentUser().getEmail());
                    if(snapshot.child("issuedby").getValue().toString().equals(mAuth.getCurrentUser().getEmail())) {
                        listItem.add(snapshot.child("bookname").getValue().toString());
                    }
                }
                System.out.println("inflating booklist");
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    //this activity creates a dialogue box and displays the fine and gives the option to return back the book by clicking yes or no.
    private void callDlgBox(String DialogueMsg, final String RetBookName, float DayDifference){
        builder = new AlertDialog.Builder(ReturnBookActivity.this);

        builder.setMessage(DialogueMsg)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //finish();
                        myRef.child(RetBookName.toLowerCase()).removeValue();
                        dialog.cancel();
                        Toast.makeText(ReturnBookActivity.this, "Book returned successfully.", Toast.LENGTH_SHORT).show();
                        listItem.remove(RetBookName);
                        adapter.notifyDataSetChanged();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Return This Book");
        alert.show();
    }
}
