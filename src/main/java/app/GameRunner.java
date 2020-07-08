package app;

import java.util.List;
import java.util.Objects;

import game.GameOfLifeState;
import game.Position;
import gui.Gui;

public class GameRunner implements Runnable {		
	
	private static final int SLEEP_TIME = 500;
	
	
	private final int boardSize;
	
	private final List<Position> seedColonies;
	

	/**
	 * @param boardSize The width and height of the game board.
	 * @param colonies The initial colony configuration. Must not be {@code null}.
	 */
	public GameRunner(int boardSize, List<Position> colonies) {
		Objects.nonNull(colonies);
		this.boardSize = boardSize;
		this.seedColonies = colonies;
	}

	/**
	 * Runs the game of life simulation with a GUI.
	 */
	public void run() {
		GameOfLifeState gameState = new GameOfLifeState(boardSize, boardSize, seedColonies);
		Gui gui = new Gui(boardSize, boardSize, seedColonies);
		while (true) {
			if (gui.isPaused()) {
				gui.lockBoard(false);
			}
			gui.waitStart();
			gui.lockBoard(true);
			if (gui.dirty()) {
				gameState = new GameOfLifeState(boardSize, boardSize, gui.getBoardState());
				
			}
			boolean hasChanged = gameState.update();
			gui.updateBoard(gameState.getColonies());
			gui.pause(!hasChanged);
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
