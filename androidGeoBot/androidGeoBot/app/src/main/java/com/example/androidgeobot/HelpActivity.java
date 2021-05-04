package com.example.androidgeobot;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;



public class HelpActivity extends AppCompatActivity {

    PDFView pdfview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_help);

        pdfview = findViewById(R.id.pdfView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pdfview.fromAsset("geobot_docs18.pdf").load();

    }
}