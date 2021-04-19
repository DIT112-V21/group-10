package com.example.androidgeobot;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ManualActivity extends AppCompatActivity {
    //joystick buttons
    private Button forwardBtn, leftBtn, rightBtn, backBtn, breakBtn, acceleration , deceleration;
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

    //Setups for some generic joystick buttons to test out manual control commmands
    // Feel free to replace this for future implementation
    public void setJoystickBtns() {
        forwardBtn = findViewById(R.id.forward_button);
        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(forwardBtn);

            }
        });

        rightBtn = findViewById(R.id.right_button);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(rightBtn);
            }
        });

        leftBtn = findViewById(R.id.left_button);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(leftBtn);
            }
        });

        backBtn = findViewById(R.id.backward_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(backBtn);
            }
        });

        breakBtn = findViewById(R.id.break_button);
        breakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(backBtn);
            }
        });

        acceleration = findViewById(R.id.accelerate_up);
        acceleration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(acceleration);
            }
        });

        deceleration = findViewById(R.id.accelerate_down);
        deceleration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.button_publish(deceleration);
            }
        });

        breakBtn = findViewById(R.id.break_button);
        breakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
