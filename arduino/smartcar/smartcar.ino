#include <vector>

#include <MQTT.h>
#include <WiFi.h>
#ifdef __SMCE__
#include <OV767X.h>
#endif

#include <Smartcar.h>

#ifndef __SMCE__
WiFiClient net;
#endif
MQTTClient mqtt;

const int SIDE_FRONT_PIN = 0;
ArduinoRuntime arduinoRuntime;
unsigned long startMillis;
unsigned long currentMillis;
const unsigned long period = 5000; //5 seconds
const auto oneSecond = 1000UL;
BrushedMotor leftMotor(arduinoRuntime, smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);
const int TRIGGER_PIN           = 6; // D6
const int ECHO_PIN              = 7; // D7
const unsigned int MAX_DISTANCE = 300;
SR04 front(arduinoRuntime, TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE);
GP2D120 sideFrontIR(arduinoRuntime,
                    SIDE_FRONT_PIN); // measure distances between 5 and 25 centimeters
SimpleCar car(control);
int latestSpeed = 0;
int latestAngle = 0;
int magnitude = 0;
int score = 0;

std::vector<char> frameBuffer;

void setup()
{

    Serial.begin(9600);
    Serial.setTimeout(200);
#ifdef __SMCE__

  Camera.begin(QVGA, RGB888, 15);
  frameBuffer.resize(Camera.width() * Camera.height() * Camera.bytesPerPixel());
  mqtt.begin(WiFi);
#else
  mqtt.begin(net);
#endif
  mqttHandler();
  startMillis = millis();
}

void loop()
{
  if (mqtt.connected()) {
    mqtt.loop();
    const auto currentTime = millis();

  #ifdef __SMCE__
  static auto previousFrame = 0UL;
  if (currentTime - previousFrame >= 65) {
    previousFrame = currentTime;
    Camera.readFrame(frameBuffer.data());
    mqtt.publish("/Group10/camera", frameBuffer.data(), frameBuffer.size(),
                   false, 0);
    }
  #endif
    static auto previousTransmission = 0UL;
    if (currentTime - previousTransmission >= oneSecond) {
      previousTransmission = currentTime;

      const auto distance = String(front.getDistance());
      mqtt.publish("/Group10/sensor/ultrasound/front", distance);
    }
  }

    currentMillis = millis(); //get the current "time" (actually the number of milliseconds since the program started)
    updateScore();
    handleInput();
    delay(35);
}

void handleInput()
{
    float distance = front.getDistance();
    //serialMsg(distance);
    distanceHandler(0, 200, distance);
}

void handleObstacle()
{
    magnitude = latestSpeed * 0.4;
    car.setSpeed(magnitude);
}

void mqttHandler()
{
    if (mqtt.connect("arduino", "public", "public")) {
        mqtt.subscribe("/Group10/manual/#", 1);
        mqtt.onMessage([](String topic, String message) {
            if (topic == "/Group10/manual/forward") {
                latestSpeed = message.toInt();
                car.setAngle(latestAngle);
                car.setSpeed(latestSpeed);

            } else if (topic == "/Group10/manual/backward") {
                latestSpeed = (-1) * message.toInt();
                car.setAngle(latestAngle);
                car.setSpeed(latestSpeed);

            } else if (topic == "/Group10/manual/turnleft") {
                latestAngle = (-1) * message.toInt();
                car.setAngle(latestAngle);

            } else if (topic == "/Group10/manual/turnright") {
                latestAngle = message.toInt();
                car.setAngle(latestAngle);

            } else if (topic == "/Group10/manual/break") {
                latestSpeed = 0;
                car.setSpeed(latestSpeed);

            } /*else if (topic == "/Group10/manual/accelerateup") {
                latestSpeed = latestSpeed * 1.1;
                car.setSpeed(latestSpeed);
            } else if (topic == "/Group10/manual/acceleratedown") {
                latestSpeed = latestSpeed * 0.9;
                car.setSpeed(latestSpeed);
            }*/ else if (topic == "/Group10/manual/nocontrol"){
                latestSpeed = latestSpeed * 0.8;
                latestAngle = 0;
                car.setSpeed(latestSpeed);
                car.setAngle(latestAngle);

            }

        });
    }
}

void distanceHandler(float lowerBound, float upperBound, float distance)
{
    if (distance > lowerBound && distance < upperBound)
    {
        handleObstacle();
    }
}

void serialMsg(float distance)
{
    if (distance > 0 && (currentMillis - startMillis) >= period) { //The user is updated on the distance to an obstacle every 7 seconds
        String msg1 = "There is an obstacle in ";
        String msg2 = " cm.";
        Serial.print(msg1);
        Serial.print(distance);
        Serial.println(msg2);
        startMillis = currentMillis;
    } else if ((currentMillis - startMillis) >= period) {
        String msg = "No obstacle detected.";
        Serial.println(msg);
        startMillis = currentMillis;
    }
    Serial.print("Current Speed: ");
    Serial.println(latestSpeed);
    Serial.print("Current Angle: ");
    Serial.println(latestAngle);
}
void updateScore(){

  float distanceToScore = sideFrontIR.getDistance();
  if(distanceToScore > 0 && distanceToScore < 15 && (currentMillis - startMillis) >= period){
    score += 1;
    Serial.println(score);
    startMillis = currentMillis;
    mqtt.publish("/Group10/manual/score", String(score));
  }
}
//void angleMsg()
//{
//    if(latestAngle > 0){
//        Serial.print("Turning ");
//        Serial.print(latestAngle);
//        Serial.println(" degrees right.");
//    }
//    else if(latestAngle == 0){
//        Serial.println("Going straight ahead.");
//    }
//    else{
//        Serial.print("Turning ");
//        Serial.print(latestAngle);
//        Serial.println(" degrees left.");
//    }
//}