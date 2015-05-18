# FinalProjectile
Multiplayer Java Game for 16.35 Final Project

Final Projectile is a shooting game with both single-player and multi-player functionality that populates a display with either one or two user-controlled vehicles and a specified number of non-user vehicles. The user vehicles are controlled using keyboard input. The objective of the game is to shoot the non-user vehicles.

One shot causes the vehicles to follow the user vehicles, a second shot “kills” the vehicle and removes it from the display. User scores are displayed onscreen. The game ends once all non- user vehicles have been destroyed, or if 200 seconds have passed and there are still remaining non-user vehicles present.

![alt tag](http://web.mit.edu/syler/www/FinalProjectileSmall.png)

## Installation

1. Download a copy of the repository using `git clone https://github.com/1635-S15/FinalProjectile.git` (HTTPS) or `git clone git@github.com:1635-S15/FinalProjectile.git` (SSH)
2. Compile the source code in the Working folder using `javac *.java` 

## Usage

1. Run the game in single-player mode using `java FinalProjectile`
2. To enable multi-player mode, run `java FinalProjectile 2`
3. To set a custom number of non-user vehicles in the simulation, use two command line arguments `java FinalProjectile <#players> <#vehicles>`

## Documentation

* Detailed [System Description](FinalProjectile%20System%20Description.pdf)
* [Software Requirements Specification](Requirements/FinalProjectile%20SRS%202.1.pdf) version 2.1
