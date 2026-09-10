package WoF.compat;

import java.util.Random;

public final class SecureRandom {
	private final Random random = new Random();
	public long nextLong() {
		return random.nextLong();
	}
}
