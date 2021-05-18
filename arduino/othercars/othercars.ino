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
int magnitude;
int randomAngle;

void setup()
{
    magnitude = speedMaker();
    randomAngle = angleMaker();
    Serial.begin(9600);
    Serial.setTimeout(200);
}

void loop()
{
    autoCar();
    delay(35);
}

void autoCar()
{
    float distance = front.getDistance();
    car.setSpeed(magnitude);
    distanceHandler(0, 200, distance);

}

/**
 * This method will handle the speed and the angle of the car while detecting an obstacle
 */
void handleObstacle()
{
    car.setSpeed(-magnitude);        //In here the car will go back in the opposite direction but with the same speed
    car.setAngle(randomAngle);                //In this line the car will turn while going backward to avoid obstacle
    distanceHandler( 0, 200, front.getDistance());

}

/**
 * In this method, the upper and lower bound of the obstacle detection are being specified and the obstacles
 * will be handled if detected.
 * @param lowerBound
 * @param upperBound
 * @param distance
 */
void distanceHandler(float lowerBound, float upperBound, float distance)
{
    if (distance > lowerBound && distance < upperBound)
    {
        handleObstacle();
    }
    car.setSpeed(magnitude);            //this makes sure the car is back to its forward direction if a turning happened.
    car.setAngle(0);                    //and this!
}

/**
 * This method generates a random speed for the car and makes sure that it's not too high or low
 * @return
 */
int speedMaker()
{
    int speed = rand()%90;
    if (speed < 60)
    {
        speed = speedMaker();
    }
    return speed;
}

/**
 * This method generates random positive or negative angles for the car to turn and makes sure that the angle value
 * is in a proper range
 * @return
 */
int angleMaker()
{
    int angle = (rand() - rand())%90;
    if (-30 < angle && angle < 30)
    {
        angle = angleMaker();
    }
    return angle;
}

