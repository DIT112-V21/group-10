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

void setup()
{
    magnitude = rand() % (100 + 1 - 30) + 30;
    Serial.begin(9600);
    Serial.setTimeout(200);
}

void loop()
{
    move();
    delay(35);
}

void move()
{
    float distance = front.getDistance();
    serialMsg(distance);
    distanceHandler(0, 200, distance);
}

void handleObstacle()
{
//    magnitude = -1 * magnitude;
//    car.setSpeed(magnitude);
    car.setAngle(50);                //In this line the car will turn while going backward to avoid obstacle
    delay(1000);                     //Here we give some time to the poor car to do previous actions
}



void distanceHandler(float lowerBound, float upperBound, float distance)
{
    if (distance > lowerBound && distance < upperBound)
    {
        handleObstacle();
    }
    car.setSpeed(magnitude);            //this makes sure the car is back to its forward direction if a turning happened.
    car.setAngle(0);                     //and this!
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
}
void angleMsg(int angle) //This function prints the direction in which the car will be going
{
    if(angle > 0){
        Serial.print("Turning ");
        Serial.print(angle);
        Serial.println(" degrees right.");
    }
    else if(angle == 0){
        Serial.println("Going straight ahead.");
    }
    else{
        Serial.print("Turning ");
        Serial.print(angle);
        Serial.println(" degrees left.");
    }
     
}
