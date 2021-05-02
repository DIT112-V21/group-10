package com.example.androidgeobot;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ManualActivity extends AppCompatActivity {
    //joystick buttons
    private Button forwardBtn, leftBtn, rightBtn, backwardBtn, breakBtn, acceleration , deceleration
            , backBtn;
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
        } else{
            Toast.makeText(this, SUCCESS, Toast.LENGTH_SHORT).show();
        }
        setJoystickBtns();
    }

    //Setups for some generic joystick buttons to test out manual control commmands
    // Feel free to replace this for future implementation
    public void setJoystickBtns() {

        forwardBtn = (Button) findViewById(R.id.forward_button);
        forwardBtn.setOnTouchListener(new View.OnTouchListener() {
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
                    client.button_publish(forwardBtn);
                    mHandler.postDelayed(this, 100);
                }
            };
        });

        rightBtn = (Button) findViewById(R.id.right_button);
        rightBtn.setOnTouchListener(new View.OnTouchListener() {
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
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }
            Runnable mAction = new Runnable() {
                @Override public void run() {
                    client.button_publish(rightBtn);
                    mHandler.postDelayed(this, 100);
                }
            };
        });

        leftBtn = (Button) findViewById(R.id.left_button);
        leftBtn.setOnTouchListener(new View.OnTouchListener() {
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
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }
            Runnable mAction = new Runnable() {
                @Override public void run() {
                    client.button_publish(leftBtn);
                    mHandler.postDelayed(this, 100);
                }
            };
        });

        backwardBtn = (Button) findViewById(R.id.backward_button);
        backwardBtn.setOnTouchListener(new View.OnTouchListener() {
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
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }
            Runnable mAction = new Runnable() {
                @Override public void run() {
                    client.button_publish(backwardBtn);
                    mHandler.postDelayed(this, 100);
                }
            };
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
}
