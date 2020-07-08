package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import game.Position;

/**
 * Simple graphical interface with a square board and a start and stop button to
 * be used for visualizing game of life iterations.
 * 
 * @author Henrik Josefsson 2020-06-30
 */
public class Gui {	
	
	/**
	 * The active cell color (square with a colony in it).
	 */
	private static final Color LIVING_CELL_COLOR = Color.YELLOW;
	
	/**
	 * The inactive cell color (square without a colony in it).
	 */
	private static final Color DEAD_CELL_COLOR = Color.LIGHT_GRAY;
	
	/**
	 * The cell border color.
	 */
	private static final Color BORDER_COLOR = Color.WHITE;
	
	/**
	 * The width and height of a single cell.
	 */
	private static final int CELL_SIZE = 15;
	
	
	private final FixedCell cells[][];	
	private final JButton start = new JButton("Start");
	private final JButton stop = new JButton("Stop");	
	private final JFrame frame = new JFrame();
	
	
	/**
	 * The resume/pause state. Whenever the stop button is pressed this will be set
	 * to true and false whenever the start button is pressed.
	 */
	private boolean isPaused = true;
	
	/**
	 * Whether the board is locked and doesn't respond to mouseclick.
	 */
	private boolean boardLocked = false;
	
	/**
	 * Whether any board cell has been manually edited.
	 */
	private boolean isDirty = false;

	
	/**
	 * Initializes the graphical interface.
	 * 
	 * @param cols            The number of game board cell columns.
	 * @param rows            The number of game board cell rows.
	 * @param initialColonies List of initial seeding colonies. May be empty but
	 *                        must not be {@code null}.
	 */
	public Gui(int cols, int rows, List<Position> initialColonies) {
		Objects.requireNonNull(initialColonies);
		initFrame();		
		cells = new FixedCell[rows][cols];
		JPanel board = initBoard();
		JPanel menu = initMenu();		
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.gridy = 0;		
		constraint.gridx = 0;
		constraint.gridy = 0;
		constraint.gridheight = 1;
		constraint.gridwidth = 1;
		frame.getContentPane().add(board, constraint);		
		constraint.gridy = 1;
		frame.getContentPane().add(menu, constraint);	
		try {
			updateBoard(initialColonies);
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize board", e);
		}
		frame.setVisible(true);		
	}
	
	/**
	 * Updates the game board with the provided list of colony positions. All
	 * colonies not on the list will be marked as dead.
	 * 
	 * @param toUpdate The list of colony positions to mark on the game board. May
	 *                 be empty, but must not be {@code null}.
	 */
	public void updateBoard(Collection<Position> toUpdate) {		
		EventQueue.invokeLater(new Runnable() {			
			@Override
			public void run() {
				update(toUpdate);
			}
		});
	}
	
	/**
	 * Pauses or resumes the game. The game board can only be edited while the game is paused.
	 * 
	 * @param pauseFlag Whether to pause or resume the game.
	 */
	public synchronized void pause(boolean pauseFlag) {
		isPaused = pauseFlag;
		this.notifyAll();
	}
	
	/**
	 * @return {@code true} if stop was the last button to be pressed in the interface.
	 */
	public synchronized boolean isPaused() {
		return isPaused;
	}
	
	/**
	 * Locks/unlocks the board for manual edit.
	 * 
	 * @param lockFlag Whether to lock the board.
	 */
	public synchronized void lockBoard(boolean lockFlag) {
		boardLocked = lockFlag;
	}
	
	/**
	 * @return Whether the board has been manually edited since last time
	 *         {@link #getBoardState()} was called.
	 */
	public synchronized boolean dirty() {
		return isDirty;
	}
	
	/**
	 * Gets the number of game board cell columns.
	 * 
	 * @return The cell board column count.
	 */
	public int getBoardWidth() {
		return cells.length;
	}
	
	/**
	 * Gets the number of game board cell rows.
	 * 
	 * @return The cell board row count.
	 */
	public int getBoardHeight() {
		return cells[0].length;
	}
	
	/**
	 * Gets the position of currently active colonies on the board.
	 * 
	 * @return The list of active colony positions. Never {@code null}.
	 * @throws InterruptedException 
	 */
	public synchronized List<Position> getBoardState() {
		List<Position> colonies = new ArrayList<>();
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[0].length; j++) {
				if (cells[i][j].getBackground().equals(LIVING_CELL_COLOR)) {
					colonies.add(new Position(i, j));
				}
			}
		}
		isDirty = false;
		return colonies;
	}

	/**
	 * Puases execution until the game is unpaused. The game is unpaused by pressing
	 * the start button in the interface and paused by pressing the stop button.
	 */
	public synchronized void waitStart() {
		while (isPaused) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// No op.
			}
		}
	}		

	private void initFrame() {			
		frame.setBounds(100, 100, 900, 900);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		frame.getContentPane().setLayout(new GridBagLayout());
	}
	
	private JPanel initMenu() {
		JPanel menu = new JPanel();
		
		menu.add(start);
		menu.add(stop);	
		start.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				pause(false);
			}
		});
		stop.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				pause(true);
			}
		});
		return menu;
	}
	
	private JPanel initBoard() {		
		JPanel board  = new JPanel();
		board.setLayout(new GridBagLayout());
		GridBagConstraints constraint = new GridBagConstraints();
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[0].length; j++) {
				cells[i][j] = new FixedCell(CELL_SIZE, CELL_SIZE);
				cells[i][j].setBackground(DEAD_CELL_COLOR);
				cells[i][j].setBorder(new LineBorder(BORDER_COLOR));
				cells[i][j].addMouseListener(new MouseListener() {
					
					@Override
					public void mouseReleased(MouseEvent e) {
						// no op.
					}
					
					@Override
					public void mousePressed(MouseEvent e) {
						// no op.
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
						// no op.
						
					}
					
					@Override
					public void mouseEntered(MouseEvent e) {
						// no op.
					}
					
					@Override
					public void mouseClicked(MouseEvent e) {
						Component c = e.getComponent();
						setManualCellColor(c);
					}
				});
				constraint.gridx = i;
				constraint.gridy = j;
				constraint.weightx = 0;
				constraint.weighty = 0;
				board.add(cells[i][j], constraint);
			}			
		}	
		return board;
	}
	
	private synchronized void update(Collection<Position> colonies) {
		Objects.requireNonNull(colonies);
		updateAllCells(c -> c.setBackground(DEAD_CELL_COLOR));
		for (Position pos : colonies) {
			cells[pos.getX()][pos.getY()].setBackground(LIVING_CELL_COLOR);
		}		
		frame.getContentPane().repaint();	
	}
	
	private void updateAllCells(Consumer<FixedCell> consumer) {
		for(int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[0].length; j++) {
				consumer.accept(cells[i][j]);
			}
		}	
	}
	
	private synchronized void setManualCellColor(Component c) {
		if (c == null || boardLocked) {
			return;
		}
		Color color = c.getBackground().equals(DEAD_CELL_COLOR) ?
				LIVING_CELL_COLOR : DEAD_CELL_COLOR;
		c.setBackground(color);
		isDirty = true;
	}
}
