package bubbles.net.domain;

public enum ControlCommand {
	ADD("ADD"), ACCEPT("ACCEPT"), SEND_ME_MULTI_DATA("SEND ME MULTI DATA"), MULTI_DATA_SEND(
			"MULTI DATA SEND"), DATA_SEND("DATA SEND");

	private String command;

	private ControlCommand(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}

	public static ControlCommand getControlCommad(String command) {
		for (int i = 0; i < values().length; i++) {
			if (values()[i].getCommand().equals(command)) {
				return values()[i];
			}
		}
		return null;
	}
}
