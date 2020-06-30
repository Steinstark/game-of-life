package gui;

import java.awt.Dimension;

import javax.swing.JPanel;

/**
 * A {@link JPanel} with fixed size. The min, preferred and max size will always
 * correspond to the values provided at initialization.
 * 
 * @author Henrik Josefsson 2020-06-30
 */
@SuppressWarnings("serial")
public class FixedCell extends JPanel {

	private final int width;
	private final int height;

	
	/**
	 * @param width The fixed width.
	 * @param height The fixed height.
	 */
	public FixedCell(int width, int height) {
		super();
		this.width = width;
		this.height = height;		
	}
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(width, height);
	}
	
	@Override
	public Dimension getMaximumSize() {
		return new Dimension(width, height);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}	
}
