package game;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

/**
 * Game of life game state. This class stores the living colonies for the
 * current generation. To advance to next generation see
 * {@link GameOfLifeState#update()}.
 * 
 * @author Henrik Josefsson 2020-06-29
 */
public class GameOfLifeState {
			
	/**
	 * Number of neighbors needed for an already present colony to survive.
	 */
	private static final int SURVIVE_COUNT = 2;
	
	/**
	 * Number of neighbors needed for a new colony to be born.
	 */
	private static final int BORN_COUNT = 3;
	
	/**
	 * Maximum legal board width.
	 */
	public static final int MAX_WIDTH = 2048;
	
	/**
	 * Maximum legal board height.
	 */
	public static final int MAX_HEIGHT = 2048;	

	
	private final int width;
	private final int height;
	
	
	private Set<Position> colonies = new TreeSet<>();
	private int generation = 0;
	

	/**
	 * Initializes the game of life game state.
	 * 
	 * @param width    The board width. Must be positive and less than
	 *                 {@value #MAX_WIDTH}.
	 * @param height   The board height. Must be positive and less than
	 *                 {@value #MAX_HEIGHT}.
	 * @param colonies The initial colony positions. May be empty, but must not be
	 *                 {@code null}.
	 */
	public GameOfLifeState(int width, int height, Collection<Position> colonies) {
		rangeCheck(width, 1, MAX_WIDTH, "width");
		rangeCheck(height, 1, MAX_HEIGHT, "height");
		Objects.requireNonNull(colonies);
		this.width = width;
		this.height = height;
		// Make a deep copy.
		for (Position pos : colonies) {
			this.colonies.add(new Position(pos.getX(), pos.getY()));
		}
	}
		
	/**
	 * Performs a game of life generation update. During the generation update
	 * colonies with too few or too many neighbors are eliminated and new colonies
	 * are born in tiles with the correct number of neighbors.
	 * 
	 * @return Whether at least one colony was removed or added during the update.
	 *         It is not meaningful to perform any further updates if false since
	 *         the game state will never change after that.
	 */
	public boolean update() {
		Set<Position> newColonies = getNewColonies();
		boolean isSame = colonies.containsAll(newColonies) && colonies.size() == newColonies.size();
		colonies = newColonies;
		generation++;
		return !isSame;
	}
	
	/**
	 * @return The current game of life colonies. Never {@code null}.
	 */
	public ImmutableSet<Position> getColonies() {
		return ImmutableSet.copyOf(colonies);
	}
	
	/**
	 * @return The current game generation.
	 */
	public int getGeneration() {
		return generation;
	}
	
	
	private Set<Position> getNewColonies() {
		Map<Position, Integer> neighbourCount = getNeighbourCount();
		return neighbourCount.entrySet().stream()
				.filter(e -> insideBoard(e.getKey()) && isAlive(e.getKey(), e.getValue()))
				.map(e -> e.getKey())
				.collect(Collectors.toSet());
	}
	
	private Map<Position, Integer> getNeighbourCount() {
		Map<Position, Integer> possibleColonies = new TreeMap<>();
		for (Position pos : colonies) {
			// Add one neighbor to all tiles in a 3x3 area centered around pos.
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					possibleColonies.compute(
							new Position(pos.getX() + i, pos.getY() + j),
							(p, val) -> val == null ? 1 : val + 1);
				}
			}
			// Subtract one neighbor from self. Can't neighbor yourself.
			possibleColonies.put(pos, possibleColonies.get(pos) - 1);
		}
		return possibleColonies;
	}
	
	private boolean isAlive(Position pos, int neighbours) {
		return SURVIVE_COUNT == neighbours && colonies.contains(pos) || BORN_COUNT == neighbours;
	}
	
	private boolean insideBoard(Position pos) {
		return inRange(pos.getX(), 0, width) && inRange(pos.getY(), 0, height);		
	}
	
	
	private static void rangeCheck(int val, int min, int max, String name) {
		if (!inRange(val, min, max + 1)) {
			String message = String.format("The %s with value %d is outside the legal range [%d..%d]", name, val, min, max);
			throw new IllegalArgumentException(message);
		}
	}
	
	private static boolean inRange(int val, int min, int max) {
		return min <= val && val < max;
	}
}
