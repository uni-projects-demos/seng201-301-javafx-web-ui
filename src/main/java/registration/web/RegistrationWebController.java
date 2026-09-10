package registration.web;

import WoF.compat.FileNotFoundException;
import WoF.compat.SQLException;
import WoF.model.History;
import WoF.model.Owner;
import WoF.model.Session;
import WoF.model.vehicle.Vehicle;
import WoF.model.vehicle.VehicleEnum;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Browser adapter for vehicle rego app controller
 */
public final class RegistrationWebController {
	private final Session wof = Session.getWoF();
	private final BrowserOwnerController owners = new BrowserOwnerController();
	private final BrowserVehicleController vehicles = new BrowserVehicleController();
	private final BrowserHistoryController histories = new BrowserHistoryController();

	public void init() {
		try {
			if (wof.getDatabase() == null)
				wof.startSession();
		} catch (SQLException e) {
			throw new IllegalStateException(e.getMessage());
		}
	}

	public boolean isRegistered(String plate) {
		return wof.getDatabase().isRegisteredVehicle(plate);
	}
	public String vehicleField(String plate, String field) {
		return wof.getDatabase().vehicleField(plate, field);
	}
	public int ownerCount() {
		return wof.getDatabase().ownerCount();
	}
	public int vehicleCount() {
		return wof.getDatabase().vehicleCount();
	}

	public String currentUserEmail() {
		return wof.getUser() == null ? "" : wof.getUser().getEmail();
	}

	public boolean login(String email, String password) {
		try {
			return owners.login(email, password);
		} catch (SQLException e) {
			return false;
		}
	}

	public void logout() {
		try {
			if (wof.getUser() != null)
				wof.logOut();
		} catch (SQLException ignored) {
		}
	}

	public void reset() {
		try {
			wof.setUser(null);
			wof.setVehicle(null);
			wof.getDatabase().databaseSetup();
		} catch (FileNotFoundException | SQLException ignored) {
		}
	}

	public int registerOwner(String forename, String surname, String email, String password) {
		try {
			boolean ok = owners.register(forename, surname, email, password);
			if (!ok)
				wof.setUser(null);
			return ok ? 1 : 0;
		} catch (SQLException e) {
			wof.setUser(null);
			return 0;
		}
	}

	public String ownerField(String email, String field) {
		Owner owner = wof.getUser();
		if (owner == null || !owner.getEmail().equalsIgnoreCase(email))
			return "";
		return switch (field) {
			case "forename" -> owner.getForename();
			case "surname" -> owner.getSurname();
			case "email" -> owner.getEmail();
			case "password" -> owner.getPassword();
			case "addressOne" -> value(owner.getAddressLineOne());
			case "addressTwo" -> value(owner.getAddressLineTwo());
			case "phone" -> value(owner.getPhone());
			default -> "";
		};
	}

	public boolean updateOwner(String email, String forename, String surname, String addressOne, String addressTwo,
			String phone, String password) {
		if (wof.getUser() == null || !wof.getUser().getEmail().equalsIgnoreCase(email))
			return false;
		try {
			return owners.updateForenamePublic(forename) && owners.updateSurnamePublic(surname)
					&& (addressOne.isEmpty() || owners.updateAddressOnePublic(addressOne))
					&& (addressTwo.isEmpty() || owners.updateAddressTwoPublic(addressTwo))
					&& (phone.isEmpty() || owners.updatePhonePublic(phone)) && owners.updatePasswordPublic(password);
		} catch (SQLException e) {
			return false;
		}
	}

	public boolean deleteOwner(String email) {
		if (wof.getUser() == null || !wof.getUser().getEmail().equalsIgnoreCase(email))
			return false;
		try {
			wof.ownerDelete();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public String vehicleListHtml(String email, String selectedPlate) {
		if (wof.getUser() == null || !wof.getUser().getEmail().equalsIgnoreCase(email))
			return "";
		List<Vehicle> list = new ArrayList<>(wof.getUser().getVehicles());
		list.sort(Comparator.comparing(Vehicle::getPlate));
		if (list.isEmpty())
			return "<div class=\"empty-state compact-empty\">No vehicles registered to this account.</div>";
		StringBuilder html = new StringBuilder();
		for (Vehicle v : list) {
			boolean active = v.getPlate().equalsIgnoreCase(selectedPlate);
			html.append("<button type=\"button\" class=\"vehicle-list-item").append(active ? " active" : "")
					.append("\" data-plate=\"").append(escape(v.getPlate())).append("\"><span class=\"vehicle-plate\">")
					.append(escape(v.getPlate())).append("</span><span class=\"vehicle-name\">")
					.append(escape(v.getMake())).append(' ').append(escape(v.getModel()))
					.append("</span><span class=\"vehicle-meta\">").append(v.getVehicleType().getValue()).append(" · ")
					.append(v.getFuelType().getValue()).append("</span></button>");
		}
		return html.toString();
	}

	public int addVehicle(String ownerEmail, String plate, String make, String model, String manufactureDate,
			String addressOne, String addressTwo, String vehicleType, String fuelType) {
		if (wof.getUser() == null || !wof.getUser().getEmail().equalsIgnoreCase(ownerEmail))
			return 0;
		try {
			return vehicles.register(plate, make, model, LocalDate.parse(manufactureDate), addressOne, addressTwo,
					vehicleType, fuelType) ? 1 : 0;
		} catch (Exception e) {
			return 0;
		}
	}

	public boolean updateVehicle(String plate, String make, String model, String manufactureDate, String addressOne,
			String addressTwo, String vehicleType, String fuelType) {
		try {
			if (!vehicles.select(plate))
				return false;
			boolean ok = vehicles.updateMakePublic(make) && vehicles.updateModelPublic(model)
					&& vehicles.updateDatePublic(LocalDate.parse(manufactureDate))
					&& vehicles.updateAddressOnePublic(addressOne) && vehicles.updateAddressTwoPublic(addressTwo)
					&& vehicles.updateTypePublic(vehicleType);
			if (VehicleEnum.T.getValue().equals(vehicleType))
				fuelType = "NA";
			return ok && vehicles.updateFuelPublic(fuelType);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean deleteVehicle(String ownerEmail, String plate) {
		if (wof.getUser() == null || !wof.getUser().getEmail().equalsIgnoreCase(ownerEmail))
			return false;
		try {
			if (!vehicles.select(plate))
				return false;
			vehicles.remove();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public boolean hasHistory(String plate) {
		try {
			return vehicles.select(plate) && wof.getVehicle().getHistory() != null;
		} catch (SQLException e) {
			return false;
		}
	}

	public String historyField(String plate, String field) {
		try {
			if (!vehicles.select(plate))
				return "";
		} catch (SQLException e) {
			return "";
		}
		History h = wof.getVehicle().getHistory();
		if (h == null)
			return "";
		return switch (field) {
			case "vin" -> h.getVin();
			case "odometer" -> value(h.getOdometerReading());
			case "registrationDate" -> h.getRegistrationDateNZ().toLocalDateTime().toLocalDate().toString();
			case "wofExpiry" -> h.getWofExpiry().toLocalDateTime().toLocalDate().toString();
			case "wofStatus" -> h.getWofStatus().getValue();
			default -> "";
		};
	}

	public int saveHistory(String plate, String vin, String odometer, String registrationDate, String expiry,
			String status) {
		try {
			if (!vehicles.select(plate))
				return 0;
			History h = wof.getVehicle().getHistory();
			LocalDate reg = LocalDate.parse(registrationDate);
			LocalDate exp = LocalDate.parse(expiry);
			if (h == null) {
				if (wof.getDatabase().vinInHistory(vin))
					return -1;
				histories.register(vin, odometer, reg, exp, status);
			} else {
				if (!h.getVin().equalsIgnoreCase(vin))
					return -1;
				if (!wof.getVehicle().getVehicleType().equals(VehicleEnum.T) && !histories.updateOdo(odometer))
					return 0;
				if (!histories.updateReg(reg))
					return 0;
				if (!histories.updateExpiry(exp, reg))
					return 0;
				if (!histories.updateStatus(status))
					return 0;
			}
			return 1;
		} catch (Exception e) {
			return 0;
		}
	}

	// Verification
	public boolean validEmail(String v) {
		return owners.validEmail(v);
	}
	public boolean validPassword(String v) {
		return owners.validPassword(v);
	}
	public boolean validName(String v) {
		return owners.validName(v);
	}
	public boolean validAddress(String v) {
		return owners.validAddress(v);
	}
	public boolean validPlate(String v) {
		return vehicles.validPlate(v);
	}
	public boolean validVehicleDate(LocalDate v) {
		return v != null && vehicles.validDate(v);
	}
	public boolean validVin(String v) {
		return histories.validVin(v);
	}
	public boolean validOdometer(String v) {
		return histories.validOdo(v);
	}
	public boolean validRegistrationDate(LocalDate v) {
		return v != null && histories.validReg(v);
	}
	public boolean validExpiry(LocalDate v, LocalDate reg) {
		return v != null && reg != null && histories.validExpiry(v, reg);
	}
	public boolean validStatus(String v) {
		return histories.validStatus(v);
	}

	private static String value(String v) {
		return v == null ? "" : v;
	}
	private static String escape(String v) {
		return value(v).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
	}
}
