package com.example.studentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.content.ContentValues.TAG;

public class ScanQRCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView zXingScannerView;
    DatabaseReference myRef;
    EditText bookname, authorname,edition, isdn, preface, yop, issueto, issuefrom, bookcat;
    RelativeLayout relativeLayout;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_scan_qrcode);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("issued-books");
        relativeLayout = findViewById(R.id.ScanQrCodeRelLayout);

        mAuth = FirebaseAuth.getInstance();


        if(isCameraPermissionGranted()){
            System.out.println("Camera permission granted");
            zXingScannerView =  new ZXingScannerView(this);
            setContentView(zXingScannerView);
            zXingScannerView.setResultHandler(this);
            zXingScannerView.startCamera();

        }
        else{
            //finish();//testing intent for result
            //startActivity(backtoHomepage);
            onBackPressed();
        }
    }

    private  boolean isCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ScanQRCodeActivity.this, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Camera Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Camera Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Camera Permission is granted");
            return true;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }



    @Override
    public void onBackPressed() {
        Intent backtoHomepage = new Intent(ScanQRCodeActivity.this, Homepage.class);
        startActivity(backtoHomepage);
        //super.onBackPressed();
    }

    @Override
    public void handleResult(Result result) {
        //Toast.makeText(this, result.getText(), Toast.LENGTH_SHORT).show();

        if(result.getText().equals("")){
            onResume();
        }
        else{
            //fetched the data from QR code. Now do the processing.
            try {
                zXingScannerView.stopCamera();
                setContentView(R.layout.activity_scan_qrcode);
                HashMap<String, String> BookDetailsHashmap = new HashMap<>();
                BookDetailsHashmap = StringToHashmap(result.getText());

                if(BookDetailsHashmap.get("authorname").equals("") || BookDetailsHashmap.get("bookname").equals("") || BookDetailsHashmap.get("edition").equals("") || BookDetailsHashmap.get("isdn").equals("") || BookDetailsHashmap.get("preface").equals("") || BookDetailsHashmap.get("yop").equals("")){
                    onBackPressed();
                    Toast.makeText(this, "Some of book data fields are empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    //populate all the fields with qr code result data.
                    System.out.println(BookDetailsHashmap.toString());
                    bookname = findViewById(R.id.booknameETQR);
                    bookname = findViewById(R.id.booknameETQR);
                    authorname = findViewById(R.id.authornameET);
                    edition = findViewById(R.id.editionET);
                    isdn = findViewById(R.id.isdnET);
                    preface = findViewById(R.id.prefaceET);
                    yop = findViewById(R.id.yopET);
                    bookcat = findViewById(R.id.bookcatET);

                    issuefrom = findViewById(R.id.IssueFromET);
                    issueto = findViewById(R.id.IssueToET);


                    bookname.setText(BookDetailsHashmap.get("bookname"));
                    authorname.setText(BookDetailsHashmap.get("authorname"));
                    edition.setText(BookDetailsHashmap.get("edition"));
                    isdn.setText(BookDetailsHashmap.get("isdn"));
                    preface.setText(BookDetailsHashmap.get("preface"));
                    yop.setText(BookDetailsHashmap.get("yop"));
                    bookcat.setText(BookDetailsHashmap.get("category"));

                    String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                    issuefrom.setText(date);

                    //logic to display datepicker on focus of issueto edittext.
                    issueto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Calendar c = Calendar.getInstance();
                            int currentDay = c.get(Calendar.DAY_OF_MONTH);
                            int currentMonth = c.get(Calendar.MONTH);
                            int currentYear = c.get(Calendar.YEAR);

                            DatePickerDialog dpd = new DatePickerDialog(ScanQRCodeActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                    String selectedTime = dayOfMonth + "/" + (month + 1) + "/" + year;
                                    issueto.setText(selectedTime);

                                }
                            }, currentDay, currentMonth, currentYear);
                            dpd.getDatePicker().setMinDate(System.currentTimeMillis());

                            //logic to set maxDate in Datepicker to 1 month
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.MONTH, +1);
                            Date result = cal.getTime();
                            dpd.getDatePicker().setMaxDate(result.getTime());
                            //end of logic

                            dpd.show();
                        }
                    });
                    //end of datepicker logicc

                }
            }catch (Exception e){
                e.printStackTrace();
                //startActivity(backtoHomepage);
                onBackPressed();
                Toast.makeText(this, "No Book Data Found", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        zXingScannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private HashMap<String, String> StringToHashmap(String mainstring){
        String[] arr = mainstring.split(", ");
        HashMap<String, String> map = new HashMap<String, String>();
        for (String str : arr) {
            str = str.replace("{", "").replace("}", "");
            String[] splited = str.split("=");

            map.put(splited[0].trim(), splited[1].trim());

        }
        return map;
    }



    public void IssueNewBook(View v){

        String issuedby = mAuth.getCurrentUser().getEmail();
        Toast.makeText(this, issuedby, Toast.LENGTH_SHORT).show();

        final BookIssueDetails bid = new BookIssueDetails(bookname.getText().toString(), authorname.getText().toString(), edition.getText().toString(), isdn.getText().toString(), preface.getText().toString(), yop.getText().toString(), issuefrom.getText().toString(), issueto.getText().toString(), issuedby);

        try {
            //add issued books details to firebasedatabase table

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(bid.getBookname())) {
                        // if book is already issued then don't issue and display warning message.
                        String getIssuedRetDate = snapshot.child(bid.getBookname()).child("issueto").getValue().toString();
                        Toast.makeText(ScanQRCodeActivity.this, "Uh Oh! This book is already issued till " + getIssuedRetDate, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //if book entry not present in issued-books table, then issue the book.
                        myRef.child(bid.getBookname()).setValue(bid).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //makesnackbar("Book Issued Successfully.");
                                //Intent HomePageRoute = new Intent(ScanQRCodeActivity.this, Homepage.class);
                                //startActivity(HomePageRoute);
                                Toast.makeText(ScanQRCodeActivity.this, "Book Issued Successfully", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    makesnackbar("Database Error! Please Retry.");
                }
            });


        }catch (DatabaseException e){
            makesnackbar("unexpected Error. Please contact administrator!");

        }


    }

    private void makesnackbar(String message){
        Snackbar snkbr = Snackbar.make(relativeLayout,message,Snackbar.LENGTH_LONG);
        snkbr.show();
    }

}
