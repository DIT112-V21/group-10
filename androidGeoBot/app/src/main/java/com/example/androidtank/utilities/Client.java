package com.example.androidtank.utilities;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Button;

import io.github.controlwear.virtual.joystick.android.JoystickView;
import com.example.androidtank.ManualActivity;
import com.example.androidtank.R;
import com.example.androidtank.opencv.Detection;
import com.google.android.material.slider.Slider;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

// Helper class between actual MqttClient and activities
//
public class Client extends MqttClient {

    // Attributes
    private final String TAG2 = this.getClass().getName();
    private int lastJoystickX = 0;
    private int lastJoysticky = 0;
    private int scoreValue;
    private static final int IMAGE_WIDTH = 320;
    private static final int IMAGE_HEIGHT = 240;

    // Topics to update to
    private static final String FAIL = "CONNECTION TO TANK COULD NOT BE ESTABLISHED";
    private static final String FORWARD_CONTROL = "/Group10/manual/forward";
    private static final String BACKWARD_CONTROL = "/Group10/manual/backward";
    private static final String TURN_LEFT = "/Group10/manual/turnleft";
    private static final String TURN_RIGHT = "/Group10/manual/turnright";
    private static final String BREAK = "/Group10/manual/break";
    private static final String STOPPING = "/Group10/manual/stopping";
    private static final String MQTT_HOST = "/Group10/manual/server/ip";
    private static final String MQTT_PORT = "/Group10/manual/server/p";

    // Topics to get data from
    private static final String ULTRASOUND_FRONT = "/Group10/sensor/ultrasound/front";
    private static final String UPDATE_SCORE = "/Group10/manual/score";

    // Connection related attributes
    protected MqttClient mqttClient;
    private static final String TAG = "localhost";
    private static final String MQTT_BROKER = "aerostun.dev";
    private static final String DEFAULT_HOST = "10.0.2.2"; // default is 10.0.2.2
    private static final String DEFAULT_PORT = "1883";
    private String customHost = "10.0.2.2"; // default is 10.0.2.2
    private String customPort = "1883";
    private static String mqtt_server = "tcp://" + DEFAULT_HOST + ":" + DEFAULT_PORT;
    private static final int QOS = 1;
    private boolean isConnected = false;
    private static Context context;


    /**
     * Constructors
     */
    public Client(Context context) {
        super(context, mqtt_server, TAG);
        Log.i(TAG2, "Instantiated new " + this.getClass());
        Client.context = context;
    }

    public Client(Context context, String host, String port) {
        super(context, mqtt_server = "tcp://" + host + ":" + port, TAG);
        Log.i(TAG2, "Instantiated new " + this.getClass());
        customHost = host;
        customPort = port;
        Client.context = context;
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
                subscribe(UPDATE_SCORE, QOS, null);
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
                } else if (topic.equals("/Group10/camera")) {
                    Bitmap bm = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);
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

                    // 1. Get the context of ManualActivity so that we can update the image
                    // 2. detection.processImage does OpenCV magic. Detecting objects and drawing shapes
                    ManualActivity manualActivity = (ManualActivity) context;
                    Detection detection = new Detection();
                    bm = detection.processImage(bm, context);
                    manualActivity.setBitmap(bm);

                } else if (topic.equals(UPDATE_SCORE)) {
                    String scoreString = message.toString();
                    scoreValue = Integer.parseInt(scoreString);
                    ManualActivity manualActivity = (ManualActivity) context;
                    TextView scoreDisplay = manualActivity.getScore();
                    String scoreMessage = "Score: " + message.toString();
                    scoreDisplay.setText(scoreMessage);
                } else {
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

    /**
     * Methods related to publishing
     */
    // Depending on ID of button the method sends appropriate messages to relevant topic.
    public void button_publish(Button button) {
        if (!(button == null) && isConnected) {
            switch (button.getId()) {
//               case R.id.accelerate_up:
//                   mqttClient.publish(ACCELERATE, Integer.toString(SPEED),QOS,null);
//                   break;
//
//               case R.id.accelerate_down:
//                   mqttClient.publish(DECELERATE, Integer.toString(SPEED),QOS,null);
//                   break;

                case R.id.break_button:
                    publish(BREAK, Integer.toString(0), QOS, null);
                    break;

                default:
            }
        } else if ((button == null) && isConnected) {

            publish("/Group10/manual/nocontrol", Integer.toString(0), QOS, null);

        } else {

            Toast.makeText(context, "Connection not established", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * @param joystickView The view that displays the joystick
     * @param angle        The angle that the joystick uses
     * @param y            The joysticks y coordinate. Translated into speed for Tank
     * @param x            The joysticks y coordinate. Translated into angle for Tank
     */
    public void joystick_publish(Context context, JoystickView joystickView, int angle, int y, int x) {
        Log.i("Stuff", "X:" + x + ", Y:" + y); // for debugging

        if (!(joystickView == null) && isConnected) {

            // Handle Speed Messages
            // This "if" stops mqtt from sending a message with the same "y" value as the previous message
            if (y != lastJoysticky) {
                if (y > 0 && angle >= 180) {
                    ManualActivity manualActivity = (ManualActivity) context;
                    Slider slider = manualActivity.getSlider();
                    float sliderValue = slider.getValue();
                    y = Math.round((float) y / 100 * sliderValue); // Convert y to slider scale
                    publish(BACKWARD_CONTROL, Float.toString(y), QOS, null);
                }
                if (y > 0 && angle < 180) {
                    ManualActivity manualActivity = (ManualActivity) context;
                    Slider slider = manualActivity.getSlider();
                    float sliderValue = slider.getValue();
                    y = Math.round((float) y / 100 * sliderValue); // Convert y to slider scale
                    publish(FORWARD_CONTROL, Float.toString(y), QOS, null);
                }
            }

            // Handle Angle Messages
            // This "if" stops mqtt from sending a message with the same "y" value as the previous message
            if (x != lastJoystickX) {
                if (angle <= 90) {
                    publish(TURN_RIGHT, Integer.toString(x), QOS, null);
                } else if (angle <= 180) {
                    publish(TURN_LEFT, Integer.toString(x), QOS, null);
                } else if (angle <= 270) {
                    publish(TURN_LEFT, Integer.toString(x), QOS, null);
                } else {
                    publish(TURN_RIGHT, Integer.toString(x), QOS, null);
                }
            }

            // Handle Joystick Released Message
            if (y == 0 && angle == 0) {
                publish(STOPPING, Integer.toString(0), QOS, null);
            }

        } else if ((joystickView == null) && isConnected) {
            publish("/Group10/manual/nocontrol", Integer.toString(0), QOS, null);
        } else {
            Toast.makeText(context, "Connection not established", Toast.LENGTH_SHORT).show();
        }

        lastJoystickX = x;
        lastJoysticky = y;
    }

    public void host_publish(String host) {
        publish(MQTT_HOST, host, QOS, null);
        Log.i("Server", host + ", ");
    }

    public void port_publish(String newPort) {
        publish(MQTT_PORT, newPort, QOS, null);
        Log.i("Server", ", " + newPort);
    }


    /**
     * Getters and setters
     */
    public int getScoreValue() {
        return this.scoreValue;
    }

    public String getMqtt() {
        return DEFAULT_HOST;
    }

    public String getPort() {
        return DEFAULT_PORT;
    }

    public String getCustomPort() {
        return customPort;
    }

    public String getCustomHost() {
        return customHost;
    }
}


