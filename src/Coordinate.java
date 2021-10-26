/**
 * 
 * @author Denizhan This Class is an object form simulator for Coordinates.
 *
 */
public class Coordinate {
	int x;
	int y;

	Coordinate(int x, int y) {
		this.x = x;
		this.y = y;

	}

	int getX() {
		return x;

	}

	int getY() {
		return y;
	}

	void setX(int x) {
		this.x = x;
	}

	void setY(int y) {
		this.y = y;
	}

	public String toString() {
		return "" + x + "," + y;
	}

}
