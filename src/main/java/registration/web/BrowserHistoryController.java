package registration.web;

import WoF.compat.SQLException;
import WoF.controller.HistoryController;
import java.time.LocalDate;

final class BrowserHistoryController extends HistoryController {
	void register(String vin, String odo, LocalDate reg, LocalDate exp, String status) throws SQLException {
		historyRegister(vin, odo, reg, exp, status);
	}
	boolean updateOdo(String v) throws SQLException {
		return updateOdometerReading(v);
	}
	boolean updateReg(LocalDate v) throws SQLException {
		return updateRegistrationDate(v);
	}
	boolean updateExpiry(LocalDate v, LocalDate reg) throws SQLException {
		return updateWofExpiry(v, reg);
	}
	boolean updateStatus(String v) throws SQLException {
		return updateWofStatus(v);
	}
	boolean hasHistoryPublic() throws SQLException {
		return hasHistory();
	}
	boolean validVin(String v) {
		return isValidVin(v);
	}
	boolean validOdo(String v) {
		return isOdometerReadingValid(v);
	}
	boolean validReg(LocalDate v) {
		return isRegistrationDateValid(v);
	}
	boolean validExpiry(LocalDate v, LocalDate reg) {
		return isWofDateValid(v, reg);
	}
	boolean validStatus(String v) {
		return isWofStatusValid(v);
	}
}
