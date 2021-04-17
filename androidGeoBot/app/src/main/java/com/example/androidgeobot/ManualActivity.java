package com.example.androidgeobot;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ManualActivity extends AppCompatActivity {

    //joystick buttons
    private Button buttonGoBack, leftBtn, rightBtn, angleReset, reverseBtn, acceleration , deceleration;
    private Client client;
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
        this.client = new Client(this);

        if (!client.connect()) {
            Toast.makeText(this, FAIL, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, SUCCESS, Toast.LENGTH_SHORT).show();
        }
        setJoystickBtns();
    }


    //Setups for some generic jooystick buttons to test out manual control commmands
    public void setJoystickBtns(){
        leftBtn = findViewById(R.id.leftBtn);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(leftBtn);

            }
        });
/*
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.publish(rightBtn.toString());
            }
        }); */
        angleReset = findViewById(R.id.angleReset);
        angleReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(angleReset);
            }
        });

        acceleration = findViewById(R.id.plus);
        acceleration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(acceleration);
            }
        });
/*
        reverseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.publish(reverseBtn.toString());
            }
        }); */
        buttonGoBack = (Button) findViewById(R.id.button_back);
        buttonGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


    }




}