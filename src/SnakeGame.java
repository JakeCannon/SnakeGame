 * Author: Jake Cannon
 * Program details: Snake game with additional features (see Program Features).
 *
 * Program Features:
 * - Basics of snake game functional
 * - Utilizes a world grid for play area
 * - Apple randomly spawns (not on where snake already is)
 * - Player score tracker on UI
 * - Size of snake grows upon eating apple (utilizes array list)
 * - Multiple game states exist: "0: over", "1: playing", "2: paused", "3: options, "4: Main Menu", "5: Help"
 * - Game over if snake collides with outer bounds or its own tail
 * - Music and sound effects implemented
 * - Ability to pause the game (freeze it)
 * - Settings menu allowing toggling of music and sound effects (on/off) and game quit
 * - Safety systems in place to prevent snake movement 180 and instantly failing
 * - Ability to restart the game once the player receives a "Game Over" screen
 * - Score multiplier: spawns once snake at max size; grants double points for 10sec
 * - Random spawning wall obstacles appear at any location on grid (not immediately adjacent to head)
 * once player reaches 100 points. Every apple eaten from 100 points moves the wall to new position,
 * direction and size.
 * - Player can control speed that the snake moves as a toggle between slow, medium, fast in settings
 * (this updates the speed only when a new game begins - not in current game if one playing)
 * - Added start menu to game (so it doesn't just immediately play upon opening) 
 * with options: "play", "Options", "Help", "Quit".
 * - Added Help screen to show the user how to control and play the game
 * - Ability to navigate the main menu with the mouse as well as keyboard
 * - Snake has 3 lives that will decrease by 1 if the player hits a wall or eats 3 red apples.
 * if you run out of lives it's game over.
 * - Size decreasing red apple; spawns after 100 points earn't. Eat 3 of them: lose a life
 * - UI displays number of lives, number of apples eaten, player score, whether an active multiplier is on,
 * snake size and brief help text.
 *
 * TODO:
 * - (DONE) Comment all code where necessary
 * - (DONE) Implement score multiplier pickup (update UI to suit)
 * - (DONE) Implement snake speed change
 * - (DONE) Implement random spawning obstacles (walls)
 * - (DONE) Add initial start screen instead of immediately starting the game
 * - (DONE) see about implementing lives
 * - (DONE) a size decreasing pickup
 * - (DONE) Polish code and tidy it up
 *
 * KNOWN ISSUES:
 * - NONE (that I am aware of)
 */

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class SnakeGame extends GameEngine {
	
	// VARIABLES
	
	// Arraylist to hold snake body elements' coords
	ArrayList<Integer> snakeArrayList = new ArrayList<Integer>();
	
	// Game window dimensions
	int width = 600;
	int height = 600;
	int tileSize = 30; // specific size of tiles used in grid (do not change)
	int scoreboardHeight = tileSize * 3; // How many tiles high you want the score area to be
	int snakeSize;
	int playerScore; 
	// Store the state of the game (0 = over, 1 = playing, 2 = paused, 3 = options, 4 = Main Menu, 5 = Help)
    int gameState = 4;
    int snakeSpeed = 1; // toggles the speed of the game (0 = slow, 1 = normal, 2 = fast);
    int snakeLives;
	
	// Variables of snake head position
	int posX;
	int posY;
	// Variables of green apple position
	int appleX;
	int appleY;
	// Variables of multiplier position
    int multiplierX;
    int multiplierY;
    int multiplierValue = 2; // sets the amount of multiplier applied to playerScore
    // Variables of wall obstacle position
    int wallX;
    int wallY;
    int wallLength;
    int wallHeight;
    // Variables of size decreaser pickup (red apple) position
    int decreaserX;
    int decreaserY;
    // Options menu variables
    int optionSelect = 1; // keeps track of current selection
    int previousState = 1; // used to know where the user came from before opening options
    // Main menu selection variable
    int menuSelect = 1;
    // Variables to count the number of each apple eaten
    int redEaten;
    int totalRedEaten;
    int greenEaten;
    
    // Timer related variables
    long multStartTime;
    long multCurrentTime;
    long multTimer = 10000; // 10 second timer for multiplier
    long pauseTime;
    long pauseStartTime;
    long startCountdown = 2000; // 2 second countdown for game start
    long gameTime;
    long gameStartTime;
	
	// Directional booleans for snake movement
	boolean goRight = true;
	boolean goLeft = false; 
	boolean goUp = false; 
	boolean goDown = false;
	// Booleans for identifying the presence of each gameplay element
	boolean applePresent = true;
	boolean multiplierPresent;
	boolean wallPresent;
	boolean decreaserPresent;
	// Flag used to prevent changing of directions until snake has moved once in its current direction
    boolean directionChanging;
    // Menu option booleans
    boolean musicOn = true;
    boolean sfxOn = true;
    // Booleans used to set an active modifier on the game
    boolean multiplierActive;
    // Specific use case boolean for distinguishing navigation to options menu from main menu
    boolean comeFromMenu = false;
    // Specific use boolean for knowing if the user if clicking on a button or not in main menu
    boolean onButton;
    
    // Images used within the game
    Image apple = loadImage("apple.png");
    Image heart = loadImage("heart.png");
    Image redApple = loadImage("redApple.png");
    Image body = loadImage("dot.png");
    Image head = loadImage("head.png");
    
    // Audio files
    // SFX
    AudioClip appleEat = loadAudio("appleEatSFX.wav");
    AudioClip lose = loadAudio("loseSFX.wav");
    AudioClip spawn = loadAudio("spawnSFX.wav");
    AudioClip select = loadAudio("select2SFX.wav");
    AudioClip confirm = loadAudio("hitSFX.wav");
    AudioClip multiplier = loadAudio("powerUpSFX.wav");
    AudioClip hurt = loadAudio("hurt.wav");
    AudioClip countdown = loadAudio("snakeGameCountdown.wav");
    AudioClip creatureHurt = loadAudio("creatureHurt.wav");
    // Music - load these where their loops are started (prevents looping error)
    AudioClip music;
    AudioClip pauseMusic;
    AudioClip multiplierMusic;
    AudioClip menuMusic;
    
    
    // Create snake game instance
	public static void main(String[] args) {
		createGame(new SnakeGame(), 60);
	}
	
	// Initialize game starting state - this is recalled when game is restarted
	public void init() {
		// Play main menu theme for first time program startup
		if (gameState == 4) {
			menuMusic = loadAudio("snakeGameMenuTheme.wav");
			startAudioLoop(menuMusic, -4);
		}
		// Set the game's speed
		// Slow
		if (snakeSpeed == 0) {
			timer.setFramerate(45); // lower framerate
		}
		// Medium
		if (snakeSpeed == 1) {
			timer.setFramerate(60); // default framerate
		}
		// Fast
		if (snakeSpeed == 2) {
			timer.setFramerate(90); // higher framerate
		}
		setWindowSize(width, height);
		snakeArrayList.clear();
		// Initialize snake's starting head position
		posX = 315;
		posY = 315;
		snakeSize = 3; // start of the size of the snake as 3 (includes head)
		// Add head coords to snake array list for all starting body parts
		// (makes snake spawn out of a singular point based on head position)
		for (int i = 0; i < snakeSize*2; i+=2) {
			snakeArrayList.add(posX);
			snakeArrayList.add(posY);
		}
		getAppleCoords(); // place the apple
		playerScore = 0; // reset player score
		// Reset boolean flags
		multiplierActive = false;
		multiplierPresent = false;
		wallPresent = false;
		decreaserPresent = false;
		// Reset appropriate variables
		snakeLives = 3;
		redEaten = 0;
		totalRedEaten = 0;
		greenEaten = 0;
	}

	@Override
	public void update(double dt) {
		// Game updates for "playing" state
		if (gameState == 1) {
			gameTime = getTime();
			gameTime = gameTime - gameStartTime;
			if (gameTime > startCountdown) {
				// Update snake position based on current direction
				if (goRight) {
					posX += tileSize; // move along by one tile
				}
				if (goLeft) {
					posX -= tileSize; // move back by one tile
				}
				if (goDown) {
					posY += tileSize; // move along by one tile
				}
				if (goUp) {
					posY -= tileSize; // move back by one tile
				}
				directionChanging = false; // update direction change flag
				
				// Add new head location to arraylist
				snakeArrayList.add(posX);
				snakeArrayList.add(posY);
				
				// Remove old head data if arraylist contains more than needed for snake size
				// (the java arraylist.remove method handles the shifting of array elements once deleted)
				if (snakeArrayList.size() > snakeSize*2) {
					snakeArrayList.remove(0);
					snakeArrayList.remove(0); // remove first element twice to get rid of posX and posY
				}
				
				// If there is no apple on the grid (the snake ate it) update coords for new apple
				if (!applePresent) {
					getAppleCoords();
					applePresent = true; 
					// Play audio file for apple spawn sfx
					sleep(30); // 30 ms delay to separate sound from apple eaten sfx
					if (sfxOn) {
						playAudio(spawn);
					}
				}
				
				// If the snake is at max length update multiplier location
				if (snakeSize == 20) {
					// Check that there is not already one on the grid or one active
					if (!multiplierPresent && !multiplierActive) {
						getMultiplierCoords(); // spawn one
						multiplierPresent = true;
					}
				}
				
				// Remove multiplier from grid if the snake size drops below 20 (eats red apple)
				if (snakeSize < 20) {
					multiplierPresent = false;
				}
				
				// Once player score reaches 50 start spawning obstacles
				if (playerScore >= 50) {
					if (!wallPresent) {
						getWallCoords();
						wallPresent = true;
					}
				}
				
				// Once player score reaches 100 update size decreaser location (red apple)
				if (playerScore >= 100) {
					if (!decreaserPresent) {
						getDecreaserCoords();
						decreaserPresent = true;
					}
				}
				
				// Handle collision of snake and apple
				if (posX == appleX && posY == appleY) {
					// Ensure snake doesn't go over 20
					if (snakeSize < 20) {
						snakeSize++; // increase snake size
					}
					applePresent = false; // remove apple (triggers !applePresent)
					// Increment player score
					if (multiplierActive) {
						playerScore += 5*multiplierValue;
					}
					else {
						playerScore += 5;
					}
					if (sfxOn) {
						playAudio(appleEat);
					}
					if (wallPresent) {
						getWallCoords(); // move wall on each apple ate
					}
					// move the decreaser pickup (red apple) if there is one
					if (playerScore >= 100) {
						decreaserPresent = false;
					}
					greenEaten++; // increment how many apples the user has eaten
				}
				
				// Handle collision of snake and outer walls
				if (posX > width || posX < 0 || posY > height || posY < scoreboardHeight) {
					gameState = 0; // set game state to over
					if (musicOn) {
						if (multiplierActive) {
							stopAudioLoop(multiplierMusic);
						}
						if (!multiplierActive) {
							stopAudioLoop(music);
						}
					}
					if (sfxOn) {
						playAudio(lose);
					}
				}
				
				// Handle collision of snake and its own tail
				for (int x = 0; x < snakeArrayList.size()-3; x+=2) {
					// If current head position equals one of the arraylist's tail coords
					if (posX == snakeArrayList.get(x)) {
						if (posY == snakeArrayList.get(x+1)) {
							gameState = 0; // set game state to over
							if (musicOn) {
								if (multiplierActive) {
									stopAudioLoop(multiplierMusic);
								}
								if (!multiplierActive) {
									stopAudioLoop(music);
								}
							}
							if (sfxOn) {
								playAudio(lose);
							}
						}
					}
				}
				
				// Handle collision of snake and multiplier
				if (multiplierPresent) {
					if (posX == multiplierX && posY == multiplierY) {
						multStartTime = getTime(); // get time of start
						multiplierPresent = false; // remove multiplier from grid
						multiplierActive = true;
						if (sfxOn) {
							playAudio(multiplier);
						}
						if (musicOn) {
							stopAudioLoop(music);
							multiplierMusic = loadAudio("snakeGameMultiplierTheme.wav");
							startAudioLoop(multiplierMusic, -4);
						}
					}
				}
				
				// Handle collision of snake and size decreaser (red apple)
				if (decreaserPresent) {
					if (posX == decreaserX && posY == decreaserY) {
						decreaserPresent = false;
						snakeSize--; // reduce the snake's size
						// Remove front snake elements from arraylist
						snakeArrayList.remove(0);
						snakeArrayList.remove(0);
						redEaten++;
						totalRedEaten++;
						// Disable any active multiplier (if there is one active)
						if (multiplierActive) {
							multiplierActive = false;
							if (musicOn) {
								stopAudioLoop(multiplierMusic);
								music = loadAudio("snakeGameTheme.wav");
								startAudioLoop(music);
							}
						}
						if (sfxOn) {
							if (redEaten % 3 == 0) {
								playAudio(creatureHurt, -4);
								playAudio(hurt);
							}
							else {
								playAudio(creatureHurt, -4);
							}
						}
						applePresent = false; // move green apple
						getWallCoords(); // move wall
					}
				}
				
				// Handle collision of snake and wall
				if (wallPresent) {
					// If direction of wall is vertical
					if (wallLength == tileSize) {
						// Check each wall section on grid for collision
						for (int i = 0; i < wallHeight; i+= tileSize) {
							if (posX == wallX && posY == wallY+(i)) {
								snakeLives--; // reduce lives by one
								if (sfxOn) {
									playAudio(hurt);
								}
							}
						}
					}
					// If direction of wall is horizontal
					else if (wallHeight == tileSize) {
						// Check each wall section on grid for collision
						for (int i = 0; i < wallLength; i+= tileSize) {
							if (posX == wallX+(i) && posY == wallY) {
								snakeLives--; // reduce lives by one
								if (sfxOn) {
									playAudio(hurt);
								}
							}
						}
					}
				}
				
				// Check if multiplier time is up (if one is active)
				if (multiplierActive) {
					multCurrentTime = getTime();
					// if the difference between the start time and time now = multTimer
					if ((multCurrentTime - multStartTime) - pauseTime >= multTimer) {
						multiplierActive = false; // disable multiplier
						if (musicOn) {
							stopAudioLoop(multiplierMusic);
							music = loadAudio("snakeGameTheme.wav");
							startAudioLoop(music, -4);
						}
					}
				}
				
				// Check if too many red apples have been eaten
				if (redEaten == 3) {
					snakeLives--; // lose a life every 3 red apples
					redEaten = 0; // reset counter for next 3
				}
				
				// Check if snake has enough lives to continue playing
				if (snakeLives < 1) {
					gameState = 0;
					if (sfxOn) {
						playAudio(lose);
					}
					if (musicOn) {
						if (multiplierActive) {
							stopAudioLoop(multiplierMusic);
						}
						if (!multiplierActive) {
							stopAudioLoop(music);
						}
					}
				}
			}
		}
		// If game paused or in settings
		if (gameState == 2 || gameState == 3) {
			// Record the time that they entered the pause menu
			pauseTime = getTime();
			// Work out how long they have been in the pause menu for (used to preserve multiplier timer)
			pauseTime = pauseTime - pauseStartTime;
		}
	}

	// Function to perform the creation of all main game content (for gameState 1)
	public void drawGameplayContent() {
		changeBackgroundColor(black);
		clearBackground(width,height);
		
		// Draw lines along the game grid
		changeColor(grey); // color for grid lines
		// Loop to draw all horizontal lines
		for (int y = 0; y < width; y+=tileSize) {
			drawLine(0, y, width, y);
		}
		// Loop to draw all vertical lines
		for (int x = 0; x < height; x+=tileSize) {
			drawLine(x, 0, x, height);
		}
		
		// Generate apple on the frame at apples current coords (use apple image)
		drawImage(apple, appleX-(tileSize/2.4), appleY-(tileSize/2.4), tileSize-5, tileSize-5); 
		
		// Generate red apple (size decreaser) on frame
		if (decreaserPresent) {
			drawImage(redApple, decreaserX-(tileSize/2.4), decreaserY-(tileSize/2.4), tileSize-5, tileSize-5); 
		}
		
		// Draw snake head
		changeColor(red);
		drawSolidCircle(posX, posY, tileSize/2);
		//drawImage(head, posX-(tileSize/2.4), posY-(tileSize/2.4), tileSize-5, tileSize-5);
		
		// Draw snake body (change color if multiplier active)
		if (multiplierActive) {
			changeColor(white);
		}
		else {
			changeColor(green);
		}
		// Traverse the arraylist backwards (because program adds to tail and deletes from front)
		for (int i = snakeSize*2; i > 2; i-=2) {
			drawSolidCircle(snakeArrayList.get(i-4), snakeArrayList.get(i-3), tileSize/2);
			//drawImage(body, snakeArrayList.get(i-4)-(tileSize/2.4), snakeArrayList.get(i-3)-(tileSize/2.4), tileSize-5, tileSize-5);
		}
		
		// Generate score multiplier on the frame
		if (multiplierPresent) {
			changeColor(white);
			drawSolidCircle(multiplierX, multiplierY, tileSize/2.4);
		}
		
		// Generate wall obstacle on the frame
		if (wallPresent) {
			changeColor(black);
			drawSolidRectangle(wallX-(tileSize/2), wallY-(tileSize/2), wallLength, wallHeight);
			changeColor(red);
			drawRectangle(wallX-(tileSize/2)+2.5, wallY-(tileSize/2)+2.5, wallLength-5, wallHeight-5, 5);
		}
		
		// Draw player score section
		changeColor(black);
		drawSolidRectangle(0, 0, width, scoreboardHeight);
		changeColor(blue);
		drawRectangle(1, 1.5, width-2.5, scoreboardHeight-1.5, 2);
		
		// Draw player's score to score section of window
		changeColor(white);
		// Dynamic positioning based on single, double, triple digits (keep it centered)
		if (playerScore < 10) {
			drawText(230, scoreboardHeight/1.6, "Score: " + playerScore);
		}
		if (playerScore >= 10 && playerScore < 100) {
			drawText(215, scoreboardHeight/1.6, "Score: " + playerScore);
		}
		if (playerScore >= 100) {
			drawText(210, scoreboardHeight/1.6, "Score: " + playerScore);
		}
		
		// Draw snake size to score section window
		drawText(450, scoreboardHeight/4, "Snake Size: " + snakeSize, "Arial", 20);
		
		// Draw help text
		drawText(5, scoreboardHeight/5, "Press ESC for Options", "Arial", 12);
		drawText(5, scoreboardHeight/2.5, "Press SPACE to pause", "Arial", 12);
		
		// Draw current score multiplier to score section
		if (multiplierActive) {
			drawText(200, scoreboardHeight/1.1, "Multiplier Active: Double Points!", "Arial", 15);
		}
		if (!multiplierActive) {
			drawText(255, scoreboardHeight/1.1, "Multiplier: None", "Arial", 15);
		}
		
		// Draw snake lives to score section
		drawText(5, scoreboardHeight/1.2, "Lives:", "Arial", 15);
		if (snakeLives == 1) {
			drawImage(heart, 55, scoreboardHeight/1.5, 20, 20);
		}
		if (snakeLives == 2) {
			drawImage(heart, 55, scoreboardHeight/1.5, 20, 20);
			drawImage(heart, 85, scoreboardHeight/1.5, 20, 20);
		}
		if (snakeLives == 3) {
			drawImage(heart, 55, scoreboardHeight/1.5, 20, 20);
			drawImage(heart, 85, scoreboardHeight/1.5, 20, 20);
			drawImage(heart, 115, scoreboardHeight/1.5, 20, 20);
		}
		
		// Draw how many apples the player has eaten this match
		drawText(470, scoreboardHeight/1.7, "Apples Eaten:", "Arial", 14);
		drawImage(apple, 450, scoreboardHeight/1.5, 20, 20);
		drawText(480, scoreboardHeight/1.15, "x " + greenEaten, "Arial", 20);
		drawImage(redApple, 520, scoreboardHeight/1.5, 20, 20);
		drawText(550, scoreboardHeight/1.15, "x " + totalRedEaten, "Arial", 20);
	}
	
	@Override
	public void paintComponent() {
		// Drawing content for "Playing" state
		if (gameState == 1) {
			drawGameplayContent(); // draw all content from gameplay function
			// Display game start countdown if within first 2 seconds of playing
			if (gameTime < startCountdown) {
				changeColor(transparentBlack);
				drawSolidRectangle(275, 270, 65, 90);
				if (startCountdown - gameTime > 1334) {
					changeColor(white);
					drawBoldText(280, 350, "3", "Arial", 100);
				}
				if (startCountdown - gameTime > 668 && startCountdown - gameTime < 1334) {
					changeColor(white);
					drawBoldText(280, 350, "2", "Arial", 100);
				}
				if (startCountdown - gameTime > 0 && startCountdown - gameTime < 668) {
					changeColor(white);
					drawBoldText(280, 350, "1", "Arial", 100);
				}
			}
		}
		
		// Game paused state
		if (gameState == 2) {
			drawGameplayContent(); // ensure game is visible in background
			// Display pause message
			changeColor(transparentBlack);
			drawSolidRectangle(147, 287, 340, 50);
			changeColor(white); 
			drawBoldText(150, 330, "Game Paused", "Arial", 50);
		}
		
		// Game over state
		if (gameState == 0) {
			drawGameplayContent(); // ensure game is visible in background
			changeColor(transparentBlack);
			drawSolidRectangle(157, 287, 290, 80);
			changeColor(white); 
			drawBoldText(160, 330, "Game Over!", "Arial", 50);
			drawText(180, 360, "Press space to play again", "Arial", 20);
		}
		
		// Game options state
		if (gameState == 3) {
			if (!comeFromMenu) {
				drawGameplayContent(); // ensure game is visible in background
			}
			changeColor(transparentBlack);
			drawSolidRectangle(100, 110, 430, 400);
			changeColor(white); 
			drawBoldText(200, 160, "Options:", "Arial", 50);
			// Music highlighted
			if (optionSelect == 1) {
				changeColor(blue);
			}
			else {
				changeColor(white);
			}
			if (musicOn) {
				drawText(120, 260, "Music: On", "Arial", 40);
			}
			else {
				drawText(120, 260, "Music: Off", "Arial", 40);
			}
			
			// Sound effects highlighted
			if (optionSelect == 2) {
				changeColor(blue);
			}
			else {
				changeColor(white);
			}
			if (sfxOn) {
				drawText(120, 320, "Sound Effects: On", "Arial", 40);
			}
			else {
				drawText(120, 320, "Sound Effects: Off", "Arial", 40);
			}
			
			// Snake speed highlighted
			if (optionSelect == 3) {
				changeColor(blue);
			}
			else {
				changeColor(white);
			}
			if (snakeSpeed == 0) {
				drawText(120, 380, "Snake Speed: Slow", "Arial", 40);
			}
			if (snakeSpeed == 1) {
				drawText(120, 380, "Snake Speed: Medium", "Arial", 40);
			}
			if (snakeSpeed == 2) {
				drawText(120, 380, "Snake Speed: Fast", "Arial", 40);
			}
			
			// Return highlighted
			if (optionSelect == 4) {
				changeColor(blue);
			}
			else {
				changeColor(white);
			}
			drawText(120, 440, "Return to Menu", "Arial", 40);
			changeColor(white);
			drawText(120, 480, "Use the arrow keys to navigate and ENTER to select", "Arial", 15);
		}
		
		// Main Menu
		if (gameState == 4) {
			changeColor(black);
			drawSolidRectangle(0, 0, width, height);
			changeColor(white);
			drawBoldText(150, 60, "Snake Game", "Arial", 50);
			// Play highlighted
			if (menuSelect == 1) {
				changeColor(blue);
			}
			else {
				changeColor(white); 
			} 
			drawText(260, 180, "Play", "Arial", 40);
			drawRectangle(250, 140, 100, 55, 2);
			// Options highlighted
			if (menuSelect == 2) {
				changeColor(blue);
			}
			else {
				changeColor(white); 
			} 
			drawText(230, 270, "Options", "Arial", 40);
			drawRectangle(220, 230, 160, 55, 2);
			// Help highlighted
			if (menuSelect == 3) {
				changeColor(blue);
			}
			else {
				changeColor(white); 
			} 
			drawText(260, 360, "Help", "Arial", 40);
			drawRectangle(250, 320, 100, 55, 2);
			// Quit highlighted
			if (menuSelect == 4) {
				changeColor(blue);
			}
			else {
				changeColor(white); 
			} 
			drawText(260, 450, "Quit", "Arial", 40);
			drawRectangle(250, 410, 100, 55, 2);
		}
		
		// Help screen text
		if (gameState == 5) {
			changeColor(black);
			drawSolidRectangle(0, 100, width, height);
			changeColor(white);
			drawText(20, 120, "Controls:", "Arial", 40);
			drawText(20, 160, "Use the arrow keys to control the direction of the snake.", "Arial", 20);
			drawText(20, 200, "Press SPACEBAR to pause an ongoing game.", "Arial", 20);
			drawText(20, 260, "How to play:", "Arial", 40);
			drawText(20, 300, "Eat the green apples to grow the snake's size.", "Arial", 20);
			drawImage(apple, 450, 280, tileSize-5, tileSize-5); 
			drawText(20, 320, "(Each apple is worth 5 points)", "Arial", 15);
			drawText(20, 360, "Eat the multiplier for 10 seconds of double points.", "Arial", 20);
			drawSolidCircle(500, 360, tileSize/2.4);
			drawText(20, 380, "(Each apple eaten while a multiplier is active is worth 10 points)", "Arial", 15);
			drawText(20, 420, "Avoid the obstacles that spawn after scoring 50 points.", "Arial", 20);
			changeColor(black);
			drawSolidRectangle(450, 430, 120, 30);
			changeColor(red);
			drawRectangle(452.5, 432.5, 115, 25, 5);
			changeColor(white);
			drawText(20, 440, "(You can pass through these, but you will lose a life if you do)", "Arial", 15);
			drawText(20, 480, "Take note of your lives left in the scoreboard.", "Arial", 20);
			drawText(20, 500, "(run out of lives and its game over!)", "Arial", 15);
			drawImage(heart, 450, 470, 30, 30);
			drawText(20, 540, "Avoid the red apples; they decrease the snake's size.", "Arial", 20);
			drawText(20, 560, "(Every 3 red apples eaten: you lose a life)", "Arial", 15);
			drawImage(redApple, 510, 520, tileSize-5, tileSize-5);
			drawBoldText(170, 590, "Press ESC return to Menu", "Arial", 20);
		}
	}
	
	// Handle the User Input
	public void keyPressed(KeyEvent e) {
		// Key events when on Options screen
		if (gameState == 3) {
			// If down arrow key is pressed
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				// Make sure option selection is one of the four
				if (optionSelect < 4) {
					optionSelect++; // move onto next option
					if (sfxOn) {
						playAudio(select);
					}
				}
			}
			// If up arrow key is pressed
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				// Make sure option selection is one of the four
				if (optionSelect > 1) {
					optionSelect--; // move back to previous option
					if (sfxOn) {
						playAudio(select);
					}
				}
			}
			// If enter key is pressed - perform action that is selected
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (sfxOn) {
					playAudio(confirm);
				}
				// Toggle music
				if (optionSelect == 1) {
					if (musicOn) {
						musicOn = false;
						stopAudioLoop(pauseMusic);
					}
					else {
						musicOn = true;
						pauseMusic = loadAudio("snakeGamePauseTheme.wav"); // reload file
						startAudioLoop(pauseMusic);
					}
					
				}
				// Toggle sfx
				if (optionSelect == 2) {
					if (sfxOn) {
						sfxOn = false;
					}
					else {
						sfxOn = true;
					}
				}
				// Toggle snake speed
				if (optionSelect == 3) {
					if (snakeSpeed != 2) {
						snakeSpeed++;
					}
					else {
						snakeSpeed = 0;
					}
				}
				// Go to main menu
				if (optionSelect == 4) {
					gameState = 4;
					if (musicOn) {
						stopAudioLoop(pauseMusic);
						menuMusic = loadAudio("snakeGameMenuTheme.wav");
						startAudioLoop(menuMusic, -4);
					}
				}
			}
		}
		// User input for main menu
		else if (gameState == 4) {
			// If down arrow key is pressed
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				// Make sure option selection is one of the four
				if (menuSelect < 4) {
					menuSelect++; // move onto next option
					if (sfxOn) {
						playAudio(select);
					}
				}
			}
			// If up arrow key is pressed
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				// Make sure option selection is one of the four
				if (menuSelect > 1) {
					menuSelect--; // move back to previous option
					if (sfxOn) {
						playAudio(select);
					}
				}
			}
			// Perform action selected - calls functions to allow for both mouse and keyboard inputs
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (sfxOn) {
					playAudio(confirm);
				}
				// Play
				if (menuSelect == 1) {
					menuSelectionOne();
				}
				// Options
				if (menuSelect == 2) {
					menuSelectionTwo();
				}
				// Help
				if (menuSelect == 3) {
					menuSelectionThree();
				}
				// Quit
				if (menuSelect == 4) {
					menuSelectionFour();
				}
			}
		}
		// Key events when not on options or main menu screen (playing the game)
		else {
			// Make sure no direction change is underway (prevents quick 180 game over within update() cycle)
			if (!directionChanging) {
				// If right arrow key is pressed
				if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
					// Prevent snake from turning 180 degrees on itself
					if (!goLeft) {
						directionChanging = true; // a change in direction has been registered
						// Update new direction
						goRight = true;
						goUp = false;
						goDown = false;
					}
				}
				// If left arrow key is pressed
				if(e.getKeyCode() == KeyEvent.VK_LEFT) {
					// Prevent snake from turning 180 degrees on itself
					if (!goRight) {
						directionChanging = true; // a change in direction has been registered
						// Update new direction
						goLeft = true;
						goUp = false;
						goDown = false;
					}
				}
				// If down arrow key is pressed
				if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					// Prevent snake from turning 180 degrees on itself
					if (!goUp) {
						directionChanging = true; // a change in direction has been registered
						// Update new direction
						goDown = true;
						goLeft = false;
						goRight = false;
					}
				}
				// If up arrow key is pressed
				if(e.getKeyCode() == KeyEvent.VK_UP) {
					// Prevent snake from turning 180 degrees on itself
					if (!goDown) {
						directionChanging = true; // a change in direction has been registered
						// Update new direction
						goUp = true;
						goLeft = false;
						goRight = false;
					}
				}
			}
			// If SpaceBar pressed: toggle game pause or game restart
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				// If game is running
				if (gameState == 1) {
					pauseStartTime = getTime();
					gameState = 2; // pause it
					if (musicOn) {
						if (multiplierActive) {
							stopAudioLoop(multiplierMusic);
						}
						if (!multiplierActive) {
							stopAudioLoop(music);
						}
						pauseMusic = loadAudio("snakeGamePauseTheme.wav"); // reload file
						startAudioLoop(pauseMusic);
					}
				}
				// If game is paused
				else if (gameState == 2) {
					gameState = 1; // unpause
					if (musicOn) {
						stopAudioLoop(pauseMusic);
						music = loadAudio("snakeGameTheme.wav"); // reload file
						if (multiplierActive) {
							startAudioLoop(multiplierMusic);
						}
						if (!multiplierActive) {
							startAudioLoop(music);
						}
					}
				}
				// If game is over
				if (gameState == 0) {
					gameState = 1; // set game state to "playing"
					gameStartTime = getTime();
					if (musicOn) {
						stopAudioLoop(menuMusic);
						music = loadAudio("snakeGameTheme.wav"); // reload file
						startAudioLoop(music, -4);
					}
					if (sfxOn) {
						playAudio(countdown);
					}
					init(); // restart the game
				}
			}
		}
		
		// Open options menu from pressing ESC - not allowed from main menu or help
		if (gameState != 4 && gameState != 5) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				// If not already in Options
				if (gameState != 3) {
					pauseStartTime = getTime();
					previousState = gameState; // record state the player came from
					gameState = 3; // set game state to "options"
					comeFromMenu = false; // player didn't open options from the menu
					if (musicOn) {
						if (previousState == 2) {
							stopAudioLoop(pauseMusic);
						}
						else {
							if (multiplierActive) {
								stopAudioLoop(multiplierMusic);
							}
							if (!multiplierActive) {
								stopAudioLoop(music);
							}
						}
						pauseMusic = loadAudio("snakeGamePauseTheme.wav"); // reload file
						startAudioLoop(pauseMusic);
					}
				}
				// If in options and ESC pressed
				else if (gameState == 3) {
					// As long as they didn't open options from the main menu
					if (!comeFromMenu) {
						gameState = previousState; // go back to the players previous state
						if (musicOn) {
							stopAudioLoop(pauseMusic);
							if (previousState == 1) {
								music = loadAudio("snakeGameTheme.wav"); // reload file
								if (multiplierActive) {
									startAudioLoop(multiplierMusic);
								}
								if (!multiplierActive) {
									startAudioLoop(music);
								}
							}
							if (previousState == 2) {
								pauseMusic = loadAudio("snakeGamePauseTheme.wav"); // reload file
								startAudioLoop(pauseMusic);
							}
						}
					}
				}
			}
		}
		// When on help screen
		if (gameState == 5) {
			// If the player presses ESC - return to main menu
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				gameState = 4;
			}
		}
	}
	
	// Handle mouse input for main menu
	public void mouseMoved(MouseEvent e) {
		// Restrict for main menu only
		if (gameState == 4) {
			// Update their selection based on where their mouse is hovering on the main menu
			// Hovering over "Play"
			if ((e.getX() > 250 && e.getX() < 350) && (e.getY() > 140 && e.getY() < 195)) {
				menuSelect = 1;
				onButton = true;
			}
			// Hovering over "Options"
			else if ((e.getX() > 220 && e.getX() < 380) && (e.getY() > 230 && e.getY() < 285)) {
				menuSelect = 2;
				onButton = true;
			}
			// Hovering over "Help"
			else if ((e.getX() > 250 && e.getX() < 350) && (e.getY() > 320 && e.getY() < 375)) {
				menuSelect = 3;
				onButton = true;
			}
			// Hovering over "Quit"
			else if ((e.getX() > 250 && e.getX() < 350) && (e.getY() > 410 && e.getY() < 465)) {
				menuSelect = 4;
				onButton = true;
			}
			// Hovering over no buttons
			else {
				onButton = false;
			}
		}
	}
	
	// Perform actions on main menu based on where the player is hovering over
	public void mouseClicked(MouseEvent e) {
		// Restrict for main menu only
		if (gameState == 4) {
			// Only perform an action if they are hovering over a button with the mouse when they click
			if (onButton) {
				if (sfxOn) {
					playAudio(confirm);
				}
				// Perform their selected option by calling associated function
				if (menuSelect == 1) {
					menuSelectionOne();
				}
				if (menuSelect == 2) {
					menuSelectionTwo();
				}
				if (menuSelect == 3) {
					menuSelectionThree();
				}
				if (menuSelect == 4) {
					menuSelectionFour();
				}
			}
		}
	}
	
	// Function to perform the "Play" option from main menu
	public void menuSelectionOne() {
		gameState = 1; // set game state to "Playing"
		init(); // reinitialize game variables
		gameStartTime = getTime(); // record time of game starting (for countdown)
		if (musicOn) {
			stopAudioLoop(menuMusic);
			music = loadAudio("snakeGameTheme.wav"); // reload file
			startAudioLoop(music, -4);
		}
		if (sfxOn) {
			playAudio(countdown);
		}
	}
	
	// Function to perform the "Options" option from main menu
	public void menuSelectionTwo() {
		if (musicOn) {
			stopAudioLoop(menuMusic);
			pauseMusic = loadAudio("snakeGamePauseTheme.wav"); // reload file
			startAudioLoop(pauseMusic);
		}
		comeFromMenu = true; // user is opening the options menu from the main menu
		gameState = 3; // goto game options
	}
		
	// Function to perform the "Help" option from main menu
	public void menuSelectionThree() {
		gameState = 5; // goto help screen
	}
	
	// Function to perform the "Quit" option from main menu
	public void menuSelectionFour() {
		System.exit(0); // quit the application
	}
	
	// Random coordinate generation for X value on the grid
	public int getRandomX() {
		// generate random number between bounds for x coord
		int randX = (int) ((tileSize/2) + (Math.random() * height-tileSize));
		// Round the coord to a multiple of tileSize (for grid)
		return ((tileSize)*(Math.round(randX/(tileSize)))+(tileSize/2));
	}
	
	// Random coordinate generation for Y value on the grid
	public int getRandomY() {
		// generate random number between bounds for y coord
		int randY = (int) ((scoreboardHeight+(tileSize/2)) + (Math.random() * width-(tileSize-15)));
		// Round the coord to a multiple of tileSize (for grid)
		return ((tileSize)*(Math.round(randY/(tileSize)))+(tileSize/2));
	}
	
	// Spawn apple
	public void getAppleCoords() {
		// get the coords from random generation functions
		appleX = getRandomX();
		appleY = getRandomY();
		// Perform check on the generated coords to see if they fall within play area
		if (!coordsOk(appleX, appleY)) {
			getAppleCoords(); // regenerate coords
		}
		// Check if wall isn't overlapping apple location
		if (!wallOk()) {
			// if it is: move the wall
			getWallCoords();
		}
	}
	
	// Spawn score multiplier
	public void getMultiplierCoords() {
		multiplierX = getRandomX();
		multiplierY = getRandomY();
		if (!coordsOk(multiplierX, multiplierY)) {
			getMultiplierCoords();
		}
		// Check if wall isn't overlapping
		if (!wallOk()) {
			getWallCoords();
		}
	}
	
	// Spawn size decreasing pickup
	public void getDecreaserCoords() {
		decreaserX = getRandomX();
		decreaserY = getRandomY();
		if(!coordsOk(decreaserX, decreaserY)) {
			getDecreaserCoords();
		}
		if (!wallOk()) {
			getWallCoords();
		}
	}
	
	// Spawn wall obstacle
	public void getWallCoords() {
		wallX = getRandomX();
		wallY = getRandomY();
		// Random choice of vertical/horizontal wall
		int whichWay = (int) (1 + (Math.random() * 2));
		// Horizontal wall
		if (whichWay == 1) {
			wallHeight = tileSize; // height will be one tile
			// Size of wall is random multiple of tileSize
			wallLength = tileSize * (int) (2 + (Math.random() * 5));
		}
		// Vertical wall
		else {
			wallLength = tileSize; // length will be one tile
			// Size of wall is random multiple of tileSize
			wallHeight = tileSize * (int) (2 + (Math.random() * 5));
		}
		
		// Perform checks on wall position
		// Not too close to outer bounds of grid space
		if (wallX > 500 || wallX < 100) {
			getWallCoords();
		}
		if (wallY > 500 || wallY < 200) {
			getWallCoords();
		}
		// Not too close to where the head of the snake is
		if (wallX - posX < 150 && wallX - posX > -150) {
			getWallCoords();
		}
		if (wallY - posY < 150 && wallY - posY > -150) {
			getWallCoords();
		}
		// Check if wall isn't overlapping any part of snake, apples or multiplier
		if (!wallOk()) {
			getWallCoords();
		}
	}
	
	// Function to prevent wall overlapping snake, apples or multiplier
	public boolean wallOk() {
		boolean wallFine = true;
		// Vertical wall check
		if (wallLength == tileSize) {
			// For however high the wall size is
			for (int i = 0; i < wallHeight; i+= tileSize) {
				// check each coord of the wall
				if (!coordsOk(wallX, wallY + i)) {
					wallFine = false;
				}
				// Make sure none of the wall spawns where the apple is 
				if (wallX == appleX && (wallY + i) == appleY) {
					wallFine = false;
				}
				// Make sure none of the wall spawns where the multiplier is 
				if (wallX == multiplierX && (wallY + i) == multiplierY) {
					wallFine = false;
				}
				// Make sure none of the wall spawns where the decreaser is 
				if (wallX == decreaserX && (wallY + i) == decreaserY) {
					wallFine = false;
				}
			}
		}
		// Horizontal wall check
		else if (wallHeight == tileSize) {
			// For however long the wall size is
			for (int i = 0; i < wallLength; i+= tileSize) {
				// check each coord of the wall
				if (!coordsOk(wallX + i, wallY)) {
					wallFine = false;
				}
				// Make sure none of the wall spawns where the apple is 
				if ((wallX + i) == appleX && wallY == appleY) {
					wallFine = false;
				}
				// Make sure none of the wall spawns where the multiplier is 
				if ((wallX + i) == multiplierX && wallY == multiplierY) {
					wallFine = false;
				}
				// Make sure none of the wall spawns where the decreaser is 
				if ((wallX + i) == decreaserX && wallY == decreaserY) {
					wallFine = false;
				}
			}
		}
		return wallFine;
	}
	
	// Failsafe function to guarantee coords don't fall outside of play grid
	public boolean coordsOk(int coord1, int coord2) {
		// If coords are on any of the snake's tail coords
		for (int i = 0; i < snakeArrayList.size()-2; i+=2) {
			if (coord1 == snakeArrayList.get(i) && coord2 == snakeArrayList.get(i+1)) {
				return false; // coords not ok
			}
		}
		// If coords are too far left or right
		if (coord1 > width || coord1 < 0) {
			return false; // coords not ok
		}
		// If coords are too high or too low
		if (coord2 > height || coord2 < scoreboardHeight) {
			return false; // coords not ok
		}
		// If coords happen to spawn on where the snakes head is
		if (coord1 == posX && coord2 == posY) {
			return false; // coords not ok
		}
		// Otherwise if no conditions above are met, the coords are ok to use
		else {
			return true;
		}
	}
}
