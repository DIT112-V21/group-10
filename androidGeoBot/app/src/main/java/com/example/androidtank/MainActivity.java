package com.example.androidtank;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.androidtank.utilities.Client;
import com.example.androidtank.utilities.HandleFiles;
import com.example.androidtank.utilities.SoundEffect;

import org.opencv.android.OpenCVLoader;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Context context;
    public static Client client;
    private static final String FAIL = "CONNECTION TO TANK COULD NOT BE ESTABLISHED";
    private static final String SUCCESS = "CONNECTION TO TANK ESTABLISHED";
    private boolean loadedFromFile = false;

    private Button buttonLaunchManualAc, buttonLaunchHelp, buttonLaunchBroker;

    MainVideoView mainVideoView;
    private int videoViewId = R.raw.mainscreen_video2;
    private SoundEffect backMusic;

    private String textHost = "";
    private String textPort = "";
    private boolean textHostPressed = false;
    private boolean textPortPressed = false;
    private boolean mqttConnection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Stops the title and the top action bar from displaying and sets window to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Set background music:
        MediaPlayer music = MediaPlayer.create(MainActivity.this, R.raw.main_music);
        backMusic = new SoundEffect(music, music);

        // Set view
        setContentView(R.layout.activity_main);

        /**
         * Try:
         * 1. loading the file with broker settings
         * 2. Creating a client from those stored settings
         */
        try {
            HandleFiles handleFiles = new HandleFiles();
            ArrayList<String> broker = handleFiles.read(MainActivity.this);
            client = new Client(this, broker.get(0), broker.get(1)); // Host(0) and port(1)
            loadedFromFile = true; // used for later in ManualActivity, to send the current host and port

            customToast("Settings: " + "IP: " + broker.get(0) + ", PORT: " + broker.get(1), Toast.LENGTH_LONG).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
            client = new Client(this); // Else create a standard Client
            loadedFromFile = false; // used for later in ManualActivity, to send the current host and port

            customToast("Settings: " + "IP: " + client.getMqtt() + ", PORT: " + client.getPort(), Toast.LENGTH_LONG).show();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            loadedFromFile = false; // used for later in ManualActivity, to send the current host and port
            client = new Client(this); // Else create a standard Client

            customToast("Broker file loading error", Toast.LENGTH_LONG).show();
        }
        checkClientConnection();

        // Set video view and load video
        initializeVideoView();

        // Check if OpenCV can be initialized and used. the code in the parameters return false if
        // OpenCV can't be loaded
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");

        // Sets the buttons in the activity layout to an actual Button objects that we can use
        buttonLaunchManualAc = findViewById(R.id.button_play);
        buttonLaunchHelp = findViewById(R.id.helpbutton);
        buttonLaunchBroker = findViewById(R.id.button_broker);

        // Creates onClickListener for the buttons and views
        buttonLaunchManualAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchActivities(ManualActivity.class);
            }
        });
        buttonLaunchHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchActivities(HelpActivity.class);
            }
        });
        buttonLaunchBroker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeServerView();
            }
        });
    }

    // An intent is used to launch an activity. Makes it possible to go from main screen to others.
    public void switchActivities(Class ActivityClass) {
        Intent switchActivity = new Intent(this, ActivityClass);
        switchActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(switchActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainVideoView.start();

        //The background music: https://www.chosic.com/
        backMusic.setEffect1(this, R.raw.main_music, 0.27f, true, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainVideoView.suspend();
        mainVideoView.stopPlayback();
        backMusic.stopEffect1();
    }

    private void checkClientConnection() {
        if (!client.connect(null, null, null, null)) {
            Toast toast = Toast.makeText(this, FAIL, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            mqttConnection = false;
        } else {
            Toast toast = Toast.makeText(this, SUCCESS, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            mqttConnection = true;
        }
    }

    // Set video view and load video
    private void initializeVideoView() {
        // TODO; 1. Don't set videoPath as null. 2. Refactor this code
        HandleFiles handleFiles = new HandleFiles();
        String videoPath = null;
        mainVideoView = new MainVideoView(this);
        mainVideoView = findViewById(R.id.mainVideoView);
        try {
            videoPath = handleFiles.getFileFromRes(this, videoViewId);

            mainVideoView.setVideoURI(Uri.parse(videoPath));
            mainVideoView.setVideoPath(videoPath);
            mainVideoView.setLayoutLook(mainVideoView); // sets the layout look for the videoview
            mainVideoView.enableRestartVideo(); // sets a listener that enables restarting video
            mainVideoView.start();
        } catch (FileNotFoundException e) {
            Log.i("TAG", "Could not load video", e);
        }
    }

    /**
     * Methods related to Server settings
     * TODO: Refactor this method and use layout xml instead, for customizing the look.
     *  A lot of magic variables here
     */
    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"}) // TODO Don't suppress accessibility.
    private void initializeServerView() {
        context = (MainActivity) MainActivity.this;
        // reset
        textPortPressed = false;
        textHostPressed = false;

        // Create a new linear layout for storing text-fields
        LinearLayout layout = new LinearLayout(context);
        layout.setLayoutParams(new ViewGroup.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        );
        layout.setOrientation(LinearLayout.VERTICAL);

        // Add text fields to the layout and set default text
        final EditText inputIP = new EditText(context); // Input text for IP
        final EditText inputPort = new EditText(context); // Input text for port
        // set text and show current host and port
        inputIP.setText("Current host: " + client.getCustomHost());
        inputPort.setText("Current port: " + client.getCustomPort());
        helpText.setText("Default host, port: 10.0.2.2, 1883");
        // Set the expected input type for the text-fields
        inputIP.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        inputPort.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);

        // Remove text from text-field when text-field is selected
        // Only happens once.
        inputIP.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    inputIP.setText("");
                    textHostPressed = true;
                }
            }
        });
        inputPort.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    inputPort.setText("");
                    textPortPressed = true;
                }
            }
        });

        // Add the text fields to the layout
        layout.addView(inputIP);
        layout.addView(inputPort);

        // Create a new Alert Dialog view and set the font for text
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.AlertDialog);
        dialog.setTitle("Broker ( Default: 10.0.2.2, 1883)"); // Title
        Typeface face = ResourcesCompat.getFont(context, R.font.bree_serif);
        inputIP.setTypeface(face);
        inputPort.setTypeface(face);

        // Set margins for the textfields
        final ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) inputIP.getLayoutParams();
        final ViewGroup.MarginLayoutParams mlp2 = (ViewGroup.MarginLayoutParams) inputPort.getLayoutParams();
        mlp.setMargins(55, 0, 55, 0);
        mlp2.setMargins(55, 0, 55, 0);

        // set layout to alert dialog
        dialog.setView(layout);

        // Setup for the two button choices
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textHost = inputIP.getText().toString();
                textPort = inputPort.getText().toString();

                // Check that both fields have been pressed and that it doesn't contain default text
                if (!textHostPressed || !textPortPressed) {
                    customToast("Please specify both host and port", Toast.LENGTH_LONG).show();
                    if (textHost.contains("Current host") || textPort.contains("Current port")){
                        customToast("Please specify both host and port", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Create a new Client if fields have been pressed, and check the connection
                    client = new Client(context, textHost, textPort);
                    checkClientConnection();
                    saveBroker(context);
                }

                // Check if input fields are empty
                if (textHost.isEmpty()) {
                    customToast("Please enter an Ip/Url", Toast.LENGTH_SHORT).show();
                } else if (textPort.isEmpty()) {
                    customToast("Please enter a Port", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the View
        dialog.show();
    }

    private void saveBroker(Context context) {
        // Save the data to broker.txt for next app start
        HandleFiles handleFiles = new HandleFiles();
        ArrayList<String> broker = new ArrayList<String>();
        broker.add(textHost);
        broker.add(textPort);
        handleFiles.write(broker, context);
    }

    private Toast customToast(String text, int length) {
        Toast toast = Toast.makeText(MainActivity.this, text, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        return toast;
    }
}