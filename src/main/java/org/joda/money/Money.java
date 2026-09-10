package org.joda.money;

public final class Money {
	private final long cents;

	private Money(long cents) {
		this.cents = cents;
	}

	public static Money parse(String text) {
		if (text == null) {
			throw new IllegalArgumentException("Money text cannot be null");
		}
		String value = text.trim();
		if (value.startsWith("NZD ")) {
			value = value.substring(4).trim();
		}
		boolean negative = value.startsWith("-");
		if (negative) {
			value = value.substring(1);
		}
		var total = getTotal(value);
		return new Money(negative ? -total : total);
	}

	private static long getTotal(String value) {
		int dot = value.indexOf('.');
		long dollars;
		long fractional = 0;
		if (dot < 0) {
			dollars = Long.parseLong(value);
		} else {
			dollars = Long.parseLong(value.substring(0, dot));
			String fraction = value.substring(dot + 1);
			if (fraction.length() == 1) {
				fractional = Long.parseLong(fraction) * 10;
			} else if (fraction.length() >= 2) {
				fractional = Long.parseLong(fraction.substring(0, 2));
			}
		}
		return dollars * 100 + fractional;
	}

	public Money plus(long amount) {
		return new Money(cents + amount * 100);
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Money && ((Money) other).cents == cents;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(cents);
	}

	@Override
	public String toString() {
		long absolute = Math.abs(cents);
		long dollars = absolute / 100;
		long fraction = absolute % 100;
		return (cents < 0 ? "NZD -" : "NZD ") + dollars + "." + (fraction < 10 ? "0" : "") + fraction;
	}
}
