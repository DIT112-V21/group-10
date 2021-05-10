package com.example.androidtank;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Button buttonLaunchManualAc,
            buttonLaunchAutoAc,
            buttonLaunchHelpAc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Stops the title and the top action bar from displaying and sets window to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // Check if OpenCV can be initialized and used.
        // System.loadLibrary("opencv_java4");
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");

        // Sets the buttons in the activity layout to an actual Button objects that we can use
        buttonLaunchManualAc = findViewById(R.id.button_manual);
        buttonLaunchAutoAc = findViewById(R.id.button_autopilot);
        buttonLaunchHelpAc = findViewById(R.id.button_help);

        // Creates onClickListener for the buttons.
        // Does the same thing as the lambdas below. Lines with "->".
        buttonLaunchManualAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities(ManualActivity.class);
            }
        });
        buttonLaunchAutoAc.setOnClickListener(view -> switchActivities(AutoActivity.class));
        buttonLaunchHelpAc.setOnClickListener(view -> switchActivities(HelpActivity.class));

    }

    // An intent is used to launch an activity. Makes it possible to go from main screen to others.
    private void switchActivities(Class ActivityClass) {
        Intent switchActivity = new Intent(this, ActivityClass);
        switchActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(switchActivity);
    }
}