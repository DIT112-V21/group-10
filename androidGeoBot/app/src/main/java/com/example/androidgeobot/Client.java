package com.example.androidgeobot;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

// Helper class between actual MqttClient and activities
//
public class Client {

    protected MqttClient mqttClient;
    private static final String FAIL = "CONNECTION TO GEOBOT COULD NOT BE ESTABLISHED";
    private static final String FORWARD_CONTROL = "/Group10/manual/forward";
    private static final String BACKWARD_CONTROL = "/Group10/manual/backward";
    private static final String TURN_LEFT = "/Group10/manual/turnleft";
    private static final String TURN_RIGHT = "/Group10/manual/turnright";
    private static final String BREAK = "/Group10/manual/break";
    private static final String ACCELERATE = "/Group10/manual/accelerateup";
    private static final String DECELERATE = "/Group10/manual/acceleratedown";

    private static final String ULTRASOUND_FRONT = "/Group10/sensor/ultrasound/front";
    private static final int SPEED = 50;
    private static final int ANGLE = 10;
//    private static final int LEFT_TURN = -75;
//    private static final int RESET_ANGLE = 0;
    private static final String TAG = "localhost";
    private static final String MQTT_BROKER = "aerostun.dev";
    private static final String LOCAL_MQTT = "10.0.2.2";
    private static final String MQTT_SERVER = "tcp://" + LOCAL_MQTT + ":1883";
    private static final int QOS = 1;
    private boolean isConnected = false;
    private Context context;

    public Client(Context context){
        this.context = context;
        this.mqttClient = new MqttClient(context, MQTT_SERVER, TAG);

    }

    public boolean connect() {

        mqttClient.connect(TAG, "", new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                final String successfulConnection = "CONNECTION TO GEOBOT ESTABLISHED";
                Log.i(TAG, successfulConnection);
                Toast.makeText(context, successfulConnection, Toast.LENGTH_SHORT).show();

                mqttClient.subscribe(ULTRASOUND_FRONT, QOS, null);
                //mqttClient.subscribe("/Group10/manual/#", QOS, null);
                isConnected = true;

            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                final String failedConnection = FAIL;
                Log.e(TAG, failedConnection);
                Toast.makeText(context, failedConnection, Toast.LENGTH_SHORT).show();
                isConnected = false;
                //throw new UnsupportedOperationException();
            }

        }, new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                isConnected = false;

                final String connectionLost = "Connection to MQTT broker lost";
                Log.w(TAG, connectionLost);
                Toast.makeText(context, connectionLost, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (topic.equals(ULTRASOUND_FRONT)) {
                     Log.i(TAG, "[MQTT] Topic: " + topic + " | Message: " + message.toString());


                } else {
                    Log.i(TAG, "[MQTT] Topic: " + topic + " | Message: " + message.toString());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "Message delivered");
            }
        });
        return isConnected;
    }


// Depending on ID of button the method sends appropriate messages to relevant topic.
    protected void button_publish(Button button){

        if(!(button == null) && isConnected){
            switch (button.getId()){

                case R.id.forward_button:
                    mqttClient.publish(FORWARD_CONTROL, Integer.toString(SPEED),QOS, null);
                    break;

                case R.id.backward_button:
                    mqttClient.publish(BACKWARD_CONTROL, Integer.toString(SPEED),QOS, null);
                    break;

                case R.id.right_button:
                    mqttClient.publish(TURN_RIGHT, Integer.toString(ANGLE),QOS, null);
                    break;

                case R.id.left_button:
                    mqttClient.publish(TURN_LEFT, Integer.toString(ANGLE),QOS,null);
                    break;

               case R.id.accelerate_up:
                   mqttClient.publish(ACCELERATE, Integer.toString(SPEED),QOS,null);
                   break;

               case R.id.accelerate_down:
                   mqttClient.publish(DECELERATE, Integer.toString(SPEED),QOS,null);
                   break;

                case R.id.break_button:
                    mqttClient.publish(BREAK, Integer.toString(0),QOS,null);
                    break;

                default:
            }
        } else if((button == null) && isConnected) {

            mqttClient.publish("/Group10/manual/nocontrol", Integer.toString(0),QOS,null);

        } else {

            Toast.makeText(context, "Connection not established", Toast.LENGTH_SHORT).show();

        }



    }




}
