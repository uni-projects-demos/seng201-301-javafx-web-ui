package airport.model.nosying;

import airport.model.Vehicle;
import airport.model.parking.ParkingLot;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;

/**
 * NosyParkable interface
 *
 * @author Adam Ross
 */
public interface NosyParkable {
	/**
	 * Gets a Set of names of each parking lot in the parking systems
	 *
	 * @return a Set of name strings for each park in the parking system
	 */
	Set<String> getParkNames();

	/**
	 * Sets a vehicle instance
	 *
	 * @param reg
	 *            the vehicle plate
	 */
	void setVehicle(String reg);

	/**
	 * Gets a vehicle instance
	 *
	 * @return the vehicle instance
	 */
	Vehicle getVehicle();

	/**
	 * Sets a parking lot instance with a string name
	 *
	 * @param parkingLot
	 *            the parking lot name string
	 */
	void setLotString(String parkingLot);

	/**
	 * Sets a parking lot instance with a parking lot instance
	 *
	 * @param parkingLot
	 *            the parking lot instance
	 */
	void setLot(ParkingLot parkingLot);

	/**
	 * Gets a parking lot instance with a parking lot instance
	 *
	 * @return the parking lot instance
	 */
	ParkingLot getLot();

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
	HashMap<String, String> computeCost(HashMap<String, Long> duration);

	/**
	 * Checks if a vehicle registration plate entered to the vehicleRegistration
	 * TextField is a valid vehicle plate
	 *
	 * @param reg
	 *            the vehicle registration plate
	 * @return true is the entered vehicle registration plate is valid, false
	 *         otherwise
	 */
	Boolean vehicleRegistrationIsValid(String reg);

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
	Boolean isDatesValid(LocalDateTime arrival, LocalDateTime departure);
}
