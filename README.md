# DIT112 - Mini Project - System Development (Group 10)

## Table of Contents
- [Demo Video](https://github.com/DIT112-V21/group-10/tree/readme#demo-video-must-watch)
- [What did we make?](https://github.com/DIT112-V21/group-10/tree/readme#what-did-we-make)
- [Why did we make this? What problem was solved?](https://github.com/DIT112-V21/group-10/tree/readme#why-did-we-make-this-what-problem-was-solved)
- [How did we make this? What kind of technology and resources have we used?](https://github.com/DIT112-V21/group-10/tree/readme#how-did-we-make-this-what-kind-of-technology-and-resources-have-we-used)
- [Game Tutorial for Users](https://github.com/DIT112-V21/group-10/tree/readme#game-tutorial-for-users)
- ["Get Started" Guide for Developers](https://github.com/DIT112-V21/group-10/tree/readme#get-started-guide-for-developers)
- [Architecture](https://github.com/DIT112-V21/group-10/tree/readme#architecture)
- [Development Team](https://github.com/DIT112-V21/group-10/tree/readme#development-team)

## Demo Video (must watch)

TODO: add video link

## What did we make?
- TANK - Total Annihilation Not Komplete
- We made an Android app with the possibility of detecting cars by looking at a camera feed
- This camera feed is received from the bot they control in SMCE
- This program will let users control a smart bot in SMCE with the objective of crashing into other SMCE bots, gaining points for every object crashed into

## Why did we make this? What problem was solved?
- Tank will assist users in hunting down objects and crashing into them.
- It can be used to cause mayhem and destruction (i.e. the dark side) and learn the findings and experience to avoid the same in real world (i.e. the noble side)
- We made this as a tool for anger management and control.

## How did we make this? What kind of technology and resources have we used?

### Front-end
First, we created UI prototype in Figma which included all the necessary screens for out features. We then recreated the screens in Android studio xml layout files. We then initialized UI elements in Java code and connected them to the backend so that MQTT messages were sent when the user was interacting with the UI elements. 

### Back-end
We used the Smartcar library to program an emulated version of the smartcar which connects to an Android app through the MQTT protocol. MQTT messages are used to publish and subscribe between the car and the Android app made with Android studio. 

We used OpenCV to process a dataset (positive and negative images of the car) and then ran a training process to teach a cascade classifier. This cascade was then used to detect the smartcar objects on the camera feed from the smartcar. 

### Technologies:
- Godot
- Java
- Machine learning
- C++
- Python (OpenCV object detection learning)

### Resources:
- SmartCar Library
- SMCE
- OpenCV
- GitHub
- Arduino IDE
- Android Studio
- Visual Studio Code
- Figma
- Google Drive
- Discord
- Zoom

## Game Tutorial for Users
Tutorial can be found **[here.](https://github.com/DIT112-V21/group-10/blob/readme/androidGeoBot/app/src/main/assets/tank_tutorial_white.pdf)**

## "Get Started" Guide for Developers
1. Download and install [SMCE](https://github.com/ItJustWorksTM/smce-gd/releases), and  [Android Studio](https://developer.android.com/studio/), using the following [setup instructions](https://github.com/ItJustWorksTM/smce-gd/wiki) for SMCE
2. Download/clone remote repository from GitHub
3. Go through following README.md to know how to prepare development environment for respective part
- [Android README](https://github.com/DIT112-V21/group-10/blob/master/androidGeoBot/README.md)
- [Arduino README](https://github.com/DIT112-V21/group-10/blob/master/arduino/README.md)
4. Open and compile smartcar.ino in Arduino IDE and SMCE
5. Open and compile othercars.ino Arduino IDE and SMCE
6. Open android folder in Android Studio, build and run it

## Software architecture
To design our software we used the Model View Controller pattern. The Model is the SMCE smartcar that contains all of the business logic. The UI (View) was created in Android studio using XML files. Finally, the Client class acts as a controller and allows to update the the state of the smartcar through the Android app. The commands from the Android app were sent using an MQTT broker. The app connects to localhost by default but it also allows the user to switch to a custom broker instead. 

We used the following sensors for this project:

| Sensor               | Usage                                                     | 
| -------------        |:---------------------------------------------------------:| 
| Ultrasonic sensor     | Obstacle avoidance in [othercars.ino](https://github.com/DIT112-V21/group-10/blob/master/arduino/othercars/othercars.ino)                      | 
| Infrared sensor      | To give user a score each time they crash into another car| 
| Camera               | To train our cascade classifier, and to stream from the car   |  

## Development Team
- [Hannah Shiels](https://github.com/hannahshiels)
- [Himank Meattle](https://github.com/HIMANKMEATTLE)
- [Irina Levkovets](https://github.com/Irina0904)
- [Lucas Nordgren](https://github.com/lucasnordic)
- [Maryam Esmaeili Darestani](https://github.com/Spidergirl93)
- [Michael Araya](https://github.com/mandemmike)
