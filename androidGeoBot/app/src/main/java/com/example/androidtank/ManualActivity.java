package com.example.androidtank;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidtank.utilities.Client;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
// we are using the joystick from https://github.com/controlwear/virtual-joystick-android
import io.github.controlwear.virtual.joystick.android.JoystickView;

import java.util.Objects;



public class ManualActivity extends AppCompatActivity {

    private static Context context;
    //joystick buttons
    private Button breakBtn, acceleration, deceleration, backBtn, helpButton;
    private Client client;
    public ImageView mCameraView;
    private JoystickView joystick;
    Slider slider;
    TextView score;

    private static final String FAIL = "CONNECTION TO TANK COULD NOT BE ESTABLISHED";
    private static final String SUCCESS = "CONNECTION TO TANK ESTABLISHED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Stops the title and the top action bar from displaying and sets windows to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Setting the layout to be used
        setContentView(R.layout.activity_manual);
        createHelpDialog();
        this.mCameraView = (ImageView) findViewById(R.id.cameraView);
        this.slider = (Slider) findViewById(R.id.speedSlider);
        this.score = (TextView) findViewById(R.id.scoreText);

        // Mqtt Client
        this.client = new Client(this);


        if (!client.connect(null, null, null, null)) {
            Toast.makeText(this, FAIL, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, SUCCESS, Toast.LENGTH_SHORT).show();
        }

        // Setting up controls
        setTankControls();
    }

    private void createHelpDialog() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(ManualActivity.this);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setTitle("Car control");
        dialogBuilder.setMessage("Move the joystick circle around to steer the car. Use the slider" +
                " to control the speed.");
        dialogBuilder.setPositiveButton("Play", (dialog, which) -> {
            //Timer starts here
        });
        dialogBuilder.show();
    }

    // Setup of the controls for the SMCE car.
    public void setTankControls() {
        // Setup ordinary buttons
        breakBtn = findViewById(R.id.break_button);
        backBtn = findViewById(R.id.button_back);
        helpButton = findViewById(R.id.help_button);
        //acceleration = (Button) findViewById(R.id.accelerate_up);
        //deceleration = (Button) findViewById(R.id.accelerate_down);
        setupOrdinaryButton(breakBtn);
        setupOrdinaryButton2(backBtn);
        setupJoystick();
        helpButton.setOnClickListener(view -> switchActivities(HelpActivity.class));
        //setupOrdinaryButton(acceleration);
        //setupOrdinaryButton(deceleration);
    }

    private void switchActivities(Class ActivityClass) {
        Intent switchActivity = new Intent(this, ActivityClass);
        switchActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(switchActivity);
    }

    /**
     * These methods takes in a Button object and makes it clickable
     */
    private void setupOrdinaryButton(Button button) {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(button);
            }
        });
    }

    // For the back button
    private void setupOrdinaryButton2(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public void setupJoystick() {
        joystick = (JoystickView) findViewById(R.id.joystickView);
        joystick.setOnMoveListener((angle, strength) -> client.joystick_publish(joystick, angle, strength));

    }
        /**
         * This method takes in a Button object and makes it into a touch button
         */
        @SuppressLint("ClickableViewAccessibility")
/*    private void setupTouchController(Button button){
        button.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        client.button_publish(null);
                        mHandler.removeCallbacksAndMessages(null);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    client.button_publish(button);
                    mHandler.postDelayed(this, 100);
                }
            };
        });
    }*/

    public Slider getSlider() {
        return this.slider;
    }

    public void setBitmap(Bitmap bm) {
        this.mCameraView.setImageBitmap(bm);
    }

    public TextView getScore() {
        return this.score;
    }
}
