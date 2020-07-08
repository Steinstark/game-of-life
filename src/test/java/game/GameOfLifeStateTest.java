package game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class GameOfLifeStateTest {
	
	/**
	 * Verifies that a colony with no neighbors is dead after
	 * {@link GameOfLifeState#update()}.
	 * 
	 * #
	 */
	@Test
	public void zeroNeighborTest() {
		List<Position> colonies = new ArrayList<>(1);
		colonies.add(new Position(5, 5));
		GameOfLifeState state = new GameOfLifeState(11, 11, colonies);
		state.update();
		assertTrue(state.getColonies().isEmpty());
	}
	
	/**
	 * Verifies that a colony with exactly one neighbor is dead after
	 * {@link GameOfLifeState#update()}.
	 * 
	 * #
	 * #
	 */
	@Test
	public void oneNeighborTest() {
		List<Position> colonies = new ArrayList<>(2);
		colonies.add(new Position(5, 5));
		colonies.add(new Position(5, 6));
		GameOfLifeState state = new GameOfLifeState(11, 11, colonies);
		state.update();
		assertTrue(state.getColonies().isEmpty());
	}
	

	/**
	 * Verifies that a colony with exactly two neighbors is alive after
	 * {@link GameOfLifeState#update()}.
	 * 
	 *  #
	 * # #
	 *  #
	 */
	@Test
	public void twoNeighborTest() {
		List<Position> colonies = new ArrayList<>(4);
		colonies.add(new Position(5, 5));
		colonies.add(new Position(4, 6));
		colonies.add(new Position(6, 6));
		colonies.add(new Position(5, 7));
		GameOfLifeState state = new GameOfLifeState(11, 11, colonies);
		assertFalse(state.update());
		Set<Position> actualColonies = state.getColonies();
		assertTrue(actualColonies.size() == colonies.size());
		assertTrue(colonies.containsAll(actualColonies));
		
	}
	
	/**
	 * Verifies that a new colony is born in a square with exactly three neighboring
	 * colonies after {@link GameOfLifeState#update()}.
	 * 
	 * # #
	 *  #
	 */
	@Test
	public void threeNeighborTest() {
		List<Position> colonies = new ArrayList<>(3);
		colonies.add(new Position(4, 5));
		colonies.add(new Position(6, 5));
		colonies.add(new Position(5, 6));
		GameOfLifeState state = new GameOfLifeState(11, 11, colonies);
		state.update();
		Set<Position> actualColonies = state.getColonies();
		assertTrue(actualColonies.size() == 2);
		assertTrue(actualColonies.contains(new Position(5, 5)));
	}
	
	/**
	 * Verifies that a colony with neighbors > 3 is dead after
	 * {@link GameOfLifeState#update()}.
	 * 
	 *  #
	 * ###
	 *  # 
	 */
	@Test
	public void tooManyNeighborsTest() {
		List<Position> colonies = new ArrayList<>(5);
		colonies.add(new Position(5, 5));
		colonies.add(new Position(4, 6));
		colonies.add(new Position(5, 6));
		colonies.add(new Position(6, 6));
		colonies.add(new Position(5, 7));
		GameOfLifeState state = new GameOfLifeState(11, 11, colonies);
		state.update();
		Set<Position> actualColonies = state.getColonies();
		assertFalse(actualColonies.contains(new Position(5,6)));		
	}
	
	/**
	 * Verifies the expected behavior of {@link GameOfLifeState#update()} for a
	 * simple square shape.
	 * 
	 * ##
	 * ## 
	 */
	@Test
	void squareTest() {
		List<Position> colonies = new ArrayList<>(4);
		colonies.add(new Position(5, 5));
		colonies.add(new Position(6, 5));
		colonies.add(new Position(5, 6));
		colonies.add(new Position(6, 6));
		GameOfLifeState state = new GameOfLifeState(11, 11, colonies);
		for (int i = 0; i < 50; i++) {
			assertFalse(state.update());
		}
		Set<Position> actualColonies = state.getColonies();
		assertTrue(actualColonies.size() == colonies.size());
		assertTrue(colonies.containsAll(actualColonies));
	}

	/**
	 * Verifies the expected behavior of {@link GameOfLifeState#update()} for a
	 * small explorer shape.
	 * 
	 *  #
	 * ###
	 * # #
	 *  # 
	 */
	@Test
	void explorerTest() {
		List<Position> colonies = new ArrayList<>(7);
		colonies.add(new Position(25, 25));
		colonies.add(new Position(24, 26));
		colonies.add(new Position(25, 26));
		colonies.add(new Position(26, 26));
		colonies.add(new Position(24, 27));
		colonies.add(new Position(26, 27));
		colonies.add(new Position(26, 27));
		colonies.add(new Position(25, 28));
		GameOfLifeState state = new GameOfLifeState(50, 50, colonies);
		// Takes 16 generations for the state to stabilize.
		for (int i = 0; i < 16; i++) {
			assertTrue(state.update());
		}
		assertFalse(state.update());
	}
}
