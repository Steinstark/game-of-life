package game;

import com.google.common.collect.ComparisonChain;

/**
 * A 2-d coordinate.
 * 
 * @author Henrik Josefsson 2020-06-29
 */
public class Position implements Comparable<Position>{
	
	private final int x;
	private final int y;
	
	
	/**
	 * @param x The x-coordinate.
	 * @param y The y-coordinate.
	 */
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return The x-coordinate.
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * @return The y-coordinate.
	 */
	public int getY() {
		return y;
	}
	
	public int compareTo(Position o) {
		return ComparisonChain.start()
				.compare(x, o.x)
				.compare(y,  o.y).result();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}		
}
