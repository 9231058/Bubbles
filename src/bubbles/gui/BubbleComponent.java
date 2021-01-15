package bubbles.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JComponent;

import bubbles.control.BubblesControl;
import bubbles.domain.Direction;

public class BubbleComponent extends JComponent {

	private static final long serialVersionUID = -6116975754590941854L;
	private Direction direction;
	private int speed;
	private Color color;
	private ColorHandler colorHandler;
	private long hashCode;

	public BubbleComponent() {
		int direction = (new Random()).nextInt(4);
		this.direction = Direction.values()[direction];
		this.speed = (new Random()).nextInt(5) + 1;
		this.color = new Color((new Random()).nextInt(256),
				(new Random()).nextInt(256), (new Random()).nextInt(256));
		this.colorHandler = new ColorHandler();
		this.hashCode = hashCode();

		setSize(new Dimension(50, 50));
		setPreferredSize(new Dimension(50, 50));
		addMouseListener(colorHandler);
	}

	public BubbleComponent(int speed, int red, int green, int blue,
			int direction) {
		this.direction = Direction.values()[direction];
		this.speed = speed;
		this.color = new Color(red, green, blue);
		this.colorHandler = new ColorHandler();
		this.hashCode = hashCode();

		setSize(new Dimension(50, 50));
		setPreferredSize(new Dimension(50, 50));
		addMouseListener(colorHandler);
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		Graphics2D graphics2d = (Graphics2D) graphics;
		graphics2d.setColor(color);
		graphics2d.fillOval(0, 0, 50, 50);
	}

	public Direction getDirection() {
		return direction;
	}

	public int getSpeed() {
		return speed;
	}

	public long getHashCode() {
		return hashCode;
	}

	public void reverse() {
		direction = direction.reverse();
	}

	private class ColorHandler extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent event) {
			if (event.getButton() == MouseEvent.BUTTON1) {
				color = new Color((new Random()).nextInt(256),
						(new Random()).nextInt(256),
						(new Random()).nextInt(256));
				repaint();
			} else if (event.getButton() == MouseEvent.BUTTON3) {
				BubblesControl.getInstance()
						.removeBubbles(BubbleComponent.this);
			}
		}
	}

	@Override
	public String toString() {
		return speed + " " + color.getRed() + " " + color.getGreen() + " "
				+ color.getBlue() + " " + Direction.indexOf(direction);
	}
}
