package airport.model.parking;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collection;

import airport.model.Vehicle;

/**
 * Parking lot abstract class
 *
 * @author Adam Ross
 */
public abstract class AbstractParkingLot implements ParkingLot {

	static final String DEFAULT_CHARGE = ParkingLotEnum.DEFAULT_CHARGE.getValue(); // default charge set in NZ currency
	static final long WEEK_DAYS = 7; // constant for days in a 7-day week
	static final long DAY_HOURS = 24; // constant for hours in a 24-hour day
	static final long HOUR_MINUTES = 60; // constant for minutes in a 60-minute hour
	private final String parkingLot; // attribute for the name of the parking lot
	private final int parkingLotCapacity; // attribute for the capacity of the parking lot
	private final HashSet<Vehicle> occupantVehicles; // attribute for array list of vehicles occupying parking lot

	/**
	 * Constructor for class.
	 *
	 * @param parkingLot
	 *            Name of the parking lot.
	 * @param initialCapacity
	 *            Capacity of the parking lot.
	 */
	AbstractParkingLot(String parkingLot, int initialCapacity) {
		this.parkingLot = parkingLot; // variable for the name of the parking lot
		this.parkingLotCapacity = initialCapacity; // variable for the maximum capacity of parking spaces in parking lot
		this.occupantVehicles = new HashSet<>(parkingLotCapacity); // array list of vehicles occupying parking lot
	}

	/**
	 * Creates a map of duration vehicle has parked in standard days, hours,
	 * minutes, and seconds
	 *
	 * @param arrival
	 *            the arrival date and time of a vehicle using a parking lot
	 * @param departure
	 *            the departure date and time of a vehicle using a parking lot
	 * @return a HashMap representing the duration in each of days, hours, minutes
	 *         and seconds
	 */
	public HashMap<String, Long> getDuration(LocalDateTime arrival, LocalDateTime departure) {
		final int SECS_PER_DAY = 86400; // The number of seconds in a 24-hour day
		final int SECS_PER_HOUR = 3600; // The number of seconds in a 60-minute hour
		final int SECS_PER_MIN = 60; // The number of seconds in a minute
		long seconds = ChronoUnit.SECONDS.between(arrival, departure);
		return new HashMap<>() {
			{
				put("days", seconds / SECS_PER_DAY);
				put("hours", seconds / SECS_PER_HOUR);
				put("minutes", seconds / SECS_PER_MIN);
				put("seconds", seconds);
			}
		};
	}

	/**
	 * Admits a Vehicle to the lot if lot is not full and updates the occupancy.
	 *
	 * @param v
	 *            The vehicle entering the lot.
	 */
	public void admit(Vehicle v) {
		if (availability() > 0) { // checks that the parking lot is not full
			this.occupantVehicles.add(v); // adds the admitted vehicle to the occupants hash set
		}
	}

	/**
	 * Allows a vehicle to depart from the lot if the lot has any vehicles and
	 * updates occupancy
	 *
	 * @param v
	 *            The vehicle leaving the lot
	 */
	public void release(Vehicle v) {
		if (occupancy() > 0) { // checks that the parking lot is not empty
			this.occupantVehicles.remove(v); // removes the released vehicle from the occupants hash set
		}
	}

	/**
	 * How many vehicles are currently parked in the lot.
	 *
	 * @return The number of vehicles in the parking lot (between 0 and capacity)
	 */
	public int occupancy() {
		return this.occupantVehicles.size(); // returns the count of vehicles currently occupying the parking lot
	}

	/**
	 * How many Vehicles can be parked in the lot.
	 *
	 * @return The capacity of the parking lot (non-negative)
	 */
	public int capacity() {
		return this.parkingLotCapacity; // returns the maximum capacity of parking spaces in the parking lot
	}

	/**
	 * How many parking spaces are currently available.
	 *
	 * @return number of empty parking spaces;
	 */
	public int availability() {
		return capacity() - occupancy();
	}

	/**
	 * Overrides the toString method
	 *
	 * @return The name of the parking lot as a string
	 */
	public String toString() {
		return this.parkingLot; // returns the name of the parking lot as a string
	}

	/**
	 * The vehicles currently parked in this lot
	 *
	 * @return A Collection of the vehicles parked in the lot (in no particular
	 *         order)
	 */
	public Collection<Vehicle> occupants() {
		return this.occupantVehicles; // returns a hash set of the vehicles currently occupying the parking lot
	}
}
