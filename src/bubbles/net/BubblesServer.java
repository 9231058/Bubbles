package bubbles.net;

import java.awt.Point;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import bubbles.control.BubblesControl;
import bubbles.gui.BubbleComponent;
import bubbles.net.domain.ControlCommand;

public class BubblesServer implements Runnable {

	private static BubblesServer bubblesServer;

	private DatagramSocket udpControl;
	private DatagramSocket udpData;
	private boolean haveAnswer;
	private int lastSize;
	private boolean haveMultiData;
	private InetAddress broadCastIP;

	public static BubblesServer getInstance() {
		if (bubblesServer == null) {
			try {
				bubblesServer = new BubblesServer();
			} catch (SocketException | UnknownHostException exception) {
				exception.printStackTrace();
			}
		}
		return bubblesServer;
	}

	private BubblesServer() throws SocketException, UnknownHostException {
		udpControl = new DatagramSocket(13731);
		udpData = new DatagramSocket(13732);
		broadCastIP = InetAddress.getByAddress(new byte[] { (byte) 192,
				(byte) 168, 1, (byte) 255 });
	}

	public void sendNewBubble(BubbleComponent bubbleComponent, int x, int y) {
		DatagramPacket packet;
		byte[] data;
		try {
			data = ControlCommand.DATA_SEND.getCommand().getBytes(
					StandardCharsets.US_ASCII);
			packet = new DatagramPacket(data, data.length, broadCastIP, 13731);
			udpControl.send(packet);
			data = new String(bubbleComponent.toString() + " " + x + " " + y)
					.getBytes(StandardCharsets.US_ASCII);
			packet = new DatagramPacket(data, data.length, broadCastIP, 13732);
			udpData.send(packet);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			byte[] data = ControlCommand.ADD.getCommand().getBytes(
					StandardCharsets.US_ASCII);
			DatagramPacket packet = new DatagramPacket(data, data.length,
					broadCastIP, 13731);
			udpControl.send(packet);
			System.err.println("SEND TO : " + packet.getAddress());
			while (true) {
				packet = new DatagramPacket(new byte[1024], 1024);
				udpControl.receive(packet);
				System.err.println("RECEIVE FROM : " + packet.getAddress());
				packet = answerHandler(packet);
				if (packet != null) {
					System.err.println("SEND TO : " + packet.getAddress());
					udpControl.send(packet);
				}
			}
		} catch (UnknownHostException exception) {
			exception.printStackTrace();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private DatagramPacket answerHandler(DatagramPacket packet) {
		byte[] data = packet.getData();
		InetAddress ip = packet.getAddress();
		if (ip.isSiteLocalAddress()) {
			return null;
		}
		ControlCommand command = ControlCommand.getControlCommad(new String(
				data, StandardCharsets.US_ASCII).trim());
		System.err.println(new String(data, StandardCharsets.US_ASCII).trim());
		if (command == null) {
			try {
				lastSize = Integer.parseInt(new String(data,
						StandardCharsets.US_ASCII).trim());
				if (haveMultiData && lastSize != 0) {
					receiveNewBubbles();
				}
			} catch (NumberFormatException exception) {
				exception.printStackTrace();
				return null;
			}
		}
		if (command == null) {
			return null;
		} else if (command.equals(ControlCommand.ACCEPT) && haveAnswer == false) {
			haveAnswer = true;
			data = ControlCommand.SEND_ME_MULTI_DATA.getCommand().getBytes(
					StandardCharsets.US_ASCII);
			return new DatagramPacket(data, data.length, ip, 13731);
		} else if (command.equals(ControlCommand.ADD)) {
			data = ControlCommand.ACCEPT.getCommand().getBytes(
					StandardCharsets.US_ASCII);
			return new DatagramPacket(data, data.length, ip, 13731);
		} else if (command.equals(ControlCommand.DATA_SEND)) {
			receiveNewBubble();
			return null;
		} else if (command.equals(ControlCommand.MULTI_DATA_SEND)) {
			haveMultiData = true;
			if (haveMultiData && lastSize != 0) {
				receiveNewBubbles();
			}
			return null;
		} else if (command.equals(ControlCommand.SEND_ME_MULTI_DATA)) {
			sendOldBubbles(ip);
			data = ControlCommand.MULTI_DATA_SEND.getCommand().getBytes(
					StandardCharsets.US_ASCII);
			return new DatagramPacket(data, data.length, ip, 13731);
		} else {
			return null;
		}
	}

	private void createBubble(String data) {
		try {
			String[] splitedData = data.split(" ");
			int speed = Integer.parseInt(splitedData[0]);
			int red = Integer.parseInt(splitedData[1]);
			int green = Integer.parseInt(splitedData[2]);
			int blue = Integer.parseInt(splitedData[3]);
			int direction = Integer.parseInt(splitedData[4]);
			int x = Integer.parseInt(splitedData[5]);
			int y = Integer.parseInt(splitedData[6]);
			BubbleComponent bubbleComponent = new BubbleComponent(speed, red,
					green, blue, direction);
			Point point = new Point(x, y);
			BubblesControl.getInstance().addBubbles(bubbleComponent, point);
		} catch (NumberFormatException exception) {
			exception.printStackTrace();
		}
	}

	private void sendOldBubble(BubbleComponent bubbleComponent, InetAddress ip) {
		DatagramPacket packet;
		byte[] data;
		int x = bubbleComponent.getX();
		int y = bubbleComponent.getY();
		try {
			data = new String(bubbleComponent.toString() + " " + x + " " + y)
					.getBytes(StandardCharsets.US_ASCII);
			packet = new DatagramPacket(data, data.length, ip, 13732);
			udpData.send(packet);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private void sendOldBubbles(InetAddress ip) {
		List<BubbleComponent> bubbleComponents = BubblesControl.getInstance()
				.getBubbleComponents();
		int size = bubbleComponents.size();
		DatagramPacket packet;
		byte[] data;
		try {
			data = String.valueOf(size).getBytes(StandardCharsets.US_ASCII);
			packet = new DatagramPacket(data, data.length, ip, 13731);
			udpControl.send(packet);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		for (int i = 0; i < size; i++) {
			sendOldBubble(bubbleComponents.get(i), ip);
		}
	}

	private void receiveNewBubble() {
		DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
		byte[] data;
		try {
			udpData.receive(packet);
			data = packet.getData();
			createBubble(new String(data, StandardCharsets.US_ASCII).trim());
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private void receiveNewBubbles() {
		int size = lastSize;
		lastSize = 0;
		haveMultiData = false;
		for (int i = 0; i < size; i++) {
			receiveNewBubble();
		}
	}

}
