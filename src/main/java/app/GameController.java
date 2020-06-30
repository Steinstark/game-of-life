package app;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

import game.GameOfLifeState;
import gui.Gui;

/**
 * Mediator class between the graphical interface and game of life simulation.
 * 
 * @author Henrik Josefsson 2020-06-30
 */
public class GameController implements Runnable {
	
	/**
	 * Milliseconds to sleep between each generation
	 */
	private static final int generationDelay = 500;
	
	
	private Semaphore lock;
	private GameOfLifeState state = null;
	private Gui gui;
	
	
	/**
	 * @param gui The graphical interface to display simulation results in.
	 */
	public GameController(Gui gui) {
		this.gui = gui;
		this.lock = new Semaphore(1);
		ActionListener startAction = new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					lock.acquire();
					state = new GameOfLifeState(gui.getBoardWidth(), gui.getBoardHeight(), gui.getBoardState());
					lock.release();
					synchronized (lock) {
						lock.notifyAll();
					}					
				} catch (InterruptedException e1) {
					return;
				}
			}
		};
		ActionListener stopAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					lock.acquire();
					state = null;
					lock.release();
					synchronized(lock) {
						lock.notifyAll();
					}
				} catch (InterruptedException e1) {
					return;
				}
			}
		};
		gui.registerStart(startAction);
		gui.registerStop(stopAction);		
	}

	@Override
	public void run() {
		while (true) {
			try {				
				boolean hasFinished = false;
				lock.acquire();
				if (state == null || hasFinished) {
					lock.release();
					synchronized(lock) {
						lock.wait();
					}
				} else {				
					hasFinished = !state.update();
					EventQueue.invokeLater(new Runnable() {				
						@Override
						public void run() {
							try {
								gui.updateBoard(state.getColonies());
							} catch (InterruptedException e) {
								throw new RuntimeException("Failed to update board.", e);
							}					
						}
					});
					lock.release();
					Thread.sleep(generationDelay);
				}								
			} catch (InterruptedException e) {
				// no op.
			}
		}
	}
}
