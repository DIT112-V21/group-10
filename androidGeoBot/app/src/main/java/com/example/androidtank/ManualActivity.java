package com.example.androidtank;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
// we are using the joystick from https://github.com/controlwear/virtual-joystick-android
import io.github.controlwear.virtual.joystick.android.JoystickView;

import android.os.Handler;
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
import com.example.androidtank.utilities.SoundEffect;
import com.google.android.material.slider.Slider;

import java.util.Objects;


public class ManualActivity extends AppCompatActivity {

    //joystick buttons
    private Button breakBtn, backBtn;
    private Client client;
    public ImageView mCameraView;
    private JoystickView joystick;
    Slider slider;
    TextView score;
    private int points = 3;


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
        this.slider = (Slider) findViewById(R.id.speedSlider);
        this.score = (TextView) findViewById(R.id.scoreText);
        this.mCameraView = (ImageView) findViewById(R.id.cameraView);

        // Mqtt Client
        this.client = new Client(this);

        // uncomment to view dialog box
        //showDialog();

        if (!client.connect(null, null, null, null)) {
            Toast.makeText(this, FAIL, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, SUCCESS, Toast.LENGTH_SHORT).show();
        }

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
            setupBackButton(finish);
            Button reload = (Button) dialog.findViewById(R.id.playAgain);
            setupReloadBtn(reload);
            dialog.show();
        } else {
            dialog.setContentView(R.layout.dialog_lose);
            Button finish = (Button) dialog.findViewById(R.id.finish);
            setupBackButton(finish);
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
        setupBreakButton(breakBtn);
        setupBackButton(backBtn);
//        setupJoystick();
        setUpJoystick1();

    }

    /**
     * These methods takes in a Button object and makes it clickable
     */

    @SuppressLint("ClickableViewAccessibility")
   private void setupBreakButton(Button button){
        button.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 100);
                        SoundEffect.startEffect(ManualActivity.this, R.raw.break_sound, 0.6f,false);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        client.button_publish(null);
                        mHandler.removeCallbacksAndMessages(null);
                        mHandler = null;
                        SoundEffect.stopEffect();
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
    }

    // For the back button
    // go back one screen
    private void setupBackButton(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


//    public void setupJoystick() {
//        joystick = (JoystickView) findViewById(R.id.joystickView);
//        int delay = 100;
//
//        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
//            public void onMove(int angle, int strength) {
//                int newX = convertJoystickX(); // for determining angle strength
//                client.joystick_publish(joystick, angle, strength, newX);
//            }
//        }, delay);
//
//    }

    @SuppressLint("ClickableViewAccessibility")
    public void setUpJoystick1() {
        joystick = (JoystickView) findViewById(R.id.joystickView);
        joystick.setOnTouchListener(new JoystickView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == event.ACTION_DOWN) {
                    SoundEffect.stopEffect();
                    SoundEffect.startEffect(ManualActivity.this, R.raw.acceleration, 0.6f, true);
                }
                if (event.getAction()  != event.ACTION_UP) {
                    int delay = 100;
                    joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
                        public void onMove(int angle, int strength) {
                            int newX = convertJoystickX(); // for determining angle strength
                            client.joystick_publish(joystick, angle, strength, newX);
                        }
                    }, delay);
                } else {
                    SoundEffect.stopEffect();
                    //TODO find a better sound effect. this is too low!
                    SoundEffect.startEffect(ManualActivity.this, R.raw.carsound, 1f, true);
                }
                return false;
            }
        });
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





