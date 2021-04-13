package com.example.project112_01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button buttonLaunchManualAc;
    private Button buttonLaunchAutoAc;
    private Button buttonLaunchMapsAc;
    private Button buttonLaunchHelpAc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Stops the title and the top action bar from displaying and sets windows to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //TODO: Dance around a fire with Pluto and Jupiter as the sun sets over the red horizon
        buttonLaunchManualAc = (Button) findViewById(R.id.button_manual);
        buttonLaunchAutoAc = (Button) findViewById(R.id.button_autopilot);
        buttonLaunchMapsAc = (Button) findViewById(R.id.button_maps);
        buttonLaunchHelpAc = (Button) findViewById(R.id.button_help);

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