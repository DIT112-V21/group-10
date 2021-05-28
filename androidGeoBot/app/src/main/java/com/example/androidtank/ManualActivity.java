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
import android.os.CountDownTimer;
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

    private Client client;
    private SoundEffect effects;

    private Button breakBtn, backBtn;
    ImageView mCameraView;
    private JoystickView joystick;
    private Slider slider;
    TextView score;

    private boolean mqttConnection = false;


    private static final String FAIL = "CONNECTION TO TANK COULD NOT BE ESTABLISHED";
    private static final String SUCCESS = "CONNECTION TO TANK ESTABLISHED";
    public int counter = 120;
    TextView timer;
    private static final String TEM = "Time is up!!!"; //TEM is Timer End Message

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
        this.timer = (TextView) findViewById(R.id.time);

        // Mqtt Client
        this.client = new Client(this);

        if (!client.connect(null, null, null, null)) {
            Toast.makeText(this, FAIL, Toast.LENGTH_SHORT).show();
            mqttConnection = false;
        } else {
            Toast.makeText(this, SUCCESS, Toast.LENGTH_SHORT).show();
            mqttConnection = true;
        }

        // Setting up controls
        setTankControls();

        //Sound effects initiallization
        // Background game music: www.bensound.com
        MediaPlayer effect1 = MediaPlayer.create(ManualActivity.this, R.raw.acceleration);
        MediaPlayer effect2 = MediaPlayer.create(ManualActivity.this, R.raw.game_bg);
        this.effects = new SoundEffect(effect1, effect2);
        effects.setEffect2(ManualActivity.this, R.raw.game_bg, 0.2f, true);
    }

    public void showDialog() {
        Dialog dialog = new Dialog(ManualActivity.this);
        effects.stopEffect2();

        // user can not click away from box without interacting with it
        dialog.setCancelable(true);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        // initial logic for winning/losing
        int score = client.getScoreValue();
        if (score > 1) {
            dialog.setContentView(R.layout.dialog_win);
            effects.setEffect1(ManualActivity.this, R.raw.win, 0.2f, false, 0);
        } else {
            dialog.setContentView(R.layout.dialog_lose);
            effects.setEffect1(ManualActivity.this, R.raw.lose, 0.2f, false, 0);
        }
        Button finish = (Button) dialog.findViewById(R.id.finish);
        setupBackButton(finish);
        Button reload = (Button) dialog.findViewById(R.id.playAgain);
        setupReloadBtn(reload);
        dialog.show();
        counter = 120; //reset counter back to 120
    }

    // Setup of the controls for the SMCE car.
    public void setTankControls() {
        // Setup ordinary buttons
        breakBtn = findViewById(R.id.break_button);
        backBtn = findViewById(R.id.button_back);
        setupBreakButton(breakBtn);
        setupBackButton(backBtn);
        setUpJoystick();
    }

    /**
     * These methods takes in a Button object and makes it clickable
     */

    @SuppressLint("ClickableViewAccessibility")
    private void setupBreakButton(Button button) {
        button.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 100);
                        effects.setEffect1(ManualActivity.this, R.raw.brake,
                                0.5f, false, 0);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        client.button_publish(null);
                        mHandler.removeCallbacksAndMessages(null);
                        mHandler = null;
                        effects.stopEffect1();
                        break;
                }
                return true;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    client.button_publish(button);
                    mHandler.postDelayed(this, 100);
                }
            };
        });

    }

    // go back one screen
    private void setupBackButton(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                effects.stopEffect2();
                finish();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setUpJoystick() {
        joystick = (JoystickView) findViewById(R.id.joystickView);
        joystick.setOnTouchListener(new JoystickView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == event.ACTION_DOWN) {
                    effects.setEffect1(ManualActivity.this, R.raw.acceleration,
                            0.2f, true, 7000);
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    effects.setEffect1(ManualActivity.this, R.raw.car_noise, 1.0f,
                            false, 0);
                }

                int delay = 100;
                joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
                    public void onMove(int angle, int strength) {
                        int newX = convertJoystickX(); // for determining angle strength
                        client.joystick_publish(joystick, angle, strength, newX);
                        timerHandler();
                    }
                }, delay);
                return false;
            }
        });
    }

    public void timerHandler() {
        if (counter == 120 && mqttConnection) {
            new CountDownTimer(120000, 1000) {
                public void onTick(long millisUntilFinished) {
                    timer.setText(String.valueOf(millisUntilFinished / 1000) +" s");
                    counter--;
                }

                public void onFinish() {
                    effects.stopEffect2();
                    timer.setText(TEM);
                    showDialog();
                }
            }.start();
        }
    }

    // Convert Joystick Angle so that Tank understands
    private int convertJoystickX() {
        int joystickX = joystick.getNormalizedX();

        if (joystickX <= 50) {
            joystickX = 50 - joystickX; // invert the value
        } else {
            joystickX -= 50; // Simply Bring the value down to between 0-50
        }
        float newX = (float) joystickX / 50 * 30; // Convert to scale 30
        return Math.round(newX); // return rounded number
    }

    // reload activity
    private void setupReloadBtn(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ManualActivity.this, ManualActivity.class);
                finish();
                overridePendingTransition(0, 0);
                startActivity(i);
                overridePendingTransition(0, 0);
                effects.stopEffect1();
                effects.stopEffect2();
            }
        });
    }

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





