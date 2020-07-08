package app;

import java.util.ArrayList;
import java.util.List;

import game.Position;

/**
 * Launches a game of life simulation.
 * 
 * @author Henrik Josefsson 2020-06-30
 */
public class App {
	
	private static final int BOARD_SIZE = 50;
	
	/**
	 * Starts a game of life simulation with a small explorer colony configuration.
	 * The user can change the initial configuration by clicking cells in the
	 * graphical interface. The simulation starts when start is pressed and stops
	 * when stop is pressed.
	 * 
	 * @param args No arguments. Any provided arguments will be ignored.
	 */
	public static void main(String[] args) {
		List<Position> colonies = new ArrayList<>(7);
		colonies.add(new Position(25, 25));
		colonies.add(new Position(24, 26));
		colonies.add(new Position(25, 26));
		colonies.add(new Position(26, 26));
		colonies.add(new Position(24, 27));
		colonies.add(new Position(26, 27));
		colonies.add(new Position(25, 28));		
		new GameRunner(BOARD_SIZE, colonies).run();
	}		
}
