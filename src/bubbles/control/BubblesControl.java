package bubbles.control;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JPanel;
import javax.swing.SpringLayout;

import bubbles.domain.Direction;
import bubbles.gui.BubbleComponent;

public class BubblesControl implements Runnable {

	private static BubblesControl bubblesControl;

	private ArrayList<BubbleComponent> bubbleComponents;
	private ArrayList<BubbleComponent> bubbleComponentsQueue;
	private ArrayList<Point> bubbleComponentsPointQueue;
	private Lock lock;
	private SpringLayout springLayout;
	private JPanel contentPane;

	public static BubblesControl getInstance() {
		if (bubblesControl == null) {
			bubblesControl = new BubblesControl();
		}
		return bubblesControl;
	}

	private BubblesControl() {
		bubbleComponents = new ArrayList<>();
		bubbleComponentsQueue = new ArrayList<>();
		bubbleComponentsPointQueue = new ArrayList<>();
		lock = new ReentrantLock();
	}

	public void setSpringLayout(SpringLayout springLayout) {
		this.springLayout = springLayout;
	}

	public void setContentPane(JPanel contentPane) {
		this.contentPane = contentPane;
	}

	public void addBubbles(BubbleComponent bubbleComponent, Point point) {
		bubbleComponentsQueue.add(bubbleComponent);
		bubbleComponentsPointQueue.add(point);
	}

	public void removeBubbles(BubbleComponent bubbleComponent) {
		bubbleComponents.remove(bubbleComponent);
		contentPane.remove(bubbleComponent);
	}

	public ArrayList<BubbleComponent> getBubbleComponents() {
		return bubbleComponents;
	}

	public Lock getLock() {
		return lock;
	}

	@Override
	public void run() {
		while (true) {
			if (lock.tryLock()) {
				springLayout = new SpringLayout();
				for (int i = 0; i < bubbleComponents.size(); i++) {
					moveBubble(bubbleComponents.get(i), i);
				}
				if (bubbleComponentsQueue.size() != 0) {
					getQueue();
				}
				contentPane.setLayout(springLayout);
				lock.unlock();
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException exception) {
				exception.printStackTrace();
			}
		}
	}

	private void moveBubble(BubbleComponent bubbleComponent, int index) {
		int speed = bubbleComponent.getSpeed();
		Direction direction = bubbleComponent.getDirection();
		int x = bubbleComponent.getX() + speed * direction.x;
		int y = bubbleComponent.getY() + speed * direction.y;
		if (x + bubbleComponent.getWidth() > contentPane.getWidth() || x < 0) {
			bubbleComponent.reverse();
			x = bubbleComponent.getX();
		}
		if (y + bubbleComponent.getHeight() > contentPane.getHeight() || y < 0) {
			bubbleComponent.reverse();
			y = bubbleComponent.getY();
		}
		for (int i = 0; i < index; i++) {
			if (haveCollision(x, y, bubbleComponents.get(i))) {
				bubbleComponent.reverse();
				x = bubbleComponent.getX();
				y = bubbleComponent.getY();
				bubbleComponents.get(i).reverse();
				springLayout.getConstraint(SpringLayout.WEST,
						bubbleComponents.get(i)).setValue(
						bubbleComponents.get(i).getX());
				springLayout.getConstraint(SpringLayout.NORTH,
						bubbleComponents.get(i)).setValue(
						bubbleComponents.get(i).getY());
				break;
			}
		}
		springLayout.putConstraint(SpringLayout.WEST, bubbleComponent, x,
				SpringLayout.WEST, contentPane);
		springLayout.putConstraint(SpringLayout.NORTH, bubbleComponent, y,
				SpringLayout.NORTH, contentPane);
	}

	private boolean haveCollision(int x1, int y1,
			BubbleComponent bubbleComponent) {
		int x2 = springLayout.getConstraint(SpringLayout.WEST, bubbleComponent)
				.getValue();
		int y2 = springLayout
				.getConstraint(SpringLayout.NORTH, bubbleComponent).getValue();
		int height = bubbleComponent.getHeight();
		int width = bubbleComponent.getWidth();
		if (Math.abs(x1 - x2) <= width) {
			if (Math.abs(y1 - y2) <= height) {
				return true;
			}
		}
		return false;
	}

	private void getQueue() {
		for (int i = 0; i < bubbleComponentsQueue.size(); i++) {
			springLayout.putConstraint(SpringLayout.NORTH,
					bubbleComponentsQueue.get(i),
					bubbleComponentsPointQueue.get(i).y, SpringLayout.NORTH,
					contentPane);
			springLayout.putConstraint(SpringLayout.WEST,
					bubbleComponentsQueue.get(i),
					bubbleComponentsPointQueue.get(i).x, SpringLayout.WEST,
					contentPane);
			contentPane.add(bubbleComponentsQueue.get(i));
			bubbleComponents.add(bubbleComponentsQueue.get(i));
			bubbleComponentsQueue.remove(i);
			bubbleComponentsPointQueue.remove(i);
		}
	}
}
