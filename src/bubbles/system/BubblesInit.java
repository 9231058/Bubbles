package bubbles.system;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bubbles.control.BubblesControl;
import bubbles.gui.BubblesFrame;
import bubbles.net.BubblesServer;

public class BubblesInit implements Runnable {

	private static BubblesInit bubblesInit;

	private ExecutorService executorService;

	public static BubblesInit getInstance() {
		if (bubblesInit == null) {
			bubblesInit = new BubblesInit();
		}
		return bubblesInit;
	}

	private BubblesInit() {
		executorService = Executors.newCachedThreadPool();
	}

	@Override
	public void run() {
		BubblesFrame bubblesFrame = new BubblesFrame();
		bubblesFrame.setVisible(true);
		executorService.submit(BubblesControl.getInstance());
		executorService.submit(BubblesServer.getInstance());
		executorService.shutdown();
	}

}
