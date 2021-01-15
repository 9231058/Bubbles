package bubbles.domain;

public enum Direction {
	UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

	public int x;
	public int y;

	public static int indexOf(Direction direction) {
		for (int i = 0; i < values().length; i++) {
			if (values()[i] == direction) {
				return i;
			}
		}
		return -1;
	}

	private Direction(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Direction reverse() {
		switch (this) {
		case DOWN:
			return UP;
		case UP:
			return DOWN;
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		}
		return null;
	}
}
