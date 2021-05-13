package com.example.androidtank;


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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidtank.utilities.Client;

import java.util.Objects;



public class ManualActivity extends AppCompatActivity implements JoystickView.JoystickListener {
    //joystick buttons
    private Button breakBtn, acceleration , deceleration, backBtn;
    private Client client;
    public ImageView mCameraView;
    JoystickView joystick;

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
        this.mCameraView = (ImageView)findViewById(R.id.cameraView);
        joystick = new JoystickView(this);

        // Mqtt Client
        this.client = new Client(this);


        if (!client.connect(null,null,null,null)) {
            Toast.makeText(this, FAIL, Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, SUCCESS, Toast.LENGTH_SHORT).show();
        }

        // Setting up controls
        setTankControls();
    }

    // Setup of the controls for the SMCE car.
    public void setTankControls() {
        // Setup ordinary buttons
        breakBtn = findViewById(R.id.break_button);
        backBtn = findViewById(R.id.button_back);
        //acceleration = (Button) findViewById(R.id.accelerate_up);
        //deceleration = (Button) findViewById(R.id.accelerate_down);
        setupOrdinaryButton(breakBtn);
        setupOrdinaryButton2(backBtn);
        //setupOrdinaryButton(acceleration);
        //setupOrdinaryButton(deceleration);
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


    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int id) {
        switch (id)
        {
            case R.id.joystick:
                client.joystick_publish(joystick,  xPercent,  yPercent);
                break;
        }
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
    public void setBitmap(Bitmap bm){
        this.mCameraView.setImageBitmap(bm);
    }
}
