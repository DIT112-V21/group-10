package com.example.androidgeobot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button buttonLaunchManualAc,
            buttonLaunchAutoAc,
            buttonLaunchMapsAc,
            buttonLaunchHelpAc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Stops the title and the top action bar from displaying and sets window to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        buttonLaunchManualAc = (Button) findViewById(R.id.button_manual);
        buttonLaunchAutoAc = (Button) findViewById(R.id.button_autopilot);
        buttonLaunchMapsAc = (Button) findViewById(R.id.button_maps);
        buttonLaunchHelpAc = (Button) findViewById(R.id.button_help);

        // Creates onClickListener for the buttons.
        // Does the same thing as the lambdas below. Lines with "->".
        buttonLaunchManualAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities(ManualActivity.class);
            }
        });
        buttonLaunchAutoAc.setOnClickListener(view -> switchActivities(AutoActivity.class));
        buttonLaunchMapsAc.setOnClickListener(view -> switchActivities(MapsActivity.class));
        buttonLaunchHelpAc.setOnClickListener(view -> switchActivities(HelpActivity.class));

    }

    // An intent is used to launch an activity. Makes it possible to go from main screen to others.
    private void switchActivities(Class ActivityClass) {
        Intent switchActivity = new Intent(this, ActivityClass);
        switchActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(switchActivity);
    }
}