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
  if (mqtt.connect("arduino", "public", "public")) {
    mqtt.subscribe("/Group10/manual/#", 1);
    mqtt.onMessage([](String topic, String message) {
      if (topic == "/Group10/manual/forward") {
          latestSpeed = message.toInt();
          car.setSpeed(latestSpeed);
        
       /* if(message.toInt() > 0 && !(latestSpeed == 0)){
        latestSpeed += message.toInt();
        car.setSpeed(latestSpeed);
        }else if(message.toInt() > 0 && latestSpeed == 0){
          latestSpeed = message.toInt();
          car.setSpeed(latestSpeed);
        }else {
          car.setSpeed(latestSpeed - message.toInt());
        }*/
      } else if (topic == "/Group10/manual/backward") {
          latestSpeed = (-1) * message.toInt();
          car.setSpeed(latestSpeed);

      } else if (topic == "/Group10/manual/turnleft") {
          car.setAngle(message.toInt());
          delay(500);
          car.setAngle(0);

      } else if (topic == "/Group10/manual/turnright") {
          int carAngle = (-1) * message.toInt();
          car.setAngle(carAngle);
          delay(500);
          car.setAngle(0);

      } else if (topic == "/Group10/manual/break") {
          latestSpeed = 0;
          car.setSpeed(latestSpeed);

      } else if (topic == "/Group10/manual/accelerateup") {
          latestSpeed = latestSpeed * 1.3;
          car.setSpeed(latestSpeed);

      } else if (topic == "/Group10/manual/acceleratedown") {
          latestSpeed = latestSpeed * 0.7;
          car.setSpeed(latestSpeed);

      } else {
        Serial.println(topic + " " + message);
      }
    });
  }
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
    handleInput();
    delay(35);
}

void handleInput()
{
    float distance = front.getDistance();
    serialMsg(distance);
    if (Serial.available())            //If the user enters input in the serial The car must move according to that input.
    {
        String input = Serial.readStringUntil('\n');
        serialReader(input);
        distanceHandler(0, 200, distance);
    } else {
        distanceHandler(0, 200, distance);
    }
}

void handleObstacle()
{
    car.setSpeed(-magnitude);        //In here the car will go back in the opposite direction but with the same speed
    car.setAngle(50);                //In this line the car will turn while going backward to avoid obstacle
    delay(1000);                     //Here we give some time to the poor car to do previous actions
}

void serialReader(String input)
{
    if (input.startsWith("m"))
    {
        int cSpeed = input.substring(1).toInt();
        magnitude = cSpeed;              //We save the user's input in here in order to have it outside of the if scope.
        car.setSpeed(cSpeed);
        Serial.print("Current speed is ");
        Serial.println(cSpeed);
    } else if (input.startsWith("t"))
    {
        int cAngle = input.substring(1).toInt();
        car.setAngle(cAngle);
        angleMsg(cAngle);
        delay(600);    //This delay is needed for the car to turn in a short while and then go back to its straight direction,
    }                 // because we dont want the car to to turn around itself for no reason!
}

void distanceHandler(float lowerBound, float upperBound, float distance)
{
    if (distance > lowerBound && distance < upperBound)
    {
        handleObstacle();
    }
    car.setSpeed(latestSpeed);            //this makes sure the car is back to its forward direction if a turning happened.
   // car.setAngle(0);                    //and this!
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
