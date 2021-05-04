#include <vector>
#include <MQTT.h>
#include <WiFi.h>
#include <Smartcar.h>

#ifndef __SMCE__
WiFiClient net;
#endif
MQTTClient mqtt;

ArduinoRuntime arduinoRuntime;
unsigned long startMillis;  
unsigned long currentMillis;
const unsigned long period = 7000; //7 seconds
const auto oneSecond = 1000UL;
BrushedMotor leftMotor(arduinoRuntime, smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);
const int TRIGGER_PIN           = 6; // D6
const int ECHO_PIN              = 7; // D7
const unsigned int MAX_DISTANCE = 300;
SR04 front(arduinoRuntime, TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE);
SimpleCar car(control);
int magnitude;                              //This we will need later
int latestSpeed = 0;

void setup()
{
    magnitude = 0;
    Serial.begin(9600);
    Serial.setTimeout(200);
#ifndef __SMCE__
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
    
    static auto previousTransmission = 0UL;
    if (currentTime - previousTransmission >= oneSecond) {
      previousTransmission = currentTime;
      const auto distance = String(front.getDistance());
      mqtt.publish("/Group10/sensor/ultrasound/front", distance);
    }
  }
  
    currentMillis = millis(); //get the current "time" (actually the number of milliseconds since the program started)
    delay(35);
}




//void serialReader(String input)
//{
//    if (input.startsWith("m"))
//    {
//        int cSpeed = input.substring(1).toInt();
//
//        car.setSpeed(latestSpeed);
//        Serial.print("Current speed is ");
//        Serial.println(latestSpeed);
//    } else if (input.startsWith("t"))
//    {
//        int cAngle = input.substring(1).toInt();
//        car.setAngle(cAngle);
//        angleMsg(cAngle);
//        delay(600);    //This delay is needed for the car to turn in a short while and then go back to its straight direction,
//    }                 // because we dont want the car to to turn around itself for no reason!
//}

void mqttHandler()
{
    if (mqtt.connect("arduino", "public", "public")) {
        mqtt.subscribe("/Group10/manual/#", 1);
        mqtt.onMessage([](String topic, String message) {
            if (topic == "/Group10/manual/forward") {
                car.setAngle(0);

            } else if (topic == "/Group10/manual/backward") {
                latestSpeed = (-1) * message.toInt();
                car.setSpeed(latestSpeed);

            } else if (topic == "/Group10/manual/turnleft") {
                car.setAngle(message.toInt());
                //delay(500);

            } else if (topic == "/Group10/manual/turnright") {
                car.setAngle(message.toInt());
               // delay(500);

            } else if (topic == "/Group10/manual/break") {
                latestSpeed = 0;
                car.setSpeed(latestSpeed);

            } else if (topic == "/Group10/manual/accelerateup") {
                if(latestSpeed != 0){
                latestSpeed += 10;
                car.setSpeed(latestSpeed);
                } else {
                  latestSpeed = 10;
                  car.setSpeed(latestSpeed);
                }

            } else if (topic == "/Group10/manual/acceleratedown") {
                latestSpeed -= 10;
                car.setSpeed(latestSpeed);

            } else {
                Serial.println(topic + " " + message);
            }
        });
    }
}
