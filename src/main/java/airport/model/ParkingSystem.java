package airport.model;

import airport.model.parking.*;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

/**
 * Class representing the overall parking business. Keeps info about the various
 * parking lots we have. When vehicles enter the system, their number plates are
 * recorded by cameras and a Vehicle record is created. This enables us to
 * prevent vehicles being in two places at the same time, and similar problems.
 *
 * @author Adam Ross
 */
public class ParkingSystem {

	private final Set<ParkingLot> parkingLots; // attribute for a Set of the parking lots

	/**
	 * Constructor for class
	 */
	public ParkingSystem() {
		this.parkingLots = new HashSet<>(); // variable for the HashSet of the parking lots
	}

	/**
	 * Returns (in no particular order) the parking lots we have.
	 *
	 * @return A list of the parking lots at the Christchurch airport
	 */
	private Collection<ParkingLot> lots() {
		return this.parkingLots; // returns a HashSet of the parking lots
	}

	/**
	 * Adds a new parking lot
	 *
	 * @param p
	 *            A parking lot
	 */
	public void addLot(ParkingLot p) {
		this.parkingLots.add(p); // adds a parking lot to the parking lot HashSet
	}

	/**
	 * Provides a list of the parking lot types offered for parking at the
	 * Christchurch airport
	 *
	 * @param lots
	 *            An empty HashSet for adding lots names to
	 * @return A list of the parking lots offered
	 */
	public Set<String> parkOptions(HashSet<String> lots) {
		for (ParkingLot lot : lots()) {
			lots.add(lot.toString());
		}
		return lots;
	}

	/**
	 * Gets te selected parking lot
	 *
	 * @param selectedLot
	 *            the selected parking lot
	 * @return the selected parking lot or null
	 */
	public ParkingLot getParkingLot(String selectedLot) {
		for (ParkingLot lot : lots()) {
			if (lot.toString().equals(selectedLot)) {
				return lot;
			}
		}
		return null;
	}

	/**
	 * Find the vehicle corresponding to the registration number. Add it if it isn't
	 * already known
	 *
	 * @param reg
	 *            The registration identification of the vehicle recognized by the
	 *            camera
	 * @return The corresponding vehicle
	 */
	public Vehicle vehicleFor(String reg) {
		for (ParkingLot lot : lots()) { // searches for a lot in parking lots
			for (Vehicle vehicle : lot.occupants()) { // searches for a vehicle in a parking lot
				if (vehicle.identityCrisis(reg)) { // checks if the registration of a new vehicle is in parking lot
					return vehicle; // returns the vehicle already parked in the parking lot
				}
			}
		}
		return new Vehicle(reg); // adds a new vehicle
	}
}
