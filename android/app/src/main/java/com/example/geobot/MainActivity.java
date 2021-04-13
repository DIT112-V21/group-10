package com.example.geobot;

import androidx.appcompat.app.AppCompatActivity;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    // The two main buttons atm on main activity
    private Button manualSwitch;
    private Button autoPilot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        buttonSetter();

    }



    // Onclick switches to Manual control activity page
    private void manualControl() {
        Intent switchActivity = new Intent(this, manualActivity.class);
        switchActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(switchActivity);
    }
    // Onclick switches to autopilot activity page
    private void pixelMap(){
        Intent switchActivity = new Intent(this, mapActivity.class);
        switchActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(switchActivity);


    }


    // Connects main activities buttons to relevant activity page
    void buttonSetter(){

        manualSwitch = (Button)findViewById(R.id.manual);
        manualSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualControl();
            }
        });

        autoPilot = (Button)findViewById(R.id.button2);
        autoPilot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pixelMap();
            }
        });



    }


}
