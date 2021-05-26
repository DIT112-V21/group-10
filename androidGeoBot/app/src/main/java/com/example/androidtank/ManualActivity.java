package com.example.androidtank;

import android.content.Context;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.annotation.SuppressLint;
import android.os.Bundle;
// we are using the joystick from https://github.com/controlwear/virtual-joystick-android
import io.github.controlwear.virtual.joystick.android.JoystickView;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidtank.utilities.Client;
import com.google.android.material.slider.Slider;

import java.util.Objects;


public class ManualActivity extends AppCompatActivity {

    private static Context context;
    //joystick buttons
    private Button breakBtn, acceleration, deceleration, backBtn;
    private Client client;
    public ImageView mCameraView;
    private JoystickView joystick;
    Slider slider;
    TextView score;
    private int points = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Stops the title and the top action bar from displaying and sets windows to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.client = new Client(this);
        client = MainActivity.client;

        // Setting the layout to be used
        setContentView(R.layout.activity_manual);
        this.slider = (Slider) findViewById(R.id.speedSlider);
        this.score = (TextView) findViewById(R.id.scoreText);
        this.mCameraView = (ImageView) findViewById(R.id.cameraView);

        // uncomment to view dialog box
        //showDialog();

        // Setting up controls
        setTankControls();
    }

    public void showDialog() {
        Dialog dialog = new Dialog(ManualActivity.this);

        // user can not click away from box without interacting with it
        dialog.setCancelable(true);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        // initial logic for winning/losing
        int score = client.getScoreValue();
        if (score > 4) {
            dialog.setContentView(R.layout.dialog_win);
            Button finish = (Button) dialog.findViewById(R.id.finish);
            setupOrdinaryButton2(finish);
            Button reload = (Button) dialog.findViewById(R.id.playAgain);
            setupReloadBtn(reload);
            dialog.show();
        } else {
            dialog.setContentView(R.layout.dialog_lose);
            Button finish = (Button) dialog.findViewById(R.id.finish);
            setupOrdinaryButton2(finish);
            Button reload = (Button) dialog.findViewById(R.id.playAgain);
            setupReloadBtn(reload);
            dialog.show();
        }
    }


    // Setup of the controls for the SMCE car.
    public void setTankControls () {
        // Setup ordinary buttons
        breakBtn = findViewById(R.id.break_button);
        backBtn = findViewById(R.id.button_back);
        //acceleration = (Button) findViewById(R.id.accelerate_up);
        //deceleration = (Button) findViewById(R.id.accelerate_down);
        setupOrdinaryButton(breakBtn);
        setupOrdinaryButton2(backBtn);
        setupJoystick();
        //setupOrdinaryButton(acceleration);
        //setupOrdinaryButton(deceleration);
    }

    /**
     * These methods takes in a Button object and makes it clickable
     */
    private void setupOrdinaryButton (Button button){

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.button_publish(button);
            }
        });

    }

    // For the back button
    // go back one screen
    private void setupOrdinaryButton2 (Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void setupJoystick() {
        joystick = (JoystickView) findViewById(R.id.joystickView);
        int delay = 100;

        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                int newX = convertJoystickX(); // for determining angle strength
                client.joystick_publish(joystick, angle, strength, newX);
            }
        }, delay);
    }

    // Convert Joystick Angle so that Tank understands
    private int convertJoystickX() {
        int joystickX = joystick.getNormalizedX();

        if (joystickX <= 50) {
            joystickX = 50 - joystickX; // invert the value
        } else {
            joystickX -= 50; // Simply Bring the value down to between 0-50
        }
        float newX = (float)joystickX/50 * 30; // Convert to scale 30
        return Math.round(newX); // return rounded number
    }

    // reload activity
    private void setupReloadBtn (Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ManualActivity.this, ManualActivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
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

    public Slider getSlider () {
        return this.slider;
    }

    public void setBitmap (Bitmap bm){
        this.mCameraView.setImageBitmap(bm);
    }

    public TextView getScore () {
        return this.score;
    }
}
