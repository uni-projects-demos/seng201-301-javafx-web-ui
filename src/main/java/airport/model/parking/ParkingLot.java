package airport.model.parking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;

import airport.model.Vehicle;
import org.joda.money.Money;

/**
 * Individual classes (such as ShortStayParkingLot) implement this. Vehicles are
 * admitted if there are spaces available.
 *
 * @author Adam Ross; resourced from Parkable, authored by nic11
 */
public interface ParkingLot {

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
	HashMap<String, Long> getDuration(LocalDateTime arrival, LocalDateTime departure);

	/**
	 * Compute the cost of parking in this lot between these times.
	 *
	 * @param duration
	 *            the duration of the time that a vehicle has been parked in a lot
	 * @return The cost of parking here.
	 */
	Money computeCharge(HashMap<String, Long> duration);

	/**
	 * Admit a Vehicle to the lot and update the occupancy.
	 *
	 * @param v
	 *            The vehicle entering the lot.
	 */
	void admit(Vehicle v);

	/**
	 * Allow vehicle to depart from the lot and update occupancy. Charges will have
	 * been paid.
	 *
	 * @param v
	 *            The vehicle leaving the lot.
	 */
	void release(Vehicle v);

	/**
	 * How many Vehicles can be parked in the lot.
	 *
	 * @return capacity (non-negative)
	 */
	int capacity();
	/**
	 * How many vehicles are currently parked in the lot.
	 *
	 * @return number of vehicles (between 0 and capacity)
	 */
	int occupancy();

	/**
	 * How many parking spaces are currently available.
	 *
	 * @return number of empty parking spaces;
	 */
	int availability();

	/**
	 * The Vehicles currently parked in this lot.
	 *
	 * @return Collection of vehicles (no particular order required)
	 */
	Collection<Vehicle> occupants();
}
