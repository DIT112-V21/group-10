package com.example.androidtank;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidtank.utilities.HandleFiles;
import org.opencv.android.OpenCVLoader;

import java.io.FileNotFoundException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Button buttonLaunchManualAc;
    MainVideoView mainVideoView;
    private int videoViewId = R.raw.mainscreen_video2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Stops the title and the top action bar from displaying and sets window to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set view
        setContentView(R.layout.activity_main);

        // Set video view and load video
        initializeVideoView();

        // Check if OpenCV can be initialized and used. the code in the parameters return false if
        // OpenCV can't be loaded
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");

        // Sets the buttons in the activity layout to an actual Button objects that we can use
        buttonLaunchManualAc = findViewById(R.id.button_manual);

        // Creates onClickListener for the buttons and views
        buttonLaunchManualAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities(ManualActivity.class);
            }
        });
    }

    // An intent is used to launch an activity. Makes it possible to go from main screen to others.
    private void switchActivities(Class ActivityClass) {
        Intent switchActivity = new Intent(this, ActivityClass);
        switchActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(switchActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainVideoView.suspend();
        mainVideoView.stopPlayback();
    }

    // Set video view and load video
    private void initializeVideoView() {
        // TODO; 1. Don't set videoPath as null. 2. Refactor this code
        HandleFiles handleFiles = new HandleFiles();
        String videoPath = null;
        mainVideoView = new MainVideoView(this);
        mainVideoView = findViewById(R.id.mainVideoView);
        try {
            videoPath = handleFiles.getFileFromRes(this, videoViewId);

            mainVideoView.setVideoURI(Uri.parse(videoPath));
            mainVideoView.setVideoPath(videoPath);
            mainVideoView.setLayoutLook(mainVideoView); // sets the layout look for the videoview
            mainVideoView.enableRestartVideo(); // sets a listener that enables restarting video
            mainVideoView.start();
        } catch (FileNotFoundException e) {
            Log.i("TAG", "Could not load video", e);
            // e.printStackTrace();
        }
    }
}