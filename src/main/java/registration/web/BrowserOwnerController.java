package registration.web;

import WoF.compat.SQLException;
import WoF.controller.OwnerController;

final class BrowserOwnerController extends OwnerController {
	boolean login(String email, String password) throws SQLException {
		return userLogin(email, password);
	}
	boolean register(String forename, String surname, String email, String password) throws SQLException {
		return userRegister(forename, surname, email, password);
	}
	boolean updateForenamePublic(String v) throws SQLException {
		return updateForename(v);
	}
	boolean updateSurnamePublic(String v) throws SQLException {
		return updateSurname(v);
	}
	boolean updateAddressOnePublic(String v) throws SQLException {
		return updateAddressOne(v);
	}
	boolean updateAddressTwoPublic(String v) throws SQLException {
		return updateAddressTwo(v);
	}
	boolean updatePhonePublic(String v) throws SQLException {
		return updatePhone(v);
	}
	boolean updatePasswordPublic(String v) throws SQLException {
		return updatePassword(v);
	}
	boolean validEmail(String v) {
		return isValidEmail(v);
	}
	boolean validPassword(String v) {
		return isValidPassword(v);
	}
	boolean validName(String v) {
		return isValidName(v);
	}
	boolean validAddress(String v) {
		return isValidAddress(v);
	}
}
