package com.example.studentapp;

import com.google.firebase.auth.FirebaseAuth;

public class BookIssueDetails {

    String bookname,authorname,edition,isdn,preface,yop,issuefrom,issueto, issuedby;
    private FirebaseAuth mAuth;


    public BookIssueDetails(){}

    public String getIssuedby() {
        return issuedby;
    }

    public void setIssuedby(String issuedby) {
        this.issuedby = issuedby;
    }

    public BookIssueDetails(String bookname, String authorname, String edition, String isdn, String preface, String yop, String issuefrom, String issueto, String issuedby){
        this.bookname = bookname;
        this.authorname = authorname;
        this.edition = edition;
        this.isdn = isdn;
        this.preface = preface;
        this.yop = yop;
        this.issuefrom = issuefrom;
        this.issueto = issueto;
        this.issuedby = issuedby;
        //mAuth = FirebaseAuth.getInstance();
        //this.issuedby = mAuth.getCurrentUser().getEmail().toString();
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getAuthorname() {
        return authorname;
    }

    public void setAuthorname(String authorname) {
        this.authorname = authorname;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getIsdn() {
        return isdn;
    }

    public void setIsdn(String isdn) {
        this.isdn = isdn;
    }

    public String getPreface() {
        return preface;
    }

    public void setPreface(String preface) {
        this.preface = preface;
    }

    public String getYop() {
        return yop;
    }

    public void setYop(String yop) {
        this.yop = yop;
    }

    public String getIssuefrom() {
        return issuefrom;
    }

    public void setIssuefrom(String issuefrom) {
        this.issuefrom = issuefrom;
    }

    public String getIssueto() {
        return issueto;
    }

    public void setIssueto(String issueto) {
        this.issueto = issueto;
    }
}
