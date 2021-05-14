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
int magnitude;                              //This we will need later

void setup()
{
    magnitude = rand()%100;
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
    car.setSpeed(magnitude);
    distanceHandler(0, 200, distance);

}

void handleObstacle()
{
    car.setSpeed(-magnitude);        //In here the car will go back in the opposite direction but with the same speed
    car.setAngle(30);                //In this line the car will turn while going backward to avoid obstacle
    delay(1000);                     //Here we give some time to the poor car to do previous actions
}


void distanceHandler(float lowerBound, float upperBound, float distance)
{
    if (distance > lowerBound && distance < upperBound)
    {
        handleObstacle();
    }
    car.setSpeed(magnitude);            //this makes sure the car is back to its forward direction if a turning happened.
    car.setAngle(0);                    //and this!
}

