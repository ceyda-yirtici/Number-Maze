import enigma.core.Enigma;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import enigma.console.TextAttributes;
import java.awt.Color;

public class NumberMaze {

	public static enigma.console.Console cn;
	public char[][] mazeTable = new char[24][57], emptyTable = new char[24][57];
	public Player player;
	public ComputerNumber[] computerNumbers;
	public CircularQueue input;	
	public static Random r=new Random();
	private int numbersCount = 0;
	private TextAttributes currentColor1, currentColor2;
	private int difficultyLevel;

	public static File file = new File("highscores.txt");
	static String filePath = "background.wav";
	static String filePath2 = "losing.wav";
	static String filePath3 = "matching1.wav";
	static String filePath4 = "born.wav";
	static String filePath5 = "eating.wav";
	static String filePath6 = "moving.wav";
	
	SimpleAudioPlayer audioPlayer = new SimpleAudioPlayer(filePath);
	SimpleAudioPlayer audioPlayer3 = new SimpleAudioPlayer(filePath3);
	SimpleAudioPlayer audioPlayer4 = new SimpleAudioPlayer(filePath4);
	SimpleAudioPlayer audioPlayer5 = new SimpleAudioPlayer(filePath5);
	SimpleAudioPlayer audioPlayer6 = new SimpleAudioPlayer(filePath6);
	
	public static TextAttributes blueColor=new TextAttributes(Color.white,Color.blue);
	public static TextAttributes blackBackground=new TextAttributes(Color.black,Color.black);
	public static TextAttributes whiteBackground=new TextAttributes(Color.black,Color.white);
	public static TextAttributes greenColor=new TextAttributes(Color.green,Color.black);
	public static TextAttributes yellowColor=new TextAttributes(Color.yellow,Color.black);
	public static TextAttributes redColor=new TextAttributes(Color.red,Color.black);
	public static TextAttributes whiteColor=new TextAttributes(Color.white,Color.black);

	public KeyListener klis; 
	
	   // ------ Standard variables for keyboard ------
    public int keypr;   // key pressed?
	public int rkey;    // key   (for press/release)
	   // ---------------------------------------------------- 
	   
	NumberMaze() throws Exception {
		//audioPlayer3.pause();
		audioPlayer4.pause();
		audioPlayer5.pause();
		audioPlayer6.pause();
		
		// --- console screen
		cn =Enigma.getConsole("Number Maze", 100,30,20);
		
		// file operations
		FileReader inputStream = null;
		inputStream = new FileReader("maze.txt");
		int con;
		int x = -1;
		int y = 0;
		while ((con = inputStream.read()) != -1) {
			if(x==56) {
				x=0;
				y++;
			}
			else {
				x++;
			}
			emptyTable[y][x]=(char)con;
		}
		inputStream.close();
		
     	// key listener code
	      klis=new KeyListener() {
	         public void keyTyped(KeyEvent e) {}
	         public void keyPressed(KeyEvent e) {
	            if(keypr==0) {
	               keypr=1;
	               rkey=e.getKeyCode();
	            }
	         }
	         public void keyReleased(KeyEvent e) {}
	      };
	      cn.getTextWindow().addKeyListener(klis);
	      
	   
   	   int px=40,py=13; // location of the cursor
   	   currentColor1 = greenColor; // current colors of music buttons (on-off)
   	   currentColor2 = whiteColor;
   	   
   	   // main game loop
       while(true) {
    	   deleteScreen();
    	   cn.getTextWindow().setCursorPosition(44, 10);
           cn.getTextWindow().output("WELCOME", blueColor);
           cn.getTextWindow().setCursorPosition(43, 13);
           cn.getTextWindow().output("PLAY ", greenColor);
           cn.getTextWindow().setCursorPosition(43, 14);
           cn.getTextWindow().output("HIGH SCORES ");
           cn.getTextWindow().setCursorPosition(43, 15);
           cn.getTextWindow().output("HOW TO PLAY "); 
           cn.getTextWindow().setCursorPosition(43, 16);
           cn.getTextWindow().output("EXIT "); 
    	   cn.getTextWindow().output(px,py,'>',greenColor);
    	  
    	  
          if(keypr==1) {    // selecting operation
             if(rkey==KeyEvent.VK_UP && py > 13) {
            	 cn.getTextWindow().output(px,py,' ');
            	 py--;
        	 }
             else if(rkey==KeyEvent.VK_DOWN && py < 16) { 
            	 cn.getTextWindow().output(px,py,' ');
            	 py++;
             }
             else if(rkey==KeyEvent.VK_ENTER) {
                if (py == 13) {
                	newGame();
        	    }
                else if (py == 14) {
                	displayScores();
                }
                else if(py == 15) {
                	howToPlay();
                }
                else System.exit(0);
                
              }
             keypr=0; 
        	}
          Thread.sleep(300);
          }
	   
	 }
	public void newGame() throws Exception {
		difficulty(); // Calling up the difficulty menu
		
		keypr=0;
		numbersCount = 0; // the amount of computer numbers in the maze
		// variables for functions running at certain times
    	int counterFiveSecond = 0, movementSecond = 0, second = 0, time = 0, moveTime = 0, soundReset = -1; 
    	
    	// copying the maze what has only walls
    	for (int i = 0; i < 57; i++) {
			for (int j= 0; j<24;j++) {
				mazeTable[j][i] = emptyTable[j][i]; 
			}
		}
    	
    	input = new CircularQueue(35);
    	computerNumbers = new ComputerNumber[1000]; // An array of type computer number that holds the existing numbers in the table
    	player = new Player(mazeTable); // main character
 	    randomNumber(35); // Creating 35 numbers and distributing 25 to the table
    	addRandomNumber(25); //  10 to the input list
    	
		int playerInput = 0, computerMovement = 0; // Values holding information from player and moving numbers
		
		// uploading music and screen
		deleteScreen();
		displayMaze();
		audioPlayer4.restart(filePath4);
		displayPlayerBeginning();
		
		// new game loop. it turns until player die
    	while(playerInput != 2 && computerMovement !=2 ) { 
    		
    		if(keypr==1) {
    			if(rkey==KeyEvent.VK_SPACE) {
    				int pause = Pause(); // uploading pause menu
    				if(pause == 1) break;
    			}
    			else playerInput = player.Input((char)rkey); // sending keyboard inputs to the player
    			if(playerInput == 6) audioPlayer6.restart(filePath6); // moving sounds work if player move
    			keypr=0;
    		}
    		
    	    // controls of the backpacks
    		boolean isMatched = player.checkMatches(); 
			if (isMatched) {
				soundReset = 0;
				audioPlayer3.restart(filePath3);
				player.setNumber((char)((int)player.getNumber()+1));
				if(player.getNumber() > '9') {
					player.setNumber('1');
					playerInput = 1;
				}
			}
			
    		displayMaze();
    		
    		// TIMING
    		cn.getTextWindow().setCursorPosition(63, 23);
    		cn.getTextWindow().output("Time : " + second);
    		Thread.sleep(50);
    		
    		audioPlayer6.pause();
    		time+=50; // it increases by seconds when it reaches 1000
    		soundReset += 50; // for the matching sound
    		if(soundReset == 1000) 
    			audioPlayer3.pause();
    		if(time == 1000) {
    			second++;
    			time = 0; 
    			audioPlayer5.pause(); // if the sound of eating worked, it stops.
    		}
    		
    		counterFiveSecond+=50; // creates a number in the table every 5 seconds
    		if(counterFiveSecond == 5000) {
    			
    			boolean isEmpty = false; // blank place check in the table
    			for (int i = 0; i < 57; i++) {
    				for (int j= 0; j<24;j++) {
    					if (mazeTable[j][i] == ' ') {
    						isEmpty = true;
    						break;
    					}
    				}
    				if (isEmpty) break;
    			}
    			if(!isEmpty) { gameOver(); break; }
    			
    			
    			addRandomNumber(1);
    			randomNumber(1);
    			counterFiveSecond=0;
    		}

    		if(playerInput == 3) { // for the numbers that the player eats by moving
    			deleteComputerNumber(-1); 
    			playerInput = 0;
    		}
    		
    		if(playerInput == 1) { // Works as long as the player is 1 and holds 4 seconds
    			movementSecond+= 50;
    			if (movementSecond == 4000) {
    				audioPlayer3.restart(filePath3);
    				player.setNumber('2');
    				movementSecond = 0;
        			playerInput = 0;
        			soundReset = 0;
    			}
    		}

    		moveTime+=50; // other numbers moves when it reaches 500
    		computerMovement = 0; // it keeps other numbers outputs
    		
    		for(int i = 0; i < numbersCount; i++) {
    			if(computerNumbers[i].getColor() == redColor && !computerNumbers[i].isDFSorBFS()) {
    				Coord playerCoordinate = new Coord(player.getPx(),player.getPy());
    				computerNumbers[i].findPathBFS(mazeTable, playerCoordinate);
    			}	
    		}
    		
    		if(moveTime==500) {
    			for(int i = 0; i < numbersCount; i++) {
    				if(computerNumbers[i].getColor() == yellowColor) {
    					computerMovement = computerNumbers[i].moveRandomly(mazeTable, player);
    				}
    				else if(computerNumbers[i].getColor() == redColor && computerNumbers[i].isDFSorBFS()){
    					computerMovement = computerNumbers[i].pathFindingDFS(mazeTable, player);
    				}//DFS
    				else if(computerNumbers[i].getColor() == redColor && !computerNumbers[i].isDFSorBFS()){
    					computerMovement = computerNumbers[i].followDirection(mazeTable, player);
    				}//BFS
    				else
    					computerMovement = 0;
    				
					if(computerMovement == 3) { // When a computer number goes over player by moving and dies 
						deleteComputerNumber(i);
						i--; // to check the replacement number for the deleted number
					}
					else if(computerMovement == 2) break; // When a computer number goes over player by moving and  player die
    			}
				moveTime=0;
    		}
    		deleteScreen();
    		
    		if(playerInput == 2 || computerMovement == 2) { // displaying game over screen
    			gameOver();
    		}
        }
	}
	public void difficulty() throws InterruptedException {
		int px=42,py=13;
		keypr = 0;
		
		deleteScreen();
    	cn.getTextWindow().setCursorPosition(43, 10);
        cn.getTextWindow().output("DIFFICULTY", blueColor);
        cn.getTextWindow().setCursorPosition(45, 13);
        cn.getTextWindow().output(" EASY", greenColor);
        cn.getTextWindow().setCursorPosition(45, 14);
        cn.getTextWindow().output("NORMAL", yellowColor);
        cn.getTextWindow().setCursorPosition(45, 15);
        cn.getTextWindow().output(" HARD", redColor); 
        
		while(true) {
			
	        cn.getTextWindow().output(px,py,'>',greenColor);
	        if(keypr==1) {    // if keyboard button pressed
	        	if(rkey==KeyEvent.VK_UP && py > 13) {
	        		cn.getTextWindow().output(px,py,' ');
	              	py--;
	          	}
	            else if(rkey==KeyEvent.VK_DOWN && py < 15) { 
	              	cn.getTextWindow().output(px,py,' ');
	              	py++;
	            }
	            else if(rkey==KeyEvent.VK_ENTER) {
	            	if (py == 13) {
	            		difficultyLevel = 1;
	 	                keypr=0;
	                	break;
	          	    }
	                else if (py == 14) {
	                	difficultyLevel = 2;
	 	                keypr=0;
	                	break;
	                }
	                else {
	                	difficultyLevel = 3;
	 	                keypr=0;
	                	break;
	                } 
	            }
	        }
	        keypr = 0;
	        Thread.sleep(300);
		}
	}
	public void gameOver() throws InterruptedException, UnsupportedAudioFileException, IOException, LineUnavailableException {
		
		// taking name from the user and ranking with other scores
		audioPlayer.pause();
		SimpleAudioPlayer audioPlayer2 = new SimpleAudioPlayer(filePath2);
		Thread.sleep(300); 
		cn.getTextWindow().setCursorPosition(44, 11);
		cn.getTextWindow().output("GAME OVER ", redColor);
		cn.getTextWindow().setCursorPosition(44, 14);
		cn.getTextWindow().output("Name:  ");
		Thread.sleep(1500);
		audioPlayer2.pause();
		Thread.sleep(1000);
		audioPlayer.resumeAudio(filePath);
		String name =cn.readLine();   
		Score score = new Score(name, player.getScore());
		sorting(score);
		Thread.sleep(1000);
	}
	
	public int Pause() throws IOException, LineUnavailableException, Exception {
		deleteScreen();
		keypr = 0;
		int px = 40, py = 14;
		
		while (true) {
			
			cn.getTextWindow().setCursorPosition(43, 9);
			cn.getTextWindow().output(" PAUSED  ",redColor);

	        cn.getTextWindow().setCursorPosition(43, 11);
			cn.getTextWindow().output(" MUSIC   ");
			cn.getTextWindow().setCursorPosition(39, 10);
			cn.getTextWindow().output("----------------  ");

			cn.getTextWindow().setCursorPosition(39, 13);
			cn.getTextWindow().output("----------------  ");
			cn.getTextWindow().setCursorPosition(42, 12);
			cn.getTextWindow().output("ON  ", currentColor1);
			cn.getTextWindow().setCursorPosition(49, 12);
			cn.getTextWindow().output("OFF   ", currentColor2);
			cn.getTextWindow().setCursorPosition(43, 14);
			cn.getTextWindow().output("CONTINUE   ");
			cn.getTextWindow().setCursorPosition(43, 15);
	        cn.getTextWindow().output("MAIN MENU  ");
	        cn.getTextWindow().setCursorPosition(42, 16);
	        cn.getTextWindow().output("HOW TO PLAY  "); 
	        cn.getTextWindow().setCursorPosition(45, 17);
	        cn.getTextWindow().output("EXIT  "); 
	        
			cn.getTextWindow().output(px,py,'>',greenColor);
			
			if(keypr==1) { // if keyboard button pressed
	            if(rkey==KeyEvent.VK_UP && py > 12&& px == 40) {
	           	 cn.getTextWindow().output(px,py,' ' );
	           	 if(py == 14) py-=2;
	           	 else py--;
	       	    }
	            else if(rkey==KeyEvent.VK_DOWN && py < 17) { 
	           	 cn.getTextWindow().output(px,py,' ');
	           	 if(py == 12) py+=2;
	           	 else py++;
	           	 px = 40;
	            }
	            else if(rkey==KeyEvent.VK_LEFT && py == 12 && px == 47) { 
		           	 cn.getTextWindow().output(px,py,' ');
		           	 px = 40;
		        }
	            else if(rkey==KeyEvent.VK_RIGHT && py == 12 && px == 40) { 
	            	cn.getTextWindow().output(px,py,' ');
		           	 px = 47;
		        }
	            else if(rkey==KeyEvent.VK_ENTER)
	            {
					if(py == 14) {
		            	 deleteScreen();
		            	 break;
					}
					else if(py == 12) {
						// the green is the active button for music (on - off)
						if (px == 40) {
							audioPlayer.resumeAudio(filePath);
							currentColor1 = greenColor; 
							currentColor2= whiteColor;
						}
						else{
							currentColor2 = greenColor;
							currentColor1= whiteColor;
							audioPlayer.pause();
						}
					}
					else if(py == 15) { 
						 deleteScreen();
	            		return 1;
	            	}
	            	else if (py == 16) {
	            		 deleteScreen();
	            		howToPlay();
	            	}
	            	else System.exit(0);
	            }
			
				keypr=0;
			}
			Thread.sleep(300);
		}
		return 0;
	}
public void howToPlay() throws InterruptedException {
		
    	deleteScreen();  // console clear
    	
    	int px = 22, py =15, count = 0, rx = 65,ry = 14, yx = 67,yy = 16;
    	cn.getTextWindow().setCursorPosition(92, 0);
		cn.getTextWindow().output("← Back  ");
		
		cn.getTextWindow().output(22,13,'▲');
		cn.getTextWindow().setCursorPosition(4, 15);
		cn.getTextWindow().output("the arrow     ◀       ▶");
		cn.getTextWindow().output(px,py, '5',blueColor);
		cn.getTextWindow().output(22,17,'▼');
		cn.getTextWindow().output(rx,ry, '9',redColor);
		cn.getTextWindow().output(yx,yy, '6',yellowColor);
		cn.getTextWindow().output(72,ry, '5',blueColor);
		cn.getTextWindow().setCursorPosition(66, 14);
		cn.getTextWindow().output("......");
		cn.getTextWindow().setCursorPosition(65, 19);
		cn.getTextWindow().output("1   2   3", greenColor);
		cn.getTextWindow().setCursorPosition(3, 5);
		cn.getTextWindow().output(" If the number is less/equal than");
		cn.getTextWindow().setCursorPosition(5, 6);
		cn.getTextWindow().output(" your number, you collect and");
		cn.getTextWindow().setCursorPosition(4, 7);
		cn.getTextWindow().output(" throw it in the left-backpack.");
		cn.getTextWindow().setCursorPosition(58, 5);
		cn.getTextWindow().output("  You earn points if you match");
		cn.getTextWindow().setCursorPosition(58, 6);
		cn.getTextWindow().output("same two numbers to the same level.");
		cn.getTextWindow().setCursorPosition(58, 7);
		cn.getTextWindow().output("After matching, you grow a number.");
		cn.getTextWindow().setCursorPosition(4, 14);
		cn.getTextWindow().output("Move with\n\n     keys.");
		cn.getTextWindow().setCursorPosition(3, 19);
		cn.getTextWindow().output("Avoid numbers higher than you.");
		cn.getTextWindow().setCursorPosition(43, 24);
		cn.getTextWindow().output("GOOD LUCK !",yellowColor);
		cn.getTextWindow().setCursorPosition(77, 14);
		cn.getTextWindow().output("  They follow");
		cn.getTextWindow().setCursorPosition(75, 15);
		cn.getTextWindow().output("  a path to eat you.");
		cn.getTextWindow().setCursorPosition(75, 17);
		cn.getTextWindow().output("  They move randomly.");
		cn.getTextWindow().setCursorPosition(75, 19);
		cn.getTextWindow().output("  They stand still.");
		cn.getTextWindow().setCursorPosition(44, 12);
		cn.getTextWindow().output("Q     W");
		cn.getTextWindow().setCursorPosition(42, 11);
		cn.getTextWindow().output("Left  Right");
		cn.getTextWindow().setCursorPosition(57, 10);
		cn.getTextWindow().output(" Score: " + 56 + "   You: ");
		cn.getTextWindow().output(75,10, '5',blueColor);
		cn.getTextWindow().setCursorPosition(42, 10);
		cn.getTextWindow().output("+---+ +---+");
		for(int i = 0 ; i < 8; i++) {
			cn.getTextWindow().setCursorPosition(42, 9 - i);
			cn.getTextWindow().output("|   | |   |");
		}
		cn.getTextWindow().setCursorPosition(42, 9);
		cn.getTextWindow().output("| 4 | | 7 |");
		cn.getTextWindow().setCursorPosition(42, 8);
		cn.getTextWindow().output("| 3 | |   |");
		cn.getTextWindow().setCursorPosition(42, 7);
		cn.getTextWindow().output("| 3 | |   |");
		
    	while(true) {
    		if(keypr==1) {    // if keyboard button pressed
	             if(rkey==KeyEvent.VK_BACK_SPACE) {
	            	 deleteScreen();
	            	 break;
	          }
	      	  keypr=0;    // last action      
	        }
    		
    		Thread.sleep(200);
    		
    		int score = 56;
    		cn.getTextWindow().setCursorPosition(44, 12);
    		cn.getTextWindow().output("Q     W");
    		cn.getTextWindow().setCursorPosition(42, 11);
    		cn.getTextWindow().output("Left  Right");
    		cn.getTextWindow().setCursorPosition(57, 10);
    		cn.getTextWindow().output(" Score: " + score + "   You: ");
    		cn.getTextWindow().output(75,10, '5',blueColor);
    		cn.getTextWindow().setCursorPosition(42, 10);
    		cn.getTextWindow().output("+---+ +---+");
    		for(int i = 0 ; i < 8; i++) {
    			cn.getTextWindow().setCursorPosition(42, 9 - i);
    			cn.getTextWindow().output("|   | |   |");
    		}
    		cn.getTextWindow().setCursorPosition(42, 9);
    		cn.getTextWindow().output("| 4 | | 7 |");
    		cn.getTextWindow().setCursorPosition(42, 8);
    		cn.getTextWindow().output("| 3 | |   |");
    		cn.getTextWindow().setCursorPosition(42, 7);
    		cn.getTextWindow().output("| 3 | |   |");
    		cn.getTextWindow().output(yx,yy, ' ');
    		if(count == 1) {
    			px-= 2;
    			yx++;
    			}
    		if(count == 2) {
    			py++;
    			yy++;
    		}
    		if(count == 3) {
    			px+= 2;
    			yx++;
    		}
    		if(count == 4) {
    			yx++;
    			rx = 65;
        		cn.getTextWindow().setCursorPosition(48, 11);
        		cn.getTextWindow().output("Right",greenColor);
        		cn.getTextWindow().output(50,12, 'W',greenColor);
        		cn.getTextWindow().setCursorPosition(42, 8);
        		cn.getTextWindow().output("| 3 | | 3 |");
        		cn.getTextWindow().setCursorPosition(42, 7);
        		cn.getTextWindow().output("|   | |   |");
    			cn.getTextWindow().setCursorPosition(66, 14);
    			cn.getTextWindow().output("......");
        		cn.getTextWindow().output(rx,ry, '9',redColor);
        		score += 3;
    			py--;
    			count = 0;
    		}
    		cn.getTextWindow().output(yx,yy, '6',yellowColor);
    		cn.getTextWindow().output(rx,ry, ' ');
    		rx++;
    		cn.getTextWindow().output(rx,ry, '9',redColor);

	      	cn.getTextWindow().output(22,15, ' ');
	        cn.getTextWindow().output(px,py, '5',blueColor);
    		
    		if(score == 59) {
    	        Thread.sleep(200);
        		cn.getTextWindow().output(75,10, '6',blueColor);
        		cn.getTextWindow().setCursorPosition(42, 8);
        		cn.getTextWindow().output("|   | |   |");
        		cn.getTextWindow().setCursorPosition(57, 10);
        		cn.getTextWindow().output(" Score: " + score);
    		}
	        Thread.sleep(200);
	        cn.getTextWindow().output(px,py, ' ');
	        px = 22;
	        py = 15;
	        cn.getTextWindow().output(px,py, '5',blueColor);
	        if (count == 0) {
    			cn.getTextWindow().output(yx,yy, ' ');
	        	yx = 67; yy = 16;
	    		cn.getTextWindow().output(yx,yy, '6',yellowColor);
	        }
    		count++;
	    
		}
    
	}
	public void displayScores() throws InterruptedException, NumberFormatException, IOException {
		deleteScreen();
		
		cn.getTextWindow().setCursorPosition(92, 0);
		cn.getTextWindow().output("← Back  ");
		
		Scanner in = new Scanner(file);
		int count = 0;
		cn.getTextWindow().setCursorPosition(43, 7);
		cn.getTextWindow().output("TOP 10");
		// the file is read line by line and split
		while (in.hasNextLine()){
			
		   String line = in.nextLine();
		   if (line != null) {
		   String[] words = line.split(";");
		   cn.getTextWindow().setCursorPosition(43, 9 + count);
		   cn.getTextWindow().output(words[0] + ":  " + words[1] + "  ");
		   count++;
		   }
		            
		}
		in.close();
		
		
    	while(true) {
	          if(keypr==1) {    // if keyboard button pressed
	             if(rkey==KeyEvent.VK_BACK_SPACE) {
	            	 deleteScreen();
	            	 break;
	          }
	      	  keypr=0;    // last action  
	      }
	      Thread.sleep(300);
	    }
	}
	
	public void displayMaze() {
		
		///---Informations---///
		cn.getTextWindow().setCursorPosition(91, 0);
		cn.getTextWindow().output("⎵ Pause");
		
		///----Maze table----///
		
		// displaying player and walls
		for (int i = 2; i < 59; i++) {
			for (int j= 1; j<25;j++) {
				
				TextAttributes currentcolor;
				if(i-2 == player.getPx() && j-1 == player.getPy()) currentcolor = blueColor;
				else if(mazeTable[j-1][i-2] == '#') currentcolor = whiteBackground;
				else currentcolor = null;
					
				cn.getTextWindow().output(i,j, mazeTable[j-1][i-2],currentcolor); 
			}
		}
		
		// displaying computer numbers and paths of the red ones
		for (int i = 0; i < numbersCount; i++) {
			if(computerNumbers[i].getCopyMaze2() != null) {
				
				for (int k = 2; k < 59; k++) {
					for (int j= 1; j<25;j++) {
						if (computerNumbers[i].getCopyMaze2()[j-1][k-2] == '.' && mazeTable[j-1][k-2] == ' ') cn.getTextWindow().output(k,j, '.');
					}
				}
			}
			cn.getTextWindow().output(computerNumbers[i].getX()+2,computerNumbers[i].getY()+1, computerNumbers[i].getNumber(),computerNumbers[i].getColor());
		}
		
		for(int i = 0; i < numbersCount; i++) {
			if(computerNumbers[i].getColor() == redColor && !computerNumbers[i].isDFSorBFS()) {
				CircularQueue path = computerNumbers[i].getPathQueue();
				while(path != null && !path.isEmpty()) {
					int x = ((Coord)path.peek()).getX();
				 	int y = ((Coord)path.peek()).getY();
				 	NumberMaze.cn.getTextWindow().setCursorPosition(x+2, y+1);
				 	NumberMaze.cn.getTextWindow().output("."); 
				 	path.dequeue();
				 }
			}
		}
		
		/// ---Input----///
		cn.getTextWindow().setCursorPosition(63, 1);
		cn.getTextWindow().output("Input");
		cn.getTextWindow().setCursorPosition(63, 2);
		cn.getTextWindow().output("<<<<<<<<<<", whiteBackground);
		CircularQueue inputTemp = new CircularQueue(10);
		for(int i = 0; i<10; i++) {
			ComputerNumber number = (ComputerNumber)(input.peek());
			cn.getTextWindow().setCursorPosition(63+i, 3);
			cn.getTextWindow().output(number.getNumber());
			inputTemp.enqueue(input.dequeue());
		}
		cn.getTextWindow().setCursorPosition(63, 4);
		cn.getTextWindow().output("<<<<<<<<<<",whiteBackground);
		for(int i = 0; i<10; i++) {
			input.enqueue(inputTemp.dequeue());
		}	
		
		///---Backpack---///
		Stack leftBackpack = player.getLeftBackpack();
		Stack rightBackpack = player.getRightBackpack();
		Stack tempStack1 = new Stack(leftBackpack.size());
		Stack tempStack2 = new Stack(rightBackpack.size());
		while(!leftBackpack.isEmpty())
			tempStack1.push(leftBackpack.pop());
		while(!rightBackpack.isEmpty())
			tempStack2.push(rightBackpack.pop());
		cn.getTextWindow().setCursorPosition(63, 21);
		cn.getTextWindow().output("Score: " + player.getScore());
		cn.getTextWindow().setCursorPosition(65, 18);
		cn.getTextWindow().output("Q     W");
		cn.getTextWindow().setCursorPosition(63, 17);
		cn.getTextWindow().output("Left  Right");
		cn.getTextWindow().setCursorPosition(63, 16);
		cn.getTextWindow().output("+---+ +---+");
		for(int i = 0 ; i < 8 ; i++) {
			cn.getTextWindow().setCursorPosition(63, 15 - i);
			cn.getTextWindow().output(String.format("| %s | | %s |", tempStack1.isEmpty()? " " : tempStack1.peek().toString()
									  , tempStack2.isEmpty()? " " : tempStack2.peek().toString()));
			if(!tempStack1.isEmpty())
				leftBackpack.push(tempStack1.pop());
			if(!tempStack2.isEmpty())
				rightBackpack.push((tempStack2.pop()));
		}
		cn.getTextWindow().setCursorPosition(64, 7);
		cn.getTextWindow().output("Backpacks");
	}
	
	public void deleteScreen() {
		 for (int i = 0; i < 100; i++)
			 for (int j= 0; j<30;j++)
				 cn.getTextWindow().output(i,j,' '); 
	 }
	

	public void randomNumber(int amount) {
		for(int i = 0; i < amount; i++){
			boolean DFSorBFS;//for the level of difficulty(easy/medium/hard)
			int a=r.nextInt(100);
			ComputerNumber number;
			
			if(difficultyLevel == 1)//easy
				DFSorBFS = true;
			else if(difficultyLevel == 2) {//medium
				if(r.nextInt(2) == 0)
					DFSorBFS = true;
				else
					DFSorBFS = false;
			}
			else//hard
				DFSorBFS = false;
			
			if(a<5) {
				number = new ComputerNumber((char)(r.nextInt(3)+7+'0'), redColor,DFSorBFS); //%5
			}
			else if(a<25) {
				number = new ComputerNumber((char)(r.nextInt(3)+4+'0'), yellowColor);//%20
			}
			else {
				number = new ComputerNumber((char)(r.nextInt(3)+1+'0'), greenColor);//%75
			}
			input.enqueue(number);	
		}
		
	}
	public void addRandomNumber(int amount) {
		for(int i = 0; i < amount; i++) {
		do {//find free space to place the number
			int y=r.nextInt(23);
			int x=r.nextInt(55);
			if(mazeTable[y][x]==' ') {
				ComputerNumber number = (ComputerNumber)input.dequeue(); 
				number.setX(x);
				number.setY(y);
				computerNumbers[numbersCount] = number;
				mazeTable[y][x]=(number.getNumber());
				numbersCount++;
				break;
			}
		}while(true);
		
	}
	}
	public void deleteComputerNumber(int a) throws UnsupportedAudioFileException, IOException, LineUnavailableException {

		audioPlayer5.restart(filePath5);
		// the number is deleted and the last number replaces it
		if (a == -1) { // when the player moves and gets a number
		for (int i = 0; i < numbersCount; i++) {
			if(computerNumbers[i] != null && computerNumbers[i].getX() == player.getPx() && computerNumbers[i].getY() == player.getPy()) {
					computerNumbers[i] = null; 
					computerNumbers[i] = computerNumbers[numbersCount-1];
					numbersCount--;
					break;
			}
		}
		}
		else { // when the player does not move and gets a number
			computerNumbers[a] = null;
			computerNumbers[a] = computerNumbers[numbersCount-1];
			numbersCount--;
		}
	}

	public void sorting(Score score) throws NumberFormatException, IOException {
		
		Score[] tempScores = new Score[11];
		tempScores[0] = score;
		Scanner in = new Scanner(file);
		int counter = 1;
		
		//The information of previously registered scores is received
		while (in.hasNextLine()) {
			String line = in.nextLine();
			   if (line != null) {
			   String[] words = line.split(";");
			   
			   tempScores[counter] = new Score(words[0],Integer.valueOf(words[1]));
			   counter++;
			   }
		}
		in.close();
		
		// sorting algorithm
		for(int i = 0; i < 11; i++){
            for (int j = 0; j < 11; j++) {
                if(tempScores[i] != null && tempScores[j] != null && tempScores[i].getScore() > tempScores[j].getScore()) {
                    Score temp = tempScores[i];
                    tempScores[i] = tempScores[j];
                    tempScores[j] = temp;
                }
            }
        }
		
		// The ranked scores are transferred to the file again
		try(BufferedWriter br = new BufferedWriter(new FileWriter(NumberMaze.file))){
			for(int i = 0; i < 10; i++) {
			if (tempScores[i] != null) {
				br.write(tempScores[i].getName() + ";" + tempScores[i].getScore());
				br.newLine();
				}
			}
		}  
		 catch (IOException e) {
			 cn.getTextWindow().output("Unable to read file " +NumberMaze.file.toString());
		}
		
	}
	
	//flashing during the player's birth
	public void displayPlayerBeginning() throws InterruptedException {
		for (int i = 2; i < 59; i++) {
			for (int j= 1; j<25;j++) {
				
				TextAttributes currentcolor;
				if(i-2 == player.getPx() && j-1 == player.getPy())
					for (int k = 0; k< 4; k++){
						currentcolor = blueColor;
						cn.getTextWindow().output( i,j, mazeTable[j-1][i-2],currentcolor); 
						Thread.sleep(100);
						currentcolor = blackBackground;
						cn.getTextWindow().output( i,j, mazeTable[j-1][i-2],currentcolor); 
						Thread.sleep(100);
				}
					
			}
		}
		audioPlayer4.pause();
	}
}