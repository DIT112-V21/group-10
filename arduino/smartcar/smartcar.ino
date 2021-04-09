#include <Smartcar.h>

ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor(arduinoRuntime, smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);
const int TRIGGER_PIN           = 6; // D6
const int ECHO_PIN              = 7; // D7
const unsigned int MAX_DISTANCE = 300;
SR04 front(arduinoRuntime, TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE);
SimpleCar car(control);
int magnitude = 0;                              //This we will need later

void setup()
{
    Serial.begin(9600);
    Serial.setTimeout(200);
}

void loop()
{
  handleInput();
  delay(35);
}

void handleInput()
{
  float distance = front.getDistance();
  Serial.println(distance);
  if (Serial.available())                           //If the user enters input in the serial The car must move according to that input.
  {
   String input = Serial.readStringUntil('\n');
   if (input.startsWith("m"))     
   {
     int cSpeed = input.substring(1).toInt();
     magnitude = cSpeed;                           //We save the user's input in here in order to have it outside of the if scope.                     
     car.setSpeed(cSpeed); 
   } else if (input.startsWith("t"))
   {
     int cAngle = input.substring(1).toInt();
     car.setAngle(cAngle);
     delay(600);                                                      //This delay is needed for the car to turn in a short while and then go back to its straight direction,  
   }                                                                  // because we dont want the car to to turn around itself for no reason!
   if (distance > 1 && distance < 200)          //This is the obstacle treshhold. The car must detect obstacle closer than 200
   {                                                                  // and avoid them
     handleObstacle();
   }  
   car.setSpeed(magnitude);                                           //this makes sure the car is back to its forward direction if a turning happened.
   car.setAngle(0);                                                   //and this!
  } else if (!Serial.available())                                     //This happens if there is no input from user in the serial.
  {
    if (distance > 1 && distance < 200)   
    {
      handleObstacle();
    }
    car.setSpeed(magnitude);
    car.setAngle(0);
  }
}

void handleObstacle() 
{
  car.setSpeed(-magnitude);                //In here the car will go back in the opposite direction but with the same speed 
  car.setAngle(50);                        //In this line the car will turn while going backward to avoid obstacle
  delay(1000);                             //Here we give some time to the poor car to do previous actions 
}
