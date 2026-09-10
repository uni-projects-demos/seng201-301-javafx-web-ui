package WoF.compat;

import java.time.LocalDateTime;

public final class Timestamp {
	private final LocalDateTime value;
	private Timestamp(LocalDateTime value) {
		this.value = value;
	}
	public static Timestamp valueOf(LocalDateTime value) {
		return new Timestamp(value);
	}
	public LocalDateTime toLocalDateTime() {
		return value;
	}
	@Override
	public String toString() {
		return value.toString();
	}
}
