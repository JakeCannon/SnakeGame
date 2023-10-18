# SnakeGame
Snake Game made in Java for a Games Programming Assignment at University

# How to run the code:
The project was created using the Eclipse IDE and as such it can be opened within the Eclipse IDE and ran from within there. The Main files for my source code can be found inside of the src folder. The SnakeGame.Java is my written source code for the game which utilises the GameEngine.Java class that is also located within the src folder.

# Additional Details:
The GameEngine.Java has 2 minor additions to the colour definitions in the graphics functions:
Added 2 new colours for usage within the game:
grey – used to draw grid lines
transparentBlack – used to provide a semi-transparent background for the floating pause, game over and options menu’s
 
mFrame has the setResizable property set to false – so user cannot drag it larger.
 
mFrame’s location was set relative to null to move the window closer to the centre of the screen when opened.
 
# Resources:
•	All music found within this game was created by me in the Digital Audio Workstation software FL Studio 20: Producer Edition. All music audio was created using the built-in free plugin FLEX that provides preset-based synthesizer sounds.

•	More information on FL Studio can be found here:
https://www.image-line.com/fl-studio/

•	More information regarding the plugin I used can be found here:
https://www.image-line.com/fl-studio/plugins/flex

•	All sound effects used were sourced from the following website (made available under their own license which allows their usage in commercial and non-commercial projects for free):
https://mixkit.co/free-sound-effects/game/

•	The heart sprite graphic I used to represent my snake’s lives (provided with creative commons licenses CC-BY 4.0 and CC-BY 3.0) is available here:
https://opengameart.org/content/heart-pixel-art

•	No code was taken from any external sources.

# Game features (also visible in the comments at the top of the SnakeGame.Java source file):
•	Utilizes a world grid for play area

•	Player score tracker on UI

•	Multiple game states exist: "0: over", "1: playing", "2: paused", "3: options, "4: Main Menu", "5: Help"

•	Music and sound effects implemented

•	Ability to pause the game (freeze it)

•	Settings menu allowing toggling of music and sound effects (on/off) and game quit

•	Ability to restart the game once the player receives a "Game Over" screen

•	Score multiplier: spawns once snake at max size; grants double points for 10sec

•	Random spawning wall obstacles appear at any location on grid (not immediately adjacent to head) once player reaches 100 points. Every apple eaten from 100 points moves the wall to new position, direction and size.

•	Player can control speed that the snake moves as a toggle between slow, medium, fast in settings (this updates the speed only when a new game begins - not in current game if one playing)

•	Added start menu to game (so it doesn't just immediately play upon opening) with options: "play", "Options", "Help", "Quit".

•	Added Help screen to show the user how to control and play the game

•	Ability to navigate the main menu with the mouse as well as keyboard

•	Snake has 3 lives that will decrease by 1 if the player hits a wall or eats 3 red apples. if you run out of lives it's game over.

•	Size decreasing red apple; spawns after 100 points earned. Eat 3 of them: lose a life

•	UI displays number of lives, number of apples eaten, player score, whether an active multiplier is on, snake size and brief help text.

•	Countdown timer when starting a new game (visuals and audio)
