package bubbles.gui;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import bubbles.control.BubblesControl;
import bubbles.net.BubblesServer;

public class BubblesFrame extends JFrame {

	private static final long serialVersionUID = -6925576918004653748L;

	private JPanel contentPane;
	private SpringLayout sl_contentPane;
	private BubbleHandler bubbleHandler;
	private ExitHandler exitHandler;

	public BubblesFrame() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setUndecorated(true);
		setBackground(new Color(0, 0, 0, 0));
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 0, 0, 10));
		setContentPane(contentPane);
		sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		BubblesControl.getInstance().setSpringLayout(sl_contentPane);
		BubblesControl.getInstance().setContentPane(contentPane);

		bubbleHandler = new BubbleHandler();
		exitHandler = new ExitHandler();
		new RefreshHandler();

		contentPane.addMouseListener(bubbleHandler);
		addKeyListener(exitHandler);
	}

	private class BubbleHandler extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent event) {
			BubblesControl.getInstance().addBubbles(new BubbleComponent(),
					event.getPoint());
			BubblesServer.getInstance().sendNewBubble(new BubbleComponent(),
					event.getPoint().x, event.getPoint().y);
		}

	}

	private class ExitHandler extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.VK_Q && event.isControlDown()) {
				System.exit(0);
			}
		}
	}

	private class RefreshHandler implements ActionListener {

		private Timer timer;

		public RefreshHandler() {
			timer = new Timer(10, this);
			timer.start();
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			if (BubblesControl.getInstance().getLock().tryLock()) {
				contentPane.revalidate();
				contentPane.repaint();
				SwingUtilities.updateComponentTreeUI(contentPane);
				BubblesControl.getInstance().getLock().unlock();
			}
			timer.restart();
		}
	}

}
