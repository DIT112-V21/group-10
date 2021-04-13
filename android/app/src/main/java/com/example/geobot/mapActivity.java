package com.example.geobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;



// possible pixel grid map for future autopilot mode
// creates a pixel grid with the given rows and columns and loads it up to the activity view

public class mapActivity extends AppCompatActivity {

    static private PixelGridView pixelGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        pixelGrid = new PixelGridView(this);
        pixelGrid.setNumColumns(40);
        pixelGrid.setNumRows(100);

        setContentView(pixelGrid);


    }
}

