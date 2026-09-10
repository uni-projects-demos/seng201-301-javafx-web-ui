package registration.web;

import WoF.compat.SQLException;
import WoF.controller.VehicleController;
import java.time.LocalDate;

final class BrowserVehicleController extends VehicleController {
	boolean register(String p, String make, String model, LocalDate d, String a1, String a2, String type, String fuel)
			throws SQLException {
		return vehicleRegister(p, make, model, d, a1, a2, type, fuel);
	}
	void remove() throws SQLException {
		vehicleRemove();
	}
	boolean select(String plate) throws SQLException {
		if (!findVehicle(plate))
			return false;
		setVehicleHistory();
		return true;
	}
	boolean updateMakePublic(String v) throws SQLException {
		return updateMake(v);
	}
	boolean updateModelPublic(String v) throws SQLException {
		return updateModel(v);
	}
	boolean updateDatePublic(LocalDate v) throws SQLException {
		return updateManufactureDate(v);
	}
	boolean updateAddressOnePublic(String v) throws SQLException {
		return updateVehicleAddressOne(v);
	}
	boolean updateAddressTwoPublic(String v) throws SQLException {
		return updateVehicleAddressTwo(v);
	}
	boolean updateTypePublic(String v) {
		return updateType(v);
	}
	boolean updateFuelPublic(String v) {
		return updateFuel(v);
	}
	boolean validPlate(String v) {
		return isPlateValid(v);
	}
	boolean validName(String v) {
		return isValidName(v);
	}
	boolean validAddress(String v) {
		return isValidAddress(v);
	}
	boolean validDate(LocalDate v) {
		return isValidDate(v);
	}
}
