#include <Smartcar.h>

const unsigned long PRINT_INTERVAL = 3000;
const int TRIGGER_PIN           = 6; // D6
const int ECHO_PIN              = 7; // D7
const unsigned int MAX_DISTANCE = 1000;
unsigned long previousPrintout     = 0;

bool direction;


ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor(arduinoRuntime, smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);
SR04 front(arduinoRuntime, TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE);

SimpleCar car(control);

void setup()
{
   car.setSpeed(50);
   car.setAngle(0);
   direction = coinToss(); // guess which direction the car will turn, if bool is true turn right otherwise left
   
}

void loop()
{
   if (front.getDistance() > 5 && front.getDistance() < 200){
    car.setSpeed(35);
    while((front.getDistance() > 5 && front.getDistance() < 200)){ // will turn until free of obstacle given the conditions
      if(front.getDistance() < 100){ // turn back with the correct angle if it senses the obstacles it getting worse
        car.setSpeed(-35);
        delay(4000);
        car.setSpeed(35);
        car.setAngle(0);
      }
    if(direction){
      car.setAngle(90);
    }else{
      car.setAngle(-90);
    }
    }

    }else{
    car.setSpeed(50);
    car.setAngle(0);
    delay(35);
    direction = coinToss();
    
   }
}



bool coinToss(void) {
  int randomNumber;
  randomNumber = 1 + rand() % 2;
  if(randomNumber == 1){
    return true;
  }else {
    return false;
  }
}
