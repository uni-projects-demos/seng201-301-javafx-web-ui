package WoF.compat;

public class SQLException extends Exception {
	public SQLException() {
	}
	public SQLException(String message) {
		super(message);
	}
	public SQLException(String message, Throwable cause) {
		super(message, cause);
	}
}
