package com.example.androidtank;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
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
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static Client client;
    private static final String FAIL = "CONNECTION TO TANK COULD NOT BE ESTABLISHED";
    private static final String SUCCESS = "CONNECTION TO TANK ESTABLISHED";

    private Button buttonLaunchManualAc, buttonLaunchHelp, buttonLaunchBroker;

    MainVideoView mainVideoView;
    private int videoViewId = R.raw.mainscreen_video2;

    private String mText = "";
    private String mText2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Stops the title and the top action bar from displaying and sets window to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set view
        setContentView(R.layout.activity_main);

        // Mqtt Client
        this.client = new Client(this);
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
        SoundEffect.startEffect(this, R.raw.main_music, 0.27f, true, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainVideoView.suspend();
        mainVideoView.stopPlayback();
        SoundEffect.stopEffect();
    }

    private void checkClientConnection() {
        if (!client.connect(null, null, null, null)) {
            Toast toast = Toast.makeText(this, FAIL, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            Toast toast = Toast.makeText(this, SUCCESS, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
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
            // e.printStackTrace();
        }
    }

    /**
     * Methods related to Server settings
     */
    private void initializeServerView() {
        Context context = MainActivity.this;

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
        layout.addView(inputIP);
        layout.addView(inputPort);
        inputIP.setText("ip/url (Default: 10.0.2.2)");
        inputPort.setText("port (Default: 1883)");

        // Set the expected input type for the text-fields
        inputIP.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        inputPort.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);

        // Remove text from text-field when text-field is selected
        // TODO: Remake this. This doesn't work well enough since user has to press twice.
        inputIP.setOnClickListener(v1 -> {
            inputIP.setText("");
        });
        inputPort.setOnClickListener(v1 -> {
            inputPort.setText("");
        });

        // Create a new Alert Dialog view and set the font for text
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Server settings"); // Title
        TextView textView = (TextView) new TextView(context);
        Typeface face = ResourcesCompat.getFont(context, R.font.bree_serif);
        textView.setTypeface(face);
        inputIP.setTypeface(face);
        inputPort.setTypeface(face);

        // set margins for the textfields
        final ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) inputIP.getLayoutParams();
        final ViewGroup.MarginLayoutParams mlp2 = (ViewGroup.MarginLayoutParams) inputPort.getLayoutParams();
        mlp.setMargins(55, 0, 55, 0);
        mlp2.setMargins(55, 0, 55, 0);

        dialog.setView(layout);

        // Setup for the two button choices
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mText = inputIP.getText().toString();
                mText2 = inputPort.getText().toString();

                if (mText.isEmpty()) {
                    Toast toast = Toast.makeText(MainActivity.this, "Please enter an Ip/Url", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (mText2.isEmpty()) {
                    Toast toast = Toast.makeText(MainActivity.this, "Please enter a Port", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (mText.contains("10.0.2.2") && mText2.contains("1883")) {
                    Toast toast = Toast.makeText(MainActivity.this, "Setting server to '10.0.2.2:1883'", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    client.server_publish("10.0.2.2");
                    client.server_publish2("1883");
                    client = new Client(MainActivity.this, "10.0.2.2", "1883");
                    checkClientConnection();
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, "Settings: " + "IP: " + mText + ", PORT: " + mText2, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    client.server_publish(mText);
                    client.server_publish2(mText2);
                    client = new Client(MainActivity.this, mText, mText2);
                    checkClientConnection();
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
}