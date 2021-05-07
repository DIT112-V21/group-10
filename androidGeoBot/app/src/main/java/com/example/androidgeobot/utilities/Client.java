package com.example.androidgeobot.utilities;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.util.Log;
import android.widget.Toast;

import android.widget.Button;

import com.example.androidgeobot.ManualActivity;
import com.example.androidgeobot.R;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

// Helper class between actual MqttClient and activities
//
public class Client extends MqttClient {

    protected MqttClient mqttClient;

    // Topics to update to
    private static final String FAIL = "CONNECTION TO TANK COULD NOT BE ESTABLISHED";
    private static final String FORWARD_CONTROL = "/Group10/manual/forward";
    private static final String BACKWARD_CONTROL = "/Group10/manual/backward";
    private static final String TURN_LEFT = "/Group10/manual/turnleft";
    private static final String TURN_RIGHT = "/Group10/manual/turnright";
    private static final String BREAK = "/Group10/manual/break";
    private static final String ACCELERATE = "/Group10/manual/accelerateup";
    private static final String DECELERATE = "/Group10/manual/acceleratedown";
    private static final int IMAGE_WIDTH = 320;
    private static final int IMAGE_HEIGHT = 240;

    // Topics to get data from
    private static final String ULTRASOUND_FRONT = "/Group10/sensor/ultrasound/front";

    // Message attributes
    private static final int SPEED = 100;
    private static final int ANGLE = 30;
//    private static final int LEFT_TURN = -75;
//    private static final int RESET_ANGLE = 0;

    // Connection attributes
    private static final String TAG = "localhost";
    private static final String MQTT_BROKER = "aerostun.dev";
    private static final String LOCAL_MQTT = "10.0.2.2";
    private static final String MQTT_SERVER = "tcp://" + LOCAL_MQTT + ":1883";
    private static final int QOS = 1;
    private boolean isConnected = false;
    private static Context context;

    public Client(Context context){
        super(context, MQTT_SERVER, TAG);
        this.context = context;
    }


    @Override
    public boolean connect(String username, String password, IMqttActionListener connectionCallback, MqttCallback clientCallback) {
        return super.connect(TAG, "", new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                final String successfulConnection = "CONNECTION TO TANK ESTABLISHED";
                Log.i(TAG, successfulConnection);
                Toast.makeText(context, successfulConnection, Toast.LENGTH_SHORT).show();

                subscribe(ULTRASOUND_FRONT, QOS, null);
                subscribe("/Group10/camera", QOS, null);
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
            public void messageArrived(String topic, MqttMessage message)throws Exception {
                if (topic.equals(ULTRASOUND_FRONT)) {
                    Log.i(TAG, "[MQTT] Topic: " + topic + " | Message: " + message.toString());
                }
                else if (topic.equals("/Group10/camera")) {
                    final Bitmap bm = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);
                    Log.d(TAG, "Message delivered");

                    final byte[] payload = (byte[]) message.getPayload();
                    final int[] colors = new int[IMAGE_WIDTH * IMAGE_HEIGHT];
                    for (int ci = 0; ci < colors.length; ++ci) {
                        final byte r = payload[3 * ci];
                        final byte g = payload[3 * ci + 1];
                        final byte b = payload[3 * ci + 2];
                        colors[ci] = Color.rgb(r, g, b);
                    }
                    bm.setPixels(colors, 0, IMAGE_WIDTH, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
                    ManualActivity manualActivity = (ManualActivity)context;
                    Detection detection = new Detection();
                    detection.processImage(bm,context);
                    manualActivity.setBitmap(bm);


                }else {
                    Log.i(TAG, "[MQTT] Topic: " + topic + " | Message: " + message.toString());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "Message delivered");
            }
        });
    }

    @Override
    public void disconnect(IMqttActionListener disconnectionCallback) {
        super.disconnect(disconnectionCallback);
    }

    @Override
    public void subscribe(String topic, int qos, IMqttActionListener subscriptionCallback) {
        super.subscribe(topic, qos, subscriptionCallback);
    }

    @Override
    public void unsubscribe(String topic, IMqttActionListener unsubscriptionCallback) {
        super.unsubscribe(topic, unsubscriptionCallback);
    }

    @Override
    public void publish(String topic, String message, int qos, IMqttActionListener publishCallback) {
        super.publish(topic, message, qos, publishCallback);
    }




// Depending on ID of button the method sends appropriate messages to relevant topic.
        public void button_publish(Button button){
            if(!(button == null) && isConnected){
                switch (button.getId()){

                    case R.id.forward_button:
                        publish(FORWARD_CONTROL, Integer.toString(SPEED),QOS, null);
                        break;

                    case R.id.backward_button:
                        publish(BACKWARD_CONTROL, Integer.toString(SPEED),QOS, null);
                        break;

                    case R.id.right_button:
                        publish(TURN_RIGHT, Integer.toString(ANGLE),QOS, null);
                        break;

                    case R.id.left_button:
                        publish(TURN_LEFT, Integer.toString(ANGLE),QOS,null);
                        break;

//               case R.id.accelerate_up:
//                   mqttClient.publish(ACCELERATE, Integer.toString(SPEED),QOS,null);
//                   break;
//
//               case R.id.accelerate_down:
//                   mqttClient.publish(DECELERATE, Integer.toString(SPEED),QOS,null);
//                   break;

                    case R.id.break_button:
                        publish(BREAK, Integer.toString(0),QOS,null);
                        break;

                    default:
                }
            } else if((button == null) && isConnected) {

                publish("/Group10/manual/nocontrol", Integer.toString(0),QOS,null);

            } else {

                Toast.makeText(context, "Connection not established", Toast.LENGTH_SHORT).show();

            }
        }
}


