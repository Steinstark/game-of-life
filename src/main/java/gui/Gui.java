package gui;

import java.awt.Color;
import java.awt.Component;
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
import java.util.concurrent.Semaphore;
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
	private final Semaphore cellLock = new Semaphore(1);
	private final JButton start = new JButton("Start");
	private final JButton stop = new JButton("Stop");

	
	private JFrame frame = new JFrame();

	
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
	public void updateBoard(Collection<Position> toUpdate) throws InterruptedException {
		Objects.requireNonNull(toUpdate);
		cellLock.acquire();
		updateAllCells(c -> c.setBackground(DEAD_CELL_COLOR));
		for (Position pos : toUpdate) {
			cells[pos.getX()][pos.getY()].setBackground(LIVING_CELL_COLOR);
		}		
		frame.getContentPane().repaint();	
		cellLock.release();
	}
	
	/**
	 * Adds a listener to the start button. Any time the start button is pressed the
	 * provided listener will be notified.
	 * 
	 * @param toRegister The action listener to register. Must not be {@code null}.
	 */
	public void registerStart(ActionListener toRegister) {
		Objects.requireNonNull(toRegister);
		start.addActionListener(toRegister);
	}
	
	/**
	 * Adds a listener to the stop button. Any time the stop button is pressed the
	 * provided listener will be notified.
	 * 
	 * @param toRegister The action listener to register. Must not be {@code null}.
	 */
	public void registerStop(ActionListener toRegister) {
		Objects.requireNonNull(toRegister);
		stop.addActionListener(toRegister);
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
	public List<Position> getBoardState() throws InterruptedException {
		cellLock.acquire();
		List<Position> colonies = new ArrayList<>();
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[0].length; j++) {
				if (cells[i][j].getBackground().equals(LIVING_CELL_COLOR)) {
					colonies.add(new Position(i, j));
				}
			}
		}
		cellLock.release();
		return colonies;
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
				updateAllCells(c -> c.setEnabled(false));
			}
		});
		stop.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				updateAllCells(c -> c.setEnabled(true));
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
						try {
							cellLock.acquire();
							Component c = e.getComponent();
							if (c != null && c.isEnabled()) {
								Color color = c.getBackground().equals(DEAD_CELL_COLOR) ? LIVING_CELL_COLOR : DEAD_CELL_COLOR;
								c.setBackground(color);
							}
						} catch (InterruptedException ex) {
							// no op.
						}		
						cellLock.release();
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
	
	private void updateAllCells(Consumer<FixedCell> consumer) {
		for(int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[0].length; j++) {
				consumer.accept(cells[i][j]);
			}
		}	
	}
}
