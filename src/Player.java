import java.util.Random;

public class Player {
	public char number;
	private char[][] mazeTable;
	private int px, py;
	private Stack leftBackpack, rightBackpack;
	private int score;
	///---Constructor---///
	public Player(char[][] mazeTable) throws Exception {

		this.mazeTable = mazeTable;//maze table's reference is kept as attribute to make processes easily
		number = '5';

		Random random = new Random();
		do {//random coordinate for the number is created
			py=random.nextInt(23);
			px=random.nextInt(55);
			if(mazeTable[py][px]==' ') {//if the randomly generated coordinate is empty
				this.mazeTable[py][px] = number;//then put the number there 
				break;
			}
		}while(true);
		
		leftBackpack = new Stack(8);
		rightBackpack = new Stack(8);
		score = 0;
		
	}
	///---Getters and Setters---///
	public int getPx() {return px;}
	public int getPy() { return py;}
	public int getScore() {	return score;	}
	public Stack getLeftBackpack() {	return leftBackpack;	}
	public Stack getRightBackpack() {	return rightBackpack;	}
	public char getNumber() {	return number;	}
	public void setNumber(char num) {
		this.number = num;
		mazeTable[py][px] = number;
		
	}
	///---Other Methods---///
	public int Input(char rckey) {
		if(rckey == 'Q' || rckey == 'q')
			transferItem(false);
		if(rckey == 'W' || rckey == 'w')
			transferItem(true);
		
		boolean moved = false;
		
		if(number != '1') {//if human number is not 1, it can move
			if (rckey=='%' && mazeTable[py][px-1] != '#') {
				mazeTable[py][px] = ' ';
				px--;
				moved = true;
			}//left
			if(rckey=='\'' && mazeTable[py][px+1] != '#') {
				mazeTable[py][px] = ' ';
				px++;
				moved = true;
			}//right
			if(rckey=='&' && mazeTable[py-1][px] != '#') {
				mazeTable[py][px] = ' ';
				py--;
				moved = true;
			}//up
			if(rckey=='(' && mazeTable[py+1][px] != '#') {
				mazeTable[py][px] = ' ';
				py++;
				moved = true;
			}//down
		}
		else return 1;//if human number is 1, it cannot move anywhere
		
		if(moved && mazeTable[py][px] != ' ') {//if human number can be moved
			if(mazeTable[py][px] <= number ) {//and computer number there is less than the human number
				collectNumber(Integer.parseInt(String.valueOf(mazeTable[py][px])));//then collect the number
				mazeTable[py][px] = number;
				return 3;//return that the number collected a number
			}
			else//and computer number there is larger than the human number
				return 2;//then return that the human number died and game is over
		}

		mazeTable[py][px] = number;
		if(moved) return 6;
		return 0; 
	}

	public void collectNumber(int number) {
		if(leftBackpack.isFull()) {//if left backpack is full
			leftBackpack.pop();//remove the top element
			leftBackpack.push(number);//then insert the newly collected number
		}
		else
			leftBackpack.push(number);
	}
	
	public void transferItem(boolean direction) {
		//direction true means left --> right
		//direction true means right --> left
		if(direction) {
			if(leftBackpack.isEmpty() || rightBackpack.isFull())
				return;
			rightBackpack.push(leftBackpack.pop());
		}
		else {
			if(rightBackpack.isEmpty() || leftBackpack.isFull())
				return;
			leftBackpack.push(rightBackpack.pop());
		}
	}
	public boolean checkMatches() {
		boolean matched = false;
		Stack tempStack1 = new Stack(leftBackpack.size());
		Stack tempStack2 = new Stack(rightBackpack.size());//temporary stacks to store the numbers in backpacks
		while(!leftBackpack.isEmpty())//all numbers in left backpack are moved to the temporary stack
			tempStack1.push(leftBackpack.pop());
		while(!rightBackpack.isEmpty())//all numbers in right backpack are moved to the temporary stack
			tempStack2.push(rightBackpack.pop());
		while(!tempStack1.isEmpty() && !tempStack2.isEmpty()) {//while both of the temporary stacks are not empty
			if(tempStack1.peek().equals(tempStack2.peek())) {//if matching occurred
				calculateScore((int)tempStack1.pop());//calculate score and pop that number in both stacks
				tempStack2.pop();
				matched = true;
			}
			else {//if matching does not occur for that level
				leftBackpack.push(tempStack1.pop());
				rightBackpack.push(tempStack2.pop());//move back the numbers in that level
			}
		}
		while(!tempStack1.isEmpty())//if tempStack2 got empty before tempStack1, there is at least one remaining number to move back
			leftBackpack.push(tempStack1.pop());
		while(!tempStack2.isEmpty())//if tempStack1 got empty before tempStack2, there is at least one remaining number to move back
			rightBackpack.push(tempStack2.pop());
		
		return matched;//return the caller whether matching occurred or not
	}
	public void calculateScore(int number) {
		if(number == 1 || number == 2 || number ==3)
			score += number;
		else if(number == 4 || number == 5 || number ==6)
			score += number * 5;
		else
			score += number * 25;
	}
}

