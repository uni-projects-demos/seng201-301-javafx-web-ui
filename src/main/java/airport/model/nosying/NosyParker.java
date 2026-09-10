package airport.model.nosying;

import airport.model.ParkingSystem;
import airport.model.Vehicle;
import airport.model.parking.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * NosyParker class
 *
 * @author Adam Ross
 */
public class NosyParker implements NosyParkable {

	private final ParkingSystem parkingSystem;
	private Set<String> parkNames;
	private ParkingLot parkingLot;
	private Vehicle vehicle;

	/**
	 * NosyParker constructor
	 */
	public NosyParker() {
		this.parkingSystem = new ParkingSystem();
		this.addParkingLots();
	}

	/**
	 * Adds each parking lot to the ParkingSystem
	 */
	private void addParkingLots() {
		final int PARK_CAP = 100; // Constant value for total parking spaces for each parking lot
		this.parkingSystem.addLot(new CraddocksIndoorParkingLot(NosyParkerEnum.CRADDICKS_INDOOR.getValue(), PARK_CAP));
		this.parkingSystem
				.addLot(new CraddocksOutdoorParkingLot(NosyParkerEnum.CRADDOCKS_OUTDOOR.getValue(), PARK_CAP));
		this.parkingSystem.addLot(new EconoParkParkingLot(NosyParkerEnum.ECONO_PARK.getValue(), PARK_CAP));
		this.parkingSystem.addLot(new ExpressParkingLot(NosyParkerEnum.EXPRESS_PARK.getValue(), PARK_CAP));
		this.parkingSystem.addLot(new LongStayParkingLot(NosyParkerEnum.LONG_STAY.getValue(), PARK_CAP));
		this.parkingSystem.addLot(new ShortStayParkingLot(NosyParkerEnum.SHORT_STAY.getValue(), PARK_CAP));
		this.parkNames = this.getParkingSystem().parkOptions(new HashSet<>());
	}

	/**
	 * Gets a Set of names of each parking lot in the parking systems
	 *
	 * @return a Set of name strings for each park in the parking system
	 */
	public Set<String> getParkNames() {
		return this.parkNames;
	}

	/**
	 * Gets the ParkingSystem
	 *
	 * @return the ParkingSystem
	 */
	private ParkingSystem getParkingSystem() {
		return this.parkingSystem;
	}

	/**
	 * Sets a vehicle instance
	 *
	 * @param reg
	 *            the vehicle plate
	 */
	public void setVehicle(String reg) {
		this.vehicle = getParkingSystem().vehicleFor(reg.toUpperCase());
	}

	/**
	 * Gets a vehicle instance
	 *
	 * @return the vehicle instance
	 */
	public Vehicle getVehicle() {
		return this.vehicle;
	}

	/**
	 * Sets a parking lot instance with a string name
	 *
	 * @param parkingLot
	 *            the parking lot name string
	 */
	public void setLotString(String parkingLot) {
		this.parkingLot = getParkingSystem().getParkingLot(parkingLot);
	}

	/**
	 * Sets a parking lot instance with a parking lot instance
	 *
	 * @param parkingLot
	 *            the parking lot instance
	 */
	public void setLot(ParkingLot parkingLot) {
		this.parkingLot = parkingLot;
	}

	/**
	 * Gets a parking lot instance with a parking lot instance
	 *
	 * @return the parking lot instance
	 */
	public ParkingLot getLot() {
		return this.parkingLot;
	}

	/**
	 * Computes the cost of parking for a vehicle in a selected parking lot for a
	 * selected duration of parking
	 *
	 * @param duration
	 *            a HashMap of days, hours, minutes, and seconds representing the
	 *            duration of parking
	 * @return a HashMap with the charge, and the duration in days, hours and
	 *         minutes, each for displaying
	 */
	public HashMap<String, String> computeCost(HashMap<String, Long> duration) {
		final int SIXTY = 60; // constant for the minutes in an hour and the seconds in a minute
		final int TWO_FOUR = 24; // constant for the hours in a day
		return new HashMap<>() {
			{
				put(NosyParkerEnum.DAYS.getValue(), duration.get(NosyParkerEnum.DAYS.getValue()).toString());
				put(NosyParkerEnum.HRS.getValue(),
						String.valueOf(duration.get(NosyParkerEnum.HRS.getValue()) % TWO_FOUR));
				put(NosyParkerEnum.MINS.getValue(),
						String.valueOf(duration.get(NosyParkerEnum.MINS.getValue()) % SIXTY));
				put(NosyParkerEnum.COST.getValue(), getLot().computeCharge(duration).toString());
			}
		};
	}

	/**
	 * Checks if a vehicle registration plate entered to the vehicleRegistration
	 * TextField is a valid vehicle plate
	 *
	 * @param reg
	 *            the vehicle registration plate
	 * @return true is the entered vehicle registration plate is valid, false
	 *         otherwise
	 */
	public Boolean vehicleRegistrationIsValid(String reg) {
		final int max = 6; // the maximum length of a valid vehicle registration plate
		return reg.toUpperCase().matches(NosyParkerEnum.REG.getValue()) && !reg.isEmpty() && reg.length() <= max;
	}

	/**
	 * Checks if entered dates are valid - departure dateTime is not before arrival
	 * datTime
	 *
	 * @param arrival
	 *            the arrival date time
	 * @param departure
	 *            the departure date time
	 * @return true if departure is not before arrival, false otherwise
	 */
	public Boolean isDatesValid(LocalDateTime arrival, LocalDateTime departure) {
		return !departure.isBefore(arrival);
	}
}
