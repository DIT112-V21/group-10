#include <Smartcar.h>

// Car and attachments
ArduinoRuntime arduinoRuntime;
BrushedMotor leftMotor(arduinoRuntime, smartcarlib::pins::v2::leftMotorPins);
BrushedMotor rightMotor(arduinoRuntime, smartcarlib::pins::v2::rightMotorPins);
DifferentialControl control(leftMotor, rightMotor);

GY50 gyroscope(arduinoRuntime, 37);

const auto pulsesPerMeter = 600;

DirectionlessOdometer leftOdometer{
    arduinoRuntime,
    smartcarlib::pins::v2::leftOdometerPin,
    []() { leftOdometer.update(); },
    pulsesPerMeter};
DirectionlessOdometer rightOdometer{
    arduinoRuntime,
    smartcarlib::pins::v2::rightOdometerPin,
    []() { rightOdometer.update(); },
    pulsesPerMeter};

SmartCar smartCar(arduinoRuntime, control, gyroscope, leftOdometer, rightOdometer);
// SimpleCar car(control);

// Sensors on car
const int TRIGGER_PIN = 6; // D6
const int ECHO_PIN = 7;    // D7
const unsigned int MAX_DISTANCE = 300;
SR04 front(arduinoRuntime, TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE);

const unsigned short FRONT_IR_PIN = 0;
const unsigned short LEFT_IR_PIN = 1;
const unsigned short RIGHT_IR_PIN = 2;
const unsigned short BACK_IR_PIN = 3;
GP2Y0A21 infraredSensor0(arduinoRuntime, FRONT_IR_PIN);
GP2Y0A21 infraredSensor1(arduinoRuntime, LEFT_IR_PIN);
GP2Y0A21 infraredSensor2(arduinoRuntime, RIGHT_IR_PIN);
GP2Y0A21 infraredSensor3(arduinoRuntime, BACK_IR_PIN);

// variables to measure time.
const long infraInterval = 1000; // 1 second. Used instead of delay()
unsigned long infraPreviousMillis = 0;
unsigned long infraCurrentMillis = 0;
boolean detection = false;

const long debugMessageInterval = 750; // Used instead of delay()
unsigned long debugPreviousMillis = 0;
unsigned long debugCurrentMillis = 0;

// variables
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
    // start time tracker(s)
    debugCurrentMillis = millis();
    infraCurrentMillis = millis();

    autoCar();
    delay(35);

    serialMsg(); // For debugging
}

void autoCar()
{
    float distance = front.getDistance();
    smartCar.setSpeed(magnitude);
    distanceHandler(0, 200, distance);
}

/**
 * This method will handle the speed and the angle of the car while detecting an obstacle
 */
void handleObstacle() // handle Ultrasonic sensor detection
{
    smartCar.setSpeed(-magnitude);  //In here the car will go back in the opposite direction but with the same speed
    smartCar.setAngle(randomAngle); //In this line the car will turn while going backward to avoid obstacle
    distanceHandler(0, 200, front.getDistance());
}

// Handle Front and Back infrared sensor obstacle detection
void handleObstacleInfrared(String detectionDirection) // handle infrared sensor detection
{
    infraCurrentMillis = millis();

    while ((infraCurrentMillis - infraPreviousMillis) <= infraInterval * 2)
    {
        infraCurrentMillis = millis();

        if (detectionDirection.equals("BACK")) // If the back sensor has detected something
        {
            smartCar.setSpeed(magnitude);
            smartCar.setAngle(randomAngle);
        }
        else // if the FRONT sensor has detected something
        {
            smartCar.setSpeed(-magnitude);
            smartCar.setAngle(randomAngle);
        }
    }
    randomAngle = angleMaker(); // new angle for car, so it doesnt turn the same way always.

    infraPreviousMillis = infraCurrentMillis;
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
    int f = infraredSensor0.getDistance();
    // int l = infraredSensor1.getDistance(); // TODO, Use left infrared
    // int r = infraredSensor2.getDistance(); // TODO, Use Right infrared
    int b = infraredSensor3.getDistance();

    if (distance > lowerBound && distance < upperBound)
    {
        handleObstacle();
    }
    else if ((f > 1 && f < 40)) // If an infrared sensor detects anything in this range, handle the obstacle as well.
    {
        handleObstacleInfrared("FRONT");
    }
    /** // TODO Use Left and Right infrared sensors
    else if ((l > 1 && l < 40))
    {
        handleObstacle("LEFT");
    }
    else if ((r > 1 && r < 40))
    {
        handleObstacle("RIGHT");
    }*/
    else if ((b > 1 && b < 40))
    {
        handleObstacleInfrared("BACK");
    }

    smartCar.setSpeed(magnitude); //this makes sure the car is back to its forward direction if a turning happened.
    smartCar.setAngle(0);         //and this!
}

/**
 * This method generates a random speed for the car and makes sure that it's not too high or low
 * @return
 */
int speedMaker()
{
    int speed = rand() % 90;
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
    int angle = (rand() - rand()) % 90;
    if (-30 < angle && angle < 30)
    {
        angle = angleMaker();
    }

    return angle;
}

/**
 * For debugging in SMCE 
 */
void serialMsg()
{
    if ((debugCurrentMillis - debugPreviousMillis) >= debugMessageInterval)
    {
        Serial.print("Speed: ");
        Serial.println(smartCar.getSpeed());
        Serial.print("Magnitude: ");
        Serial.println(magnitude);

        int f = infraredSensor0.getDistance();
        int l = infraredSensor1.getDistance();
        int r = infraredSensor2.getDistance();
        int b = infraredSensor3.getDistance();
        Serial.print("infraFront: ");
        Serial.println(f);
        Serial.print("infraLeft: ");
        Serial.println(l);
        Serial.print("infraRight: ");
        Serial.println(r);
        Serial.print("infraBack: ");
        Serial.println(b);

        Serial.println("");

        debugPreviousMillis = debugCurrentMillis;
    }
}