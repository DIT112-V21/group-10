package com.example.androidtank;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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

    private String textHost = "";
    private String textPort = "";

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

        /**
         * Try:
         * 1. loading the file with broker settings
         * 2. Creating a client from those stored settings
         * 3. publish these settings to Tank
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

        publishCurrentClient(); // Publish the current chosen broker settings to Tank.
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

        publishCurrentClient(); // Publish the current chosen broker settings to Tank.

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

    // Publish the current chosen broker settings to Tank.
    private void publishCurrentClient() {
        // This CountDownTimer is necessary since we need to have a connection with Tank first.
        // If we publish too soon after connecting we will get NullPointerException
        new CountDownTimer(1000, 1000)
        {
            public void onTick(long millisUntilFinished) {
                // Do nothing
            }
            public void onFinish() {
                try {
                    if (loadedFromFile) {
                        client.host_publish(client.getCustomHost());
                        client.port_publish(client.getCustomPort());
                    } else {
                        client.host_publish(client.getMqtt());
                        client.port_publish(client.getPort());
                    }
                } catch (NullPointerException e) {
                    Log.i("Publish Broker", "Could not publish broker to Tank");
                }
            }
        }.start();
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
        }
    }

    /**
     * Methods related to Server settings
     */
    @SuppressLint("SetTextI18n")
    private void initializeServerView() {
        context = (MainActivity) MainActivity.this;
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
        inputIP.setText("Current host: " + client.getCustomHost());
        inputPort.setText("Current port: " + client.getCustomPort());

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
        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.AlertDialog);
        dialog.setTitle("Server ( Default: 10.0.2.2, 1883)"); // Title
        TextView textView = (TextView) new TextView(context);
        Typeface face = ResourcesCompat.getFont(context, R.font.bree_serif);
        textView.setTypeface(face);
        inputIP.setTypeface(face);
        inputPort.setTypeface(face);

        // Set margins for the textfields
        final ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) inputIP.getLayoutParams();
        final ViewGroup.MarginLayoutParams mlp2 = (ViewGroup.MarginLayoutParams) inputPort.getLayoutParams();
        mlp.setMargins(55, 0, 55, 0);
        mlp2.setMargins(55, 0, 55, 0);
        dialog.setView(layout);

        // Setup for the two button choices
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textHost = inputIP.getText().toString();
                textPort = inputPort.getText().toString();

                // Check if input fields are empty
                if (textHost.isEmpty()) {
                    customToast("Please enter an Ip/Url", Toast.LENGTH_SHORT).show();
                } else if (textPort.isEmpty()) {
                    customToast("Please enter a Port", Toast.LENGTH_SHORT).show();
                }

                // Create a new Client, check the connection and save it to a file, broker.txt
                // TODO Change "else if" to not rely on "Current host" and "Current port".
                if (!textHost.contains("Current host") && !textPort.contains("Current port")){
                    customToast("Settings: " + "IP: " + textHost + ", PORT: " + textPort, Toast.LENGTH_LONG).show();
                    client.host_publish(textHost);
                    client.port_publish(textPort);
                    client = new Client(context, textHost, textPort);

                    checkClientConnection();
                    saveBroker(context);
                } else {
                    customToast("Please specify both host and port", Toast.LENGTH_LONG).show();
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