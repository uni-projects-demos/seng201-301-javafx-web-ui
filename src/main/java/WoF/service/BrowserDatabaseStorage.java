package WoF.service;

import org.teavm.jso.JSBody;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Browser persistence adapter for vehicle rego app JDBC/SQLite.
 */
public final class BrowserDatabaseStorage {

	private static final String STORAGE_KEY = "vehicle-reg-wof";
	private static final String FORMAT = "WOFDB1";

	private final List<OwnerData> owners = new ArrayList<>();
	private final List<VehicleData> vehicles = new ArrayList<>();
	private final List<LinkData> links = new ArrayList<>();
	private final List<HistoryData> histories = new ArrayList<>();
	private final boolean browserStorage;
	private String memoryStorage;
	private String sessionEmail = "";

	/**
	 * Create browser-backed registration store used by the static site.
	 */
	public BrowserDatabaseStorage() {
		this(true);
	}

	/**
	 * Create registration store with optional browser persistence.
	 *
	 * @param browserStorage
	 *            true to persist through browser localStorage
	 */
	public BrowserDatabaseStorage(boolean browserStorage) {
		this.browserStorage = browserStorage;
	}

	/**
	 * Load browser-local state or generated seed exported from SQLite DB.
	 */
	public void init() {
		String stored = readStored();
		if (stored == null || !stored.startsWith(FORMAT + "\n")) {
			reset();
			return;
		}
		try {
			load(stored);
		} catch (RuntimeException ex) {
			reset();
		}
	}

	/**
	 * Restores the registration store to the SQLite-derived build-time seed.
	 */
	public void reset() {
		owners.clear();
		vehicles.clear();
		links.clear();
		histories.clear();
		sessionEmail = "";
		WoF.service.generated.RegistrationSeedData.populate(this);
		save();
	}

	/**
	 * Adds a seed owner. Called only by generated registration seed code.
	 */
	public void seedOwner(String forename, String surname, String email, String password, String addressOne,
			String addressTwo, String phone) {
		owners.add(new OwnerData(forename, surname, email, password, addressOne, addressTwo, phone));
	}

	/**
	 * Adds a seed vehicle. Called only by generated registration seed code.
	 */
	public void seedVehicle(String plate, String make, String model, String manufactureDate, String addressOne,
			String addressTwo, String vehicleType, String fuelType) {
		vehicles.add(
				new VehicleData(plate, make, model, manufactureDate, addressOne, addressTwo, vehicleType, fuelType));
	}

	/**
	 * Adds a seed ownership link. Called only by generated registration seed code.
	 */
	public void seedLink(String ownerEmail, String plate, String vin) {
		links.add(new LinkData(ownerEmail, plate, vin));
	}

	/**
	 * Adds a seed WoF history row. Called only by generated registration seed code.
	 */
	public void seedHistory(String vin, String odometer, String registrationDate, String wofExpiry, String wofStatus) {
		histories.add(new HistoryData(vin, odometer, registrationDate, wofExpiry, wofStatus));
	}

	/**
	 * Checks whether the plate exists in the shared vehicle registry.
	 *
	 * @param plate
	 *            registration plate
	 * @return true when the vehicle is registered
	 */
	public boolean isRegistered(String plate) {
		return findVehicle(plate) != null;
	}

	/**
	 * Retrieves one vehicle field used by the parking and registration UIs.
	 */
	public String vehicleField(String plate, String field) {
		VehicleData vehicle = findVehicle(plate);
		if (vehicle == null || field == null) {
			return "";
		}
		return switch (field) {
			case "plate" -> vehicle.plate;
			case "make" -> vehicle.make;
			case "model" -> vehicle.model;
			case "manufactureDate" -> vehicle.manufactureDate;
			case "addressOne" -> vehicle.addressOne;
			case "addressTwo" -> vehicle.addressTwo;
			case "vehicleType" -> vehicle.vehicleType;
			case "fuelType" -> vehicle.fuelType;
			default -> "";
		};
	}

	/**
	 * Retrieves one owner field used by the registration UI.
	 */
	public String ownerField(String email, String field) {
		OwnerData owner = findOwner(email);
		if (owner == null || field == null) {
			return "";
		}
		return switch (field) {
			case "forename" -> owner.forename;
			case "surname" -> owner.surname;
			case "email" -> owner.email;
			case "password" -> owner.password;
			case "addressOne" -> owner.addressOne;
			case "addressTwo" -> owner.addressTwo;
			case "phone" -> owner.phone;
			default -> "";
		};
	}

	/**
	 * Retrieves one WoF history field for a registered plate.
	 */
	public String historyField(String plate, String field) {
		HistoryData history = findHistoryForPlate(plate);
		if (history == null || field == null) {
			return "";
		}
		return switch (field) {
			case "vin" -> history.vin;
			case "odometer" -> history.odometer;
			case "registrationDate" -> history.registrationDate;
			case "wofExpiry" -> history.wofExpiry;
			case "wofStatus" -> history.wofStatus;
			default -> "";
		};
	}

	/**
	 * Checks whether a plate has a WoF history row.
	 */
	public boolean hasHistory(String plate) {
		return findHistoryForPlate(plate) != null;
	}

	/**
	 * Gets the browser-local logged-in owner email.
	 */
	public String currentUserEmail() {
		return sessionEmail;
	}

	/**
	 * Logs in an owner using the registration seed/browser-local account data.
	 */
	public boolean login(String email, String password) {
		OwnerData owner = findOwner(email);
		if (owner == null || !owner.password.equals(password == null ? "" : password)) {
			return false;
		}
		sessionEmail = owner.email;
		save();
		return true;
	}

	/**
	 * Clears the active browser-local owner session.
	 */
	public void logout() {
		sessionEmail = "";
		save();
	}

	/**
	 * Registers a new owner account.
	 *
	 * @return 1 on success, 0 when the email already exists
	 */
	public int registerOwner(String forename, String surname, String email, String password) {
		String key = normalizeEmail(email);
		if (findOwner(key) != null) {
			return 0;
		}
		owners.add(new OwnerData(upper(forename), upper(surname), key, password == null ? "" : password, "", "", ""));
		sessionEmail = key;
		save();
		return 1;
	}

	/**
	 * Updates an existing owner account.
	 */
	public boolean updateOwner(String email, String forename, String surname, String addressOne, String addressTwo,
			String phone, String password) {
		OwnerData owner = findOwner(email);
		if (owner == null) {
			return false;
		}
		owner.forename = upper(forename);
		owner.surname = upper(surname);
		owner.addressOne = upper(addressOne);
		owner.addressTwo = upper(addressTwo);
		owner.phone = phone == null ? "" : phone;
		owner.password = password == null ? "" : password;
		save();
		return true;
	}

	/**
	 * Removes an owner, their ownership links, & vehicles not associated with
	 * another owner.
	 */
	public boolean deleteOwner(String email) {
		String key = normalizeEmail(email);
		OwnerData owner = findOwner(key);
		if (owner == null) {
			return false;
		}

		List<String> ownerPlates = new ArrayList<>();
		List<String> ownerVins = new ArrayList<>();
		for (LinkData link : links) {
			if (normalizeEmail(link.ownerEmail).equals(key)) {
				ownerPlates.add(normalizePlate(link.plate));
				ownerVins.add(link.vin);
			}
		}
		links.removeIf(link -> normalizeEmail(link.ownerEmail).equals(key));

		for (int i = 0; i < ownerPlates.size(); i++) {
			String plate = ownerPlates.get(i);
			boolean stillLinked = false;
			for (LinkData link : links) {
				if (normalizePlate(link.plate).equals(plate)) {
					stillLinked = true;
					break;
				}
			}
			if (!stillLinked) {
				String vin = ownerVins.get(i);
				vehicles.removeIf(vehicle -> normalizePlate(vehicle.plate).equals(plate));
				if (!vin.isEmpty()) {
					histories.removeIf(history -> history.vin.equalsIgnoreCase(vin));
				}
			}
		}

		owners.remove(owner);
		sessionEmail = "";
		save();
		return true;
	}

	/**
	 * Builds the vehicle-list markup shown for an owner in the browser UI.
	 */
	public String vehicleListHtml(String email, String selectedPlate) {
		String key = normalizeEmail(email);
		String selected = normalizePlate(selectedPlate);
		List<String> plates = new ArrayList<>();
		for (LinkData link : links) {
			if (normalizeEmail(link.ownerEmail).equals(key)) {
				plates.add(normalizePlate(link.plate));
			}
		}
		plates.sort(Comparator.naturalOrder());
		if (plates.isEmpty()) {
			return "<div class=\"empty-state compact-empty\">No vehicles registered to this account.</div>";
		}

		StringBuilder html = new StringBuilder();
		for (String plate : plates) {
			VehicleData vehicle = findVehicle(plate);
			if (vehicle == null) {
				continue;
			}
			String active = selected.equals(plate) ? " active" : "";
			html.append("<button type=\"button\" class=\"vehicle-list-item").append(active).append("\" data-plate=\"")
					.append(escapeHtml(plate)).append("\"><span class=\"vehicle-plate\">").append(escapeHtml(plate))
					.append("</span><span class=\"vehicle-name\">").append(escapeHtml(vehicle.make)).append(' ')
					.append(escapeHtml(vehicle.model)).append("</span><span class=\"vehicle-meta\">")
					.append(escapeHtml(vehicle.vehicleType)).append(" · ")
					.append(escapeHtml(vehicle.fuelType.isEmpty() ? "NA" : vehicle.fuelType))
					.append("</span></button>");
		}
		return html.toString();
	}

	/**
	 * Registers a vehicle for an owner.
	 *
	 * @return 1 on success, 0 when the plate already exists
	 */
	public int addVehicle(String ownerEmail, String plate, String make, String model, String manufactureDate,
			String addressOne, String addressTwo, String vehicleType, String fuelType) {
		String key = normalizePlate(plate);
		if (findVehicle(key) != null) {
			return 0;
		}
		vehicles.add(new VehicleData(key, upper(make), upper(model), value(manufactureDate), upper(addressOne),
				upper(addressTwo), upper(vehicleType), upperOrNa(fuelType)));
		links.add(new LinkData(normalizeEmail(ownerEmail), key, ""));
		save();
		return 1;
	}

	/**
	 * Updates an existing vehicle registration.
	 */
	public boolean updateVehicle(String plate, String make, String model, String manufactureDate, String addressOne,
			String addressTwo, String vehicleType, String fuelType) {
		VehicleData vehicle = findVehicle(plate);
		if (vehicle == null) {
			return false;
		}
		vehicle.make = upper(make);
		vehicle.model = upper(model);
		vehicle.manufactureDate = value(manufactureDate);
		vehicle.addressOne = upper(addressOne);
		vehicle.addressTwo = upper(addressTwo);
		vehicle.vehicleType = upper(vehicleType);
		vehicle.fuelType = upperOrNa(fuelType);
		save();
		return true;
	}

	/**
	 * Removes one owner/vehicle relationship & deletes vehicle when no other owner
	 * still references it.
	 */
	public boolean deleteVehicle(String ownerEmail, String plate) {
		String email = normalizeEmail(ownerEmail);
		String key = normalizePlate(plate);
		LinkData selected = null;
		for (LinkData link : links) {
			if (normalizeEmail(link.ownerEmail).equals(email) && normalizePlate(link.plate).equals(key)) {
				selected = link;
				break;
			}
		}
		if (selected == null) {
			return false;
		}
		String vin = selected.vin;
		links.remove(selected);

		boolean stillLinked = false;
		for (LinkData link : links) {
			if (normalizePlate(link.plate).equals(key)) {
				stillLinked = true;
				break;
			}
		}
		if (!stillLinked) {
			vehicles.removeIf(vehicle -> normalizePlate(vehicle.plate).equals(key));
			if (!vin.isEmpty()) {
				histories.removeIf(history -> history.vin.equalsIgnoreCase(vin));
			}
		}
		save();
		return true;
	}

	/**
	 * Inserts or updates the WoF history associated with a vehicle.
	 *
	 * @return 1 on success, 0 when the plate has no ownership link, -1 when the VIN
	 *         conflicts
	 */
	public int saveHistory(String plate, String vin, String odometer, String registrationDate, String expiry,
			String status) {
		String key = normalizePlate(plate);
		LinkData link = findLinkByPlate(key);
		if (link == null) {
			return 0;
		}
		String normalizedVin = upper(vin);
		for (HistoryData history : histories) {
			if (history.vin.equalsIgnoreCase(normalizedVin) && !link.vin.equalsIgnoreCase(normalizedVin)) {
				return -1;
			}
		}

		HistoryData history = findHistoryForPlate(key);
		if (history == null) {
			history = new HistoryData(normalizedVin, "", "", "", "PASSED");
			histories.add(history);
		} else {
			history.vin = normalizedVin;
		}
		history.odometer = value(odometer);
		history.registrationDate = value(registrationDate);
		history.wofExpiry = value(expiry);
		history.wofStatus = upper(status);
		link.vin = normalizedVin;
		save();
		return 1;
	}

	String[] ownerVehiclePlates(String email) {
		String key = normalizeEmail(email);
		List<String> plates = new ArrayList<>();
		for (LinkData link : links) {
			if (normalizeEmail(link.ownerEmail).equals(key))
				plates.add(normalizePlate(link.plate));
		}
		plates.sort(Comparator.naturalOrder());
		return plates.toArray(new String[0]);
	}

	void setSessionEmail(String email) {
		sessionEmail = normalizeEmail(email);
		save();
	}

	String linkedVinForPlate(String plate) {
		return linkedVin(plate);
	}

	boolean vinExists(String vin) {
		String key = upper(vin);
		for (HistoryData history : histories)
			if (history.vin.equalsIgnoreCase(key))
				return true;
		return false;
	}

	/**
	 * Gets the number of registered owners.
	 */
	public int ownerCount() {
		return owners.size();
	}

	/**
	 * Gets the number of registered vehicles.
	 */
	public int vehicleCount() {
		return vehicles.size();
	}

	/**
	 * Gets the number of WoF history rows.
	 */
	public int historyCount() {
		return histories.size();
	}

	private OwnerData findOwner(String email) {
		String key = normalizeEmail(email);
		for (OwnerData owner : owners) {
			if (normalizeEmail(owner.email).equals(key)) {
				return owner;
			}
		}
		return null;
	}

	private VehicleData findVehicle(String plate) {
		String key = normalizePlate(plate);
		for (VehicleData vehicle : vehicles) {
			if (normalizePlate(vehicle.plate).equals(key)) {
				return vehicle;
			}
		}
		return null;
	}

	private LinkData findLinkByPlate(String plate) {
		String key = normalizePlate(plate);
		for (LinkData link : links) {
			if (normalizePlate(link.plate).equals(key)) {
				return link;
			}
		}
		return null;
	}

	private HistoryData findHistoryForPlate(String plate) {
		LinkData link = findLinkByPlate(plate);
		if (link == null || link.vin.isEmpty()) {
			return null;
		}
		for (HistoryData history : histories) {
			if (history.vin.equalsIgnoreCase(link.vin)) {
				return history;
			}
		}
		return null;
	}

	private String linkedVin(String plate) {
		LinkData link = findLinkByPlate(plate);
		return link == null ? "" : link.vin;
	}

	private void save() {
		StringBuilder out = new StringBuilder(FORMAT).append('\n');
		appendLine(out, "S", sessionEmail);
		for (OwnerData owner : owners) {
			appendLine(out, "O", owner.forename, owner.surname, owner.email, owner.password, owner.addressOne,
					owner.addressTwo, owner.phone);
		}
		for (VehicleData vehicle : vehicles) {
			appendLine(out, "V", vehicle.plate, vehicle.make, vehicle.model, vehicle.manufactureDate,
					vehicle.addressOne, vehicle.addressTwo, vehicle.vehicleType, vehicle.fuelType);
		}
		for (LinkData link : links) {
			appendLine(out, "L", link.ownerEmail, link.plate, link.vin);
		}
		for (HistoryData history : histories) {
			appendLine(out, "H", history.vin, history.odometer, history.registrationDate, history.wofExpiry,
					history.wofStatus);
		}
		writeStored(out.toString());
	}

	private void load(String stored) {
		owners.clear();
		vehicles.clear();
		links.clear();
		histories.clear();
		sessionEmail = "";

		String[] lines = stored.split("\\n", -1);
		if (lines.length == 0 || !FORMAT.equals(lines[0])) {
			throw new IllegalArgumentException("Unsupported registration store format");
		}
		for (int i = 1; i < lines.length; i++) {
			if (lines[i].isEmpty()) {
				continue;
			}
			String[] parts = lines[i].split("\\|", -1);
			if (parts.length == 0) {
				continue;
			}
			switch (parts[0]) {
				case "S" :
					sessionEmail = part(parts, 1);
					break;
				case "O" :
					owners.add(new OwnerData(part(parts, 1), part(parts, 2), part(parts, 3), part(parts, 4),
							part(parts, 5), part(parts, 6), part(parts, 7)));
					break;
				case "V" :
					vehicles.add(new VehicleData(part(parts, 1), part(parts, 2), part(parts, 3), part(parts, 4),
							part(parts, 5), part(parts, 6), part(parts, 7), part(parts, 8)));
					break;
				case "L" :
					links.add(new LinkData(part(parts, 1), part(parts, 2), part(parts, 3)));
					break;
				case "H" :
					histories.add(new HistoryData(part(parts, 1), part(parts, 2), part(parts, 3), part(parts, 4),
							part(parts, 5)));
					break;
				default :
					break;
			}
		}
	}

	private static void appendLine(StringBuilder out, String type, String... fields) {
		out.append(type);
		for (String field : fields) {
			out.append('|').append(encode(field));
		}
		out.append('\n');
	}

	private static String part(String[] parts, int index) {
		return index < parts.length ? decode(parts[index]) : "";
	}

	private static String encode(String text) {
		return value(text).replace("%", "%25").replace("|", "%7C").replace("\r", "%0D").replace("\n", "%0A");
	}

	private static String decode(String text) {
		return value(text).replace("%0D", "\r").replace("%0A", "\n").replace("%7C", "|").replace("%25", "%");
	}

	private static String normalizeEmail(String email) {
		return value(email).trim().toLowerCase();
	}

	private static String normalizePlate(String plate) {
		return value(plate).trim().toUpperCase();
	}

	private static String upper(String value) {
		return BrowserDatabaseStorage.value(value).toUpperCase();
	}

	private static String upperOrNa(String value) {
		String upper = upper(value);
		return upper.isEmpty() ? "NA" : upper;
	}

	private static String value(String value) {
		return value == null ? "" : value;
	}

	private static String escapeHtml(String text) {
		return value(text).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&#039;");
	}

	private String readStored() {
		return browserStorage ? storageGet(STORAGE_KEY) : memoryStorage;
	}

	private void writeStored(String value) {
		if (browserStorage) {
			storageSet(STORAGE_KEY, value);
		} else {
			memoryStorage = value;
		}
	}

	@JSBody(params = {"key"}, script = "return window.localStorage.getItem(key);")
	private static native String storageGet(String key);

	@JSBody(params = {"key", "value"}, script = "window.localStorage.setItem(key, value);")
	private static native void storageSet(String key, String value);

	private static final class OwnerData {
		String forename;
		String surname;
		final String email;
		String password;
		String addressOne;
		String addressTwo;
		String phone;

		private OwnerData(String forename, String surname, String email, String password, String addressOne,
				String addressTwo, String phone) {
			this.forename = upper(forename);
			this.surname = upper(surname);
			this.email = normalizeEmail(email);
			this.password = value(password);
			this.addressOne = upper(addressOne);
			this.addressTwo = upper(addressTwo);
			this.phone = value(phone);
		}
	}

	private static final class VehicleData {
		final String plate;
		String make;
		String model;
		String manufactureDate;
		String addressOne;
		String addressTwo;
		String vehicleType;
		String fuelType;

		private VehicleData(String plate, String make, String model, String manufactureDate, String addressOne,
				String addressTwo, String vehicleType, String fuelType) {
			this.plate = normalizePlate(plate);
			this.make = upper(make);
			this.model = upper(model);
			this.manufactureDate = value(manufactureDate);
			this.addressOne = upper(addressOne);
			this.addressTwo = upper(addressTwo);
			this.vehicleType = upper(vehicleType);
			this.fuelType = upperOrNa(fuelType);
		}
	}

	private static final class LinkData {
		final String ownerEmail;
		final String plate;
		String vin;

		private LinkData(String ownerEmail, String plate, String vin) {
			this.ownerEmail = normalizeEmail(ownerEmail);
			this.plate = normalizePlate(plate);
			this.vin = upper(vin);
		}
	}

	private static final class HistoryData {
		String vin;
		String odometer;
		String registrationDate;
		String wofExpiry;
		String wofStatus;

		private HistoryData(String vin, String odometer, String registrationDate, String wofExpiry, String wofStatus) {
			this.vin = upper(vin);
			this.odometer = value(odometer);
			this.registrationDate = value(registrationDate);
			this.wofExpiry = value(wofExpiry);
			this.wofStatus = upper(wofStatus);
		}
	}
}
