package airport.web;

import WoF.model.HistoryEnum;
import WoF.model.vehicle.FuelEnum;
import WoF.model.vehicle.VehicleEnum;
import airport.model.Vehicle;
import airport.model.nosying.NosyParkable;
import airport.model.nosying.NosyParker;
import airport.model.parking.ParkingLot;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import registration.web.RegistrationWebController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TeaVM web app. Airport parking (legacy & modern modes) & vehicle registration
 * pages.
 */
public final class AirportParkingWebApp {

	private enum AppMode {
		LEGACY, MODERN
	}

	private enum AppPage {
		PARKING, REGO
	}

	private AppMode mode = AppMode.MODERN;
	private String selectedVehiclePlate = "";
	private final RegistrationWebController regoStore = new RegistrationWebController();
	private final LegacyEngine legacy = new LegacyEngine();
	private final ModernEngine modern = new ModernEngine();

	public static void main(String[] args) {
		new AirportParkingWebApp().start();
	}

	private void start() {
		regoStore.init();

		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
		setValue("arrival", formatDateTime(now));
		setValue("departure", formatDateTime(now.plusDays(1).plusHours(2)));

		// Global nav
		onClick("navParking", () -> showPage(AppPage.PARKING));
		onClick("navRego", () -> showPage(AppPage.REGO));

		// Airport parking
		onClick("quote", this::quote);
		onClick("checkIn", this::checkIn);
		onClick("checkOut", this::checkOut);
		onClick("versionLegacy", () -> switchMode(AppMode.LEGACY));
		onClick("versionModern", () -> switchMode(AppMode.MODERN));
		onEnter("vehicleReg", this::checkIn);
		onInput("vehicleReg", this::refreshVehicleRegistration);
		onChange("quoteParkingLot", this::refreshRateSummary);
		onChange("gateParkingLot", this::refreshHeaderStats);

		// Vehicle registration
		onClick("loginOwner", this::loginOwner);
		onEnter("loginPassword", this::loginOwner);
		onClick("registerOwner", this::registerOwner);
		onEnter("registerPassword", this::registerOwner);
		onClick("logoutOwner", this::logoutOwner);
		onClick("resetRegistry", this::resetRegistry);
		onClick("saveOwnerProfile", this::saveOwnerProfile);
		onClick("removeOwnerProfile", this::removeOwnerProfile);
		onClick("newVehicle", this::newVehicle);
		onClick("saveVehicle", this::saveRegistrationVehicle);
		onClick("removeVehicle", this::removeRegistrationVehicle);
		onClick("vehicleDetailsTab", () -> showVehicleEditorTab(false));
		onClick("vehicleHistTab", () -> showVehicleEditorTab(true));
		onClick("saveHist", this::saveRegistrationHist);
		onDataClick("vehicleList", "plate", this::selectRegistrationVehicle);

		switchMode(AppMode.MODERN);
		showPage(AppPage.PARKING);
		refreshRegistrationUI();
	}

	private void showPage(AppPage newPage) {
		boolean isParking = newPage == AppPage.PARKING;
		setVisible("parkingPage", isParking);
		setVisible("regoPage", !isParking);
		setVisible("parkingHeaderTools", isParking);
		setVisible("regoHeaderTools", !isParking);
		setActiveButton("navParking", isParking);
		setActiveButton("navRego", !isParking);
		if (isParking) {
			text("pageTitle", "Christchurch International Airport Parking");
			text("pageSymbol", "P");
			setVersionUI();
			refreshVehicleRegistration();
		} else {
			text("pageTitle", "NZTA Vehicle Registration");
			text("pageSymbol", "R");
			refreshRegistrationUI();
		}
	}

	private void refreshRegistrationUI() {
		text("registryOwnerCount", String.valueOf(regOwnerCount()));
		text("registryVehicleCount", String.valueOf(regVehicleCount()));
		String email = regCurrentUserEmail();
		boolean isLoggedIn = email != null && !email.isEmpty();
		setVisible("registrationAuth", !isLoggedIn);
		setVisible("registrationDashboard", isLoggedIn);
		setVisible("logoutOwner", isLoggedIn);
		text("registrySessionBadge", isLoggedIn ? email : "Signed out");
		if (!isLoggedIn) {
			selectedVehiclePlate = "";
			return;
		}
		isRegPopulateOwnerForm(email);
		refreshVehicleList();
		if (selectedVehiclePlate == null || selectedVehiclePlate.isEmpty()
				|| !isRegisteredVehicleExists(selectedVehiclePlate)) {
			newVehicle();
		} else {
			selectRegistrationVehicle(selectedVehiclePlate);
		}
	}

	private void loginOwner() {
		String email = value("loginEmail").trim().toLowerCase();
		String password = value("loginPassword");
		if (!isValidEmail(email) || password == null || password.isEmpty()) {
			setRegistryAuthMessage("Enter a valid email and password.", "error");
			return;
		}
		if (!isRegLogin(email, password)) {
			setRegistryAuthMessage("Email or password was not recognised.", "error");
			return;
		}
		setValue("loginPassword", "");
		selectedVehiclePlate = "";
		refreshRegistrationUI();
		setRegistryMessage("Logged in as " + email + ".", "success");
	}

	private void registerOwner() {
		String forename = value("registerForename").trim();
		String surname = value("registerSurname").trim();
		String email = value("registerEmail").trim().toLowerCase();
		String password = value("registerPassword");
		if (!isValidName(forename) || !isValidName(surname)) {
			setRegistryAuthMessage(
					"Forename and surname are required and may contain letters, numbers, spaces or hyphens.", "error");
			return;
		}
		if (!isValidEmail(email)) {
			setRegistryAuthMessage("Enter a valid email address.", "error");
			return;
		}
		if (!isValidPassword(password)) {
			setRegistryAuthMessage("Enter a non-empty password without spaces.", "error");
			return;
		}
		if (regRegisterOwner(forename, surname, email, password) == 0) {
			setRegistryAuthMessage(email + " is already registered to an account.", "error");
			return;
		}
		setValue("registerPassword", "");
		selectedVehiclePlate = "";
		refreshRegistrationUI();
		setRegistryMessage(email + " successfully registered.", "success");
	}

	private void logoutOwner() {
		regLogout();
		selectedVehiclePlate = "";
		refreshRegistrationUI();
		setRegistryAuthMessage("Logged out.", "neutral");
	}

	private void resetRegistry() {
		if (!isConfirmAction("Reset data changes back to the SQLite DB seed?")) {
			return;
		}
		regReset();
		selectedVehiclePlate = "";
		refreshRegistrationUI();
		refreshVehicleRegistration();
		text("registryOwnerCount", String.valueOf(regOwnerCount()));
		text("registryVehicleCount", String.valueOf(regVehicleCount()));
		setRegistryAuthMessage("Data reset to the SQLite DB seed.", "success");
	}

	private void saveOwnerProfile() {
		String email = regCurrentUserEmail();
		if (email == null || email.isEmpty()) {
			refreshRegistrationUI();
			return;
		}
		String forename = value("ownerForename").trim();
		String surname = value("ownerSurname").trim();
		String addressOne = value("ownerAddressOne").trim();
		String addressTwo = value("ownerAddressTwo").trim();
		String phone = value("ownerPhone").trim();
		String password = value("ownerPassword");

		if (!isValidName(forename) || !isValidName(surname)) {
			setRegistryMessage("Forename and surname are invalid.", "error");
			return;
		}
		if ((!addressOne.isEmpty() && !isValidAddress(addressOne))
				|| (!addressTwo.isEmpty() && !isValidAddress(addressTwo))) {
			setRegistryMessage("Address fields contain unsupported characters.", "error");
			return;
		}
		if (!phone.isEmpty() && !phone.matches("^[0-9]+$")) {
			setRegistryMessage("Phone must contain digits only.", "error");
			return;
		}
		if (!isValidPassword(password)) {
			setRegistryMessage("Password cannot be empty or contain spaces.", "error");
			return;
		}
		isRegUpdateOwner(email, forename, surname, addressOne, addressTwo, phone, password);
		isRegPopulateOwnerForm(email);
		setRegistryMessage("Owner profile saved.", "success");
	}

	private void removeOwnerProfile() {
		String email = regCurrentUserEmail();
		if (email == null || email.isEmpty()) {
			return;
		}
		if (!isConfirmAction("Remove account " + email + " and all vehicles registered to it?")) {
			return;
		}
		isRegDeleteOwner(email);
		selectedVehiclePlate = "";
		refreshRegistrationUI();
		refreshVehicleRegistration();
		setRegistryAuthMessage("Account removed from this browser registry.", "success");
	}

	private void newVehicle() {
		selectedVehiclePlate = "";
		regClearVehicleForm();
		regClearHistForm();
		setDisabled("regPlate", false);
		setVisible("removeVehicle", false);
		setButtonText("saveVehicle", "Register vehicle");
		text("vehicleEditorTitle", "Vehicle registration");
		text("vehicleEditorSubtitle", "Register a new vehicle to the current owner.");
		text("histEmptyHint", "Save the vehicle first, then add its WoF history.");
		refreshVehicleList();
		showVehicleEditorTab(false);
	}

	private void selectRegistrationVehicle(String plate) {
		if (plate == null || plate.isEmpty() || !isRegisteredVehicleExists(plate)) {
			return;
		}
		selectedVehiclePlate = plate.toUpperCase();
		isRegPopulateVehicleForm(selectedVehiclePlate);
		boolean hasHist = isRegPopulateHistForm(selectedVehiclePlate);
		setDisabled("regPlate", true);
		setVisible("removeVehicle", true);
		setButtonText("saveVehicle", "Save vehicle");
		text("vehicleEditorTitle", selectedVehiclePlate);
		String make = registeredVehicleField(selectedVehiclePlate, "make");
		String model = registeredVehicleField(selectedVehiclePlate, "model");
		text("vehicleEditorSubtitle", make + (model.isEmpty() ? "" : " " + model));
		text("histEmptyHint",
				hasHist ? "WoF history is registered for this vehicle." : "No WoF history yet. Add it below.");
		refreshVehicleList();
		showVehicleEditorTab(false);
	}

	private void refreshVehicleList() {
		String email = regCurrentUserEmail();
		if (email == null || email.isEmpty()) {
			setHtml("vehicleList", "");
			return;
		}
		setHtml("vehicleList", regVehicleListHtml(email, selectedVehiclePlate == null ? "" : selectedVehiclePlate));
		text("registryOwnerCount", String.valueOf(regOwnerCount()));
		text("registryVehicleCount", String.valueOf(regVehicleCount()));
	}

	private void showVehicleEditorTab(boolean hasHist) {
		setVisible("vehicleDetailsPane", !hasHist);
		setVisible("vehiclehistPane", hasHist);
		setActiveButton("vehicleDetailsTab", !hasHist);
		setActiveButton("vehiclehistTab", hasHist);
	}

	private void saveRegistrationVehicle() {
		String email = regCurrentUserEmail();
		if (email == null || email.isEmpty()) {
			setRegistryMessage("Login before registering a vehicle.", "error");
			return;
		}
		String plate = value("regPlate").trim().toUpperCase();
		String make = value("regMake").trim();
		String model = value("regModel").trim();
		String manufactureDate = value("regManufactureDate").trim();
		String addressOne = value("regAddressOne").trim();
		String addressTwo = value("regAddressTwo").trim();
		String vehicleType = value("regVehicleType").trim().toUpperCase();
		String fuelType = value("regFuelType").trim().toUpperCase();

		if (!regoStore.validPlate(plate)) {
			setRegistryMessage("Plate must be 1-6 letters or numbers.", "error");
			return;
		}
		if (!isValidName(make) || !isValidName(model)) {
			setRegistryMessage("Make and model are required.", "error");
			return;
		}
		LocalDate manufacture = parseDate(manufactureDate);
		if (!regoStore.validVehicleDate(manufacture)) {
			setRegistryMessage("Manufacture date must be today or earlier.", "error");
			return;
		}
		if (!isValidAddress(addressOne) || !isValidAddress(addressTwo)) {
			setRegistryMessage("Both vehicle address fields are required.", "error");
			return;
		}
		if (!isVehicleType(vehicleType) || !isFuelType(fuelType)) {
			setRegistryMessage("Select a valid vehicle and fuel type.", "error");
			return;
		}

		boolean isCreating = selectedVehiclePlate == null || selectedVehiclePlate.isEmpty();
		if (isCreating) {
			if (regAddVehicle(email, plate, make, model, manufactureDate, addressOne, addressTwo, vehicleType,
					fuelType) == 0) {
				setRegistryMessage(plate + " is already registered to an account.", "error");
				return;
			}
			selectedVehiclePlate = plate;
			setRegistryMessage(plate + " successfully registered to " + email + ".", "success");
		} else {
			isRegUpdateVehicle(selectedVehiclePlate, make, model, manufactureDate, addressOne, addressTwo, vehicleType,
					fuelType);
			setRegistryMessage(selectedVehiclePlate + " registration saved.", "success");
		}
		selectRegistrationVehicle(selectedVehiclePlate);
		refreshVehicleRegistration();
	}

	private void removeRegistrationVehicle() {
		String email = regCurrentUserEmail();
		if (email == null || email.isEmpty() || selectedVehiclePlate == null || selectedVehiclePlate.isEmpty()) {
			return;
		}
		String plate = selectedVehiclePlate;
		if (!isConfirmAction("Remove vehicle " + plate + " from the registry?")) {
			return;
		}
		isRegDeleteVehicle(email, plate);
		selectedVehiclePlate = "";
		newVehicle();
		refreshVehicleRegistration();
		setRegistryMessage(plate + " removed from the registry.", "success");
	}

	private void saveRegistrationHist() {
		if (selectedVehiclePlate == null || selectedVehiclePlate.isEmpty()) {
			setRegistryMessage("Select or register a vehicle first.", "error");
			return;
		}
		String vin = value("histVin").trim().toUpperCase();
		String odometer = value("histOdometer").trim();
		String registrationDate = value("histRegistrationDate").trim();
		String expiry = value("histExpiry").trim();
		String status = value("histStatus").trim().toUpperCase();

		if (!regoStore.validVin(vin)) {
			setRegistryMessage("VIN must be exactly 17 letters or numbers.", "error");
			return;
		}
		String vehicleType = registeredVehicleField(selectedVehiclePlate, "vehicleType");
		if (!"T".equals(vehicleType) && !odometer.matches("^[0-9]+$")) {
			setRegistryMessage("Odometer must contain digits only.", "error");
			return;
		}
		LocalDate manufacture = parseDate(registeredVehicleField(selectedVehiclePlate, "manufactureDate"));
		LocalDate registration = parseDate(registrationDate);
		LocalDate wofExpiry = parseDate(expiry);
		if (manufacture == null || registration == null || registration.isAfter(LocalDate.now())
				|| registration.isBefore(manufacture)) {
			setRegistryMessage("NZ registration date cannot precede manufacture or be in the future.", "error");
			return;
		}
		if (wofExpiry == null || !wofExpiry.isAfter(registration) || !wofExpiry.isAfter(manufacture)) {
			setRegistryMessage("WoF expiry must be after the registration and manufacture dates.", "error");
			return;
		}
		if (!isWofStatus(status)) {
			setRegistryMessage("Select PASSED, FAILED or EXPIRED.", "error");
			return;
		}
		int result = regSaveHist(selectedVehiclePlate, vin, "T".equals(vehicleType) ? "" : odometer, registrationDate,
				expiry, status);
		if (result < 0) {
			setRegistryMessage("VIN " + vin + " is already registered to another vehicle.", "error");
			return;
		}
		if (result == 0) {
			setRegistryMessage("Unable to find the selected vehicle.", "error");
			return;
		}
		isRegPopulateHistForm(selectedVehiclePlate);
		text("histEmptyHint", "WoF history is registered for this vehicle.");
		setRegistryMessage("WoF history saved for " + selectedVehiclePlate + ".", "success");
	}

	private boolean isValidName(String value) {
		return regoStore.validName(value);
	}

	private boolean isValidAddress(String value) {
		return regoStore.validAddress(value);
	}

	private boolean isValidPassword(String value) {
		return regoStore.validPassword(value);
	}

	private boolean isValidEmail(String value) {
		return regoStore.validEmail(value);
	}

	private static boolean isVehicleType(String value) {
		try {
			VehicleEnum.valueOf(value);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private static boolean isFuelType(String value) {
		try {
			FuelEnum.valueOf(value);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private static boolean isWofStatus(String value) {
		try {
			HistoryEnum.valueOf(value);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private static LocalDate parseDate(String value) {
		try {
			return value == null || value.isEmpty() ? null : LocalDate.parse(value);
		} catch (RuntimeException e) {
			return null;
		}
	}

	private void setRegistryMessage(String message, String type) {
		setMessageBox("registryMessage", message, type);
	}

	private void setRegistryAuthMessage(String message, String type) {
		setMessageBox("registryAuthMessage", message, type);
	}

	private void switchMode(AppMode newMode) {
		this.mode = newMode;
		ParkingEngine engine = engine();
		populateLotSelect("quoteParkingLot", engine.lots(), 0);
		populateLotSelect("gateParkingLot", engine.lots(), 0);
		setVersionUI();
		refreshRateSummary();
		refreshHeaderStats();
		updateCapacities();
		text("quoteTotal", "—");
		text("quoteDuration", newMode == AppMode.LEGACY ? "Use legacy rates." : "Use current rates.");
		refreshVehicleRegistration();
		setMessage(
				newMode == AppMode.LEGACY
						? "Legacy mode loaded. Only vehicles in the registration database can use the gate."
						: "Modern mode loaded. Only vehicles in the registration database can use the gate.",
				"neutral");
	}

	private void setVersionUI() {
		boolean isModern = mode == AppMode.MODERN;
		setActiveButton("versionLegacy", !isModern);
		setActiveButton("versionModern", isModern);
		text("modeBadge", isModern ? "Current" : "Legacy");
	}

	private void quote() {
		ParkingEngine engine = engine();
		String lotId = value("quoteParkingLot");
		LocalDateTime arrivalTime = readDateTime("arrival", "arrival");
		if (arrivalTime == null) {
			return;
		}
		LocalDateTime departureTime = readDateTime("departure", "departure");
		if (departureTime == null) {
			return;
		}
		if (departureTime.isBefore(arrivalTime)) {
			setMessage("Departure cannot be before arrival.", "error");
			return;
		}
		QuoteResult result = engine.quote(lotId, arrivalTime, departureTime);
		if (!result.isOk) {
			setMessage(result.message, "error");
			return;
		}
		text("quoteTotal", result.total);
		text("quoteDuration", result.detail);
		setMessage(result.message, "success");
	}

	private void checkIn() {
		ParkingEngine engine = engine();
		String reg = normalizedRegistration();
		if (!isValidRegistration(reg, engine) || !isRequireRegisteredVehicle(reg)) {
			return;
		}
		GateResult result = engine.checkIn(reg, value("gateParkingLot"),
				LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
		setMessage(result.message, result.type);
		if (result.isOk) {
			updateCapacities();
			refreshHeaderStats();
		}
	}

	private void checkOut() {
		ParkingEngine engine = engine();
		String reg = normalizedRegistration();
		if (!isValidRegistration(reg, engine) || !isRequireRegisteredVehicle(reg)) {
			return;
		}
		GateResult result = engine.checkOut(reg, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
		setMessage(result.message, result.type);
		if (result.isOk) {
			updateCapacities();
			refreshHeaderStats();
		}
	}

	private void refreshRateSummary() {
		ParkingEngine engine = engine();
		String lotId = value("quoteParkingLot");
		text("selectedLotLabel", engine.displayName(lotId));
		text("rateSummary", engine.rateSummary(lotId));
	}

	private void refreshHeaderStats() {
		CapacityInfo selected = engine().capacityFor(value("gateParkingLot"));
		if (selected == null) {
			text("gateSelectedLot", "—");
			text("gateSelectedFree", "—");
			return;
		}
		text("gateSelectedLot", selected.label);
		text("gateSelectedFree", selected.free + " free");
	}

	private void updateCapacities() {
		CapacityInfo[] capacities = engine().capacities();
		StringBuilder html = new StringBuilder();
		for (CapacityInfo info : capacities) {
			int percent = info.capacity == 0 ? 0 : (int) Math.round(info.occupancy * 100.0 / info.capacity);
			html.append("<div class=\"capacity-row\" style=\"--lot-color:").append(info.color).append("\">")
					.append("<div class=\"capacity-title\"><span class=\"lot-dot\"></span><strong>")
					.append(escapeHtml(info.label)).append("</strong><span class=\"capacity-count\">")
					.append(info.occupancy).append(" / ").append(info.capacity).append("</span></div>")
					.append("<div class=\"capacity-meter-line\"><div class=\"capacity-track\"><span class=\"capacity-fill\" style=\"width:")
					.append(percent).append("%\"></span></div><span class=\"free-count\">").append(info.free)
					.append(" free</span></div></div>");
		}
		setHtml("capacityList", html.toString());
	}

	private ParkingEngine engine() {
		return mode == AppMode.MODERN ? modern : legacy;
	}

	private void populateLotSelect(String selectId, LotInfo[] lots, int selectedIdx) {
		StringBuilder html = new StringBuilder();
		for (int i = 0; i < lots.length; i++) {
			LotInfo lot = lots[i];
			html.append("<option value=\"").append(escapeHtmlAttribute(lot.id)).append("\"")
					.append(i == selectedIdx ? " selected" : "").append(">").append(escapeHtml(lot.label))
					.append("</option>");
		}
		setHtml(selectId, html.toString());
	}

	private String normalizedRegistration() {
		String reg = value("vehicleReg");
		return reg == null ? "" : reg.trim().toUpperCase();
	}

	private boolean isValidRegistration(String reg, ParkingEngine engine) {
		if (reg.isEmpty()) {
			setMessage("Enter a registration plate to begin.", "error");
			return false;
		}
		if (!engine.isValidRegistration(reg)) {
			setMessage(reg + " is not a valid registration plate.", "error");
			return false;
		}
		setValue("vehicleReg", reg);
		return true;
	}

	private boolean isRequireRegisteredVehicle(String reg) {
		if (!isRegisteredVehicleExists(reg)) {
			showVehicleRegistration(false, reg + " is not registered",
					"Check the vehicle registration DB before using the gate.");
			setMessage(reg + " is not a registered vehicle. Check-in and check-out are blocked.", "error");
			return false;
		}
		String make = registeredVehicleField(reg, "make");
		String model = registeredVehicleField(reg, "model");
		String fuel = registeredVehicleField(reg, "fuelType");
		String detail = make + (model.isEmpty() ? "" : " " + model);
		if (!fuel.isEmpty()) {
			detail += " · " + fuel;
		}
		showVehicleRegistration(true, "Registered vehicle", detail);
		return true;
	}

	private void refreshVehicleRegistration() {
		String reg = normalizedRegistration();
		if (reg.isEmpty()) {
			showVehicleRegistration(false, "Registration required", "Only registered vehicles can check in or out.");
			return;
		}
		ParkingEngine engine = engine();
		if (!engine.isValidRegistration(reg)) {
			showVehicleRegistration(false, "Invalid plate format", "Enter up to 6 letters or numbers.");
			return;
		}
		if (!isRegisteredVehicleExists(reg)) {
			showVehicleRegistration(false, "Not registered", reg + " is not present in the vehicle registry.");
			return;
		}
		String make = registeredVehicleField(reg, "make");
		String model = registeredVehicleField(reg, "model");
		String type = registeredVehicleField(reg, "vehicleType");
		String fuel = registeredVehicleField(reg, "fuelType");
		String detail = make + (model.isEmpty() ? "" : " " + model);
		if (!type.isEmpty())
			detail += " · " + type;
		if (!fuel.isEmpty())
			detail += " · " + fuel;
		showVehicleRegistration(true, "Registered vehicle", detail);
	}

	private void showVehicleRegistration(boolean isRegistered, String title, String detail) {
		text("registrationStatusTitle", title);
		text("registrationStatusDetail", detail);
		setClassName("registrationStatus",
				isRegistered ? "registration-status registered" : "registration-status unregistered");
	}

	private LocalDateTime readDateTime(String id, String label) {
		try {
			LocalDateTime parsed = parseDateTime(value(id)).truncatedTo(ChronoUnit.MINUTES);
			setValue(id, formatDateTime(parsed));
			return parsed;
		} catch (RuntimeException ex) {
			setMessage("Enter a valid " + label + " date and time.", "error");
			return null;
		}
	}

	private void setMessage(String message, String type) {
		setMessageBox("message", message == null ? "" : message, type == null ? "neutral" : type);
	}

	static LocalDateTime parseDateTime(String text) {
		String value = text == null ? "" : text.trim().replace(' ', 'T');
		if (value.length() < 16) {
			throw new IllegalArgumentException("Expected YYYY-MM-DDTHH:MM");
		}
		return LocalDateTime.of(Integer.parseInt(value.substring(0, 4)), Integer.parseInt(value.substring(5, 7)),
				Integer.parseInt(value.substring(8, 10)), Integer.parseInt(value.substring(11, 13)),
				Integer.parseInt(value.substring(14, 16)));
	}

	static String formatDateTime(LocalDateTime value) {
		return value.getYear() + "-" + two(value.getMonthValue()) + "-" + two(value.getDayOfMonth()) + "T"
				+ two(value.getHour()) + ":" + two(value.getMinute());
	}

	private static String two(int value) {
		return value < 10 ? "0" + value : String.valueOf(value);
	}

	private static long minutesBetween(LocalDateTime start, LocalDateTime end) {
		return Math.max(0, Duration.between(start, end).toMinutes());
	}

	private static String durationText(LocalDateTime start, LocalDateTime end) {
		long totalMinutes = minutesBetween(start, end);
		long days = totalMinutes / (24 * 60);
		long hours = (totalMinutes % (24 * 60)) / 60;
		long minutes = totalMinutes % 60;
		return days + " days · " + hours + " hours · " + minutes + " min";
	}

	private static String escapeHtml(String text) {
		return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	private static String escapeHtmlAttribute(String text) {
		return escapeHtml(text).replace("\"", "&quot;");
	}

	private interface ParkingEngine {
		LotInfo[] lots();
		QuoteResult quote(String lotId, LocalDateTime arrival, LocalDateTime departure);
		GateResult checkIn(String reg, String lotId, LocalDateTime now);
		GateResult checkOut(String reg, LocalDateTime now);
		CapacityInfo[] capacities();
		CapacityInfo capacityFor(String lotId);
		boolean isValidRegistration(String reg);
		String displayName(String lotId);
		String rateSummary(String lotId);
	}

	private record LotInfo(String id, String label) {
	}

	private record QuoteResult(boolean isOk, String total, String detail, String message) {
	}

	private record GateResult(boolean isOk, String type, String message) {
	}

	private static final class CapacityInfo {
		final String id;
		final String label;
		final int occupancy;
		final int capacity;
		final int free;
		final String color;

		private CapacityInfo(String id, String label, int occupancy, int capacity, String color) {
			this.id = id;
			this.label = label;
			this.occupancy = occupancy;
			this.capacity = capacity;
			this.free = Math.max(0, capacity - occupancy);
			this.color = color;
		}
	}

	private static final class LegacyEngine implements ParkingEngine {

		private static final String EXPRESS = "Express Parking Lot";
		private static final String SHORT = "Short Stay Parking Lot";
		private static final String LONG = "Long Stay Parking Lot";
		private static final String ECONO = "Econo Park Parking Lot";
		private static final String INDOOR = "Craddocks Indoor Parking Lot";
		private static final String OUTDOOR = "Craddocks Outdoor Parking Lot";

		private static final String[] ORDER = {EXPRESS, SHORT, LONG, ECONO, INDOOR, OUTDOOR};

		private final NosyParkable nosy = new NosyParker();

		@Override
		public LotInfo[] lots() {
			LotInfo[] lots = new LotInfo[ORDER.length];
			for (int i = 0; i < ORDER.length; i++) {
				lots[i] = new LotInfo(ORDER[i], displayName(ORDER[i]));
			}
			return lots;
		}

		@Override
		public QuoteResult quote(String lotId, LocalDateTime arrival, LocalDateTime departure) {
			nosy.setLotString(lotId);
			ParkingLot lot = nosy.getLot();
			if (lot == null) {
				return new QuoteResult(false, "—", "", "Select a car park.");
			}
			HashMap<String, String> data = nosy.computeCost(lot.getDuration(arrival, departure));
			return new QuoteResult(true, data.get("cost"),
					data.get("days") + " days · " + data.get("hours") + " hours · " + data.get("minutes") + " min",
					"Legacy quote calculated for " + displayName(lotId) + ".");
		}

		@Override
		public GateResult checkIn(String reg, String lotId, LocalDateTime now) {
			nosy.setLotString(lotId);
			ParkingLot lot = nosy.getLot();
			if (lot == null) {
				return new GateResult(false, "error", "Select a car park.");
			}
			if (lot.availability() <= 0) {
				return new GateResult(false, "error", displayName(lotId) + " is currently full.");
			}
			nosy.setVehicle(reg);
			if (nosy.getVehicle().getParkingLot() != null) {
				return new GateResult(false, "error", reg + " is already checked in to "
						+ displayName(nosy.getVehicle().getParkingLot().toString()) + ".");
			}
			lot.admit(nosy.getVehicle());
			nosy.getVehicle().setArrival(now);
			nosy.getVehicle().setParkingLot(lot);
			return new GateResult(true, "success", reg + " checked in to " + displayName(lotId) + ".");
		}

		@Override
		public GateResult checkOut(String reg, LocalDateTime now) {
			nosy.setVehicle(reg);
			Vehicle vehicle = nosy.getVehicle();
			ParkingLot lot = vehicle.getParkingLot();
			if (lot == null) {
				return new GateResult(false, "error", reg + " is not currently checked in.");
			}
			vehicle.setDeparture(now);
			nosy.setLot(lot);
			HashMap<String, String> data = nosy
					.computeCost(lot.getDuration(vehicle.getArrival(), vehicle.getDeparture()));
			lot.release(vehicle);
			vehicle.setParkingLot(null);
			return new GateResult(true, "success", reg + " checked out · " + data.get("cost") + " · " + data.get("days")
					+ " days · " + data.get("hours") + " hours · " + data.get("minutes") + " min");
		}

		@Override
		public CapacityInfo[] capacities() {
			List<CapacityInfo> list = new ArrayList<>();
			for (String lotId : ORDER) {
				nosy.setLotString(lotId);
				ParkingLot lot = nosy.getLot();
				list.add(new CapacityInfo(lotId, displayName(lotId), lot.occupancy(), lot.capacity(), colorFor(lotId)));
			}
			return list.toArray(new CapacityInfo[0]);
		}

		@Override
		public CapacityInfo capacityFor(String lotId) {
			for (CapacityInfo info : capacities()) {
				if (info.id.equals(lotId)) {
					return info;
				}
			}
			return null;
		}

		@Override
		public boolean isValidRegistration(String reg) {
			return nosy.vehicleRegistrationIsValid(reg);
		}

		@Override
		public String displayName(String lotId) {
			if (EXPRESS.equals(lotId))
				return "Express Park";
			if (SHORT.equals(lotId))
				return "Short Stay";
			if (LONG.equals(lotId))
				return "Long Stay";
			if (ECONO.equals(lotId))
				return "Econo Park";
			if (INDOOR.equals(lotId))
				return "Craddocks Indoor";
			if (OUTDOOR.equals(lotId))
				return "Craddocks Outdoor";
			return lotId;
		}

		@Override
		public String rateSummary(String lotId) {
			if (EXPRESS.equals(lotId))
				return "$5 first hour, then +$3/hr to a $30 daily cap.";
			if (SHORT.equals(lotId))
				return "$8 first hour, then +$5/hr to a $35 daily cap.";
			if (LONG.equals(lotId))
				return "$6 first hour, then +$4/hr to a $25 daily cap.";
			if (ECONO.equals(lotId))
				return "$4 first hour, then +$3/hr to a $20 daily cap.";
			if (INDOOR.equals(lotId))
				return "$7 first hour, then +$5/hr to a $32 daily cap.";
			if (OUTDOOR.equals(lotId))
				return "$5 first hour, then +$4/hr to a $24 daily cap.";
			return "Legacy pricing rule.";
		}

		private String colorFor(String lotId) {
			if (EXPRESS.equals(lotId))
				return "#f15a52";
			if (SHORT.equals(lotId))
				return "#ebb848";
			if (LONG.equals(lotId))
				return "#4da9d8";
			if (ECONO.equals(lotId))
				return "#7bc35b";
			if (INDOOR.equals(lotId))
				return "#9778d2";
			if (OUTDOOR.equals(lotId))
				return "#46bcb6";
			return "#20bdb7";
		}
	}

	private static final class ModernEngine implements ParkingEngine {

		private final Map<String, ModernLot> lots = new LinkedHashMap<>();
		private final Map<String, Session> activeSessions = new LinkedHashMap<>();

		private ModernEngine() {
			lots.put("express", new ModernLot("express", "Express Park", 620, 547, "#f15a52",
					new Tariff("$3 0-15 min · $12 first hour · $55 first day · $35/day after day 4") {
						@Override
						String price(long minutes) {
							if (minutes <= 15)
								return money(3);
							if (minutes <= 30)
								return money(6);
							if (minutes <= 45)
								return money(9);
							if (minutes <= 60)
								return money(12);
							if (minutes <= 120)
								return money(20);
							if (minutes <= 180)
								return money(27);
							if (minutes <= 240)
								return money(34);
							if (minutes <= 1440)
								return money(55);
							if (minutes <= 2880)
								return money(100);
							if (minutes <= 4320)
								return money(140);
							if (minutes <= 5760)
								return money(175);
							return money(175 + extraDays(minutes, 5760) * 35L);
						}
					}));
			lots.put("parkride", new ModernLot("parkride", "Park and Ride", 840, 490, "#9778d2",
					new Tariff("$29 first day · then tiered to $95 by day 4 · +$18/day after") {
						@Override
						String price(long minutes) {
							if (minutes <= 1440)
								return money(29);
							if (minutes <= 2880)
								return money(58);
							if (minutes <= 4320)
								return money(77);
							if (minutes <= 5760)
								return money(95);
							return money(95 + extraDays(minutes, 5760) * 18L);
						}
					}));
			lots.put("shortstay", new ModernLot("shortstay", "Short Stay", 740, 561, "#ebb848",
					new Tariff("$12 first hour · $45 first day · +$26/day after day 4") {
						@Override
						String price(long minutes) {
							if (minutes <= 60)
								return money(12);
							if (minutes <= 120)
								return money(20);
							if (minutes <= 180)
								return money(27);
							if (minutes <= 240)
								return money(34);
							if (minutes <= 1440)
								return money(45);
							if (minutes <= 2880)
								return money(86);
							if (minutes <= 4320)
								return money(116);
							if (minutes <= 5760)
								return money(144);
							return money(144 + extraDays(minutes, 5760) * 26L);
						}
					}));
			lots.put("longstay", new ModernLot("longstay", "Long Stay", 1020, 798, "#4da9d8",
					new Tariff("$12 first hour · $45 first day · +$24/day after day 4") {
						@Override
						String price(long minutes) {
							if (minutes <= 60)
								return money(12);
							if (minutes <= 120)
								return money(20);
							if (minutes <= 180)
								return money(27);
							if (minutes <= 240)
								return money(34);
							if (minutes <= 1440)
								return money(45);
							if (minutes <= 2880)
								return money(86);
							if (minutes <= 4320)
								return money(116);
							if (minutes <= 5760)
								return money(132);
							return money(132 + extraDays(minutes, 5760) * 24L);
						}
					}));
			lots.put("orchard", new ModernLot("orchard", "Orchard Road Car Park", 510, 130, "#7bc35b",
					new Tariff("$12 up to 2 hrs · $40 first day · +$22/day after day 4") {
						@Override
						String price(long minutes) {
							if (minutes <= 120)
								return money(12);
							if (minutes <= 180)
								return money(20);
							if (minutes <= 240)
								return money(26);
							if (minutes <= 1440)
								return money(40);
							if (minutes <= 2880)
								return money(75);
							if (minutes <= 4320)
								return money(104);
							if (minutes <= 5760)
								return money(129);
							return money(129 + extraDays(minutes, 5760) * 22L);
						}
					}));
		}

		@Override
		public LotInfo[] lots() {
			LotInfo[] result = new LotInfo[lots.size()];
			int i = 0;
			for (ModernLot lot : lots.values()) {
				result[i++] = new LotInfo(lot.id, lot.label);
			}
			return result;
		}

		@Override
		public QuoteResult quote(String lotId, LocalDateTime arrival, LocalDateTime departure) {
			ModernLot lot = lots.get(lotId);
			if (lot == null) {
				return new QuoteResult(false, "—", "", "Select a car park.");
			}
			return new QuoteResult(true, lot.tariff.price(minutesBetween(arrival, departure)),
					durationText(arrival, departure), "Modern quote calculated for " + lot.label + ".");
		}

		@Override
		public GateResult checkIn(String reg, String lotId, LocalDateTime now) {
			ModernLot lot = lots.get(lotId);
			if (lot == null) {
				return new GateResult(false, "error", "Select a car park.");
			}
			if (activeSessions.containsKey(reg)) {
				return new GateResult(false, "error",
						reg + " is already checked in to " + activeSessions.get(reg).lot.label + ".");
			}
			if (currentOccupancy(lot) >= lot.capacity) {
				return new GateResult(false, "error", lot.label + " is currently full.");
			}
			activeSessions.put(reg, new Session(reg, lot, now));
			return new GateResult(true, "success", reg + " checked in to " + lot.label + ".");
		}

		@Override
		public GateResult checkOut(String reg, LocalDateTime now) {
			Session session = activeSessions.remove(reg);
			if (session == null) {
				return new GateResult(false, "error", reg + " is not currently checked in.");
			}
			String total = session.lot.tariff.price(minutesBetween(session.arrival, now));
			return new GateResult(true, "success",
					reg + " checked out · " + total + " · " + durationText(session.arrival, now));
		}

		@Override
		public CapacityInfo[] capacities() {
			CapacityInfo[] result = new CapacityInfo[lots.size()];
			int i = 0;
			for (ModernLot lot : lots.values()) {
				result[i++] = new CapacityInfo(lot.id, lot.label, currentOccupancy(lot), lot.capacity, lot.color);
			}
			return result;
		}

		@Override
		public CapacityInfo capacityFor(String lotId) {
			ModernLot lot = lots.get(lotId);
			return lot == null
					? null
					: new CapacityInfo(lot.id, lot.label, currentOccupancy(lot), lot.capacity, lot.color);
		}

		@Override
		public boolean isValidRegistration(String reg) {
			return reg.matches("(?i)^[A-Z0-9]{1,6}$");
		}

		@Override
		public String displayName(String lotId) {
			ModernLot lot = lots.get(lotId);
			return lot == null ? lotId : lot.label;
		}

		@Override
		public String rateSummary(String lotId) {
			ModernLot lot = lots.get(lotId);
			return lot == null ? "Official drive-up rate" : lot.tariff.summary;
		}

		private int currentOccupancy(ModernLot lot) {
			int sessions = 0;
			for (Session session : activeSessions.values()) {
				if (session.lot.id.equals(lot.id)) {
					sessions++;
				}
			}
			return lot.baselineOccupancy + sessions;
		}

		private static long extraDays(long minutes, long afterMinutes) {
			long remaining = Math.max(0, minutes - afterMinutes);
			return (remaining + 1439) / 1440;
		}

		private static String money(long dollars) {
			return "NZD " + dollars + ".00";
		}

		private record ModernLot(String id, String label, int capacity, int baselineOccupancy, String color,
				Tariff tariff) {
		}

		private abstract static class Tariff {
			final String summary;

			private Tariff(String summary) {
				this.summary = summary;
			}

			abstract String price(long minutes);
		}

		private record Session(String reg, ModernLot lot, LocalDateTime arrival) {
		}
	}

	@JSFunctor
	private interface Callback extends JSObject {
		void run();
	}

	@JSFunctor
	private interface StringCallback extends JSObject {
		void run(String value);
	}

	@JSBody(params = {"id", "callback"}, script = "document.getElementById(id).addEventListener('click', callback);")
	private static native void onClick(String id, Callback callback);

	@JSBody(params = {"id", "callback"}, script = "document.getElementById(id).addEventListener('change', callback);")
	private static native void onChange(String id, Callback callback);

	@JSBody(params = {"id", "callback"}, script = "document.getElementById(id).addEventListener('input', callback);")
	private static native void onInput(String id, Callback callback);

	@JSBody(params = {"id",
			"callback"}, script = "document.getElementById(id).addEventListener('keydown', function(e){ if(e.key==='Enter'){ e.preventDefault(); callback(); }});")
	private static native void onEnter(String id, Callback callback);

	@JSBody(params = {"id"}, script = "return document.getElementById(id).value;")
	private static native String value(String id);

	@JSBody(params = {"id", "value"}, script = "document.getElementById(id).value = value;")
	private static native void setValue(String id, String value);

	@JSBody(params = {"id", "value"}, script = "document.getElementById(id).textContent = value;")
	private static native void text(String id, String value);

	@JSBody(params = {"id", "value"}, script = "document.getElementById(id).innerHTML = value;")
	private static native void setHtml(String id, String value);

	@JSBody(params = {"id",
			"active"}, script = "var e=document.getElementById(id); if(active){e.classList.add('active');} else {e.classList.remove('active');}")
	private static native void setActiveButton(String id, boolean active);

	@JSBody(params = {"id", "className"}, script = "document.getElementById(id).className=className;")
	private static native void setClassName(String id, String className);

	private boolean isRegisteredVehicleExists(String reg) {
		return regoStore.isRegistered(reg);
	}

	private String registeredVehicleField(String reg, String field) {
		return regoStore.vehicleField(reg, field);
	}

	@JSBody(params = {"containerId", "dataName",
			"callback"}, script = "document.getElementById(containerId).addEventListener('click',function(e){var t=e.target.closest('[data-'+dataName+']'); if(t && this.contains(t)) callback(t.getAttribute('data-'+dataName));});")
	private static native void onDataClick(String containerId, String dataName, StringCallback callback);

	@JSBody(params = {"id",
			"visible"}, script = "var e=document.getElementById(id); if(visible)e.classList.remove('hidden');else e.classList.add('hidden');")
	private static native void setVisible(String id, boolean isVisible);

	@JSBody(params = {"id", "disabled"}, script = "document.getElementById(id).disabled=disabled;")
	private static native void setDisabled(String id, boolean isDisabled);

	@JSBody(params = {"id", "value"}, script = "document.getElementById(id).textContent=value;")
	private static native void setButtonText(String id, String value);

	@JSBody(params = {"message"}, script = "return window.confirm(message);")
	private static native boolean isConfirmAction(String message);

	private String regCurrentUserEmail() {
		return regoStore.currentUserEmail();
	}

	private boolean isRegLogin(String email, String password) {
		return regoStore.login(email, password);
	}

	private void regLogout() {
		regoStore.logout();
	}

	private void regReset() {
		regoStore.reset();
	}

	private int regRegisterOwner(String forename, String surname, String email, String password) {
		return regoStore.registerOwner(forename, surname, email, password);
	}

	private boolean isRegPopulateOwnerForm(String email) {
		if (regoStore.ownerField(email, "email").isEmpty()) {
			return false;
		}
		String forename = regoStore.ownerField(email, "forename");
		String surname = regoStore.ownerField(email, "surname");
		setValue("ownerForename", forename);
		setValue("ownerSurname", surname);
		setValue("ownerEmail", regoStore.ownerField(email, "email"));
		setValue("ownerAddressOne", regoStore.ownerField(email, "addressOne"));
		setValue("ownerAddressTwo", regoStore.ownerField(email, "addressTwo"));
		setValue("ownerPhone", regoStore.ownerField(email, "phone"));
		setValue("ownerPassword", regoStore.ownerField(email, "password"));
		text("ownerNameDisplay", (forename + " " + surname).trim());
		text("ownerEmailDisplay", regoStore.ownerField(email, "email"));
		return true;
	}

	private boolean isRegUpdateOwner(String email, String forename, String surname, String addressOne,
			String addressTwo, String phone, String password) {
		return regoStore.updateOwner(email, forename, surname, addressOne, addressTwo, phone, password);
	}

	private boolean isRegDeleteOwner(String email) {
		return regoStore.deleteOwner(email);
	}

	private String regVehicleListHtml(String email, String selectedPlate) {
		return regoStore.vehicleListHtml(email, selectedPlate);
	}

	private boolean isRegPopulateVehicleForm(String plate) {
		if (!regoStore.isRegistered(plate)) {
			return false;
		}
		setValue("regPlate", regoStore.vehicleField(plate, "plate"));
		setValue("regMake", regoStore.vehicleField(plate, "make"));
		setValue("regModel", regoStore.vehicleField(plate, "model"));
		setValue("regManufactureDate", regoStore.vehicleField(plate, "manufactureDate"));
		setValue("regAddressOne", regoStore.vehicleField(plate, "addressOne"));
		setValue("regAddressTwo", regoStore.vehicleField(plate, "addressTwo"));
		setValue("regVehicleType", regoStore.vehicleField(plate, "vehicleType"));
		setValue("regFuelType", regoStore.vehicleField(plate, "fuelType"));
		return true;
	}

	private void regClearVehicleForm() {
		setValue("regPlate", "");
		setValue("regMake", "");
		setValue("regModel", "");
		setValue("regManufactureDate", "");
		setValue("regAddressOne", "");
		setValue("regAddressTwo", "");
		setValue("regVehicleType", "MA");
		setValue("regFuelType", "PETROL");
	}

	private int regAddVehicle(String ownerEmail, String plate, String make, String model, String manufactureDate,
			String addressOne, String addressTwo, String vehicleType, String fuelType) {
		return regoStore.addVehicle(ownerEmail, plate, make, model, manufactureDate, addressOne, addressTwo,
				vehicleType, fuelType);
	}

	private boolean isRegUpdateVehicle(String plate, String make, String model, String manufactureDate,
			String addressOne, String addressTwo, String vehicleType, String fuelType) {
		return regoStore.updateVehicle(plate, make, model, manufactureDate, addressOne, addressTwo, vehicleType,
				fuelType);
	}

	private boolean isRegDeleteVehicle(String ownerEmail, String plate) {
		return regoStore.deleteVehicle(ownerEmail, plate);
	}

	private boolean isRegPopulateHistForm(String plate) {
		if (!regoStore.hasHistory(plate)) {
			regClearHistForm();
			return false;
		}
		setValue("histVin", regoStore.historyField(plate, "vin"));
		setValue("histOdometer", regoStore.historyField(plate, "odometer"));
		setValue("histRegistrationDate", regoStore.historyField(plate, "registrationDate"));
		setValue("histExpiry", regoStore.historyField(plate, "wofExpiry"));
		setValue("histStatus", regoStore.historyField(plate, "wofStatus"));
		return true;
	}

	private void regClearHistForm() {
		setValue("histVin", "");
		setValue("histOdometer", "");
		setValue("histRegistrationDate", "");
		setValue("histExpiry", "");
		setValue("histStatus", "PASSED");
	}

	private int regSaveHist(String plate, String vin, String odometer, String registrationDate, String expiry,
			String status) {
		return regoStore.saveHistory(plate, vin, odometer, registrationDate, expiry, status);
	}

	private int regOwnerCount() {
		return regoStore.ownerCount();
	}

	private int regVehicleCount() {
		return regoStore.vehicleCount();
	}

	@JSBody(params = {"id", "value",
			"type"}, script = "var e=document.getElementById(id); e.textContent=value; e.className='session-message' + (id.indexOf('registry')===0?' compact-message':'') + (type==='success'?' success':type==='error'?' error':'');")
	private static native void setMessageBox(String id, String value, String type);
}
