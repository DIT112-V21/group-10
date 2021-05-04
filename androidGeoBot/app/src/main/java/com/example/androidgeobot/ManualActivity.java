package com.example.androidgeobot;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class ManualActivity extends AppCompatActivity{
    //joystick buttons
    private Button forwardBtn, leftBtn, rightBtn, backwardBtn, breakBtn, acceleration , deceleration
            , backBtn;
    private Client client;
    public ImageView mCameraView;
    private static final String FAIL = "CONNECTION TO GEOBOT COULD NOT BE ESTABLISHED";
    private static final String SUCCESS = "CONNECTION TO GEOBOT ESTABLISHED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Stops the title and the top action bar from displaying and sets windows to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_manual);
        this.mCameraView = (ImageView)findViewById(R.id.cameraView);

        this.client = new Client(this);


        if (!client.connect(null,null,null,null)) {
            Toast.makeText(this, FAIL, Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, SUCCESS, Toast.LENGTH_SHORT).show();
        }
        setJoystickBtns();
    }

    //Setups for some generic joystick buttons to test out manual control commmands
    // Feel free to replace this for future implementation
    public void setJoystickBtns() {
        forwardBtn = (Button) findViewById(R.id.forward_button);
        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(forwardBtn);

            }
        });

        rightBtn = (Button) findViewById(R.id.right_button);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(rightBtn);
            }
        });

        leftBtn = (Button) findViewById(R.id.left_button);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(leftBtn);
            }
        });

        backwardBtn = (Button) findViewById(R.id.backward_button);
        backwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(backwardBtn);
            }
        });

        breakBtn = (Button) findViewById(R.id.break_button);
        breakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(breakBtn);
            }
        });

        acceleration = (Button) findViewById(R.id.accelerate_up);
        acceleration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(acceleration);
            }
        });

        deceleration = (Button) findViewById(R.id.accelerate_down);
        deceleration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.button_publish(deceleration);
            }
        });

        backBtn = (Button) findViewById(R.id.button_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setBitmap(Bitmap bm){
        this.mCameraView.setImageBitmap(bm);
    }
}