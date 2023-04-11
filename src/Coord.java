
public class Coord {
	private int x, y;
	private Coord preCoord;
	
	public Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}//Constructor 2
	public Coord(int x, int y, Coord preCoord) {
		this.x = x;
		this.y = y;
		this.preCoord = preCoord;
	}//Constructor 3

	///---Getters and Setters---/
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public Coord getPreCoord() {
		return preCoord;
	}

	public void setX(int x) {
		this.x = x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void setPreCoord(Coord preCoord) {
		this.preCoord = preCoord;
	}
	
	///---Other Methods---///
	public int findDirection(Coord nextMove) {
		if(x+1 == nextMove.getX() && y == nextMove.getY())//if nextMove is in the right side 
			return 1;
		if(x == nextMove.getX() && y-1 == nextMove.getY())//if nextMove is in the up side 
			return 2;
		if(x-1 == nextMove.getX() && y == nextMove.getY())//if nextMove is in the left side 
			return 3;
		if(x == nextMove.getX() && y+1 == nextMove.getY())//if nextMove is in the down side 
			return 4;
		return 0;//if they are not adjacent, then return 0
	}
	public boolean ısItsLastCoordinate(Coord toBeChecked) {
		if(preCoord.x == toBeChecked.x && preCoord.y == toBeChecked.y)
			return true;
		else
			return false;
	}
}
