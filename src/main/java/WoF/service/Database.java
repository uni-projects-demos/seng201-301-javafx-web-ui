package WoF.service;

import WoF.compat.FileNotFoundException;
import WoF.compat.SQLException;
import WoF.compat.Timestamp;
import WoF.model.History;
import WoF.model.HistoryEnum;
import WoF.model.Session;
import WoF.model.vehicle.Vehicle;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Browser implementation of the original WoF SQLite DB service boundary
 */
public class Database {
	private static boolean browserStorageEnabled = true;
	private final Session wof = Session.getWoF();
	private final BrowserDatabaseStorage storage;

	public Database() throws SQLException {
		this.storage = new BrowserDatabaseStorage(browserStorageEnabled);
		this.storage.init();
	}

	public static void setBrowserStorageEnabled(boolean enabled) {
		browserStorageEnabled = enabled;
	}

	public void databaseSetup() throws FileNotFoundException, SQLException {
		storage.reset();
	}
	public Boolean isConnected() throws SQLException {
		return true;
	}

	public Object getOwner(String email) throws SQLException {
		return storage.ownerField(email, "email").isEmpty() ? null : new Object();
	}

	public String[] ownerLogIn(String email, String password) throws SQLException {
		if (!storage.login(email, password))
			return null;
		return new String[]{storage.ownerField(email, "forename"), storage.ownerField(email, "surname"),
				emptyToNull(storage.ownerField(email, "addressOne")),
				emptyToNull(storage.ownerField(email, "addressTwo")), emptyToNull(storage.ownerField(email, "phone"))};
	}

	public Boolean insertOwner(String forename, String surname, String email, String password) throws SQLException {
		return storage.registerOwner(forename, surname, email, password) == 1;
	}

	public Object getVehicleInfo(String plate) throws SQLException {
		return storage.isRegistered(plate) ? new Object() : null;
	}

	public void getVehicles() throws SQLException {
		HashSet<LinkedList<Object>> vehicles = new HashSet<>();
		if (wof.getUser() != null) {
			for (String plate : storage.ownerVehiclePlates(wof.getUser().getEmail())) {
				vehicles.add(new LinkedList<>(
						Arrays.asList(plate, storage.vehicleField(plate, "make"), storage.vehicleField(plate, "model"),
								dateTimestamp(storage.vehicleField(plate, "manufactureDate")),
								storage.vehicleField(plate, "addressOne"), storage.vehicleField(plate, "addressTwo"),
								storage.vehicleField(plate, "vehicleType"), storage.vehicleField(plate, "fuelType"))));
			}
		}
		wof.addVehicle(vehicles);
	}

	public void insertVehicle(String plate, String make, String model, Timestamp manufactureDate, String addressOne,
			String addressTwo, String vehicleType, String fuelType) throws SQLException {
		if (!storage.isRegistered(plate) && wof.getUser() != null) {
			storage.addVehicle(wof.getUser().getEmail(), plate, make, model, dateString(manufactureDate), addressOne,
					addressTwo, vehicleType, fuelType);
		}
	}

	public void deleteOwner() throws SQLException {
		if (wof.getUser() != null)
			storage.deleteOwner(wof.getUser().getEmail());
	}

	public void deleteVehicle(Vehicle vehicle) throws SQLException {
		if (wof.getUser() != null && vehicle != null)
			storage.deleteVehicle(wof.getUser().getEmail(), vehicle.getPlate());
	}

	public History getVehicleHistory() throws SQLException {
		if (wof.getVehicle() == null || !storage.hasHistory(wof.getVehicle().getPlate()))
			return null;
		String plate = wof.getVehicle().getPlate();
		return new History(storage.historyField(plate, "vin"), emptyToNull(storage.historyField(plate, "odometer")),
				dateTimestamp(storage.historyField(plate, "registrationDate")),
				dateTimestamp(storage.historyField(plate, "wofExpiry")),
				HistoryEnum.valueOf(storage.historyField(plate, "wofStatus")));
	}

	public Boolean vinInHistory(String vin) throws SQLException {
		return storage.vinExists(vin);
	}
	public void ownerLogOut() throws SQLException {
		storage.logout();
	}

	public void insertHistory(String vin, String odo, Timestamp reg, Timestamp wofExp, String wofStat)
			throws SQLException {
		if (wof.getVehicle() != null)
			storage.saveHistory(wof.getVehicle().getPlate(), vin, odo == null ? "" : odo, dateString(reg),
					dateString(wofExp), wofStat);
	}

	public void updateHistory(String param, Object value) throws SQLException {
		if (wof.getVehicle() == null || wof.getVehicle().getHistory() == null)
			return;
		History h = wof.getVehicle().getHistory();
		storage.saveHistory(wof.getVehicle().getPlate(), h.getVin(),
				h.getOdometerReading() == null ? "" : h.getOdometerReading(), dateString(h.getRegistrationDateNZ()),
				dateString(h.getWofExpiry()), h.getWofStatus().getValue());
	}

	public void updateVehicle(String param, Object value) throws SQLException {
		Vehicle v = wof.getVehicle();
		if (v == null)
			return;
		storage.updateVehicle(v.getPlate(), v.getMake(), v.getModel(), dateString(v.getManufactureDate()),
				v.getAddressLineOne(), v.getAddressLineTwo(), v.getVehicleType().getValue(),
				v.getFuelType().getValue());
	}

	public void updateOwner(String param, String value) throws SQLException {
		if (wof.getUser() == null)
			return;
		storage.updateOwner(wof.getUser().getEmail(), wof.getUser().getForename(), wof.getUser().getSurname(),
				nullToEmpty(wof.getUser().getAddressLineOne()), nullToEmpty(wof.getUser().getAddressLineTwo()),
				nullToEmpty(wof.getUser().getPhone()), wof.getUser().getPassword());
	}

	public void closeConnections() throws SQLException {
	}

	// Browser integration helpers
	public boolean isRegisteredVehicle(String plate) {
		return storage.isRegistered(plate);
	}
	public String vehicleField(String plate, String field) {
		return storage.vehicleField(plate, field);
	}
	public int ownerCount() {
		return storage.ownerCount();
	}
	public int vehicleCount() {
		return storage.vehicleCount();
	}
	public void resetBrowserData() {
		storage.reset();
	}

	private static Timestamp dateTimestamp(String text) {
		String date = text == null || text.isEmpty() ? "1970-01-01" : text.substring(0, Math.min(10, text.length()));
		return Timestamp.valueOf(LocalDate.parse(date).atStartOfDay());
	}
	private static String dateString(Timestamp value) {
		return value == null ? "" : value.toLocalDateTime().toLocalDate().toString();
	}
	private static String emptyToNull(String value) {
		return value == null || value.isEmpty() ? null : value;
	}
	private static String nullToEmpty(String value) {
		return value == null ? "" : value;
	}
}
