import java.util.Random;

import enigma.console.TextAttributes;

public class ComputerNumber {

	private TextAttributes color;
	private char number;
	private int x, y, randomX = 0, randomY = 0;
	private Stack stack, prevTour;
	private char[][] copyMaze2;
	private Coord prevCoord, prevTourNextCoord;
	private Random rnd = new Random();
	private boolean isStuck = false;
	private int direction;//1 = right, 2 = up, 3 = left, 4 = down;
	private CircularQueue pathQueue;
	private boolean DFSorBFS;
	
	public ComputerNumber(char number, TextAttributes color) {

		this.number=number;
		this.color=color;
	}//constructor 1 --> used for green and yellow numbers
	
	public ComputerNumber(char number, TextAttributes color, boolean DFSorBFS) {

		this.number=number;
		this.color=color;
		this.DFSorBFS = DFSorBFS;
	}//constructor 2 --> used for red numbers
	
	///---Getters and Setters---///
	public TextAttributes getColor() {
		return color;
	}
	public char getNumber() {
		return number;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	 public char[][] getCopyMaze2() {
		return copyMaze2;
	}
	public CircularQueue getPathQueue() {
		return pathQueue;
	}
	public boolean isDFSorBFS() {
		return DFSorBFS;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}

	///---Other Methods---///
	public int moveRandomly(char[][] mazeTable, Player player) {
		boolean up = true, down = true, left = true,right = true;
		do {
			boolean flag = true;
			if(randomY!=0 || randomX!=0) {//if number's direction was determined before, and there is still remanining steps
				if(randomX!=0) {//if the direction in x axis
					if(randomX>0) {//right
						if(mazeTable[y][x+1]==' ' || (x+1 == player.getPx() && y == player.getPy()&& number<player.getNumber())) {//if human number is there and its value is less
							mazeTable[y][x+1]=number;
							mazeTable[y][x]=' ';
							x++;
							randomX--;
						}
						else {
							flag = false;
							right = false;
							randomX=0;
						}
					}
					else if(randomX<0) {//left
						if(mazeTable[y][x-1]==' ' || (x-1 == player.getPx() && y == player.getPy()&& number<player.getNumber())) {//if human number is there and its value is less
							mazeTable[y][x-1]=number;
							mazeTable[y][x]=' ';
							x--;
							randomX++;
						}
						else {
							flag = false;
							left =false;
							randomX=0;
						}
					}
					
				}
				else if(randomY!=0) {//if direction is in y axis
					if(randomY>0) {//down
						if(mazeTable[y+1][x]==' '|| (x == player.getPx() && y+1 == player.getPy()&& number<player.getNumber())) {//if human number is there and its value is less
							mazeTable[y+1][x]=number;
							mazeTable[y][x]=' ';
							y++;
							randomY--;
						}
						else {
							flag = false;
							down = false;
							randomY=0;
						}
					}
					else if(randomY<0) {//up
						if(mazeTable[y-1][x]==' '|| (x == player.getPx() && y-1 == player.getPy()&& number<player.getNumber())) {//if human number is there and its value is less
							mazeTable[y-1][x]=number;
							mazeTable[y][x]=' ';
							y--;
							randomY++;
						}
						else {
							flag = false;
							up =false;
							randomY=0;
						}
					}
					
				}
			}
			else if(!up && !down && !right && !left) {
				randomX = 0;
				randomY = 0;
				break;
			}
			else {
				flag=false;
			}
			
			if(flag==true) {
				if((x == player.getPx() && y == player.getPy())) {
					if(mazeTable[y][x] <= player.getNumber()) {
						player.collectNumber(Integer.parseInt(String.valueOf(mazeTable[y][x])));
						mazeTable[y][x]=' ';
						mazeTable[y][x] = player.getNumber();
						return 3;
					}
					else {
						return 2;
					}
				}
				break;
			}
			else {//if there is not any remaining step for the number, determine direction and step value
				int r = rnd.nextInt(100);
				if(r<50) {//x axis
					r = rnd.nextInt(31)-15;//between -15 and 15 steps
					randomX=r;
				}
				else {//y axis
					r = rnd.nextInt(31)-15;//between -15 and 15 steps
					randomY=r;
				}
			}
		}while(true);
		return 0;
	}//end of moveRandomly
	
		public int pathFindingDFS(char[][] mazeTable, Player player) {
		
		stack = new Stack(10000); // stack to store coordinates from number to player
		Stack currentTour = new Stack(10000); //The stack to which we transfer the previous coordinates of elements of the stack
		Coord firstCoord = new Coord(x,y); // first coordination of the number
		stack.push(firstCoord);
	
		char [][] copyMaze1 = new char[24][57]; // the current version of the table is copied
		for (int i = 0; i < 57; i++) {
			for (int j= 0; j< 24;j++) {
				copyMaze1[j][i] = mazeTable[j][i];
			}
		}
	
		dfs(copyMaze1, firstCoord, player); //Process the DFS algorithm and fill the stack
		if (stack.isEmpty()) return 0; // means that all paths of the number are blocked, and the tour turns empty until it is cleared.
		
		currentTour = fillPath(currentTour); // otherwise, the stack is transferred and the points are determined.
		Coord movementCoord = (Coord)currentTour.pop(); //the first point of the path
		
		if (movementCoord == null) { // means that it does not have the previous coordinate 
			// this is because the player is located at the point where it moves.
			if( number > player.getNumber()) return 2; 
			else { 
				player.collectNumber(Integer.parseInt(String.valueOf(mazeTable[y][x])));
				mazeTable[y][x]=' ';
				x = player.getPx();
				y = player.getPy();
				mazeTable[y][x] = player.getNumber();
				return 3;
			}
		}
		
		// The location of the previous tour is compared with the location it wants to go.
		if (prevTour != null && prevTourNextCoord!= null) {
			
			// if it is stuck, it continues to follow the points from the previous tour until it does not want to go back to its old location.
			if (prevCoord.getX() == movementCoord.getX() && prevCoord.getY() == movementCoord.getY() && 
					mazeTable[prevTourNextCoord.getY()][prevTourNextCoord.getX()] == ' ') {
				isStuck = true;
				movementCoord = prevTourNextCoord;
			}
			else isStuck = false;
		}
		else isStuck = false;

		// if it is not stuck, the previous coordinate is replaced by the one used
		if (!isStuck || prevTour.isEmpty()) prevTour = currentTour; 
		prevTourNextCoord = (Coord)prevTour.pop();
		prevCoord = firstCoord;
		
		
		mazeTable[movementCoord.getY()][movementCoord.getX()]= number;
		mazeTable[y][x]=' ';
	
		// checking player
		if((x == player.getPx() && y == player.getPy())) {
			if(mazeTable[y][x] <= player.getNumber()) {
				player.collectNumber(Integer.parseInt(String.valueOf(mazeTable[y][x])));
				x = movementCoord.getX();
				y = movementCoord.getY();
				mazeTable[y][x] = player.getNumber();
				return 3;
			}
			else {
				return 2;
			}
		}

		// new coordinates
		x = movementCoord.getX();
		y = movementCoord.getY();
		return 0;
	}


	public Stack fillPath(Stack currentTour){
		
		copyMaze2 = new char[24][57]; // it keeps points
		Coord preCoord = ((Coord)stack.pop()).getPreCoord();
		while(true)
		{
			if (preCoord == null) break;
			if(preCoord.getX() == x && (preCoord.getY() == y)) {
				break;
			}
			
			copyMaze2[preCoord.getY()][preCoord.getX()] = '.';
			currentTour.push(preCoord);
			
			// The path is drawn by taking the previous coordinate of the previous coordinate from the player to the number.
			preCoord = preCoord.getPreCoord();
		} 
		
		return currentTour;
	}

	

	public void dfs(char[][] maze, Coord coord,  Player player) {	   
		 
		   boolean push = false;
		   int x = coord.getX();
		   int y = coord.getY();
		   
		   if((x == player.getPx() && y == player.getPy())) { // it continues until it reachs the player
			   return;
		   }
		   
		   maze[y][x] = '#'; // tried points are marked
		   
		   if(maze[y][x+1] == ' ' || (x + 1 == player.getPx() && y == player.getPy())){ // right
			   Coord coord1 = new Coord(x + 1, y);
			   coord1.setPreCoord(coord);
			   stack.push(coord1);
			   push = true;
		   } 
		   
		   if(maze[y+1][x] == ' ' || (x == player.getPx() && y + 1 == player.getPy())){ //down
			   Coord coord1 = new Coord(x , y + 1);
			   coord1.setPreCoord(coord);
			   stack.push(coord1);
			   push = true;
		   }
		   
		   if(maze[y][x-1] == ' ' || (x - 1 == player.getPx() && y == player.getPy())){ // left
			   Coord coord1 = new Coord(x - 1, y);
			   coord1.setPreCoord(coord);
			   stack.push(coord1);
			   push = true;
		   } 

		   if(maze[y-1][x] == ' ' || (x == player.getPx() && y - 1 == player.getPy())){ // up
			   Coord coord1 = new Coord(x , y - 1);
			   coord1.setPreCoord(coord);
			   stack.push(coord1);
			   push = true;
		   }
		  
		   if(!push) { 
			   stack.pop(); // when stuck, it pops until it's fixed
		   }
		   if (stack.isEmpty()) return; // when all direction are close, it skips the tour
		   dfs(maze, (Coord)(stack.peek()), player);
	   }
	
	
	public void findPathBFS(char[][] mazeTable, Coord playerCoordinate) {
		Coord startCoordinate = new Coord(x,y);//start coordinate is determined
		if(startCoordinate.findDirection(playerCoordinate) != 0) {//if computer number is adjacent to the human number
			pathQueue = null;//there is no need to make a path
 			direction = startCoordinate.findDirection(playerCoordinate);//just determine in which direction the number should go 
 			return;//and return the caller and skip the remaining code parts
 		}
		boolean[][] mazeTableCopy = new boolean[24][57];//make a copy of the mazeTable
		int numberOfSpaces = 0;//this variable is used to declare queues dynamically
		for(int i = 0 ; i < 24 ; i++)
			for(int j = 0 ; j < 57; j++)
				if(mazeTable[i][j] == ' ') {//if there is not any number or wall
					mazeTableCopy[i][j] = true;
					numberOfSpaces++;
				}
		int currentX = x, currentY = y;
		int counter = 0;//counter is used to prevent errors when the computer number cannot reach to the human number
		CircularQueue pathQueueTrying = new CircularQueue(numberOfSpaces);
		while(counter < 100000) {
			Coord currentCoord = new Coord(currentX, currentY);
 			if(mazeTableCopy[currentY][currentX+1]) {
 				Coord tempCoordinate = new Coord(currentX+1,currentY,currentCoord);
 				pathQueueTrying.enqueue(tempCoordinate);
 				mazeTableCopy[currentY][currentX+1] = false;
 			}//try to go right 
 			if(mazeTableCopy[currentY-1][currentX]) {
 				Coord tempCoordinate = new Coord(currentX,currentY-1,currentCoord);
 				pathQueueTrying.enqueue(tempCoordinate);
 				mazeTableCopy[currentY-1][currentX] = false;
 			}//try to go up 
 			if(mazeTableCopy[currentY][currentX-1]) {
 				Coord tempCoordinate = new Coord(currentX-1,currentY,currentCoord);
 				pathQueueTrying.enqueue(tempCoordinate);
 				mazeTableCopy[currentY][currentX-1] = false;
 			}//try to go left
 			if(mazeTableCopy[currentY+1][currentX]) {
 				Coord tempCoordinate = new Coord(currentX,currentY+1,currentCoord);
 				pathQueueTrying.enqueue(tempCoordinate);
 				mazeTableCopy[currentY+1][currentX] = false;
 			}//try to go down
 			if(!pathQueueTrying.isEmpty()) {//if number can go at least one step
 				currentX = ((Coord)pathQueueTrying.peek()).getX();
 				currentY = ((Coord)pathQueueTrying.peek()).getY();//set current coordinate as that square's coordinate
 				pathQueueTrying.enqueue(pathQueueTrying.dequeue());
 			}
 			else//if the number cannot go anywhere
 				break;
 			if(((Coord)pathQueueTrying.peek()).findDirection(playerCoordinate) != 0)//if the computer number reached to the human number
 				break;
 			counter++;
 		}
		
		CircularQueue pathFound = new CircularQueue(numberOfSpaces);
		pathFound.enqueue(pathQueueTrying.dequeue());//the adjacent square to the human number
		while(!((Coord)pathFound.peek()).ısItsLastCoordinate(startCoordinate)) {
			if(((Coord)pathFound.peek()).ısItsLastCoordinate(((Coord)pathQueueTrying.peek()))) {
				pathFound.enqueue(pathQueueTrying.dequeue());
				for(int i = 0 ; i < pathFound.size() - 1 ; i++)
					pathFound.enqueue(pathFound.dequeue());
			}
			else
				pathQueueTrying.enqueue(pathQueueTrying.dequeue());
		}//go back to the computer number one by one
		direction = (startCoordinate.findDirection((Coord)pathFound.peek()));
		pathFound.enqueue(pathFound.dequeue());//making the queue in order again
		if (playerCoordinate.findDirection((Coord)pathFound.peek()) == 0)//if the computer number can reach to the human number
			pathQueue = pathFound;
	}//end of findPathBFS
	
	public int followDirection(char[][] mazeTable, Player player) {
		if(direction == 0)//if direction is not determined 
			return 0;
		else if(direction == 1) {//right
			mazeTable[y][x] = ' ';
			setX(x+1);
			if(x == player.getPx() && y == player.getPy() && number > player.getNumber()) {//if there is human number and it is less than the computer number
				mazeTable[y][x] = number;//move the computer number to that place
				return 2;//and inform the caller that the game is over
			}
				
			else if(x == player.getPx() && y == player.getPy() && number <= player.getNumber()) {//human number is larger than the computer number
				player.collectNumber(Integer.parseInt(String.valueOf(number)));
				return 3;
			}
				
			else//space
				mazeTable[y][x] = number;	
		}
		else if(direction == 2) {//up
			mazeTable[y][x] = ' ';
			setY(y-1);
			if(x == player.getPx() && y == player.getPy() && number > player.getNumber()) {//if there is human number and it is less than the computer number
				mazeTable[y][x] = number;//move the computer number to that place
				return 2;//and inform the caller that the game is over
			}
			else if(x == player.getPx() && y == player.getPy() && number <= player.getNumber()) {//human number is larger than the computer number
				player.collectNumber(Integer.parseInt(String.valueOf(number)));
				return 3;
			}
			else//space
				mazeTable[y][x] = number;
		}
		else if(direction == 3) {//left
			mazeTable[y][x] = ' ';
			setX(x-1);
			if(x== player.getPx() && y == player.getPy() && number > player.getNumber()){//if there is human number and it is less than the computer number
				mazeTable[y][x] = number;//move the computer number to that place
				return 2;//and inform the caller that the game is over
			}
			else if(x == player.getPx() && y == player.getPy() && number <= player.getNumber()){//human number is larger than the computer number
				player.collectNumber(Integer.parseInt(String.valueOf(number)));
				return 3;
			}
			else//space
				mazeTable[y][x] = number;
		}
		else if(direction == 4) {//down
			mazeTable[y][x] = ' ';
			setY(y+1);
			if(x == player.getPx() && y == player.getPy() && number > player.getNumber()) {//if there is human number and it is less than the computer number
				mazeTable[y][x] = number;//move the computer number to that place
				return 2;//and inform the caller that the game is over
			}
			else if(x == player.getPx() && y == player.getPy() && number <= player.getNumber()){//human number is larger than the computer number
				player.collectNumber(Integer.parseInt(String.valueOf(number)));
				return 3;
			}
			else//space
				mazeTable[y][x] = number;
		}
		return 0;
	}//end of followDirection
}
