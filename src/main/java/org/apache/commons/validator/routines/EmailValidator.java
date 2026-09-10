package org.apache.commons.validator.routines;

public final class EmailValidator {
	private static final EmailValidator INSTANCE = new EmailValidator();
	private EmailValidator() {
	}
	public static EmailValidator getInstance(boolean allowLocal) {
		return INSTANCE;
	}
	public boolean isValid(String email) {
		return email != null && email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
	}
}
