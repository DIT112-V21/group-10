# Car Manual

To begin controlling the car you should have SMCE installed. Open the program, compile the smartcar sketch. Afterwards, compile and start the othercars sketch, running as many cars you would like to be able to crash into. Finally, you should start the smartcar sketch.

The smartcar sketch is what is used for allowing the player to control the car. 

The othercars sketch is used for automatic driving. The car will avoid obstacles to make it challenging for the player to crash into.

How to control the smartcar:

- You must run the android application, and be connected to a broker, follow instructions in the android folder for set up.
- If you want to use a custom broker:
  - You would have to edit the smartcar.ino file and change the line with "mqtt.begin(WiFi);"
  - Replace the line with mqtt.begin("broker host adress/ip", "port", WiFi);
- To move the car, you should move the inner circle of the joystick in whichever direction you would like to go.
- To change the current speed of the car, move the speed bar to your desired speed.
- To stop the car, simply touch the brake button.
