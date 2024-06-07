# CS61B-SE-Proj
This is the Project 3 (Software Engineering) of CS61B in UC Berkeley.

## Introduction
I used java to implement a 2D tile-based world exploration engine, which is a 2D world where users can interact with the objects in it. The world consists of rooms, hallways, doors, and a exit. You can move the avatar around in the world.

## Features

<img src="imgs\screen.png" width="750" height="360">

- **Ramdom World**: You can generate a world using a seed. Each seed, e.g., 123, 777, corresponds to a different world.
- **Heads Up Display**: You can move the mouse around, and a HUD(Heads Up Display) will be displayed showing which object you are pointing at.
- **Saving and Loading**: You can save the world in any time. When you load the game next time, the world is in exactly the same state as it was before the project was terminated.
- **Rename**: You view the name of the avatar by moving your mouse on it. You can change the name of the avatar in the menu window.
- **Others**:
    - You can turn on/off lights during the game.
    - You can turn enable/disable the line of sight during the game.
    - Each room is colored during world creation.


## How to run
Open byow folder in your IDE, select SDK which is Java 17 or higher. Also include the Library library-sp23 to the project. Navigate to byow/Core/Main, and run it, then a menu window will show up:

<img src="imgs\window.png" width="400" height="240">

- Type N, then enter a number, then type S to create a new world.
- Type L to load a previously saved game.
- Type S to change the name of avatar.
- During the game, type :Q to save and terminate the program.
- During the game, W: move up, S: move down, A: move left, D: move right.
- During the game, press l to enable/disable the line of sight.
- During the game, press o to turn on/off the light.
- If you reach the blue tile, you win the game!



